package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.vdot.VDOTData;
import com.leidos.ode.agent.parser.jsoup.ODEJsoup;
import com.leidos.ode.agent.parser.jsoup.VDOTJsoup;

import java.util.List;

public class VDOTParser extends ODEDataParser {

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        getLogger().debug("Parsing VDOT data.");
        ODEJsoup.ODEJsoupResponse<VDOTData> response = VDOTJsoup.getInstance().parseData(bytes);
        getLogger().debug("Parse response: " + response.getJsoupReport());
        List<VDOTData> vdotData = response.getData();

        return new ODEAgentMessage().setFormattedMessage(vdotData).setMessagePayload(bytes);
    }
}
