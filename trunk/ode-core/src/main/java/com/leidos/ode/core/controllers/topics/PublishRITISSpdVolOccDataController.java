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
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PublishRITISSpdVolOccDataController extends PublishDataController {
    @Value("${leidos.ode.publisher.topic.ritisspdvolocc}")
    private String topicName;
    
    @Override
    public String getTopicName() {
        return topicName;
    }
    
    @Override
    @RequestMapping(value = PublishEndpoints.RITIS_SPD_VOL_OCC, method = RequestMethod.POST)
    public @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
        System.out.println("RITIS Speed message received");
        return publish(odeAgentMessage);
    }
}
