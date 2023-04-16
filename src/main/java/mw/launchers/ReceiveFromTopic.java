package mw.launchers;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import mw.MWClient;

/***
 * example for "receiveFromExchange"
 * (ReceiveLog)
 * 
 * @author HBSIM
 *
 */
public class ReceiveFromTopic {

	public static void main(String[] argv) throws Exception {
    	String exchangeName = "topic_logs";
    	String type = MWClient.exchangeType_TOPIC;
    	String[] routingKeys = new String[]{"pattern1"};
 
    	DeliverCallback listener = new DeliverCallback() {
 			@Override
			public void handle(String consumerTag, Delivery delivery) throws IOException {
 	            JsonParser parser = new JsonParser();
 	            JsonObject jMessage = (JsonObject) parser.parse(new String (delivery.getBody()));          
 	            Gson gson = new GsonBuilder().setPrettyPrinting().create();
 	            System.out.println(gson.toJson(jMessage));
			}
    	};
    	
    	MWClient client = new MWClient();
		client.initializeHost("localhost", 5672, "admin", "admin");
		client.receiveFromExchange(
				exchangeName, 
				type, 
				routingKeys,
				listener);
		
//		client.close();
    }
}

