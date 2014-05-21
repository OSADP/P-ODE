package com.leidos.ode.core.rde.model;

/**
 * Exception thrown when attempting to store data in the RDE.
 *
 * @author lamde
 */
public class RDEStoreException extends Exception {

    public RDEStoreException(){
        super();
    }

    public RDEStoreException(String message){
        super(message);
    }

    public RDEStoreException(String message, Throwable throwable){
        super(message, throwable);
    }

}
