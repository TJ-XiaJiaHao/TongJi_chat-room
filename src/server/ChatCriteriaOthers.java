package server;

import java.util.ArrayList;
import java.util.List;

public class ChatCriteriaOthers implements ChatCriteria{

	@Override
	public List<ChatThread> meetCriteria(List<ChatThread> clients, String username) {
		// TODO Auto-generated method stub
		List<ChatThread> meet = new ArrayList<ChatThread>();
		for(ChatThread chatThread : clients){
			if(chatThread.getIdentifier().equals(username)){
				continue;
			}
			else{
				meet.add(chatThread);
			}
		}
		return meet;
	}
	
}
