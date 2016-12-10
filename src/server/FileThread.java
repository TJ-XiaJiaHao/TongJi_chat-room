package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

public class FileThread extends Thread {
    private Socket sock;
    private DataInputStream getter;
    private DataOutputStream sender;
    private String username;
    private List<FileThread> clients;

    public DataOutputStream getSender() { return sender; }
    public int getPort() { return sock.getPort(); }
    public String getIP() { return sock.getInetAddress().getHostAddress(); }
    public String getUsername() { return username; }
    
    public FileThread(Socket sock,FileServer fileServer) {
      try {
        this.sock = sock;
        this.getter = new DataInputStream(sock.getInputStream());
        this.sender = new DataOutputStream(sock.getOutputStream());
        this.username = getter.readUTF();
        this.clients = fileServer.getClients();
        System.out.println("FileServer: [ONLINE] [" + username + "] [" + getIP() + ":" + getPort() + "]");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // 缇ゅ彂鏂囦欢鏁版嵁
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

    // 缇ゅ彂鏂囦欢鍩烘湰淇℃伅锛屾枃浠跺悕鍜屾枃浠堕暱搴�
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

    // 鍙戦�佹枃浠舵暟鎹粰鐗瑰畾鐢ㄦ埛
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

    // 鍙戦�佹枃浠跺熀鏈俊鎭粰鐗瑰畾鐢ㄦ埛
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
          if (command.equals("GROUP")) {// 缇ゅ彂鏂囦欢
            sendBasicInfoToAllUser("GROUP[#]" + fileName + "[#]" + username, fileLength);
            toAll = true;
          } else if (command.equals("P2P")) {// 绉佸彂鏂囦欢
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

    // 鐢ㄦ埛涓嬬嚎锛屽叧闂璼ocket璧勬簮骞剁Щ闄よ绾跨▼
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

