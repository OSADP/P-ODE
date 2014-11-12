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
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PublishBluFaxLinkDataController extends PublishDataController {
    
    @Value("${leidos.ode.publisher.topic.blufaxlink}")
    private String topicName;
    
    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    @RequestMapping(value = PublishEndpoints.BLUFAX_LINK, method = RequestMethod.POST)
    public @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
        return publish(odeAgentMessage);
    }
}
