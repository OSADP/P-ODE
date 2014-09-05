package com.leidos.ode.core.registration;

import com.leidos.ode.core.controllers.DistributeDataController;
import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.core.rde.controllers.RDEStoreController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SubscriptionRegistrationService {

    @Autowired
    private RDEStoreController storeDataController;
    @Autowired
    private DistributeDataController distributeDataController;

    @Autowired
    private RegistrationDAO regDao;

    @RequestMapping(value = "registerSubscribe", method = RequestMethod.POST)
    public
    @ResponseBody
    ODERegistrationResponse registerSubscriptionIntent(@RequestBody RegistrationInformation regInfo) {
        ODERegistrationResponse response = null;

        regInfo = regDao.storeRegistration(regInfo);
        QueueInfo qi = getQueueNameForRegistration(regInfo);
        response = createResponse(regInfo, qi);

        notifiyDistribute(response);

        return response;
    }

    private void notifiyDistribute(ODERegistrationResponse response) {
        distributeDataController.receiveSubscriptionNotification(response);
    }

    private QueueInfo getQueueNameForRegistration(RegistrationInformation regInfo) {
        return regDao.getQueueForRegistration(regInfo);
    }

    private ODERegistrationResponse createResponse(RegistrationInformation regInfo, QueueInfo qInfo) {
        ODERegistrationResponse resp = new ODERegistrationResponse();
        resp.setAgentId(regInfo.getAgentId());
        resp.setMessageType(regInfo.getMessageType());
        resp.setRegion(regInfo.getRegion());
        resp.setRegistrationId(regInfo.getId());
        resp.setRegistrationType(regInfo.getRegistrationType());
        resp.setTargetAddress(regInfo.getSubscriptionReceiveAddress());
        resp.setTargetPort(regInfo.getSubscriptionReceivePort());

        resp.setQueueConnFact(qInfo.getQueueConnectionFactory());
        resp.setQueueName(qInfo.getQueueName());
        resp.setQueueHostURL(qInfo.getTargetAddress());
        resp.setQueueHostPort(qInfo.getTargetPort());

        return resp;
    }

    /**
     * @return the storeDataController
     */
    public RDEStoreController getStoreDataController() {
        return storeDataController;
    }

    /**
     * @param storeDataController the storeDataController to set
     */
    public void setStoreDataController(RDEStoreController storeDataController) {
        this.storeDataController = storeDataController;
    }

    /**
     * @return the distributeDataController
     */
    public DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }

    /**
     * @param distributeDataController the distributeDataController to set
     */
    public void setDistributeDataController(DistributeDataController distributeDataController) {
        this.distributeDataController = distributeDataController;
    }


}
