package server;

import java.util.List;

public interface ChatCriteria {
	public List<ChatThread> meetCriteria(List<ChatThread> clients,String username);
}
