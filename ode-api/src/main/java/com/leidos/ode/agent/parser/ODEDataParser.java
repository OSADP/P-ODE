package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.data.PodeDataDelivery;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class ODEDataParser {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);

    protected abstract ODEDataParserResponse parse(byte[] bytes);

    public final ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        getLogger().debug("Parsing data.");
        ODEDataParserResponse response = parse(bytes);
        getLogger().debug("Parse response: " + response.getReportCode());
        Object data = response.getData();

        return new ODEAgentMessage().setFormattedMessage(data).setMessagePayload(bytes);
    }

    protected final Document getMessageDocument(byte[] bytes) {
        if (bytes != null) {
            String messageString = new String(bytes);
            return Jsoup.parse(messageString);
        }
        return null;
    }

    protected final Logger getLogger() {
        return logger;
    }

    public enum ODEDataParserReportCode {
        PARSE_SUCCESS, PARSE_ERROR, DATA_TYPE_UNKNOWN, NO_DATA, UNEXPECTED_DATA_FORMAT, DATA_SOURCE_SERVER_ERROR, UNKNOWN_ERROR
    }

    public final class ODEDataParserResponse {

        private Object data;
        private List<PodeDataDelivery> podeMessages;
        private ODEDataParserReportCode reportCode;

        public ODEDataParserResponse(Object data, ODEDataParserReportCode reportCode) {
            this.data = data;
            this.reportCode = reportCode;
        }
        
       

        public Object getData() {
            return data;
        }

        public ODEDataParserReportCode getReportCode() {
            return reportCode;
        }

        /**
         * @return the podeMessages
         */
        public List<PodeDataDelivery> getPodeMessages() {
            return podeMessages;
        }

        /**
         * @param podeMessages the podeMessages to set
         */
        public void setPodeMessages(List<PodeDataDelivery> podeMessages) {
            this.podeMessages = podeMessages;
        }
    }

    public final class ODEParseException extends Exception {

        private static final long serialVersionUID = 1L;

        public ODEParseException() {
            super();
        }

        public ODEParseException(String message) {
            super(message);
        }

        public ODEParseException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }
}
