import java.io.IOException;

import server.ChatServer;
import server.FileServer;

public class ServerMain {
	public static void main(String args[]) {
		try {
			ChatServer chatServer = ChatServer.getChatServer();
			FileServer fileServer = FileServer.getFileServer();
			chatServer.start();
			fileServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
