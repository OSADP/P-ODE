package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.data.blufax.BluFaxLinkData;
import com.leidos.ode.agent.data.blufax.BluFaxRouteData;
import com.leidos.ode.agent.parser.JAXBEnabledParser;
import org.tmdd._3.messages.LinkStatusMsg;
import org.tmdd._3.messages.RouteStatusMsg;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/17/14
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class BluFaxParser extends JAXBEnabledParser {

    private static final String BLUFAX_LINK_TAG = "tmdd:linkStatusMsg";
    private static final String BLUFAX_ROUTE_TAG = "tmdd:routeStatusMsg";

    @Override
    protected ODEDataParserResponse parseDocumentByTag(String tag, byte[] bytes) {
        if (tag.equalsIgnoreCase(BLUFAX_LINK_TAG)) {
            getLogger().debug("Parsing BluFax Link Status Message.");
            return parseBluFaxLinkStatusMessage(bytes);
        } else if (tag.equalsIgnoreCase(BLUFAX_ROUTE_TAG)) {
            getLogger().debug("Parsing BluFax Route Status Message.");
            return parseBluFaxRouteStatusMessage(bytes);
        } else {
            getLogger().debug("Unknown data type for tag: " + tag);
            return new ODEDataParserResponse(null, ODEDataParserReportCode.DATA_TYPE_UNKNOWN);
        }
    }

    private ODEDataParserResponse parseBluFaxLinkStatusMessage(byte[] bytes) {
        LinkStatusMsg linkStatusMsg = (LinkStatusMsg) unmarshalBytes(bytes, org.tmdd._3.messages.ObjectFactory.class);
        if (linkStatusMsg != null) {
            BluFaxLinkData bluFaxLinkData = new BluFaxLinkData();
            bluFaxLinkData.setLinkStatusMsg(linkStatusMsg);
            return new ODEDataParserResponse(bluFaxLinkData, ODEDataParserReportCode.PARSE_SUCCESS);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }

    private ODEDataParserResponse parseBluFaxRouteStatusMessage(byte[] bytes) {
        RouteStatusMsg routeStatusMsg = (RouteStatusMsg) unmarshalBytes(bytes, org.tmdd._3.messages.ObjectFactory.class);
        if (routeStatusMsg != null) {
            BluFaxRouteData bluFaxRouteData = new BluFaxRouteData();
            bluFaxRouteData.setRouteStatusMsg(routeStatusMsg);
            return new ODEDataParserResponse(bluFaxRouteData, ODEDataParserReportCode.PARSE_SUCCESS);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }
}
