package com.leidos.ode.core.distribute;

import com.leidos.ode.core.rde.model.RDEData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DistributeDataController  {

    public DistributeDataController() {
        super();
    }

    @RequestMapping(value = "distribute", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity distributeData(@RequestBody RDEData rdeData) {
        return null;
    }
}