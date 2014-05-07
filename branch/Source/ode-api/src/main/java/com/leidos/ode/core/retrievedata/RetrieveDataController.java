package com.leidos.ode.core.retrievedata;

import com.leidos.ode.core.distribute.DistributeDataController;
import com.leidos.ode.core.rde.agent.RDERetrieveAgent;
import com.leidos.ode.core.rde.model.RDEData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public class RetrieveDataController   {

    private DistributeDataController distributeDataController;
    private RDERetrieveAgent rdeRetrieveAgent;

    public RetrieveDataController() {
        super();
    }

    @RequestMapping(value = "retrieve", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity retrieveData(@RequestBody RDEData rdeData) {
        //Retrieve data
        //build RDE request from metadata, retrieved data should be used to build a DataBean
        //which is then sent to the DistributeDataController.

        return new ResponseEntity(HttpStatus.OK);
    }

    public DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }

    public void setDistributeDataController(DistributeDataController distributeDataController) {
        this.distributeDataController = distributeDataController;
    }

    public RDERetrieveAgent getRdeRetrieveAgent() {
        return rdeRetrieveAgent;
    }

    public void setRdeRetrieveAgent(RDERetrieveAgent rdeRetrieveAgent) {
        this.rdeRetrieveAgent = rdeRetrieveAgent;
    }
}
