package com.leidos.ode.core.rde.request.impl;

import com.leidos.ode.core.rde.request.BasicRDERequest;
import com.leidos.ode.core.rde.request.RDERequest;
import com.leidos.ode.core.rde.request.model.RDEData;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/21/14
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class RDERetrieveRequest extends BasicRDERequest implements RDERequest {

    public RDERetrieveRequest(RDEData rdeData) {
        super(rdeData);
    }

    @Override
    public Object request() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
