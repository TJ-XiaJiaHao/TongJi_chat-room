package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

//	为客户端-消息提供服务的线程类
public class ChatThread extends Thread{
	private Socket socket;
	private BufferedReader getter;
	private PrintWriter sender;
	private String username;
	//	函数-获取相关参数
	public PrintWriter getSender() { return sender; }
	public int getPort() { return socket.getPort(); }
	public String getIP() { return socket.getInetAddress().getHostAddress(); }
	public String getUsername() { return username; }
	public String getIdentifier() { return socket.getInetAddress().getHostAddress() + ":" + socket.getPort(); }

	
	public ChatThread(Socket socket) {
		try {
			this.socket = socket;
			this.getter = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.sender = new PrintWriter(socket.getOutputStream(), true);
			this.username = getter.readLine();
			
			List<ChatThread> clients = ChatServer.getClients();
			//	将新上线用户信息群发给其他所有在线用户
			sendToAllUser("ONLINE[#]" + username + "[#]" + getIP() + "[#]" + getPort());
	        // 	将当前在线用户信息发送给新上线用户
			for (int i = 0; i < clients.size(); ++i) {
				if (clients.get(i).getIdentifier().equals(this.getIdentifier()))
					continue;
				sender.println("INFO[#]" + clients.get(i).getUsername() + "[#]"
					+ clients.get(i).getIP() + "[#]" + clients.get(i).getPort());
		        }
			sender.flush();
			System.out.println("ChatServer: [ONLINE] [" + username + "] [" + getIdentifier() + "]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//	主程序
	@Override
    public void run() {
    	String line;
    	while (true) {
    		try {
    			line = getter.readLine();	//	读取消息
    			if ("[OFFLINE]".equals(line)) {// 用户下线
    				offline();
    				return;
    				} else {// 普通消息
    					System.out.println(line);
    					StringTokenizer tokenizer = new StringTokenizer(line, "[#]");
    					String command = tokenizer.nextToken();
    					String message = tokenizer.nextToken();
    					if ("GROUP".equals(command)) {// 群聊消息
    						sendToAllUser("GROUP[#]" + username + "[#]" + message);
    					} else {// 私聊消息
    						String dest = tokenizer.nextToken();
    						sendToSpecificUser("P2P[#]" + username + "[#]" + message, dest);
    					}
    				}
    		} catch (Exception e) {
    			offline();
    			return;
    		}
    	}
    }

	// 	群发消息
    private void sendToAllUser(String message) {
    	List<ChatThread> clients = ChatServer.getClients();
    	for (int i = 0; i < clients.size(); ++i) {
    		if (clients.get(i).getIdentifier().equals(this.getIdentifier()))
    			continue;
    		clients.get(i).getSender().println(message);
    		clients.get(i).getSender().flush();
      }
    }

    // 	私聊消息
    private void sendToSpecificUser(String message, String dest) {
    	List<ChatThread> clients = ChatServer.getClients();
    	for (int i = 0; i < clients.size(); ++i) {
    		if (clients.get(i).getUsername().equals(dest)) {
    			clients.get(i).getSender().println(message);
    			clients.get(i).getSender().flush();
    			}
    		}
    }

    //	用户下线，转告其他用户，关闭socket资源并移除该线程
    private void offline() {
    	List<ChatThread> clients = ChatServer.getClients();
    	try {
    		sendToAllUser("OFFLINE[#]" + username + "[#]" + getIP() + "[#]" + getPort());
    		System.out.println("ChatServer: [OFFLINE] [" + username + "] [" + getIdentifier() + "]");
    		getter.close();
    		sender.close();
    		socket.close();
    		for (int i = 0; i < clients.size(); ++i) {
    			if (clients.get(i).getIdentifier().equals(this.getIdentifier())) {
    				clients.remove(i);
    				return;
    			}
    		}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
}
