package mw.launchers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import mw.MWClient;

public class PublishToQueue {

    public static void main(String[] argv) throws Exception {
		boolean durable = true;
		boolean exclusive = false;
		boolean autoDelete = false;

		String exchangeName = "";
    	String queueName = "bizServer";
    	String message = "testMessage";

    	Map<String, Object> arguments = null;
		arguments = new HashMap<String,Object>();
		arguments.put("x-message-ttl", 10000);
    	
    	MWClient client = new MWClient();
    	client.initializeHost("localhost", 5672, "admin", "admin");
		
    	client.publishToQueue(
    			exchangeName, 
    			queueName, 
    			durable,
    			exclusive,
    			autoDelete,
    			arguments, 
    			message.getBytes(StandardCharsets.UTF_8));
		
    	client.close();
    }
}
