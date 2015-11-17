package com.leidos.ode.core.dao;

import java.util.List;

import javax.ejb.Local;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.core.data.RegistrationInformation;

@Local
public interface RegistrationDao {
	
	public RegistrationInformation storeRegistration(RegistrationInformation regInfo);
	
	public RegistrationInformation getRegistrationForId(int id);
	
	public List<RegistrationInformation> getAllRegistration();
	
	public List<RegistrationInformation> getAllRegistrationForAgent(String agentId);
	
	public QueueInfo getQueueForRegistration(RegistrationInformation regInfo);
	
	

}
