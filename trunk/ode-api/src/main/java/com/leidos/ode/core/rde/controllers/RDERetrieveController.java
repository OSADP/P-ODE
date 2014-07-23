package com.leidos.ode.core.rde.agent;

import com.leidos.ode.core.rde.model.RDEData;
import com.leidos.ode.core.rde.model.RDERetrieveResponse;
import com.leidos.ode.core.rde.model.RDERetrieveException;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 5/6/14
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RDERetrieveAgent {

    public RDERetrieveResponse retrieve(RDEData rdeData) throws RDERetrieveException;

}
