package server;

import java.util.ArrayList;
import java.util.List;

public class FileCriteriaOne implements FileCriteria{

	@Override
	public List<FileThread> meetCriteria(List<FileThread> clients, String username) {
		// TODO Auto-generated method stub
		List<FileThread> meet = new ArrayList<FileThread>();
		for(FileThread fileThread : clients){
			if(fileThread.getUsername().equals(username)){
				meet.add(fileThread);
				break;
			}
		}
		return meet;
	}

}
