package mw.launchers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.*;

import mw.MWClient;

public class PublishToHeader {

    public static void main(String[] argv) throws Exception {
    	String routingKey = "";
        Map<String, Object> headers = new HashMap<String, Object>();

        for (int i = 1; i < 2; i++) {
            System.out.println("Adding header " + i + " with value " + (i+1) + " to Map");
            headers.put(String.format("%s", i), String.format("%s", i + 2));
            i++;
        }

        String message = "testMessage";
    	
    	MWClient client = new MWClient();
		client.initializeHost("localhost", 5672, "admin", "admin");   

		AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties().builder();

		client.publishToExchange(
				"header_test", 
				routingKey, 
				MWClient.exchangeType_HEADERS, 
				true, 
				builder.headers(headers).build(), 
				message.getBytes(StandardCharsets.UTF_8));

		System.out.println("Sent message: '" + message + "'");
    }
}

