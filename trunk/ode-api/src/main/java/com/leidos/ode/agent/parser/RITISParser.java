package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.ritis.RITISData;
import com.leidos.ode.agent.parser.jsoup.RITISJsoup;

import java.util.List;

public class RITISParser extends ODEDataParser {

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        getLogger().debug("Parsing RITIS data.");
        List<RITISData> ritisData = RITISJsoup.parseRITISData(bytes);
        getLogger().debug("Successfully parsed RITIS data.");

        return new ODEAgentMessage().setFormattedMessage(ritisData).setMessagePayload(bytes);
    }
}