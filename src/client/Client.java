package client;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import client.ClientView.updateForFile;
import client.ClientView.updateForGroup;
import client.ClientView.updateForOFFINE;
import client.ClientView.updateForONLINE;
import client.ClientView.updateForP2P;
import client.ClientView.updateGUI;

public class Client extends Thread{
	private ClientFactory clientFactory;
	private ClientBasic chatClient;
	private ClientBasic fileClient;
	private ClientView clientView;
	private Map<String, ArrayList<String>> chatRecords = new HashMap<String, ArrayList<String>>();
	private boolean stopClient = false;
	private String username;
	
	//	绑定GUI层
	public void setClientViewThread(ClientView clientView) {
		this.clientView = clientView;
	}
	
	//	主程序
	public void run() {
		while(!stopClient) {
			//	...
		}
	}

	//	为GUI连接按钮提供服务
	public void connect(String ip, int port, String username) {
		try {
			this.username = username;
			clientFactory = new ClientFactory();
			chatClient = clientFactory.getChatClient(ip, port, username, this);
			fileClient = clientFactory.getFileClient(ip, port + 1, username, this);
			chatRecords.put("GroupChat", new ArrayList<String>());
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	//	为GUI退出按钮提供服务
	public void disconnect() {
		chatClient.stopThread();
	    fileClient.stopThread();
	    stopClient = true;
	}
	  
	// 为聊天线程收到新消息提供服务
	public void receiveMessage(String message) {
		StringTokenizer tokenizer = new StringTokenizer(message, "[#]");
	    String command = tokenizer.nextToken();
	    String usr = tokenizer.nextToken();
	    if (command.equals("INFO") || command.equals("ONLINE")) {// 在线用户及有人上线
	      String ip = tokenizer.nextToken();
	      String port = tokenizer.nextToken();
	      chatRecords.put(usr, new ArrayList<String>());
	      updateForONLINE update = clientView.new updateForONLINE();
	      update.updateGUI(usr, "");
	    } else if (command.equals("GROUP")) {// 群聊消息
	      String msg = tokenizer.nextToken();
	      chatRecords.get("GroupChat").add(usr + "[#]" + msg);
	      updateForGroup update = clientView.new updateForGroup();
	      update.updateGUI(msg, usr);
	    } else if (command.equals("P2P")) {// 私聊消息
	      String msg = tokenizer.nextToken();
	      chatRecords.get(usr).add(usr + "[#]" + msg);
	      updateForP2P update = clientView.new updateForP2P();
	      update.updateGUI(msg, usr);
	    } else if (command.equals("OFFLINE")) {// 有人下线
	      String ip = tokenizer.nextToken();
	      String port = tokenizer.nextToken();
	      chatRecords.remove(usr);
	      updateForOFFINE update = clientView.new updateForOFFINE();
	      update.updateGUI(usr, "");
	    } else if (command.equals("FILE")) {
	      String filename = tokenizer.nextToken();
	      String username = tokenizer.nextToken(); 
	      updateForFile update = clientView.new updateForFile();
	      update.updateGUI(username + "向你发送了文件", "");
	    }
	  }

	//	为GUI发送消息服务
	public void sendMessage(String message) {
		if (message.equals("[OFFLINE]")) {
			chatClient.send(message);
			return;
		}
		StringTokenizer tokenizer = new StringTokenizer(message, "[#]");
		String command = tokenizer.nextToken();
		String msg = tokenizer.nextToken();
		//	添加到本地聊天记录
		if (command.equals("P2P")) {
			String usr = tokenizer.nextToken();
			chatRecords.get(usr).add(username + "[#]" + msg);
		} else {
			chatRecords.get("GroupChat").add(username + "[#]" + msg);
		}
		//	将消息发送至socket
		chatClient.send(message);
	}

	//	为GUI发送文件服务
	public void sendFile(String info, String filename) {
		fileClient.send(info, filename);
	}

	//	为GUI刷新页面提供聊天记录
	public List<String> getChatRecords(String usermame) {
		return chatRecords.get(usermame);
	}
}
