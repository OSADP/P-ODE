package com.leidos.ode.core.dao;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.data.DataSubscriptionResponse;
import com.leidos.ode.data.PodeDataPulicationRegistration;
import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.registration.request.ODERegistrationRequest;

import java.util.List;

public interface RegistrationDAO {

    public void storeServiceRequest(ServiceRequest serviceRequest, boolean subscribe);
    
    
    public ODERegistrationRequest storeRegistration(ODERegistrationRequest regInfo);

    public ODERegistrationRequest getRegistrationForId(int id);

    public List<ODERegistrationRequest> getAllRegistration();

    public List<ODERegistrationRequest> getAllRegistrationForAgent(String agentId);

    public QueueInfo getQueueForRegistration(ODERegistrationRequest regInfo);

    public QueueInfo getQueueForMessageType(String messageType);
    
    public void updateRegistration(PodeDataPulicationRegistration pubRegistration);
    
    public void updateRegistration(PodeSubscriptionRequest subRegistration, DataSubscriptionResponse subResponse);
    
    public ServiceRequest getServiceRequestForRequestId(byte[] requestId);
    
    
}
