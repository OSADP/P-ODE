/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.core.controllers.topics;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.controllers.PublishDataController;
import com.leidos.ode.logging.ODELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author cassadyja
 */
@Controller
public class PublishTravelTimeDataController extends PublishDataController {
    @Value("${leidos.ode.publisher.topic.traveltime}")
    private String topicName;
    
    @Autowired
    @Qualifier("odeLogger")
    private ODELogger odeLogger;    
    
    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    @RequestMapping(value = PublishEndpoints.TRAVEL_TIME, method = RequestMethod.POST)
    protected @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
        return publish(odeAgentMessage);
    }

    /**
     * @return the odeLogger
     */
    public ODELogger getOdeLogger() {
        return odeLogger;
    }

    /**
     * @param odeLogger the odeLogger to set
     */
    public void setOdeLogger(ODELogger odeLogger) {
        this.odeLogger = odeLogger;
    }    
}
