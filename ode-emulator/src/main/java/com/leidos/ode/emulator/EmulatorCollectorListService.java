/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator;

import javax.ws.rs.Produces;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author cassadyja
 */
@Controller

public class EmulatorCollectorListService {
    
    private EmulatorCollectorList collectorList = new EmulatorCollectorList();
    
    @RequestMapping(value = "getCollectorList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EmulatorCollectorList getCollectorList(){
        return collectorList;
    }
}
