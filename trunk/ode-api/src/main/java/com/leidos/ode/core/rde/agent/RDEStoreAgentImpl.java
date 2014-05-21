package com.leidos.ode.core.rde.agent;

import com.leidos.ode.core.rde.model.RDEData;
import com.leidos.ode.core.rde.model.RDEStoreException;
import com.leidos.ode.core.rde.model.RDEStoreResponse;
import org.springframework.stereotype.Component;

/**
 * Class representing the RDE data storage agent. Responsible for connecting to the RDE and storing RDE data.
 *
 * @author lamde
 */
@Component
public class RDEStoreAgentImpl implements RDEStoreAgent {

    public RDEStoreAgentImpl(){

    }

    @Override
    public RDEStoreResponse store(RDEData rdeData) throws RDEStoreException{
        //Format the data feed for the RDE. Connect to RDE and store the formatted data. Build a response object from the data.
        return new RDEStoreResponse("Successfully stored data: " + rdeData.getName());
    }

}
