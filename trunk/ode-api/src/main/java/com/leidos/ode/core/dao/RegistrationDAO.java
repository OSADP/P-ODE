package com.leidos.ode.core.dao;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.core.registration.RegistrationInformation;

import java.util.List;


public interface RegistrationDAO {

    public RegistrationInformation storeRegistration(RegistrationInformation regInfo);

    public RegistrationInformation getRegistrationForId(int id);

    public List<RegistrationInformation> getAllRegistration();

    public List<RegistrationInformation> getAllRegistrationForAgent(String agentId);

    public QueueInfo getQueueForRegistration(RegistrationInformation regInfo);

}
