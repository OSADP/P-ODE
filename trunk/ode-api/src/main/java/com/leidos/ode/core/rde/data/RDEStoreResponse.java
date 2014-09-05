package com.leidos.ode.core.rde.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing the response received from the RDE upon storing data.
 *
 * @lamde
 */
public class RDEStoreResponse {

    @JsonProperty("message")
    private String message;

    public RDEStoreResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
