package server;

import java.util.ArrayList;
import java.util.List;

public class ChatCriteriaOff implements ChatCriteria{

	@Override
	public List<ChatThread> meetCriteria(List<ChatThread> clients, String username) {
		// TODO Auto-generated method stub
		List<ChatThread> meet = new ArrayList<ChatThread>();
		for(ChatThread cThread : clients){
			if(cThread.getIdentifier().equals(username)){
				meet.add(cThread);
				break;
			}
		}
		return meet;
	}

}
