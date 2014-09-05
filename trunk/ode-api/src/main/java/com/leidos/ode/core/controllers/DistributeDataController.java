package com.leidos.ode.core.controllers;

import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.core.distribute.DataDistributor;
import com.leidos.ode.core.distribute.UDPDataDistributor;
import com.leidos.ode.core.rde.data.RDEData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class DistributeDataController {

    private Map<String, DataDistributor> distributors = new HashMap<String, DataDistributor>();

    public DistributeDataController() {

    }

    @RequestMapping(value = "distribute", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity distributeData(@RequestBody RDEData rdeData) {
        return null;
    }

    @RequestMapping(value = "subscriptionNotification", method = RequestMethod.POST)
    public void receiveSubscriptionNotification(@RequestBody ODERegistrationResponse response) {
        //Create a new DataDistributor with the info from the subscription.
        DataDistributor dist = new UDPDataDistributor(response.getQueueHostURL(), response.getQueueHostPort(), response.getQueueConnFact(), response.getQueueName(), response.getTargetAddress(), response.getTargetPort());

        //Start up the distributor
        new Thread(dist).start();
        distributors.put(response.getAgentId(), dist);
    }

    @RequestMapping(value = "subscriptionCancel", method = RequestMethod.POST)
    public void stopSubscription(@RequestBody String agentId) {
        distributors.get(agentId).setInterrupted(true);
        distributors.remove(agentId);
    }

    @PreDestroy
    public void cleanup() {
        Iterator<String> it = distributors.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            distributors.get(key).setInterrupted(true);
        }
    }

}
