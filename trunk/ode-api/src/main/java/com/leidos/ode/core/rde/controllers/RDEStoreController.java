package com.leidos.ode.core.rde.agent;

import com.leidos.ode.core.rde.model.RDEStoreResponse;
import com.leidos.ode.core.rde.model.RDEData;
import com.leidos.ode.core.rde.model.RDEStoreException;

public interface RDEStoreAgent {

    public RDEStoreResponse store(RDEData dataBean) throws RDEStoreException;

}
