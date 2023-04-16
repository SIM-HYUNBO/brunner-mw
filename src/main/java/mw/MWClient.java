package mw;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/***
 * Rabbit MQƯ¡ (https://skibis.tistory.com/310)

 * @author HBSIM
 *
 */
public class MWClient {
	
	/***
	 * 
	 */
	final public static String exchangeType_DIRECT = BuiltinExchangeType.DIRECT.name().toLowerCase();

	/***
	 * 
	 */
	final public static String exchangeType_FANOUT = BuiltinExchangeType.FANOUT.name().toLowerCase();
	
	/***
	 * 
	 */
	final public static String exchangeType_TOPIC = BuiltinExchangeType.TOPIC.name().toLowerCase();

	/***
	 * 
	 */
	final public static String exchangeType_HEADERS = BuiltinExchangeType.HEADERS.name().toLowerCase();
	
	
    private Connection connection;
    private Channel channel;
    private String host;
    ConnectionFactory factory;
    
    public Channel getChannel() {
    	return this.channel;
    }
    
    public boolean IsOpen() {
    	if(this.channel== null)
    		return false;
    
    	return this.channel.isOpen();
    }
    
	public void initializeHost(String host, int port, String userName, String password) throws IOException, TimeoutException {
		this.host = host;
        this.factory = new ConnectionFactory();
        factory.setHost(this.host);
        factory.setPort(port);
        factory.setUsername(userName);
        factory.setPassword(password);

        this.connection = factory.newConnection();
        this.channel = this.connection.createChannel();		
	}

	public byte[] requestSync(
    		String requestQueueName, 
    		byte[] message,
    		int timeoutMS) throws IOException, InterruptedException, TimeoutException {
        
    	final String correlationId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .expiration("1000")
                .correlationId(correlationId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message);

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
        	
            if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.poll(timeoutMS, TimeUnit.MILLISECONDS);
        if (result == null)
        	throw new TimeoutException(String.format("The request [%s] timed out.", props.getCorrelationId()));
        
        channel.basicCancel(ctag);
        
        return result.getBytes("UTF-8");
    }
    
    public void publishToExchange(
    		String exchangeName, 
    		String routingKey, 
    		String exchangeType, 
    		boolean durable,
    		BasicProperties props, 
    		byte[] message) throws IOException {
        
    	channel.exchangeDeclare(exchangeName, exchangeType, durable);
    	channel.basicPublish(exchangeName, routingKey, props, message);

        System.out.println(String.format("Sent [%s]", new String(message, "UTF-8")));    	
    }

    public void publishToQueue(
    		String exchangeName,
    		String queueName,
    		boolean durable,
    		boolean exclusive,
    		boolean autoDelete,
    		Map<String, Object> arguments,
    		byte[] message) throws IOException {
    	
        channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
        channel.basicPublish(exchangeName, queueName, null, message);
        
        System.out.println(String.format("Sent message [%s] to queue [%s]", message, queueName));   
    }
    
    public void receiveFromExchange(
    		String exchangeName, 
    		String type,
    		String[] routingKeys, 
    		DeliverCallback listener) throws IOException {
        
    	channel.exchangeDeclare(exchangeName, type);
        String queueName = channel.queueDeclare().getQueue();
        
        for(String routingKey : routingKeys)
        	channel.queueBind(queueName, exchangeName, routingKey);

        System.out.println(String.format("Waiting for messages from exchange [%s] type: [%s]. To exit press CTRL+C", exchangeName, type));

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        	
        	if(listener != null)
        		listener.handle(consumerTag, delivery);
        };
        
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
    
    public void receiveFromExchange(
    		String exchangeName, 
    		String type,
    		String[] routingKeys,
    		String queueInputName,
    		boolean durable,
    		boolean exclusive, 
    		boolean autoDelete, 
    		Map<String, Object> arguments,
    		Map<String, Object> headers,
    		DeliverCallback listener
    		) throws IOException {
        
    	channel.exchangeDeclare(
    			exchangeName, 
    			type, 
    			durable);
        
    	String queueName = channel.queueDeclare(
        		queueInputName, 
        		durable, 
        		exclusive, 
        		autoDelete, 
        		arguments).getQueue();
        
        for(String routingKey : routingKeys)
        	channel.queueBind(queueName, exchangeName, routingKey, headers);

        System.out.println(String.format("Waiting for messages from exchange [%s] type: [%s]. To exit press CTRL+C", exchangeName, type));

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        	listener.handle(consumerTag, delivery);
        };
        
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
    
    public void receiveFromQueue(
    		String queueName,
    		boolean durable,
    		boolean exclusive,
    		boolean autoDelete,
    		Map<String, Object> arguments, 
    		DeliverCallback listener) throws IOException {
        
        channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
        System.out.println(String.format("Waiting for messages from queue [%s]. To exit press CTRL+C", queueName));

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        	listener.handle(consumerTag, delivery);
        };
        
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
    
    public void close() throws IOException {
    	if(connection != null)
    		connection.close();
    }
}
