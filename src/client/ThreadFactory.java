package client;

import java.io.IOException;

public class ThreadFactory {
	//仍有问题，此文件作废
	public Thread geThread(String strType){
		if(strType == null)return null;
		else if(strType.equals("ClientView")) return new ClientView();
		else if(strType.equals("Client")) return new Client();
		return null;
	}
	public Thread geThread(String strType,String serverIP, int port, String username, Client parentThread) throws IOException{
		if(strType == null)return null;
		else if(strType.equals("ChatClient")) return new ChatClient(serverIP,port,username,parentThread);
		else if(strType.equals("FileClent")) return new FileClient(serverIP,port,username,parentThread);
		return null;
	}
}
