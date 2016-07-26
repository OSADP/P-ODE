package com.leidos.ode.rdequery;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.leidos.ode.core.rde.ReplayDataDistributor;
import com.leidos.ode.data.PodeDataDelivery;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dot.rdeapi.client.websocket.sockjs.PodeQueryResult;

import java.io.*;

/**
 * Extension of ReplayDataDistributor for the purpose of the query tool.
 *
 * Dumps all data to a file at the same time in contrast to standard behavior.
 */
public class QueryDataDistributor extends ReplayDataDistributor {

    private Logger log = LogManager.getLogger(QueryDataDistributor.class);

    private String query;
    private JsonGenerator out;
    private StringWriter writer;
    private String outputFileLocation = "queryresults.json";
    private int count = 0;

    public QueryDataDistributor(String query) {
        super("QUERY:" + query.hashCode());
        this.query = query;

        JsonFactory factory = new JsonFactory();
        writer = new StringWriter();
        try {
            out = factory.createGenerator(writer);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.addMixIn(Object.class, RemovePreparedDataMixin.class);
            out.setCodec(mapper);
            out.writeStartArray();
        } catch (IOException e) {
            log.error("Unable to create JSON generator.");
            System.exit(1);
        }
    }

    @Override
    protected void delayMessage(PodeQueryResult result, Long prevDate, Long curDate) {
        // No delay for query data
    }

    @Override
    public void sendMessage(PodeQueryResult message) {
        log.info("Recieved query result: " + message.toString());
        try {
            out.writeStartObject();
                out.writeStringField("type", message.getType());
                out.writeNumberField("date", message.getDate());
                out.writeNumberField("latitude", message.getLatitude());
                out.writeNumberField("longitude", message.getLongitude());
                out.writeStringField("elevation", message.getElevation());
                out.writeStringField("value", message.getValue());

                PodeDataDelivery delivery = PodeParser.parsePodeDataDelivery(message.getValue());
                if (delivery != null) {
                    out.writeObjectField("parsedValue", delivery);
                }
            out.writeEndObject();

            count++;
        } catch (IOException e) {
            log.error("Unable to write JSON results.");
        }
    }

    @Override
    public void cleanupConnection() {
        try {
            out.writeEndArray();
        } catch (IOException e) {
            log.error("Unable to write JSON results.");
        }

        File f = new File(outputFileLocation);
        try (FileOutputStream fos = new FileOutputStream(f)) {
            log.info("Writing [" + count + "] query results to file [" + outputFileLocation + "]");
            fos.write(writer.toString().getBytes());
            log.info("Write successful!");
        } catch (FileNotFoundException e) {
            log.error("Write unsuccessful!");
        } catch (IOException e) {
            log.error("Write unsuccessfull!");
        }

    }

    @Override
    protected String formatQueryRequest() {
        return query;
    }
}
