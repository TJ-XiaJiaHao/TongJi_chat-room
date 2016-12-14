package client;

import java.net.Socket;

abstract public class ClientBasic extends Thread {
	  protected String serverIP;
	  protected String username;
	  protected int port;
	  Socket socket;
	  boolean stopThread = false;
	  Client parentThread;
	  
	  public ClientBasic()  {};
	  public void stopThread() { stopThread = true; }
	  
	  abstract void init(String name);
	  abstract void printToSocket(String message);
	  public final void send(String...arr){
			 if(arr.length>1)
				 init(arr[1]);
			 printToSocket(arr[0]);
	  }
	  abstract public void run();
}

