package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

public class ChatThread extends Thread{
	 private Socket sock;
	 private BufferedReader getter;
	 private PrintWriter sender;
	 private String username;
	 private List<ChatThread> clients;

	 public PrintWriter getSender() { return sender; }
	 public int getPort() { return sock.getPort(); }
	 public String getIP() { return sock.getInetAddress().getHostAddress(); }
	 public String getUsername() { return username; }
	 public String getIdentifier() { return sock.getInetAddress().getHostAddress() + ":" + sock.getPort(); }

	 public ChatThread(Socket sock,ChatServer chatServer) {
		 try {
			 this.sock = sock;
			 this.getter = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			 this.sender = new PrintWriter(sock.getOutputStream(), true);
			 this.username = getter.readLine();
			 this.clients = chatServer.getClients();
			 // 灏嗘柊涓婄嚎鐢ㄦ埛淇℃伅缇ゅ彂缁欏叾浠栨墍鏈夊凡鍦ㄧ嚎鐢ㄦ埛
			 sendToAllUser("ONLINE[#]" + username + "[#]" + getIP() + "[#]" + getPort());
			 // 灏嗗綋鍓嶅湪绾跨敤鎴蜂俊鎭彂閫佺粰鏂颁笂绾跨敤鎴�
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

    // 缇ゅ彂娑堟伅
	 private void sendToAllUser(String message) {
		 for (int i = 0; i < clients.size(); ++i) {
			 System.out.println(clients.get(i).getIdentifier());
			 if (clients.get(i).getIdentifier().equals(this.getIdentifier()))
				 continue;
			 clients.get(i).getSender().println(message);
			 clients.get(i).getSender().flush();
		 }
	 }

    // 绉佽亰娑堟伅
	 private void sendToSpecificUser(String message, String dest) {
		 for (int i = 0; i < clients.size(); ++i) {
			 if (clients.get(i).getUsername().equals(dest)) {
				 clients.get(i).getSender().println(message);
				 clients.get(i).getSender().flush();
			 }
		 }
	 }

	 public void run() {
		 String line;
		 while (true) {
			 try {
				 line = getter.readLine();
				 if (line.equals("[OFFLINE]")) {// 鐢ㄦ埛涓嬬嚎
					 offline();
					 return;
				 } else {// 鏅�氭秷鎭�
					 System.out.println(line);
					 StringTokenizer tokenizer = new StringTokenizer(line, "[#]");
					 String command = tokenizer.nextToken();
					 String message = tokenizer.nextToken();
					 if (command.equals("GROUP")) {// 缇よ亰娑堟伅
						 sendToAllUser("GROUP[#]" + username + "[#]" + message);
					 } else {// 绉佽亰娑堟伅
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

	 // 鐢ㄦ埛涓嬬嚎锛岃浆鍛婂叾浠栫敤鎴凤紝鍏抽棴socket璧勬簮骞剁Щ闄よ绾跨▼
	 private void offline() {
		 try {
			 sendToAllUser("OFFLINE[#]" + username + "[#]" + getIP() + "[#]" + getPort());
			 System.out.println("ChatServer: [OFFLINE] [" + username + "] [" + getIdentifier() + "]");
			 getter.close();
			 sender.close();
			 sock.close();
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
