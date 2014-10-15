package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDEStoreResponse;

public interface RDEStoreController {

    public RDEStoreResponse store(RDEData dataBean) throws RDEStoreException;

    /**
     * Exception thrown when attempting to store data on the RDE.
     *
     * @author lamde
     */
    public class RDEStoreException extends Exception {

        public RDEStoreException() {
            super();
        }

        public RDEStoreException(String message) {
            super(message);
        }

        public RDEStoreException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
