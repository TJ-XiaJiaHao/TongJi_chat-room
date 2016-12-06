package client;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class SuperClient {
  private ClientFactory clientFactory;
  private Client client;
  private ClientBasic cc;
  private ClientBasic fc;
  private ClientView clientView;
  private Map<String, ArrayList<String>> chatRecords = new HashMap<String, ArrayList<String>>();
  private boolean stopClient = false;
  private String username;

  public void setClientViewThread(ClientView cv) { clientView = cv; }
  
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