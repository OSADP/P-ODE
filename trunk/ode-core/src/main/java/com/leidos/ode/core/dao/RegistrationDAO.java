package com.leidos.ode.core.dao;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.registration.request.ODERegistrationRequest;

import java.util.List;

public interface RegistrationDAO {

    public ODERegistrationRequest storeRegistration(ODERegistrationRequest regInfo);

    public ODERegistrationRequest getRegistrationForId(int id);

    public List<ODERegistrationRequest> getAllRegistration();

    public List<ODERegistrationRequest> getAllRegistrationForAgent(String agentId);

    public QueueInfo getQueueForRegistration(ODERegistrationRequest regInfo);

}
