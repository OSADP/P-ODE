package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import org.apache.log4j.Logger;

public abstract class ODEDataParser {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);

    public abstract ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException;

    protected Logger getLogger(){
        return logger;
    }

    public class ODEParseException extends Exception {

        private static final long serialVersionUID = 1L;

        public ODEParseException() {
            super();
        }

        public ODEParseException(String message) {
            super(message);
        }

        public ODEParseException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }
}
