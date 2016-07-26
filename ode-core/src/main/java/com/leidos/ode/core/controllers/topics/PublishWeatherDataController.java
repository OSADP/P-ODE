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
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 9/5/14
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PublishWeatherDataController extends PublishDataController {
    
    @Value("${leidos.ode.publisher.topic.weather}")
    private String topicName;
    
    @Autowired
    @Qualifier("odeLogger")
    private ODELogger odeLogger;    
    
    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    @RequestMapping(value = PublishEndpoints.WEATHER, method = RequestMethod.POST)
    public @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
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
