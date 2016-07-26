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
 * Controller responsible for handling incoming incident data deliveries.
 */
@Controller
public class PublishIncidentDataController extends PublishDataController {

    @Value("${leidos.ode.publisher.topic.incident}")
    private String topic;

    @Autowired
    @Qualifier("odeLogger")
    private ODELogger odeLogger;


    @Override
    @RequestMapping(value = PublishEndpoints.INCIDENT, method = RequestMethod.POST)
    protected @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
        return publish(odeAgentMessage);
    }

    @Override
    public String getTopicName() {
        return topic;
    }

    @Override
    public ODELogger getOdeLogger() {
        return odeLogger;
    }
}
