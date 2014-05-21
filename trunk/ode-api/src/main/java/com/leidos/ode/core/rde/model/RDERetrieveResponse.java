package com.leidos.ode.core.rde.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing the response received from the RDE upon retrieving data.
 *
 * @author lamde
 */
public class RDERetrieveResponse {

    @JsonProperty("message")
    private String message;

    public RDERetrieveResponse(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

}
