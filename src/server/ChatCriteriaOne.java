package server;

import java.util.ArrayList;
import java.util.List;

public class ChatCriteriaOne implements ChatCriteria{

	@Override
	public List<ChatThread> meetCriteria(List<ChatThread> clients, String username) {
		List<ChatThread> meet = new ArrayList<ChatThread>();
		for(ChatThread chatThread : clients){
			if(chatThread.getUsername().equals(username)){
				meet.add(chatThread);
				break;
			}
		}
		return meet;
	}

}
