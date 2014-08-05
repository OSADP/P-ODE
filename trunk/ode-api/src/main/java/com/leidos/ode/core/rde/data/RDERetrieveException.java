package com.leidos.ode.core.rde.data;

/**
 * Exception thrown when attempting to retrieve vdotdata in the RDE.
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
