package com.leidos.ode.core.registration;



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
public class PublicationRegistrationService {

    @Autowired
    private RegistrationDAO regDao;

    @RequestMapping(value = "registerPublish", method = RequestMethod.POST)
    public @ResponseBody ODERegistrationResponse registerPublicationIntent(@RequestBody RegistrationInformation regInfo) {
        ODERegistrationResponse response = null;
        QueueInfo qInfo = getQueueNameForRegistration(regInfo);
        if (qInfo != null) {
            regInfo = regDao.storeRegistration(regInfo);
            response = createRegResponse(regInfo, qInfo);

        }

        return response;
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
        return resp;
    }

    private QueueInfo getQueueNameForRegistration(RegistrationInformation regInfo) {
        return regDao.getQueueForRegistration(regInfo);
    }

    public RegistrationDAO getRegDao() {
        return regDao;
    }

    public void setRegDao(RegistrationDAO regDao) {
        this.regDao = regDao;
    }

}
