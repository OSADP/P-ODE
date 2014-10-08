package com.leidos.ode.core.registration;

import com.leidos.ode.agent.data.RegistrationInformation;
import com.leidos.ode.core.controllers.DistributeDataController;
import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.core.data.QueueInfo;
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
    ODERegistrationResponse registerSubscriptionIntent(@RequestBody RegistrationInformation registrationInformation) {
        ODERegistrationResponse response = null;
        registrationInformation = getRegistrationDAO().storeRegistration(registrationInformation);
        QueueInfo queueInfo = getQueueNameForRegistration(registrationInformation);
        if (queueInfo != null) {
            response = createRegResponse(registrationInformation, queueInfo);
            notifiyDistribute(response);
        }
        return response;
    }

    private void notifiyDistribute(ODERegistrationResponse response) {
        getDistributeDataController().receiveSubscriptionNotification(response);
    }

    private QueueInfo getQueueNameForRegistration(RegistrationInformation regInfo) {
        return getRegistrationDAO().getQueueForRegistration(regInfo);
    }

    private ODERegistrationResponse createRegResponse(RegistrationInformation regInfo, QueueInfo qInfo) {
        ODERegistrationResponse resp = new ODERegistrationResponse();
        resp.setAgentId(regInfo.getAgentId());
        resp.setMessageType(regInfo.getMessageType());
        resp.setRegion(regInfo.getRegion());
        resp.setRegistrationId(regInfo.getId());
        resp.setRegistrationType(regInfo.getRegistrationType());

        resp.setQueueConnFact(qInfo.getQueueConnectionFactory());
        resp.setQueueName(qInfo.getQueueName());
        resp.setQueueHostURL(qInfo.getTargetAddress());
        resp.setQueueHostPort(qInfo.getTargetPort());
        resp.setTargetAddress(regInfo.getSubscriptionReceiveAddress());
        resp.setTargetPort(regInfo.getSubscriptionReceivePort());

        return resp;
    }

    private DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }

    private RegistrationDAO getRegistrationDAO() {
        return registrationDAO;
    }
}
