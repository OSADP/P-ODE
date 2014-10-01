package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.Logger;

public abstract class ODEDataParser {

    protected final String TAG = getClass().getSimpleName();
    protected final Logger logger = Logger.getLogger(TAG);
    private ODEMessageType odeMessageType = ODEMessageType.UNDEFINED;

    public abstract ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException;

    public final ODEMessageType getODEMessageType() {
        return odeMessageType;
    }

    public final ODEDataParser setODEMessageType(String odeMessageType) {
        this.odeMessageType = ODEMessageType.valueOf(odeMessageType);
        return this;
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
