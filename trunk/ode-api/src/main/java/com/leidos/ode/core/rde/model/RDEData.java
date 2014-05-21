package com.leidos.ode.core.rde.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing the RDE data set.
 *
 * @author lamde
 */
public class RDEData {

    @JsonProperty("name")
    private String name;
    @JsonProperty("metadata")
    private RDEMetadata rdeMetadata;

    //By default, Jackson binding uses the default constructor. Must use the @JsonCreator annotation to tell
    //the mapper to use this constructor instead.
    @JsonCreator
    public RDEData(@JsonProperty("name") String name, @JsonProperty("metadata") RDEMetadata rdeMetadata) {
        this.name = name;
        this.rdeMetadata = rdeMetadata;
    }

    public String getName() {
        return name;
    }

    public RDEMetadata getMetadata() {
        return rdeMetadata;
    }
}