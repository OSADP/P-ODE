package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.ODECollectedData;
import com.leidos.ode.agent.parser.helper.ODEParserHelper;
import com.leidos.ode.agent.parser.helper.VDOTParserHelper;

public class VDOTParser extends ODEDataParser {

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        getLogger().debug("Parsing VDOT data.");
        ODEParserHelper.ODEHelperResponse response = VDOTParserHelper.getInstance().parseData(bytes);
        getLogger().debug("Parse response: " + response.getReport());
        ODECollectedData data = response.getData();

        return new ODEAgentMessage().setFormattedMessage(data).setMessagePayload(bytes);
    }
}
