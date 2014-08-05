package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDERetrieveException;
import com.leidos.ode.core.rde.data.RDERetrieveResponse;
import org.springframework.stereotype.Component;

/**
 * Class representing the RDE vdotdata retrieve agent. Responsible for connecting to the RDE and retrieving RDE vdotdata.
 *
 * @author lamde
 */
@Component
public class RDERetrieveControllerImpl implements RDERetrieveController {

    public RDERetrieveControllerImpl(){

    }

    @Override
    public RDERetrieveResponse retrieve(RDEData rdeData) throws RDERetrieveException {
        //Connect to RDE and retrieve vdotdata. Build a response object from the vdotdata.
        return new RDERetrieveResponse("Successfully retrieved vdotdata: " + rdeData.getName());
    }
}
