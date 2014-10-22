package com.leidos.ode.core.controllers;

import com.leidos.ode.core.distribute.DataDistributor;
import com.leidos.ode.core.distribute.UDPDataDistributor;
import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.registration.response.ODERegistrationResponse;
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
import org.springframework.beans.factory.DisposableBean;

@Controller
public class DistributeDataController implements  DisposableBean{

    private Map<String, DataDistributor> distributors = new HashMap<String, DataDistributor>();
    private int startingServerPort = 12000;
    
    public DistributeDataController() {

    }

    @RequestMapping(value = "distribute", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity distributeData(@RequestBody RDEData rdeData) {
        return null;
    }

    @RequestMapping(value = "subscriptionNotification", method = RequestMethod.POST)
    public void receiveSubscriptionNotification(@RequestBody ODERegistrationResponse registrationResponse) {
        //Create a new DataDistributor with the info from the subscription.
        System.out.println("New Subscription creating Distributor");
        DataDistributor dist = new UDPDataDistributor(registrationResponse.getQueueHostURL(), registrationResponse.getQueueHostPort(), registrationResponse.getQueueConnFact(), registrationResponse.getQueueName(), registrationResponse.getTargetAddress(), registrationResponse.getTargetPort(), startingServerPort++);

        //Start up the distributor
        new Thread(dist).start();
        distributors.put(registrationResponse.getAgentId(), dist);
    }

    @RequestMapping(value = "subscriptionCancel", method = RequestMethod.POST)
    public void stopSubscription(@RequestBody String agentId) {
        if(distributors.get(agentId) != null){
            distributors.get(agentId).setInterrupted(true);
            distributors.remove(agentId);
        }
        
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Closing Distributors.");
        Iterator<String> it = distributors.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            distributors.get(key).setInterrupted(true);
        }
    }

    public void destroy() throws Exception {
        cleanup();
    }

}
