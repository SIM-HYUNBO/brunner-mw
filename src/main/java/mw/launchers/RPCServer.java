package mw.launchers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import mw.MWClient;
import mw.utility.JsonUtil;

public class RPCServer {

	public static void main(String[] argv) throws Exception {
		RPCServer server = new RPCServer();
		server.start("0.0.0.0", 5672, "admin", "admin", "BrunnerServer");
	}

	Object runningMonitor = new Object();
	boolean isRunning = false;

	public void start(
			String host,
			int port,
			String userName,
			String password,
			String queueName) throws IOException, TimeoutException {

		boolean durable = true;
		boolean exclusive = false;
		boolean autoDelete = false;
		Map<String, Object> arguments = null;

		arguments = new HashMap<String, Object>();
		arguments.put("x-message-ttl", 10000);

		MWClient client = new MWClient();

		DeliverCallback listener = new DeliverCallback() {
			@Override
			public void handle(String consumerTag, Delivery delivery) throws IOException {
				byte[] reply = null;
				AMQP.BasicProperties replyProps = null;
				try {
					replyProps = new AMQP.BasicProperties.Builder()
							.correlationId(delivery.getProperties().getCorrelationId())
							.build();

					reply = executeService(delivery.getBody());
				} catch (RuntimeException e) {
					System.out.println(e.toString());
				} finally {
					client.getChannel().basicPublish("", delivery.getProperties().getReplyTo(), replyProps, reply);
					client.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
					// RabbitMq consumer worker thread notifies the RPC server owner thread

					if (reply != null) {
						System.out.println("Sent Reply:"
								+ JsonUtil.toBeautifyString((JsonObject) new JsonParser().parse(new String(reply))));
					} else {
						System.out.println("Sent Response: null");
					}

					synchronized (runningMonitor) {
						runningMonitor.notify();
					}
				}
			}
		};

		client.initializeHost(host, port, userName, password);

		client.getChannel().queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
		client.getChannel().queuePurge(queueName);
		client.getChannel().basicQos(1);

		System.out.println("The rpc server started to awaiting RPC requests");

		client.getChannel().basicConsume(queueName, false, listener, (consumerTag -> {
		}));
		// Wait and be prepared to consume the message from RPC client.
		isRunning = true;
		while (isRunning) {
			synchronized (runningMonitor) {
				try {
					runningMonitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		client.close();
	}

	protected byte[] executeService(byte[] requestData) {
		System.out.println("Received Resquest:"
				+ JsonUtil.toBeautifyString((JsonObject) new JsonParser().parse(new String(requestData))));

		String strRequest = new String(requestData);
		JsonObject jRequest = (JsonObject) new JsonParser().parse(strRequest);
		JsonObject jReply = executeService(jRequest);

		try {
			return jReply.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected JsonObject executeService(JsonObject request) {
		JsonObject reply = new JsonObject();
		reply.addProperty("requestData", request.get("Test Value").getAsString());
		reply.addProperty("replyData", "OK");

		return reply;
	}

	public void stop() {
		isRunning = false;

		synchronized (runningMonitor) {
			runningMonitor.notifyAll();
		}
	}
}