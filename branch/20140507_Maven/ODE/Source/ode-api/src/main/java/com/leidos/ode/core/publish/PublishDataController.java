package com.leidos.ode.core.publish;

import com.leidos.ode.core.distribute.DistributeDataController;
import com.leidos.ode.core.rde.model.RDEData;
import com.leidos.ode.core.storedata.StoreDataController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Publish data listens to the JMS queues for data.
 * Once data is pulled from it queue it is routed to Distribute data
 * and store data.
 * @author cassadyja
 *
 */
@Controller
public class PublishDataController   {

    private StoreDataController storeDataController;
    private DistributeDataController distributeDataController;

    public PublishDataController() {
        super();
    }

    @RequestMapping(value = "publish", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity publishData(@RequestBody RDEData rdeData) {
        return null;
    }

    public StoreDataController getStoreDataController() {
        return storeDataController;
    }

    public void setStoreDataController(StoreDataController storeDataController) {
        this.storeDataController = storeDataController;
    }

    public DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }

    public void setDistributeDataController(DistributeDataController distributeDataController) {
        this.distributeDataController = distributeDataController;
    }
}
