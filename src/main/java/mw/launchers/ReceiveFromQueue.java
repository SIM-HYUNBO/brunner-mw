package mw.launchers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import mw.MWClient;

public class ReceiveFromQueue {

    public static void main(String[] argv) throws Exception {
		boolean durable = true;
		boolean exclusive = false;
		boolean autoDelete = false;
		Map<String, Object> arguments = null;
		
		arguments = new HashMap<String,Object>();
		arguments.put("x-message-ttl", 10000);
		
    	DeliverCallback listener = new DeliverCallback() {
 			@Override
			public void handle(String consumerTag, Delivery message) throws IOException {
				
			}
    	};	
		
    	MWClient client = new MWClient();

    	String queueName = "bizServer";
		client.initializeHost("localhost", 5672, "admin", "admin");
		client.receiveFromQueue(
				queueName, 
				durable, 
				exclusive, 
				autoDelete, 
				arguments, 
				listener);;

//		client.close();
    }
}
