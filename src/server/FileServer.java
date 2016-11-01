package server;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FileServer extends Thread {
  private List<ClientThread> clients = new ArrayList<ClientThread>();
  private ServerSocket server;
  public FileServer(int port) throws IOException {
    server = new ServerSocket(port);
    System.out.println("FileServer start at 127.0.0.1:" + port);
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
    private DataInputStream getter;
    private DataOutputStream sender;
    private String username;

    public DataOutputStream getSender() { return sender; }
    public int getPort() { return sock.getPort(); }
    public String getIP() { return sock.getInetAddress().getHostAddress(); }
    public String getUsername() { return username; }
    
    public ClientThread(Socket sock) {
      try {
        this.sock = sock;
        this.getter = new DataInputStream(sock.getInputStream());
        this.sender = new DataOutputStream(sock.getOutputStream());
        this.username = getter.readUTF();
        System.out.println("FileServer: [ONLINE] [" + username + "] [" + getIP() + ":" + getPort() + "]");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // 群发文件数据
    private void sendFileToAllUser(byte[] buff, int length) {
      try {
        for (int i = 0; i < clients.size(); ++i) {
          if (clients.get(i).getUsername().equals(this.username))
            continue;
          clients.get(i).getSender().write(buff, 0, length);
          clients.get(i).getSender().flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // 群发文件基本信息，文件名和文件长度
    private void sendBasicInfoToAllUser(String fileInfo, long fileLength) {
      try {
        for (int i = 0; i < clients.size(); ++i) {
          if (clients.get(i).getUsername().equals(this.username))
            continue;
          clients.get(i).getSender().writeUTF(fileInfo);
          clients.get(i).getSender().flush();
          clients.get(i).getSender().writeLong(fileLength);
          clients.get(i).getSender().flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // 发送文件数据给特定用户
    private void sendFileToSpecificUser(byte[] buff, int length, String dest) {
      try {
        for (int i = 0; i < clients.size(); ++i) {
          if (clients.get(i).getUsername().equals(dest)) {
            clients.get(i).getSender().write(buff, 0, length);
            clients.get(i).getSender().flush();
            break;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // 发送文件基本信息给特定用户
    private void sendBasicInfoToSpecificUser(String fileInfo, long fileLength, String dest) {
      try {
        for (int i = 0; i < clients.size(); ++i) {
          if (clients.get(i).getUsername().equals(dest)) {
            clients.get(i).getSender().writeUTF(fileInfo);
            clients.get(i).getSender().flush();
            clients.get(i).getSender().writeLong(fileLength);
            clients.get(i).getSender().flush();
            break;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public void run() {
      byte[] buff = new byte[1024];
      while (true) {
        try {
          String message = getter.readUTF();
          if (message.equals("[OFFLINE]")) {
            offline();
            return;
          }
          StringTokenizer stringTokenizer = new StringTokenizer(message, "[#]");
          String command = stringTokenizer.nextToken();
          String fileName = stringTokenizer.nextToken();
          String dest = null;
          boolean toAll = false;
          long fileLength = getter.readLong();
          if (command.equals("GROUP")) {// 群发文件
            sendBasicInfoToAllUser("GROUP[#]" + fileName + "[#]" + username, fileLength);
            toAll = true;
          } else if (command.equals("P2P")) {// 私发文件
            dest = stringTokenizer.nextToken();
            sendBasicInfoToSpecificUser("P2P[#]" + fileName + "[#]" + username, fileLength, dest);
          }
          int length = 0, total = 0;
          while (total < fileLength) {
            length = getter.read(buff);
            total += length;
            if (toAll)
              sendFileToAllUser(buff, length);
            else
              sendFileToSpecificUser(buff, length, dest);
          }
        } catch (IOException e) {
          offline();
          return;
        }
      }
    }

    // 用户下线，关闭socket资源并移除该线程
    private void offline() {
      try {
        System.out.println("FileServer: [OFFLINE] [" + username + "] [" + getIP() + ":" + getPort() + "]");
        getter.close();
        sender.close();
        sock.close();
        for (int i = 0; i < clients.size(); ++i) {
          if (clients.get(i).getUsername().equals(this.username)) {
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
