package server;

import java.util.List;

//	过滤器接口
public interface FileCriteria {
	public List<FileThread> meetCriteria(List<FileThread> clients,String username);
}
