package server;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.ServerSocket;

public class FileServer extends Thread{
	//	单例模式-参数
	private static FileServer instance = null;
	private static int port = 8081;
	private static List<FileThread> clients = new ArrayList<FileThread>();
	private ServerSocket serverSocket;
	
	
	//	单例模式-get函数
	public static FileServer getFileServer() throws IOException {
		if(instance == null)
			return new FileServer(port);
		return instance;
	}

	//	构造函数
	public FileServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("FileServer start at 127.0.0.1:" + port);
	}

	//	服务器主程序
	public void run() {
		while (true) {
			try {
				FileThread client = new FileThread(serverSocket.accept(), this);
		        client.start();
		        clients.add(client);
		      } catch (IOException e) {
		        e.printStackTrace();
		      }
		    }
	}

	//	获取clients列表
	public static List<FileThread> getClients() {
		return clients;
	}
}
