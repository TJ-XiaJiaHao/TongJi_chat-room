package server;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import client.FileClient;

public class FileServer extends Thread {
  public static int port = -1;
  private static FileServer instance = null;
  public static FileServer getFileServer() throws IOException {
	if(port == -1) port = 8081;
	if(instance == null) {
		  if(instance == null) {
			  instance = new FileServer(port);
		  }
	}
	return instance;
  }
  private List<FileThread> clients = new ArrayList<FileThread>();
  
  public List<FileThread> getClients() {
	return clients;
}
private ServerSocket server;
  public FileServer(int port) throws IOException {
    server = new ServerSocket(port);
    System.out.println("FileServer start at 127.0.0.1:" + port);
  }
  // 服务器主程序
  public void run() {
    while (true) {
      try {
    	  FileThread client = new FileThread(server.accept(),this);
        client.start();
        clients.add(client);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // 为客户端提供服务的线程类
 }
