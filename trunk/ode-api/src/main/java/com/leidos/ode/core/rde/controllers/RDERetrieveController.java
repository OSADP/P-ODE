package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDERetrieveException;
import com.leidos.ode.core.rde.data.RDERetrieveResponse;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 5/6/14
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RDERetrieveController {

    public RDERetrieveResponse retrieve(RDEData rdeData) throws RDERetrieveException;

}
