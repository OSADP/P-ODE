package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.parser.helper.BluFaxParserHelper;
import com.leidos.ode.agent.parser.helper.ODEParserHelper;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/17/14
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class BluFaxParser extends ODEDataParser {

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        getLogger().debug("Parsing BluFax data.");
        ODEParserHelper.ODEHelperResponse response = BluFaxParserHelper.getInstance().parseData(bytes);
        getLogger().debug("Parse response: " + response.getReport());
        Object data = response.getData();

        return new ODEAgentMessage().setFormattedMessage(data).setMessagePayload(bytes);
    }
}
