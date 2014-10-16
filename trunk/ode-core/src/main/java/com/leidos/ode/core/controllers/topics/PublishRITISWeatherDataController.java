package com.leidos.ode.core.controllers.topics;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.controllers.PublishDataController;
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
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PublishRITISWeatherDataController extends PublishDataController {
    @Value("${leidos.ode.publisher.topic.ritisweather}")
    private String topicName;
    
    @Override
    public String getTopicName() {
        return topicName;
    }
    
    
    @Override
    @RequestMapping(value = PublishEndpoints.RITIS_WEATHER, method = RequestMethod.POST)
    public @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
        return publish(odeAgentMessage);
    }
}
