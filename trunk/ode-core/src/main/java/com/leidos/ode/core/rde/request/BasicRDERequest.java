package com.leidos.ode.core.rde.request;

import com.leidos.ode.core.rde.request.model.RDEData;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/21/14
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicRDERequest {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private RDEData rdeData;

    public BasicRDERequest(RDEData rdeData) {
        this.rdeData = rdeData;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected RDEData getRdeData() {
        return rdeData;
    }
}
