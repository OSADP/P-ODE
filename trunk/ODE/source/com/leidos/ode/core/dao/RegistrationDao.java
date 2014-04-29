package com.leidos.ode.core.dao;

import java.util.List;

import com.leidos.ode.core.data.RegistrationInformation;

public interface RegistrationDao {
	
	public RegistrationInformation storeRegistration(RegistrationInformation regInfo);
	
	public RegistrationInformation getRegistrationForId(int id);
	
	public List<RegistrationInformation> getAllRegistration();
	
	public List<RegistrationInformation> getAllRegistrationForAgent(String agentId);
	
	public String getQueueForRegistration(RegistrationInformation regInfo);

}
