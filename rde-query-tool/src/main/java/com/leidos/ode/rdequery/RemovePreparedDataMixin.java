package com.leidos.ode.rdequery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.bn.coders.IASN1PreparedElementData;

/**
 * Interface necessary to remove prepared data from output.
 *
 * By registering this as a mixin to all instances of type Object in the Jackson
 * ObjectMapper system it applies (and overrides) the properties here to all objects
 * it formats. Namely, we're trying to remove the IASN1PreparedElementData as it creates
 * a lot of noise in the ouput and doesn't add value. We're also cutting out any null values
 * for the same reason.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface RemovePreparedDataMixin {
    @JsonIgnore
    IASN1PreparedElementData getPreparedData();
}
