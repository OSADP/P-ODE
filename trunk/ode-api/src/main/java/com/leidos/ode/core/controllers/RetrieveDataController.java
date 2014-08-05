package com.leidos.ode.core.controllers;

import com.leidos.ode.core.rde.controllers.RDERetrieveController;
import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDERetrieveException;
import com.leidos.ode.core.rde.data.RDERetrieveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class representing the Retrieve Data controller. Responsible for interacting with the RDE to retrieve
 * vdotdata that has been stored there by the ODE. When a subscription for historical vdotdata is received by
 * the Pub/Sub Registration component, it will make a request to the Retrieve Data component to pull that
 * vdotdata from the RDE. The metadata for the request will be passed to this component at that time. The
 * Retrieve Data component uses the metadata to construct a request for vdotdata and send it to the RDE. Any
 * retrieved vdotdata will then be sent back to the Distribute Data component.
 *
 * @author lamde
 *
 */
@Controller
public class RetrieveDataController {

    @Autowired
    private DistributeDataController distributeDataController;
    @Autowired
    private RDERetrieveController rdeRetrieveAgent;

    public RetrieveDataController() {

    }

    @RequestMapping(value = "retrieve", method = RequestMethod.POST)
    public @ResponseBody RDERetrieveResponse retrieveData(@RequestBody final RDEData rdeData){
        RDERetrieveResponse rdeRetrieveResponse;
        getDistributeDataController().distributeData(rdeData);
        try {
            rdeRetrieveResponse = getRdeRetrieveAgent().retrieve(rdeData);
        } catch (RDERetrieveException e) {
            return new RDERetrieveResponse(e.getLocalizedMessage());
        }
        return rdeRetrieveResponse;
    }

    public DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }

    public RDERetrieveController getRdeRetrieveAgent() {
        return rdeRetrieveAgent;
    }

}
