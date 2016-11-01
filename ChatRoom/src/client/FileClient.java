package client;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Random;

public class FileClient extends Thread {
  private String serverIP;
  private String username;
  private int port;
  DataOutputStream sender;
  DataInputStream getter;
  Socket socket;
  boolean stopFile = false;
  Client parentThread;
  ListenerThread listener;

  public void stopFileThread() { stopFile = true; }

  public FileClient(String serverIP, int port, String username, Client parentThread) throws IOException {
    this.serverIP = serverIP;
    this.port = port;
    this.parentThread = parentThread;
    this.username = username;
    this.socket = new Socket(serverIP, port);
    sender = new DataOutputStream(socket.getOutputStream());
    getter = new DataInputStream(socket.getInputStream());
    sender.writeUTF(username);
    listener = new ListenerThread();
    listener.start();
  }

  public void sendFile(String info, String filename) {
    try {
      File file = new File(filename);
      FileInputStream fis = new FileInputStream(file);
      sender.writeUTF(info);
      sender.writeLong(file.length());
      byte[] buff = new byte[1024];
      int length = 0;
      while ((length = fis.read(buff, 0, buff.length)) > 0) {
        sender.write(buff, 0, length);
        sender.flush();
      }
    } catch (Exception e) {
      // ...
    }
  }

  public void run() {
    try {
      while (!stopFile) {
        // ...
      }
      listener.shutdown();
      sender.writeUTF("[OFFLINE]");
      sender.close();
      getter.close();
      socket.close();
    } catch (Exception e) {
      return;
    }
  }

  private class ListenerThread extends Thread {
    private boolean stop = false;
    public void run() {
      while (!stop) {
        try {
          String info = getter.readUTF();
          long filelength = getter.readLong();
          StringTokenizer tokenizer = new StringTokenizer(info, ".");
          String extendName = tokenizer.nextToken();
          extendName = tokenizer.nextToken();
          File file = new File(username + (new Random()).nextInt(1000) + "." + extendName);
          FileOutputStream fos = new FileOutputStream(file);
          byte[] buff = new byte[1024];
          int length = 0, total = 0;
          while (total < filelength) {
            length = getter.read(buff);
            total += length;
            fos.write(buff, 0, length);
            fos.flush();
          }
          fos.close();
          parentThread.receiveMessage("FILE[#]" + info);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    public void shutdown() { stop = true; }
  }
}