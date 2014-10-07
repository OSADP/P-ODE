package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import org.apache.log4j.Logger;

public abstract class ODEDataParser {

    protected final String TAG = getClass().getSimpleName();
    protected final Logger logger = Logger.getLogger(TAG);

    public abstract ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException;

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
