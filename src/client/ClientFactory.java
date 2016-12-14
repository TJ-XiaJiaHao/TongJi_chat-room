package client;

import java.io.IOException;

public class ClientFactory {
	//	获取chatclient
	public ClientBasic getChatClient(String serverIP, int port, String username, Client parentThread) throws IOException{
		return new ChatClient(serverIP,port,username,parentThread);
	}
	
	//	获取fileclient
	public ClientBasic getFileClient(String serverIP, int port, String username, Client parentThread) throws IOException{
		return new FileClient(serverIP, port, username, parentThread);
	}
}

