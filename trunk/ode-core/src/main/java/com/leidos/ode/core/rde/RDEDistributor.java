package com.leidos.ode.core.rde;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
/*import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.agent.parser.impl.ODEJ2735DataParser; */
import com.leidos.ode.agent.ODEAgent;
import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.agent.parser.impl.ODEJ2735DataParser;
import com.leidos.ode.core.distribute.DataDistributor;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.Position3D;
import com.leidos.ode.util.ODEMessageType;
import org.dot.rdelive.api.config.CharsetType;
import org.dot.rdelive.api.config.DataType;
import org.dot.rdelive.impl.GenericDatum;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

/**
 * Class representing the Store Data controller. Responsible for sending published data received by the ODE to the RDE,
 * and recording that transfer to the Pub/Sub registration. Contains an RDE Interface sub-component, which is responsible
 * for formatting the data feed to the RDE as well as the metadata document that will accompany the data feed. Once data
 * is transferred to the RDE, Store Data will use the Pub/Sub registration interface sub-component to the log the data
 * transfer so it can be discovered later for playback.
 *
 * @author lamde
 */

public class RDEDistributor extends DataDistributor {

    private final String TAG = getClass().getSimpleName();
    private RDEDataWriter writer;
    
    public RDEDistributor(){
        
    }

    @Override
    protected void cleanup() {
        // Empty method because theres nothing to cleanup here.
    }

    @Override
    protected void connectTarget() throws DistributeException {
        // Empty method because we don't connect here.
    }

    @Override
    protected void sendData(Message rawMessage) throws DistributeException {
        // Generate the datum object
        GenericDatum<char[]> datum = new GenericDatum<char[]>();

        // Copy the above generated values into the Datum
        datum.setData(generateJson(rawMessage).toCharArray());
        datum.setDataType(DataType.CHARACTER);
        datum.setEncoding(CharsetType.UTF8);

        // Send it off to the RDE
        try {
            writer.send(datum);
        } catch (InterruptedException e) {
            throw new DistributeException("Interrupted while waiting on writer queue.");
        }
    }

    private String generateJson(Message rawMessage) throws DistributeException {
        String json = "";

        // Open the generators and writers
        JsonFactory jsonFactory = new JsonFactory();
        Writer stringWriter = new StringWriter();

        try {
            // Get the bytes of our rawMessage and write that to the JSON as a char[]
            ODEJ2735DataParser parser = new ODEJ2735DataParser();
            BytesMessage msg = (BytesMessage) rawMessage;
            byte[] encoded = new byte[(int) msg.getBodyLength()];
            msg.readBytes(encoded);

            ODEAgentMessage parsed = parser.parseMessage(encoded);

            // Parse RDE metadata
            Position3D pos = getPosition(parsed);
            String timestamp = getTimestamp(parsed);
            String messageType = getMessageType(parsed);

            // Start writing the JSON string
            JsonGenerator generator = jsonFactory.createGenerator(stringWriter);

            generator.writeStartObject();
            generator.writeObjectFieldStart(messageType);
            generator.writeStringField("latitude", pos.getLat().toString());
            generator.writeStringField("longitude", pos.getLon().toString());
            generator.writeStringField("elevation", pos.getElevation().toString());
            generator.writeStringField("date", timestamp);
            generator.writeFieldName("value");
            generator.writeBinary(encoded);
            generator.writeEndObject();

            // Close it all out
            generator.close();
            stringWriter.close();
            json = stringWriter.toString();
        } catch (IOException e) {
            throw new DistributeException("Unable to create create RDE Datum JSON structure.");
        } catch (JMSException e) {
            throw new DistributeException("Unable to parse data from JMS Message for RDE.");
        } catch (ODEDataParser.ODEParseException e) {
            throw new DistributeException("ODEDataParser error occurred while trying to parse RDE data.");
        }

        return json;
    }

    /**
     * Take in an {@link com.leidos.ode.agent.data.ODEAgentMessage} object and return the {@link com.leidos.ode.data.Position3D}
     * associated with its detector.
     * @param message An ODEAgentMessage parsed with {@link com.leidos.ode.agent.parser.impl.ODEJ2735DataParser}
     * @return the Position3D object of the {@link com.leidos.ode.data.PodeDetectorData} for the message
     * @throws DistributeException if the parse originally failed and thus the message is null.
     */
    private Position3D getPosition(ODEAgentMessage message) throws DistributeException{
        if (message.getFormattedMessage() == null) {
            throw new DistributeException();
        }

        PodeDataDelivery data = (PodeDataDelivery) message.getFormattedMessage();
        return data.getPodeData().getPodeData().getDetector().getPosition();
    }

    /**
     * Take in an {@link com.leidos.ode.agent.data.ODEAgentMessage} object and return the J2735 timestamp string associated
     * with it.
     * @param message An ODEAgentMessage parsed with {@link com.leidos.ode.agent.parser.impl.ODEJ2735DataParser}
     * @return The J2735 timestamp for the {@link com.leidos.ode.data.PodeDataDelivery} as a String
     * @throws DistributeException if the parse originally failed and thus the message is null.
     */
    private String getTimestamp(ODEAgentMessage message) throws DistributeException {
        if (message.getFormattedMessage() == null) {
            throw new DistributeException();
        }
        PodeDataDelivery data = (PodeDataDelivery) message.getFormattedMessage();
        return data.getPodeData().getLastupdatetime().toString();
    }

    /**
     * Take in an {@link com.leidos.ode.agent.data.ODEAgentMessage} object and return the string representation of the
     * message type (BSM, VSDM, SPaT, MAP, etc).
     * @param message An ODEAgentMessage parsed with {@link com.leidos.ode.agent.parser.impl.ODEJ2735DataParser}
     * @return A string representation of the message type
     * @throws DistributeException if the parse originally failed and thus the message is null.
     */
    private String getMessageType(ODEAgentMessage message) throws DistributeException {
        if (message.getFormattedMessage() == null) {
            throw new DistributeException();
        }
        PodeDataDelivery data = (PodeDataDelivery) message.getFormattedMessage();
        return data.getPodeData().getSource().toString();
    }
}
