package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.StringTokenizer;

//	为客户端-文件提供服务的线程类
public class FileThread extends Thread {
    private Socket sock;
    private DataInputStream getter;
    private DataOutputStream sender;
    private String username;
    private FileCriteria fileCriteriaOthers = new FileCriteriaOthers();
    private FileCriteria fileCriteriaOne = new FileCriteriaOne();

    //	函数-获取相关参数
    public DataOutputStream getSender() { return sender; }
    public int getPort() { return sock.getPort(); }
    public String getIP() { return sock.getInetAddress().getHostAddress(); }
    public String getUsername() { return username; }
    
    public FileThread(Socket sock, FileServer fileServer) {
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

    // 群发文件
    private void sendFileToAllUser(byte[] buff, int length) {
        try {
      	  List<FileThread> res = fileCriteriaOthers.meetCriteria(FileServer.getClients(), this.username);
      	  for (FileThread ft : res) {
      		  ft.getSender().write(buff, 0, length);
      		  ft.getSender().flush();
      	  }
        } catch (IOException e) {
      	  e.printStackTrace();
        	}
      }

    // 群发文件基本信息
    private void sendBasicInfoToAllUser(String fileInfo, long fileLength) {
        try { 
      	  List<FileThread> res = fileCriteriaOthers.meetCriteria(FileServer.getClients(), this.username);
      	  for (FileThread ft : res) {
      		  ft.getSender().writeUTF(fileInfo);
      		  ft.getSender().flush();
      		  ft.getSender().writeLong(fileLength);
      		  ft.getSender().flush();
      	  }
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
      }
    
    // 私发文件
    private void sendFileToSpecificUser(byte[] buff, int length, String dest) {
        try {
      	  List<FileThread> res = fileCriteriaOne.meetCriteria(FileServer.getClients(), dest);
      	  for (FileThread ft : res) {
      		  ft.getSender().write(buff, 0, length);
      		  ft.getSender().flush();
      	  }
        } catch (IOException e) {
      	  e.printStackTrace();
        }
      }

    // 私发文件基本信息
    private void sendBasicInfoToSpecificUser(String fileInfo, long fileLength, String dest) {
        try {
      	  List<FileThread> res = fileCriteriaOne.meetCriteria(FileServer.getClients(), dest);
      	  for (FileThread ft : res) {
      		  ft.getSender().writeUTF(fileInfo);
      		  ft.getSender().flush();
      		  ft.getSender().writeLong(fileLength);
      		  ft.getSender().flush();
      	  }
        } catch (IOException e) {
      	  e.printStackTrace();
        	}
      }

    //	主程序
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

    // 用户下线，转告其他用户，关闭socket资源并移除该线程
    private void offline() {
        try {
          System.out.println("FileServer: [OFFLINE] [" + username + "] [" + getIP() + ":" + getPort() + "]");
          getter.close();
          sender.close();
          sock.close();
          List<FileThread> res = fileCriteriaOne.meetCriteria(FileServer.getClients(), this.username);
    	  	for (FileThread ft : res) {
    	  		FileServer.getClients().remove(ft);
              return;
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
  }
