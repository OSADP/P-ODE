package com.leidos.ode.core.controllers.topics;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.controllers.PublishDataController;
import com.leidos.ode.logging.ODELogger;
import javax.annotation.PostConstruct;
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
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PublishVDOTSpdVolOccDataController extends PublishDataController {

    @Value("${leidos.ode.publisher.topic.vdotspdvolocc}")
    private String topicName;
    
    @Autowired
    @Qualifier("odeLogger")
    private ODELogger odeLogger;    
    
    @Override
    public String getTopicName() {
        return topicName;
    }
    
    @Override
    @RequestMapping(value = PublishEndpoints.VDOT_SPD_VOL_OCC, method = RequestMethod.POST)
    public @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
        System.out.println("VDOT Speed message received");
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
