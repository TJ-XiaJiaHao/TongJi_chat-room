package client_business;

import java.io.*;
import java.util.StringTokenizer;

import client_aview.ClientView;
import client_dataTransfer.ClientBasic;
import client_dataTransfer.ClientFactory;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class Client extends Thread {
	  private ClientFactory clientFactory = new ClientFactory();
  private ClientBasic cc;
  private ClientBasic fc;
  private ClientView clientView;
  private Map<String, ArrayList<String>> chatRecords = new HashMap<String, ArrayList<String>>();
  private boolean stopClient = false;
  private String username;

  public void setClientViewThread(ClientView cv) { clientView = cv; }

  public void run() {
    while (!stopClient) {
      // ...
    }
  }

  // 为GUI连接按钮提供服务
  public void connect(String ip, int port, String username) {
    try {
      this.username = username;
      cc = clientFactory.getClient("ChatClient", ip, port, username,this);
      fc = clientFactory.getClient("FileClient", ip, port, username, this);
      chatRecords.put("GroupChat", new ArrayList<String>());
    } catch (Exception e) {
      //...
        System.out.println("error");
    }
  }

  // 为GUI发送消息提供服务
  public void sendMessage(String message) {
	  System.out.println("cr size in c : " + chatRecords.size());
	System.out.println("c send! message is :" + message);
    if (message.equals("[OFFLINE]")) {
      cc.send(message);
      return;
    }
    StringTokenizer tokenizer = new StringTokenizer(message, "[#]");
    String command = tokenizer.nextToken();
    String msg = tokenizer.nextToken();
    if (command.equals("P2P")) {
      String usr = tokenizer.nextToken();
      chatRecords.get(usr).add(username + "[#]" + msg);
      // System.out.println(chatRecords.get(usr).get(chatRecords.get(usr).size()-1));
    } else {
      chatRecords.get("GroupChat").add(username + "[#]" + msg);
      // System.out.println(chatRecords.get("GroupChat").get(chatRecords.get("GroupChat").size()-1));
    }
    
    cc.send(message);
  }

  // 为GUI退出按钮提供服务
  public void disconnect() {
    cc.stopThread();
    fc.stopThread();
    stopClient = true;
  }

  // 获取对应用户的聊天记录
  public List<String> getChatRecords(String username) {
    return chatRecords.get(username);
  }

 
  // 为GUI的发送文件按钮提供服务
  public void sendFile(String info, String filename) {
    fc.send(info, filename);
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
      clientView.updateGUI("ONLINE", usr, "");
    } else if (command.equals("GROUP")) {// 群聊消息
      String msg = tokenizer.nextToken();
      chatRecords.get("GroupChat").add(usr + "[#]" + msg);
      clientView.updateGUI("GROUP", msg, usr);
    } else if (command.equals("P2P")) {// 私聊消息
      String msg = tokenizer.nextToken();
      chatRecords.get(usr).add(usr + "[#]" + msg);
      clientView.updateGUI("P2P", msg, usr);
    } else if (command.equals("OFFLINE")) {// 有人下线
      String ip = tokenizer.nextToken();
      String port = tokenizer.nextToken();
      chatRecords.remove(usr);
      clientView.updateGUI("OFFLINE", usr, "");
    } else if (command.equals("FILE")) {
      String filename = tokenizer.nextToken();
      String username = tokenizer.nextToken();
      clientView.updateGUI("FILE", username + "向你发送了文件" + filename, "");
    }
  }
}