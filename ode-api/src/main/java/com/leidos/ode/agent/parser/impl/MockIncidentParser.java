package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.parser.ODEDataParser;

/**
 * Created by rushk1 on 5/19/2016.
 */
public class MockIncidentParser extends ODEDataParser {

    @Override
    protected ODEDataParserResponse parse(byte[] bytes) {
        return new ODEDataParserResponse(bytes, ODEDataParserReportCode.PARSE_SUCCESS);
    }
}
