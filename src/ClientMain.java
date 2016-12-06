import client.ClientView;
import client.SuperClient;
import client.Client;
import java.io.IOException;

public class ClientMain {
  public static void main(String args[]) {
	SuperClient superClient = new SuperClient();
	superClient.start();
//    ClientView cv = new ClientView();
//    Client c = new Client();
//    cv.setClientThread(c);
//    c.setClientViewThread(cv);
//    cv.start();
//    c.start();
  }
}
