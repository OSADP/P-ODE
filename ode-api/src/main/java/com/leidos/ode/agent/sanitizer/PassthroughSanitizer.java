package com.leidos.ode.agent.sanitizer;

import com.leidos.ode.agent.data.ODEAgentMessage;

public class PassthroughSanitizer implements ODESanitizer {

    @Override
    public ODEAgentMessage sanitizeMessage(ODEAgentMessage message) throws ODESanitizeException {
        //TODO
        return message;
    }

}
