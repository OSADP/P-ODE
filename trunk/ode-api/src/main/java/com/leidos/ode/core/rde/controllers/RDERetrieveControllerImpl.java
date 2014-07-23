package com.leidos.ode.core.rde.agent;

import com.leidos.ode.core.rde.model.RDEData;
import com.leidos.ode.core.rde.model.RDERetrieveException;
import com.leidos.ode.core.rde.model.RDERetrieveResponse;
import org.springframework.stereotype.Component;

/**
 * Class representing the RDE data retrieve agent. Responsible for connecting to the RDE and retrieving RDE data.
 *
 * @author lamde
 */
@Component
public class RDERetrieveAgentImpl implements RDERetrieveAgent {

    public RDERetrieveAgentImpl(){

    }

    @Override
    public RDERetrieveResponse retrieve(RDEData rdeData) throws RDERetrieveException {
        //Connect to RDE and retrieve data. Build a response object from the data.
        return new RDERetrieveResponse("Successfully retrieved data: " + rdeData.getName());
    }
}
