package com.leidos.ode.agent.parser.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ODEParserHelper {

    public abstract ODEHelperResponse parseData(byte[] bytes);

    protected final Document getMessageDocument(byte[] bytes) {
        if (bytes != null) {
            String messageString = new String(bytes);
            return Jsoup.parse(messageString);
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