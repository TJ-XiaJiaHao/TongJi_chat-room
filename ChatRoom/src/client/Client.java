package client;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class Client extends Thread {
  private ChatClient cc;
  private FileClient fc;
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
      cc = new ChatClient(ip, port, username, this);
      fc = new FileClient(ip, port + 1, username, this);
      chatRecords.put("GroupChat", new ArrayList<String>());
    } catch (Exception e) {
      //...
    }
  }

  // 为GUI发送消息提供服务
  public void sendMessage(String message) {
    if (message.equals("[OFFLINE]")) {
      cc.sendMessage(message);
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
    cc.sendMessage(message);
  }

  // 为GUI退出按钮提供服务
  public void disconnect() {
    cc.stopChatThread();
    fc.stopFileThread();
    stopClient = true;
  }

  // 获取对应用户的聊天记录
  public List<String> getChatRecords(String username) {
    return chatRecords.get(username);
  }

  // 为GUI的发送文件按钮提供服务
  public void sendFile(String info, String filename) {
    fc.sendFile(info, filename);
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