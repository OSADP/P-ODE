package com.leidos.ode.agent.parser.helper;

import com.leidos.ode.agent.data.blufax.BluFaxLinkData;
import com.leidos.ode.agent.data.blufax.BluFaxRouteData;
import org.apache.log4j.Logger;
import org.tmdd._3.messages.LinkStatusMsg;
import org.tmdd._3.messages.RouteStatusMsg;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/17/14
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class BluFaxParserHelper extends ODEParserHelper {

    private static final String TAG = "BluFaxParserHelper";
    private static final String BLUFAX_LINK_TAG = "tmdd:linkStatusMsg";
    private static final String BLUFAX_ROUTE_TAG = "tmdd:routeStatusMsg";
    private static Logger logger = Logger.getLogger(TAG);
    private static BluFaxParserHelper instance;

    public static BluFaxParserHelper getInstance() {
        if (instance == null) {
            instance = new BluFaxParserHelper();
        }
        return instance;
    }

    @Override
    public ODEHelperResponse parseData(byte[] bytes) {
        return parseDocumentByTag(bytes);
    }

    @Override
    protected ODEHelperResponse parseDocumentByTag(String tag, byte[] bytes) {
        if (tag.equalsIgnoreCase(BLUFAX_LINK_TAG)) {
            logger.debug("Parsing BluFax Link Status Message.");
            return parseBluFaxLinkStatusMessage(bytes);
        } else if (tag.equalsIgnoreCase(BLUFAX_ROUTE_TAG)) {
            logger.debug("Parsing BluFax Route Status Message.");
            return parseBluFaxRouteStatusMessage(bytes);
        } else {
            logger.debug("Unknown data type for tag: " + tag);
            return new ODEHelperResponse(null, HelperReport.DATA_TYPE_UNKNOWN);
        }
    }

    private Object unmarshalBytes(byte[] bytes, Class[] objectFactoryForContext) {
        if (bytes != null && objectFactoryForContext != null) {
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                JAXBContext jaxbContext = JAXBContext.newInstance(objectFactoryForContext);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                return jaxbUnmarshaller.unmarshal(byteArrayInputStream);
            } catch (JAXBException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        return null;
    }

    private ODEHelperResponse parseBluFaxLinkStatusMessage(byte[] bytes) {
        LinkStatusMsg linkStatusMsg = (LinkStatusMsg) unmarshalBytes(bytes, new Class[]{org.tmdd._3.messages.ObjectFactory.class});
        if (linkStatusMsg != null) {
            BluFaxLinkData bluFaxLinkData = new BluFaxLinkData();
            bluFaxLinkData.setLinkStatusMsg(linkStatusMsg);
            return new ODEHelperResponse(bluFaxLinkData, HelperReport.PARSE_SUCCESS);
        }
        return new ODEHelperResponse(null, HelperReport.PARSE_ERROR);
    }

    private ODEHelperResponse parseBluFaxRouteStatusMessage(byte[] bytes) {
        RouteStatusMsg routeStatusMsg = (RouteStatusMsg) unmarshalBytes(bytes, new Class[]{org.tmdd._3.messages.ObjectFactory.class,com.fastlanesw.bfw.ObjectFactory.class});
        if (routeStatusMsg != null) {
            BluFaxRouteData bluFaxRouteData = new BluFaxRouteData();
            bluFaxRouteData.setRouteStatusMsg(routeStatusMsg);
            return new ODEHelperResponse(bluFaxRouteData, HelperReport.PARSE_SUCCESS);
        }
        return new ODEHelperResponse(null, HelperReport.PARSE_ERROR);
    }
}
