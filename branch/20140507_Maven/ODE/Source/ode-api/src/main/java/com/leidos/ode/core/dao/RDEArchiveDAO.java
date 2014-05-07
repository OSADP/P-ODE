package com.leidos.ode.core.dao;

import com.leidos.ode.core.rde.model.RDEArchiveInfo;
import java.util.List;

public interface RDEArchiveDAO {
	
	public void storeRDEArchiveInfo(RDEArchiveInfo archiveInfo);
	
	public List<RDEArchiveInfo> getArchiveInfoForMsgType(String msgType);
	
}
