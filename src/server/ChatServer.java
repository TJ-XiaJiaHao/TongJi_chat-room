package server;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import client.Client.receiveMessage;

public class ChatServer extends Thread {
	/*single pattern*/
	private static ChatServer instance = null;
	private static int port = -1;
	public static ChatServer getChatServer() throws IOException {
		if(instance == null) {
			if(port == -1) port = 8080;
			instance = new ChatServer(port);
		}
		return instance;
	}
	
	/*user clients*/
	private List<ChatThread> clients = new ArrayList<ChatThread>();
	private ServerSocket server;
	public List<ChatThread> getClients() {
		return clients;
	}
	
	public ChatServer(int port) throws IOException {
		server = new ServerSocket(port);
		System.out.println("ChatServer start at 127.0.0.1:" + port);
	}
	// 服务器主程序
	public void run() {	
		while (true) {
			try {
				ChatThread client = new ChatThread(server.accept(),this);
				client.start();
				clients.add(client);
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
