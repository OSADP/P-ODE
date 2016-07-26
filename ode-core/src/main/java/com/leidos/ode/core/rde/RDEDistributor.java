package com.leidos.ode.core.rde;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.agent.parser.impl.ODEJ2735DataParser;
import com.leidos.ode.core.distribute.DataDistributor;
import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.PodeSource;
import com.leidos.ode.data.Position3D;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.*;

/*import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.agent.parser.impl.ODEJ2735DataParser; */

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
    int current = 0;
    private final List<Message> messages = new ArrayList<Message>();
    private DatagramSocket sock;

    @Value("${ode.rdeuploader.port}")
    private int DATA_UPLOADER_PORT;
    

    public RDEDistributor(String topicHostURL, int topicHostPort,
                          String connFactName, String topicName){
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
        try {
            sock = new DatagramSocket();
        } catch (SocketException e) {
            logger.error("Unable to bind socket for RDE distributor.");
            throw new DistributeException();
        }
    }
    
    @Override
    protected void sendData(Message rawMessage) throws DistributeException {
        // Generate the datum object
        logger.info("Received message " + rawMessage +  "at RDE Distributor.");
        String json = generateJson(rawMessage);
        logger.info("Built JSON for message: " + json);

        byte[] buf = json.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                new InetSocketAddress("localhost", DATA_UPLOADER_PORT));

        try {
            sock.send(packet);
        } catch (IOException e) {
            logger.error("Unable to send packet to data uploader!");
            throw new DistributeException();
        }
        logger.info("Message placed on RDEUploader queue");
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
            if(pos != null && pos.getLat() != null && pos.getLon() != null){
                generator.writeStringField("latitude", pos.getLat().getValue()+"");
                generator.writeStringField("longitude", pos.getLon().getValue()+"");
                if(pos.getElevation() != null){
                    generator.writeStringField("elevation", pos.getElevation().toString());
                }
            }else{
                logger.debug("No position information available.");
            }
            generator.writeStringField("date", timestamp);
            generator.writeStringField("type", messageType);
            generator.writeStringField("value",getHexForByteArray(encoded));
            generator.writeEndObject();

            // Close it all out
            generator.close();
            stringWriter.close();
            msg.reset();
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

    protected double getLatitudeValue(int lat){
        double base = 10000000.0;
        
        return lat/base;
    }
    
    protected double getLongitudeValue(int lon){
        double base = 10000000.0;
        return lon/base;
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
        }else if (data.getPodeData().getPodeData().isIncidentSelected()) {
            return data.getPodeData().getPodeData().getIncident().getLocation().getGeoLocation();
        } else {
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
        Calendar convCal = new GregorianCalendar();
        convCal.set(time.getYear().getValue(), time.getMonth().getValue()-1, time.getDay().getValue(),
                    time.getHour().getValue(), time.getMinute().getValue(), time.getSecond().getValue());

        return Long.toString(convCal.getTime().getTime());
    }

    /**
     * Take in an {@link com.leidos.ode.agent.data.ODEAgentMessage} object and return the string representation of the
     * message type (BSM, VSDM, SPaT, MAP, etc).
     * @param message An ODEAgentMessage parsed with {@link com.leidos.ode.agent.parser.impl.ODEJ2735DataParser}
     * @return A string representation of the message type
     * @throws DistributeException if the parse originally failed and thus the message is null.
     */
    private String getMessageType(ODEAgentMessage message) throws DistributeException {
        /**
         * NOTE:
         *
         * This is tightly coupled to the title of the subscription topic as defined in the context file. This assumes
         * that the Topic name being subscribed to represents a valid PodeDataType entry. This is necessary so that the
         * RDE query can operate correctly when requesting data of a certain type. If the topic names must be changed
         * the messages will be archived on the RDE under the new topic name and will not be able to be found in the
         * query results. To correct that the switch statement in ReplayDataDistributor#formatDataType must be changed to
         * match the new topic name for each type.
         *
         * - KR
         */
        return getTopicName().split("Topic")[0].toLowerCase();
    }
}
