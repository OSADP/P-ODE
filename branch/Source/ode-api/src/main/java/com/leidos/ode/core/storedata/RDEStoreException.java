package com.leidos.ode.core.storedata;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 5/7/14
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class RDEStoreException extends Exception {

    private static final long serialVersionUID = 1L;

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
