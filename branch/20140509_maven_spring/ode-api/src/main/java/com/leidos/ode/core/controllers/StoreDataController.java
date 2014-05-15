package com.leidos.ode.core.controllers;

import com.leidos.ode.core.dao.RDEArchiveDAO;
import com.leidos.ode.core.rde.agent.RDEStoreAgent;
import com.leidos.ode.core.rde.model.RDEArchiveInfo;
import com.leidos.ode.core.rde.model.RDEData;
import com.leidos.ode.core.rde.model.RDEStoreException;
import com.leidos.ode.core.rde.model.RDEStoreResponse;
import com.leidos.ode.logging.ODELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class representing the Store Data controller. Responsible for sending published data received by the ODE to the RDE,
 * and recording that transfer to the Pub/Sub registration. Contains an RDE Interface sub-component, which is responsible
 * for formatting the data feed to the RDE as well as the metadata document that will accompany the data feed. Once data
 * is transferred to the RDE, Store Data will use the Pub/Sub registration interface sub-component to the log the data
 * transfer so it can be discovered later for playback.
 *
 * @author lamde
 */
@Controller
public class StoreDataController {

    @Autowired
    private ODELogger odeLogger;
    @Autowired
    private RDEStoreAgent rdeStoreAgent;
    @Autowired
    private RDEArchiveDAO rdeArchiveDAO;

    public StoreDataController() {

    }

    @RequestMapping(value = "store", method = RequestMethod.POST)
    public @ResponseBody RDEStoreResponse storeData(@RequestBody final RDEData rdeData) {
        getOdeLogger().odeLogEvent("StoreData", "Request received for: " + rdeData.getName());

        RDEStoreResponse rdeStoreResponse;
        //Store the data in the archive for playback
        getRdeArchiveDAO().storeRDEArchiveInfo(new RDEArchiveInfo());
        try {
            //Transfer data to the RDE
            rdeStoreResponse = getRdeStoreAgent().store(rdeData);
        } catch (RDEStoreException e) {
           return new RDEStoreResponse(e.getLocalizedMessage());
        }
        return rdeStoreResponse;
    }

    public RDEStoreAgent getRdeStoreAgent() {
        return rdeStoreAgent;
    }

    public RDEArchiveDAO getRdeArchiveDAO() {
        return rdeArchiveDAO;
    }

    private ODELogger getOdeLogger(){
        return odeLogger;
    }

}
