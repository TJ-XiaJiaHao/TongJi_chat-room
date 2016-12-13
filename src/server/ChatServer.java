package server;

import java.util.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

//聊天主线程
public class ChatServer extends Thread{
	//单例模式-参数
	private static ChatServer instance = null;
	private static int port = 8080;
	private static List<ChatThread> clients = new ArrayList<ChatThread>();	//在线用户集合
	private ServerSocket serverSocket;
	
	//单例模式-get函数
	public static ChatServer getChatServer() throws IOException {
		if(instance == null)
			instance = new ChatServer(port);
		return instance;
	}
	
	//构造函数
	public ChatServer(int port) throws IOException{
		serverSocket = new ServerSocket(port);	//绑定socket
		System.out.println("ChatServer start at 127.0.0.1:" + port);
	}
	
	//获取在线客户端
	public static List<ChatThread> getClients() {
		return clients;
	}
	 
	 //服务器主程序
	public void run() {
		 while(true) {
			 try {
				 ChatThread chatThread = new ChatThread(serverSocket.accept(), this);
				 chatThread.start();
				 clients.add(chatThread);
			 } catch (IOException e) {
				// TODO: handle exception
				 e.printStackTrace();
			}
		 }
	 }
	
}
