package client;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Random;
//可以是接口
abstract public class ClientBasic extends Thread {
	  protected String serverIP;
	  protected String username;
	  protected int port;
	  Socket socket;
	  boolean stopThread = false;
	  Client parentThread;
	  
	  public ClientBasic()  {};

	  public void stopThread() { stopThread = true; }
	  public void sendMessage(String message) {};
	  public void sendFile(String Info,String fileName){};
	  abstract public void run();
}
