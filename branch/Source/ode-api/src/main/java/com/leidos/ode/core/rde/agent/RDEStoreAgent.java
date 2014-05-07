package com.leidos.ode.core.rde.agent;

import com.leidos.ode.core.rde.model.RDEStoreRequest;
import com.leidos.ode.core.rde.model.RDEStoreResponse;
import com.leidos.ode.core.rde.model.RDEData;
import com.leidos.ode.core.storedata.RDEStoreException;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 4/21/14
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RDEStoreAgent {

    public RDEStoreResponse store(RDEData dataBean) throws RDEStoreException;

}
