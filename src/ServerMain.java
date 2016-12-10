import server.ChatServer;
import server.FileServer;
import java.io.IOException;

public class ServerMain {
  public static void main(String args[]) {
    if (args.length != 1) {
      //System.out.println("请在命令行参数中给出端口号，java Server [port]");
      //return;
    }
    try {
      int port = 8080;//Integer.parseInt(args[0]);
      ChatServer cs = ChatServer.getChatServer();
      FileServer fs = FileServer.getFileServer();
      cs.start();
      fs.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
