package client;

import java.io.IOException;

public class ClientFactory {
	public ClientBasic getChatClient(String serverIP, int port, String username, Client parentThread) throws IOException{
		return new ChatClient(serverIP,port,username,parentThread);
	}
	
	public ClientBasic getFileClient(String serverIP, int port, String username, Client parentThread) throws IOException{
		return new FileClient(serverIP, port, username, parentThread);
	}
	public ClientBasic getClient(String strType,String serverIP, int port, String username, Client parentThread) throws IOException{
		if(strType == null)return null;
		else if(strType.equals("ChatClient")) return new ChatClient(serverIP,port,username,parentThread);
		else if(strType.equals("FileClient")) return new FileClient(serverIP,port,username,parentThread);
		return null;
	}

	public ClientBasic getChatClient(String serverIP, int port, String username, SuperClient superClient) {
		// TODO Auto-generated method stub
//		return new ChatClient(serverIP,port,username,superClient);
		return null;
	}
}
