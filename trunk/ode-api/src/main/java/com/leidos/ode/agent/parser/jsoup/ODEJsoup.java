package com.leidos.ode.agent.parser.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ODEJsoup<T> {

    public abstract ODEJsoupResponse<T> parseData(byte[] bytes);

    protected final Document getMessageDocument(byte[] bytes) {
        if (bytes != null) {
            String messageString = new String(bytes);
            return Jsoup.parse(messageString);
        }
        return null;
    }

    protected final ODEJsoupResponse<T> buildJsoupResponseFromParsedData(List<T> data) {
        if (data != null && !data.isEmpty()) {
            return new ODEJsoupResponse<T>(data, JsoupReport.PARSE_SUCCESS);
        } else {
            return new ODEJsoupResponse<T>(data, JsoupReport.NO_RESULTS);
        }
    }

    public enum JsoupReport {
        PARSE_SUCCESS, DOCUMENT_PARSE_ERROR, NO_RESULTS, RESULTS_PARSE_ERROR, UNKNOWN
    }

    public static class ODEJsoupResponse<T> {

        private List<T> data;
        private JsoupReport jsoupReport;

        public ODEJsoupResponse(List<T> data, JsoupReport jsoupReport) {
            this.data = data;
            this.jsoupReport = jsoupReport;
        }

        public List<T> getData() {
            return data;
        }

        public JsoupReport getJsoupReport() {
            return jsoupReport;
        }
    }
}