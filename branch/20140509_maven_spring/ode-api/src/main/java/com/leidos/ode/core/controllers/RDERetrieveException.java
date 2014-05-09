package com.leidos.ode.core.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 5/7/14
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class RDERetrieveException extends Exception {

    private static final long serialVersionUID = 1L;

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
