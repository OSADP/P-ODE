package com.leidos.ode.agent.sanitizer;

import com.leidos.ode.agent.data.ODEAgentMessage;

public interface ODESanitizer {

    public ODEAgentMessage sanitizeMessage(ODEAgentMessage message) throws ODESanitizeException;

    public class ODESanitizeException extends Exception {

        private static final long serialVersionUID = 1L;

        public ODESanitizeException() {
            super();
        }

        public ODESanitizeException(String message) {
            super(message);
        }

        public ODESanitizeException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }
}
