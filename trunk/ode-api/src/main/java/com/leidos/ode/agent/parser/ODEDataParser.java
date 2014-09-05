package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;

public interface ODEDataParser {

    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException;

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
