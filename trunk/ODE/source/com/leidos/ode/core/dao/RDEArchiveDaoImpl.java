package com.leidos.ode.core.dao;

import java.util.List;

import javax.ejb.Stateless;

import com.leidos.ode.core.data.RDEArchiveInfo;

@Stateless
public class RDEArchiveDaoImpl implements RDEArchiveDao {

	@Override
	public void storeRDEArchiveInfo(RDEArchiveInfo archiveInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<RDEArchiveInfo> getArchiveInfoForMsgType(String msgType) {
		// TODO Auto-generated method stub
		return null;
	}

}
