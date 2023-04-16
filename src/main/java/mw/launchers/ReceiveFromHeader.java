package mw.launchers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import mw.MWClient;

public class ReceiveFromHeader {
    public static void main(String[] argv) throws Exception {

    	DeliverCallback listener = new DeliverCallback() {
 			@Override
			public void handle(String consumerTag, Delivery message) throws IOException {
				
			}
    	};

    	MWClient client = new MWClient();
    	
    	String routingKey = "";
        String queueInputName = "abc";
        
        boolean durable = true;
        boolean exclusive = false;
        boolean autoDelete = false;
        Map<String, Object> arguments = null;

        Map<String, Object> headers = new HashMap<>();
        for (int i = 1; i < 2; i++) {
            System.out.println("Adding header " + i + " with value " + (i+2) + " to Map");
            headers.put(String.format("%s", i), String.format("%s", i + 2));
            i++;
        }
		client.initializeHost("localhost", 5672, "admin", "admin");

		client.receiveFromExchange(
				"header_test", 
				MWClient.exchangeType_HEADERS, 
				new String[] { routingKey }, 
				queueInputName, 
				durable,
				exclusive,
				autoDelete,
				arguments,
				headers,
				listener);
		
//		client.close();
    }
}

