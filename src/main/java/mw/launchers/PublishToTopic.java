package mw.launchers;

import java.nio.charset.StandardCharsets;

import com.rabbitmq.client.AMQP.BasicProperties;

import mw.MWClient;

/***
 * example for "PublishToTopicExchange"
 * (EmitLogTopic)
 * 
 * @author HBSIM
 *
 */
public class PublishToTopic {

    public static void main(String[] argv) throws Exception {
    	String exchangeName = "topic_logs";
    	String routingKey = "pattern1";
    	String type = MWClient.exchangeType_TOPIC;
    	boolean durable = false;
    	BasicProperties props = null;
    	String message = "testMessage";
    	
    	MWClient client = new MWClient();
		client.initializeHost("localhost", 5672, "admin", "admin");
		
		client.publishToExchange(
				exchangeName, 
				routingKey, 
				type, 
				durable, 
				props, 
				message.getBytes(StandardCharsets.UTF_8)
				);
		
		client.close();
    }
}

