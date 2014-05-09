package com.leidos.ode.core.controllers;

import com.leidos.ode.core.rde.agent.RDERetrieveAgent;
import com.leidos.ode.core.rde.model.RDEData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RetrieveDataController   {

    private DistributeDataController distributeDataController;
    private RDERetrieveAgent rdeRetrieveAgent;

    public RetrieveDataController() {

    }

    @RequestMapping(value = "retrieve", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity retrieveData(@RequestBody RDEData rdeData) {
        return null;
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
