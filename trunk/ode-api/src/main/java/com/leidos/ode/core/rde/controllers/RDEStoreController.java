package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.data.RDEData;
import com.leidos.ode.core.rde.data.RDEStoreException;
import com.leidos.ode.core.rde.data.RDEStoreResponse;

public interface RDEStoreController {

    public RDEStoreResponse store(RDEData dataBean) throws RDEStoreException;

}
