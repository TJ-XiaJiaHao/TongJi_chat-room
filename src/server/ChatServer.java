package server;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ChatServer extends Thread {
  public static int port = -1;
  private static ChatServer instance = null;
  public static ChatServer getChatServer() throws IOException {
	  if(instance == null) {
		  if(port == -1)port = 8080;
		  instance = new ChatServer(port);
	  }
	  return instance;
  }
  public List<ClientThread> clients = new ArrayList<ClientThread>();

  public List<ClientThread> getClients(){
	return clients;
  }
  
  private ServerSocket server;
  private ChatServer(int port) throws IOException {
    server = new ServerSocket(port);
    System.out.println("ChatServer start at 127.0.0.1:" + port);
  }
  // 服务器主程序
  public void run() {
    while (true) {
      try {
        ClientThread client = new ClientThread(server.accept());
        client.start();
        clients.add(client);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  }
