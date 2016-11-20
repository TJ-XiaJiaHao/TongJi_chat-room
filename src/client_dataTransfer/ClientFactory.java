package client_dataTransfer;

import java.io.IOException;

import client_business.Client;

public class ClientFactory {
	
	public ClientBasic getClient(String strType,String serverIP, int port, String username, Client parentThread) throws IOException{
		if(strType == null)return null;
		else if(strType.equals("ChatClient")) return new ChatClient(serverIP,port,username,parentThread);
		else if(strType.equals("FileClent")) return new FileClient(serverIP,port,username,parentThread);
		return null;
	}
}
