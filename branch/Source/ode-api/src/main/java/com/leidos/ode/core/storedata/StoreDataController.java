package com.leidos.ode.core.storedata;

import com.leidos.ode.core.rde.agent.RDEStoreAgent;
import com.leidos.ode.core.rde.model.RDEData;
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

    public StoreDataController() {
        super();
    }

    @RequestMapping(value = "store", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity storeData(@RequestBody RDEData rdeData) {
        store(rdeData);
        return new ResponseEntity(HttpStatus.OK);
    }

    private void store(RDEData rdeData) {
        //Store data in the RDE, and record transfer to the pub/sub registration.

    }

    public RDEStoreAgent getRdeStoreAgent() {
        return rdeStoreAgent;
    }

    public void setRdePublishAgent(RDEStoreAgent rdeStoreAgent) {
        this.rdeStoreAgent = rdeStoreAgent;
    }

}
