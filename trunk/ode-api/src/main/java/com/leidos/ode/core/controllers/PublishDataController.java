package com.leidos.ode.core.controllers;

import com.leidos.ode.core.rde.model.RDEData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class representing the Publish Data controller. Responsible for retrieving data from the Publisher ODE
 * Agents and verifying the data published is in accordance with publication registrations. Verified data
 * is sent to the Store Data component and made available to subscribers via the Distribute Data component.
 *
 * @author cassadyja, lamde
 *
 */
@Controller
public class PublishDataController   {

    @Autowired
    private StoreDataController storeDataController;
    @Autowired
    private DistributeDataController distributeDataController;

    public PublishDataController() {

    }

    @RequestMapping(value = "publish", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity publishData(@RequestBody RDEData rdeData) {
        return null;
    }

    public StoreDataController getStoreDataController() {
        return storeDataController;
    }

    public DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }

}
