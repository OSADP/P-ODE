package com.leidos.ode.core.rde.factory;

import com.leidos.ode.core.rde.request.RDERequest;
import com.leidos.ode.core.rde.request.impl.RDERetrieveRequest;
import com.leidos.ode.core.rde.request.impl.RDEStoreRequest;
import com.leidos.ode.core.rde.request.model.RDEData;
import com.leidos.ode.core.rde.request.model.RDERetrieveData;
import com.leidos.ode.core.rde.request.model.RDEStoreData;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/21/14
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RDERequestFactory {

    public static RDERequest storeRequest(RDEData rdeData) {
        if (rdeData instanceof RDEStoreData) {
            return new RDEStoreRequest(rdeData);
        } else {
            return null;
        }
    }

    public static RDERequest retrieveRequest(RDEData rdeData) {
        if (rdeData instanceof RDERetrieveData) {
            return new RDERetrieveRequest(rdeData);
        } else {
            return null;
        }
    }
}
