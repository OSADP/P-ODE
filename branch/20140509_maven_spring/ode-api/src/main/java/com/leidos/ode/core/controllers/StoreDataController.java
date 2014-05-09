package com.leidos.ode.core.controllers;

import com.leidos.ode.core.dao.RDEArchiveDAO;
import com.leidos.ode.core.rde.agent.RDEStoreAgent;
import com.leidos.ode.core.rde.model.RDEArchiveInfo;
import com.leidos.ode.core.rde.model.RDEData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StoreDataController {

    private RDEStoreAgent rdeStoreAgent;
    private RDEArchiveDAO rdeArchiveDAO;

    public StoreDataController() {

    }

    @RequestMapping(value = "store", method = RequestMethod.GET)
    public ResponseEntity storeData() {
        getRdeArchiveDAO().storeRDEArchiveInfo(new RDEArchiveInfo());
        return new ResponseEntity(HttpStatus.OK);
    }

    public RDEStoreAgent getRdeStoreAgent() {
        return rdeStoreAgent;
    }

    public void setRdeStoreAgent(RDEStoreAgent rdeStoreAgent) {
        this.rdeStoreAgent = rdeStoreAgent;
    }

    public RDEArchiveDAO getRdeArchiveDAO() {
        return rdeArchiveDAO;
    }

    public void setRdeArchiveDAO(RDEArchiveDAO rdeArchiveDAO) {
        this.rdeArchiveDAO = rdeArchiveDAO;
    }

}
