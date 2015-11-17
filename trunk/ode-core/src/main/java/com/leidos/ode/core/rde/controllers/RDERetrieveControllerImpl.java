package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.controllers.DistributeDataController;
import com.leidos.ode.core.rde.RDEClientContext;
import com.leidos.ode.core.rde.RDEConfig;
import com.leidos.ode.core.rde.request.model.RDEData;
import com.leidos.ode.core.rde.response.impl.RDERetrieveResponse;
import org.dot.rdelive.client.impl.RDEClientDownloadDirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
//@Controller
//@Component
public class RDERetrieveControllerImpl implements RDERetrieveController {

    private final String TAG = getClass().getSimpleName();
    @Autowired
    private DistributeDataController distributeDataController;
    private RDEClientContext context;
    private RDEClientDownloadDirector director;

    public RDERetrieveControllerImpl() {        
    }
    
    public RDERetrieveControllerImpl(RDEClientContext context) {
        this.context = context;
        this.director = new RDEClientDownloadDirector(context, null);
    }

    @Override
    public RDERetrieveResponse retrieve(RDEData rdeData) throws RDERetrieveException {

        return new RDERetrieveResponse();
    }

    public DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }
}
