package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import org.apache.log4j.Logger;

public class ODERITISParser implements ODEDataParser {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        return null;
    }

}