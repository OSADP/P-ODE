package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDERetrieveException;
import com.leidos.ode.core.rde.data.RDERetrieveResponse;
import org.springframework.stereotype.Component;

/**
 * Class representing the RDE data retrieve agent. Responsible for connecting to the RDE and retrieving RDE data.
 *
 * @author lamde
 */
@Component
public class RDERetrieveControllerImpl implements RDERetrieveController {

    public RDERetrieveControllerImpl(){

    }

    @Override
    public RDERetrieveResponse retrieve(RDEData rdeData) throws RDERetrieveException {
        //Connect to RDE and retrieve data. Build a response object from the data.
        return new RDERetrieveResponse("Successfully retrieved data: " + rdeData.getName());
    }
}
