import java.io.IOException;

import client_aview.ClientView;
import client_business.Client;

public class ClientMain {
  public static void main(String args[]) {
    ClientView cv = new ClientView();
    Client c = new Client();
    cv.setClientThread(c);
    c.setClientViewThread(cv);
    cv.start();
    c.start();
  }
}
