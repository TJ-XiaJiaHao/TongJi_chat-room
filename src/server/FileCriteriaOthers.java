package server;

import java.util.ArrayList;
import java.util.List;

public class FileCriteriaOthers implements FileCriteria{

	@Override
	public List<FileThread> meetCriteria(List<FileThread> clients,String username) {
		// TODO Auto-generated method stub
		List<FileThread> meet = new ArrayList<FileThread>();
		for(FileThread fileThread : clients){
			if (fileThread.getUsername().equals(username))continue;
			else meet.add(fileThread);
		}
		return meet;
	}

}
