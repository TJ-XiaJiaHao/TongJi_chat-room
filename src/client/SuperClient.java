package client;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class SuperClient {
  private Client client;
  private ClientView clientView;
  
  
  public SuperClient() {
	clientView = new ClientView();
    client = new Client();
    clientView.setClientThread(client);
    client.setClientViewThread(clientView);
  }
  
  public void start() {
	  clientView.start();
	  client.start();
  }
}