package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import net.sourceforge.exist.ns.exist.ObjectFactory;
import net.sourceforge.exist.ns.exist.Result;

public class ODEVDOTParser implements ODEDataParser {

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        ObjectFactory objectFactory = new ObjectFactory();
        Result result = objectFactory.createResult();
        Result.Collection collection = objectFactory.createResultCollection();
        result.setCollection(collection);
        Result.Collection.Resource resource = objectFactory.createResultCollectionResource();

        return null;
    }

}
