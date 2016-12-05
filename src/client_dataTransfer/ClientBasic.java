package client_dataTransfer;

import java.net.*;
import client_business.Client;

abstract public class ClientBasic extends Thread {
	  protected String serverIP;
	  protected String username;
	  protected int port;
	  Socket socket;
	  boolean stopThread = false;
	  Client parentThread;
	  
	  public ClientBasic()  {};
	  public void stopThread() { stopThread = true; }
	  
	  abstract public void send(String...arr);
	  abstract public void run();
}
