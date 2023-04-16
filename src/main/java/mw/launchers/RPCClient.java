package mw.launchers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import mw.MWClient;

public class RPCClient {
    MWClient client = null;
    
    public MWClient getClient() {
    	return client;
    }
    
    public boolean IsOpen() {
    	return client != null && client.IsOpen() == true;
    }
    
    public String requestSync(String queueName, String request, int timeoutMS) throws IOException, InterruptedException, TimeoutException {
    	byte[] bReply = client.requestSync(queueName, request.getBytes(StandardCharsets.UTF_8), timeoutMS);
    	return new String(bReply, StandardCharsets.UTF_8);
    }
    
    public void connect(String host, int port, String userName, String password, String queueName) throws IOException, TimeoutException {
		client = new MWClient();
		client.initializeHost(host, port, userName, password);
    }
    
    public void close() throws IOException {
    	if(client != null)
    		client.close();
    }
}