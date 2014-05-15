package com.leidos.ode.core.rde.model;

/**
 * Exception thrown when attempting to retrieve data in the RDE.
 *
 * @author lamde
 */
public class RDERetrieveException extends Exception {

    public RDERetrieveException(){
        super();
    }

    public RDERetrieveException(String message){
        super(message);
    }

    public RDERetrieveException(String message, Throwable throwable){
        super(message, throwable);
    }
}
