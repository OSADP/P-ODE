package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDEStoreException;
import com.leidos.ode.core.rde.data.RDEStoreResponse;
import org.springframework.stereotype.Component;

/**
 * Class representing the RDE data storage agent. Responsible for connecting to the RDE and storing RDE data.
 *
 * @author lamde
 */
@Component
public class RDEStoreControllerImpl implements RDEStoreController {

    public RDEStoreControllerImpl(){

    }

    @Override
    public RDEStoreResponse store(RDEData rdeData) throws RDEStoreException {
        //Format the data feed for the RDE. Connect to RDE and store the formatted data. Build a response object from the data.
        return new RDEStoreResponse("Successfully stored data: " + rdeData.getName());
    }

}
