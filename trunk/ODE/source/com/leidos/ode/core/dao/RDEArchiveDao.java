package com.leidos.ode.core.dao;

import java.util.List;

import com.leidos.ode.core.data.RDEArchiveInfo;

public interface RDEArchiveDao {

	
	
	public void storeRDEArchiveInfo(RDEArchiveInfo archiveInfo);
	
	public List<RDEArchiveInfo> getArchiveInfoForMsgType(String msgType);
	
	
}
