package server;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ChatServer extends Thread {
  private List<ClientThread> clients = new ArrayList<ClientThread>();
  private ServerSocket server;
  public ChatServer(int port) throws IOException {
    server = new ServerSocket(port);
    System.out.println("ChatServer start at 127.0.0.1:" + port);
  }
  // 服务器主程序
  public void run() {
    while (true) {
      try {
        ClientThread client = new ClientThread(server.accept());
        client.start();
        clients.add(client);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // 为客户端提供服务的线程类
  private class ClientThread extends Thread {
    private Socket sock;
    private BufferedReader getter;
    private PrintWriter sender;
    private String username;

    public PrintWriter getSender() { return sender; }
    public int getPort() { return sock.getPort(); }
    public String getIP() { return sock.getInetAddress().getHostAddress(); }
    public String getUsername() { return username; }
    public String getIdentifier() { return sock.getInetAddress().getHostAddress() + ":" + sock.getPort(); }

    public ClientThread(Socket sock) {
      try {
        this.sock = sock;
        this.getter = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        this.sender = new PrintWriter(sock.getOutputStream(), true);
        this.username = getter.readLine();
        // 将新上线用户信息群发给其他所有已在线用户
        sendToAllUser("ONLINE[#]" + username + "[#]" + getIP() + "[#]" + getPort());
        // 将当前在线用户信息发送给新上线用户
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

    // 群发消息
    private void sendToAllUser(String message) {
      for (int i = 0; i < clients.size(); ++i) {
        if (clients.get(i).getIdentifier().equals(this.getIdentifier()))
          continue;
        clients.get(i).getSender().println(message);
        clients.get(i).getSender().flush();
      }
    }

    // 私聊消息
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
          if (line.equals("[OFFLINE]")) {// 用户下线
            offline();
            return;
          } else {// 普通消息
            System.out.println(line);
            StringTokenizer tokenizer = new StringTokenizer(line, "[#]");
            String command = tokenizer.nextToken();
            String message = tokenizer.nextToken();
            if (command.equals("GROUP")) {// 群聊消息
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

    // 用户下线，转告其他用户，关闭socket资源并移除该线程
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
}
