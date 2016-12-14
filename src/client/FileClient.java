package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;


public class FileClient extends ClientBasic {
	  DataOutputStream sender;
	  DataInputStream getter;
	  ListenerThread listener;
	  File file;    
	  FileInputStream fis;

	  public void stopFileThread() { stopThread = true; }

	  public FileClient(String serverIP, int port, String username, Client parentThread) throws IOException {
	    this.serverIP = serverIP;
	    this.port = port;
	    this.parentThread = parentThread;
	    this.username = username;
	    this.socket = new Socket(serverIP, port);
	    sender = new DataOutputStream(socket.getOutputStream());
	    getter = new DataInputStream(socket.getInputStream());
	    sender.writeUTF(username);
	    listener = new ListenerThread();
	    listener.start();
	  }

	  public void sendFile(String info, String filename) {
	    try {
	    	File file = new File(filename);
	    	FileInputStream fileInputStream = new FileInputStream(file);
	    	sender.writeUTF(info);
	    	sender.writeLong(file.length());
	    	byte[] buff = new byte[1024];
	    	int length = 0;
	    	while ((length = fileInputStream.read(buff, 0, buff.length)) > 0) {
	    		sender.write(buff, 0, length);
	    		sender.flush();
	    	}
	    	} catch (Exception e) {
	    		// ...
	    	}
	  }

	  public void run() {
	    try {
	    	while (!stopThread) {
	    		// ...
	    	}
	    	listener.shutdown();
	    	sender.writeUTF("[OFFLINE]");
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
					  	String info = getter.readUTF();
					  	long filelength = getter.readLong();
					  	StringTokenizer tokenizer = new StringTokenizer(info, ".");
					  	String extendName = tokenizer.nextToken();
					  	extendName = tokenizer.nextToken();
					  	File file = new File(username + (new Random()).nextInt(1000) + "." + extendName);
					  	FileOutputStream fos = new FileOutputStream(file);
					  	byte[] buff = new byte[1024];
					  	int length = 0, total = 0;
					  	while (total < filelength) {
					  		length = getter.read(buff);
					  		total += length;
					  		fos.write(buff, 0, length);
					  		fos.flush();
					  	}
					  	fos.close();
					  	parentThread.receiveMessage("FILE[#]" + info);
				  } catch (IOException e) {
					  e.printStackTrace();
				  }
	        }
	    }
	    public void shutdown() { stop = true; }
	  }

	  @Override
	  void init(String name) {
	  	// TODO Auto-generated method stub
	  	try{
	  	    file = new File(name);
	  	    fis = new FileInputStream(file);
	  	}catch(Exception e){
	  		System.out.println("init error!");
	  	}
	  	
	  }
	  
	  @Override
	  void printToSocket(String message) {
	  	// TODO Auto-generated method stub
	  	try{
	  		sender.writeUTF(message);
	  	    sender.writeLong(file.length());
	  	    byte[] buff = new byte[1024];
	  	    int length = 0;
	  	    while ((length = fis.read(buff, 0, buff.length)) > 0) {
	  	      sender.write(buff, 0, length);
	  	      sender.flush();
	  	    }
	  	}catch(Exception e){
	  		System.out.println("print to socket error!");
	  	}
	  }
}