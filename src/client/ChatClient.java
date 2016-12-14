package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ChatClient extends ClientBasic {
	  PrintWriter sender;
	  BufferedReader getter;
	  ListenerThread listener;

	  public void stopChatThread() { stopThread = true; }

	  public ChatClient(String serverIP, int port, String username, Client parentThread) throws IOException {
	    this.serverIP = serverIP;
	    this.port = port;
	    this.parentThread = parentThread;
	    this.socket = new Socket(serverIP, port);
	    this.username = username;
	    sender = new PrintWriter(socket.getOutputStream(), true);
	    getter = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    sender.println(username);
	    listener = new ListenerThread();
	    listener.start();
	  }

	  public void sendMessage(String message) {
	    sender.println(message);
	    sender.flush();
	  }

	  @Override
	  void init(String name) {
	  	// TODO Auto-generated method stub
	  	
	  }
	  
	  @Override
	  void printToSocket(String message) {
	  	// TODO Auto-generated method stub
	  	sender.println(message);
		sender.flush();
	  }
	  
	  public void run() {
	    try {
	      while (!stopThread) {
	        // ...
	      }
	      listener.shutdown();
	      sender.println("[OFFLINE]");
	      sender.close();
	      getter.close();
	      socket.close();
	    } catch (Exception e) {
	      return;
	    }
	  }

	  private class ListenerThread extends Thread {
	    private boolean stop = false;
	    public void run() {
	      while (!stop) {
	        try {
	          String message = getter.readLine();
	          if (message == null) 
	        	  continue;
	          parentThread.receiveMessage(message);
	          } catch (IOException e) {
	        	e.printStackTrace();
	          }
	      }
	    }
	    public void shutdown() { stop = true; }
	  }

}