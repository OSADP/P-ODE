package com.leidos.ode.core.registration;

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
public class PublicationRegistrationService {

    @Autowired
    private RegistrationDAO regDao;

    @RequestMapping(value = "registerPublish", method = RequestMethod.POST, consumes  = "application/xml", produces = "application/xml")
    public
    @ResponseBody
    ODERegistrationResponse registerPublicationIntent(@RequestBody ODERegistrationRequest registrationRequest) {
        ODERegistrationResponse registrationResponse = null;
        registrationRequest = getRegDao().storeRegistration(registrationRequest);
        QueueInfo qInfo = getQueueNameForRegistration(registrationRequest);
        if (qInfo != null) {
            registrationResponse = createRegResponse(registrationRequest, qInfo);
        }

        return registrationResponse;
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
        resp.setPublishWebServiceAddress(qInfo.getWsURL());
        return resp;
    }

    private QueueInfo getQueueNameForRegistration(ODERegistrationRequest registrationRequest) {
        return getRegDao().getQueueForRegistration(registrationRequest);
    }

    public RegistrationDAO getRegDao() {
        return regDao;
    }

    public void setRegDao(RegistrationDAO regDao) {
        this.regDao = regDao;
    }
}
