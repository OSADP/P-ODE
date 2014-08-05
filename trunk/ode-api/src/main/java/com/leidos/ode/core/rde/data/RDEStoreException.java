package com.leidos.ode.core.rde.data;

/**
 * Exception thrown when attempting to store vdotdata in the RDE.
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
