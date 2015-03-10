package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.registration.RegistrationResponse;


public interface ODEDataTarget {

    public void configure(RegistrationResponse registrationResponse) throws DataTargetException;

    public void sendMessage(ODEAgentMessage message) throws DataTargetException;

    public void close();

    public class DataTargetException extends Exception {

        public DataTargetException() {
            super();
        }

        public DataTargetException(String message) {
            super(message);
        }

        public DataTargetException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }
}
