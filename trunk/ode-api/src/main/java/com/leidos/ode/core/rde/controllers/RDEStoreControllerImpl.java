package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDEStoreException;
import com.leidos.ode.core.rde.data.RDEStoreResponse;
import org.springframework.stereotype.Component;

/**
 * Class representing the RDE vdotdata storage agent. Responsible for connecting to the RDE and storing RDE vdotdata.
 *
 * @author lamde
 */
@Component
public class RDEStoreControllerImpl implements RDEStoreController {

    public RDEStoreControllerImpl(){

    }

    @Override
    public RDEStoreResponse store(RDEData rdeData) throws RDEStoreException {
        //Format the vdotdata feed for the RDE. Connect to RDE and store the formatted vdotdata. Build a response object from the vdotdata.
        return new RDEStoreResponse("Successfully stored vdotdata: " + rdeData.getName());
    }

}
