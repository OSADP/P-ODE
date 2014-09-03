package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDEStoreException;
import com.leidos.ode.core.rde.data.RDEStoreResponse;
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
public class RDEStoreControllerImpl implements RDEStoreController {

    private final String TAG = getClass().getSimpleName();

    @Autowired
    private ODELogger odeLogger;

    @Override
    @RequestMapping(value = "rdeStore", method = RequestMethod.POST)
    public @ResponseBody
    RDEStoreResponse store(@RequestBody RDEData rdeData) throws RDEStoreException {
        getOdeLogger().odeLogEvent(TAG, "Request received for: " + rdeData.getName());

        return null;
    }

    private ODELogger getOdeLogger() {
        return odeLogger;
    }
}
