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
import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.PodeSource;
import com.leidos.ode.data.Position3D;
import com.leidos.ode.util.ODEMessageType;
import org.dot.rdelive.api.config.CharsetType;
import org.dot.rdelive.api.config.DataType;
import org.dot.rdelive.impl.GenericDatum;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import org.apache.log4j.Logger;

/**,
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
    private Logger logger = Logger.getLogger(TAG);    
    private RDEDataWriter writer;

    public RDEDistributor(RDEDataWriter writer, String topicHostURL, int topicHostPort,
                          String connFactName, String topicName){
        this.writer = writer;
        setTopicHostURL(topicHostURL);
        setTopicHostPort(topicHostPort);
        setTopicName(topicName);
        setConnFactName(connFactName);

        // Set us to essentially never expire.
        Date end = new Date();
        end.setTime(Long.MAX_VALUE);
        setSubscriptionEndDate(end);
    }

    @Override
    protected void cleanup() {
        setInterrupted(true);
    }

    @Override
    protected void connectTarget() throws DistributeException {
        // Empty method because we don't connect here.
    }

    @Override
    protected void sendData(Message rawMessage) throws DistributeException {
        // Generate the datum object
        logger.debug("Received message at RDE Distributor.");
        GenericDatum<char[]> datum = new GenericDatum<char[]>();

        // Copy the above generated values into the Datum
        datum.setData(generateJson(rawMessage).toCharArray());
        datum.setDataType(DataType.CHARACTER);
        datum.setEncoding(CharsetType.UTF8);

        // Send it off to the RDE
        try {
            logger.debug("Writing message...");
            writer.send(datum);
            logger.debug("Message Written to RDE.");
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
            if(pos != null && pos.getLat() != null && pos.getLon() != null){
                generator.writeStringField("latitude", pos.getLat().toString());
                generator.writeStringField("longitude", pos.getLon().toString());
                if(pos.getElevation() != null){
                    generator.writeStringField("elevation", pos.getElevation().toString());
                }
            }
            generator.writeStringField("date", timestamp);
            generator.writeStringField("value",getHexForByteArray(encoded));
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
    
    
    private String getHexForByteArray(byte[] bytes){
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        for(int i=0; i<bytes.length; i++) {
                pw.printf("%02X ", bytes[i]);
        }	        
        return sw.toString();
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
        if(data.getPodeData().getPodeData().isDetectorSelected()){
            return data.getPodeData().getPodeData().getDetector().getPosition();
        }else if(data.getPodeData().getPodeData().isWeatherSelected()){
            return data.getPodeData().getPodeData().getWeather().getPosition();
        }else {
            return null;
        }
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
        return convertDDateTime(data.getPodeData().getLastupdatetime());
    }

    /**
     * Converts a PodeSource enum into a string representation
     * @param enumType The PodeSource.EnumType
     * @return The string representation
     */
    private String getSourceName(PodeSource.EnumType enumType){
        if(enumType.equals(PodeSource.EnumType.blufax)){
            return "blufax";
        }else if(enumType.equals(PodeSource.EnumType.dms)){
            return "dms";
        }else if(enumType.equals(PodeSource.EnumType.ritis)){
            return "ritis";
        }else if(enumType.equals(PodeSource.EnumType.rtms)){
            return "rtms";
        }else if(enumType.equals(PodeSource.EnumType.spat)){
            return "spat";
        }else if(enumType.equals(PodeSource.EnumType.vdot)){
            return "vdot";
        }else if(enumType.equals(PodeSource.EnumType.wxde)){
            return "wxde";
        }
        return "unknown";
    }

    /**
     * Converts a DDateTime object into a string representation of the data contained within.
     * @param time
     * @return a string conversion in M/D/Y H:M:S format
     */
    private String convertDDateTime(DDateTime time) {
        StringBuilder out = new StringBuilder();
        out.append(time.getMonth().getValue());
        out.append("/");
        out.append(time.getDay().getValue());
        out.append("/");
        out.append(time.getYear().getValue());
        out.append(" ");

        out.append(time.getHour().getValue());
        out.append(":");
        out.append(time.getMinute().getValue());
        out.append(":");
        out.append(time.getSecond().getValue());

        return out.toString();
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
        return getSourceName(data.getPodeData().getSource().getValue());
    }
}
