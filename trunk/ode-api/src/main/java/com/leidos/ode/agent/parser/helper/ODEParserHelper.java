package com.leidos.ode.agent.parser.helper;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ODEParserHelper {

    private static Logger logger = Logger.getLogger("ODEParserHelper");

    public abstract ODEHelperResponse parseData(byte[] bytes);

    protected abstract ODEHelperResponse parseDocumentByTag(String tag, byte[] bytes);

    protected final Document getMessageDocument(byte[] bytes) {
        if (bytes != null) {
            String messageString = new String(bytes);
            return Jsoup.parse(messageString);
        }
        return null;
    }

    protected final ODEHelperResponse parseDocumentByTag(byte[] bytes) {
        Document document = getMessageDocument(bytes);
        if (document != null) {
            if (document != null) {
                Element headElement = document.head();
                Element bodyElement = document.body();
                if (bodyElement != null) {
                    Elements bodyChildren = bodyElement.children();
                    if (bodyChildren != null) {
                        Element element = bodyChildren.first();
                        if (element != null) {
                            String elementTag = element.tagName();
                            if (elementTag != null) {
                                return parseDocumentByTag(elementTag, bytes);
                            }
                        } else {
                            return new ODEHelperResponse(null, HelperReport.NO_DATA);
                        }
                    } else {
                        logger.debug("Unable to parse body elements. Body element has no children.");
                    }
                } else {
                    logger.debug("Unable to parse body element. Document body was null..");
                }
            }
            return new ODEHelperResponse(null, HelperReport.UNEXPECTED_DATA_FORMAT);
        }
        return null;
    }

    public enum HelperReport {
        PARSE_SUCCESS, PARSE_ERROR, DATA_TYPE_UNKNOWN, NO_DATA, UNEXPECTED_DATA_FORMAT, DATA_SOURCE_SERVER_ERROR, UNKNOWN_ERROR
    }

    public static class ODEHelperResponse {

        private Object data;
        private HelperReport report;

        public ODEHelperResponse(Object data, HelperReport report) {
            this.data = data;
            this.report = report;
        }

        public Object getData() {
            return data;
        }

        public HelperReport getReport() {
            return report;
        }
    }
}