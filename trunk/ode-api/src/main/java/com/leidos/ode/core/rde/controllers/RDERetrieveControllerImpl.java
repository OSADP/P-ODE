package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.controllers.DistributeDataController;
import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDERetrieveException;
import com.leidos.ode.core.rde.data.RDERetrieveResponse;
import com.leidos.ode.logging.ODELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * Class representing the Retrieve Data controller. Responsible for interacting with the RDE to retrieve
 * data that has been stored there by the ODE. When a subscription for historical data is received by
 * the Pub/Sub Registration component, it will make a request to the Retrieve Data component to pull that
 * data from the RDE. The metadata for the request will be passed to this component at that time. The
 * Retrieve Data component uses the metadata to construct a request for data and send it to the RDE. Any
 * retrieved data will then be sent back to the Distribute Data component.
 *
 * @author lamde
 */
@Controller
@Component
public class RDERetrieveControllerImpl implements RDERetrieveController {

    private final String TAG = getClass().getSimpleName();

    @Autowired
    private ODELogger odeLogger;

    @Autowired
    private DistributeDataController distributeDataController;



    @Override
    public RDERetrieveResponse retrieve(RDEData rdeData) throws RDERetrieveException {
        getOdeLogger().odeLogEvent(TAG, "Request received for: " + rdeData.getName());

        return null;
    }

    public DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }

    private ODELogger getOdeLogger() {
        return odeLogger;
    }

}
