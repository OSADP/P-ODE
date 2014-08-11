package com.leidos.ode.core.controllers;

import com.leidos.ode.agent.data.ODEAgentMessage;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class representing the Publish Data controller. Responsible for retrieving vdotdata from the Publisher ODE
 * Agents and verifying the vdotdata published is in accordance with publication registrations. Verified vdotdata
 * is sent to the Store Data component and made available to subscribers via the Distribute Data component.
 *
 * @author cassadyja, lamde
 *
 */
@Controller
public class PublishDataController   {

    @Autowired
    private StoreDataController storeDataController;
    @Autowired
    private DistributeDataController distributeDataController;
    private int i = 0;
    
    public PublishDataController() {

    }

    @RequestMapping(value = "publish", method = RequestMethod.POST)
    public @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
        System.out.println("~~~~~~~Received message ."+ ++i);
        return "OK";
    }

    public StoreDataController getStoreDataController() {
        return storeDataController;
    }

    public DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }
    
    @PostConstruct
    public void initTopicConnection(){
        
    }

}
