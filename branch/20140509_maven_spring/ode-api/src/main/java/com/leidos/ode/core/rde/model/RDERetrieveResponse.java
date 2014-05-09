package com.leidos.ode.core.rde.model;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 5/6/14
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class RDERetrieveResponse {

    private String message;
    private RDEData rdeData;

    public RDERetrieveResponse(String message, RDEData rdeData){
        this.message = message;
        this.rdeData = rdeData;
    }

    public String getMessage(){
        return message;
    }

    public RDEData getRdeData() {
        return rdeData;
    }

}
