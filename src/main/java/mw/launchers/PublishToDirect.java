package mw.launchers;

import java.nio.charset.StandardCharsets;

import com.rabbitmq.client.AMQP.BasicProperties;

import mw.MWClient;

/***
 * example for "publishToExchange"
 * (EmitLog)
 * 
 * @author HBSIM
 *
 */
public class PublishToDirect {

    public static void main(String[] argv) throws Exception {
    	String exchangeName = "direct_logs";
    	String routingKey = "direct_subject";
    	String type = MWClient.exchangeType_DIRECT;
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

