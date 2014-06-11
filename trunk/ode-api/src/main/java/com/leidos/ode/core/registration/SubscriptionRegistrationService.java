package com.leidos.ode.core.registration;


import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.data.ODERegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SubscriptionRegistrationService {

    @Autowired
    private RegistrationDAO regDao;

    @RequestMapping(value = "registerSubscribe", method = RequestMethod.POST)
    public @ResponseBody ODERegistrationResponse registerSubscriptionIntent(@RequestBody RegistrationInformation regInfo) {
        ODERegistrationResponse response = null;

        regInfo = regDao.storeRegistration(regInfo);
        response = createResponse(regInfo);

        return response;
    }

    private ODERegistrationResponse createResponse(RegistrationInformation regInfo) {
        ODERegistrationResponse resp = new ODERegistrationResponse();
        resp.setAgentId(regInfo.getAgentId());
        resp.setMessageType(regInfo.getMessageType());
        resp.setRegion(regInfo.getRegion());
        resp.setRegistrationId(regInfo.getId());
        resp.setRegistrationType(regInfo.getRegistrationType());
        return resp;
    }
}
