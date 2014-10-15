package com.leidos.ode.core.registration;

import com.leidos.ode.core.controllers.DistributeDataController;
import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SubscriptionRegistrationService {

    @Autowired
    private DistributeDataController distributeDataController;
    @Autowired
    private RegistrationDAO registrationDAO;

    @RequestMapping(value = "registerSubscribe", method = RequestMethod.POST)
    public
    @ResponseBody
    ODERegistrationResponse registerSubscriptionIntent(@RequestBody ODERegistrationRequest registrationRequest) {
        ODERegistrationResponse response = null;
        registrationRequest = getRegistrationDAO().storeRegistration(registrationRequest);
        QueueInfo queueInfo = getQueueNameForRegistration(registrationRequest);
        if (queueInfo != null) {
            response = createRegResponse(registrationRequest, queueInfo);
            notifiyDistribute(response);
        }
        return response;
    }

    private void notifiyDistribute(ODERegistrationResponse registrationResponse) {
        getDistributeDataController().receiveSubscriptionNotification(registrationResponse);
    }

    private QueueInfo getQueueNameForRegistration(ODERegistrationRequest registrationRequest) {
        return getRegistrationDAO().getQueueForRegistration(registrationRequest);
    }

    private ODERegistrationResponse createRegResponse(ODERegistrationRequest registrationRequest, QueueInfo qInfo) {
        ODERegistrationResponse resp = new ODERegistrationResponse();
        resp.setAgentId(registrationRequest.getAgentId());
        resp.setMessageType(registrationRequest.getMessageType());
        resp.setRegion(registrationRequest.getRegion());
        resp.setRegistrationId(registrationRequest.getRegistrationId());
        resp.setRegistrationType(registrationRequest.getRegistrationType());

        resp.setQueueConnFact(qInfo.getQueueConnectionFactory());
        resp.setQueueName(qInfo.getQueueName());
        resp.setQueueHostURL(qInfo.getTargetAddress());
        resp.setQueueHostPort(qInfo.getTargetPort());
        resp.setTargetAddress(registrationRequest.getSubscriptionReceiveAddress());
        resp.setTargetPort(registrationRequest.getSubscriptionReceivePort());

        return resp;
    }

    private DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }

    private RegistrationDAO getRegistrationDAO() {
        return registrationDAO;
    }
}
