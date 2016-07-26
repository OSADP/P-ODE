package com.leidos.ode.core.controllers;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.PodeDataDistribution;
import com.leidos.ode.logging.ODELogger;
import com.leidos.ode.util.ASNObjectUtils;
import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.logging.Level;


public abstract class PublishDataController {

    private String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private Session session;
    private MessageProducer messageProducer;
//    private String hostAddress;
//    private int hostPort;
//    private String connectionFactoryName;
    @Value("${leidos.ode.publisher.hostaddress}")
    private  String hostAddress;
    @Value("${leidos.ode.publisher.hostport}")
    private int hostPort;
    @Value("${leidos.ode.publisher.connectionfactoryname}")
    private String connectionFactoryName;

    protected abstract String publishData(ODEAgentMessage odeAgentMessage);

    protected final String publish(ODEAgentMessage odeAgentMessage) {
        initTopicConnection();
        String system = getSystemFromMessage(odeAgentMessage);
        
        startLogEntry(ODELogger.ODEStage.RECEIVED, odeAgentMessage.getMessageId(),system);
        finishLogEntry();
        startLogEntry(ODELogger.ODEStage.DISTRIBUTED, odeAgentMessage.getMessageId(),system);
        
        sendMessage(odeAgentMessage);
        
        finishLogEntry();
        return "OK";
    }
    
    private void startLogEntry(ODELogger.ODEStage stage, String message, String system){
        try {
            getOdeLogger().start(stage, message, system);
        } catch (ODELogger.ODELoggerException ex) {
            logger.warn("Error starting log event",ex);
        }
        
    }

    
    private void finishLogEntry(){
        try {
            getOdeLogger().finish();
        } catch (ODELogger.ODELoggerException ex) {
            logger.warn("Error finishing log entry",ex);
        }
    }
    
    private String getSystemFromMessage(ODEAgentMessage message){
        try {
            PodeDataDelivery pdd = convertDataDistribution(message.getMessagePayload());
            return ASNObjectUtils.getSourceName(pdd.getPodeData().getSource().getValue());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PublishDataController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    
    
    protected void initTopicConnection() {
        if (messageProducer == null) {
            try {
                System.out.println("Looking up topic on host: "+getHostAddress());
                Properties env = new Properties();
                env.put(Context.SECURITY_PRINCIPAL, "admin");
                env.put(Context.SECURITY_CREDENTIALS, "admin");
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.appserv.naming.S1ASCtxFactory");
                env.put(Context.PROVIDER_URL, "iiop://" + getHostAddress() + ":" + getHostPort());

                env.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
                env.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
                env.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
                env.setProperty("org.omg.CORBA.ORBInitialHost", getHostAddress());

                // optional. Defaults to 3700. Only needed if target orb port is not 3700.
                env.setProperty("org.omg.CORBA.ORBInitialPort", "3700");

                logger.info("Getting Context");
                InitialContext ctx = new InitialContext(env);

                logger.info("Looking up Connection Factory: " + getConnectionFactoryName());
                ConnectionFactory cf = (ConnectionFactory) ctx.lookup(getConnectionFactoryName());

                logger.info("Looking up Queue: " + getTopicName());
                Topic topic = (Topic) ctx.lookup(getTopicName());

                logger.info("Getting connection");
                Connection connection = cf.createConnection();

                logger.info("Getting session");
                session = (connection.createSession(false, Session.AUTO_ACKNOWLEDGE));

                logger.info("Getting producer");
                messageProducer = session.createProducer(topic);

            } catch (JMSException e) {
                logger.error("Error connecting to Topic", e);
            } catch (NamingException e) {
                logger.error("Error connecting to Topic", e);
            }
        }
    }

    private void sendMessage(ODEAgentMessage odeAgentMessage) {
        try {
            //puts the message on the topic.
            logger.info("Preparing message for topic");
            System.out.println("RAW Message Base 64: "+odeAgentMessage.getMessagePayloadBase64());
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.writeBytes(encodePodeMessage(convertDataDistribution(odeAgentMessage.getMessagePayload())));
            messageProducer.send(bytesMessage);

        } catch (JMSException ex) {
            logger.error("Error creating message.", ex);
        } catch (Exception e) {
            logger.error("Error creating message.", e);
        }
    }

    private byte[] encodePodeMessage(PodeDataDelivery data) throws Exception{
        IEncoder encoder = CoderFactory.getInstance().newEncoder("BER");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        encoder.encode(data, bos);
        return bos.toByteArray();
    }
    
    
    private PodeDataDelivery convertDataDistribution(byte[] bytes) throws Exception{
        PodeDataDelivery dataDelivery = new PodeDataDelivery();
        IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        PodeDataDistribution message = decoder.decode(bis, PodeDataDistribution.class);
        dataDelivery.setPodeData(message.getPodeData());
        dataDelivery.setMessageID(message.getMessageID());
        
        return dataDelivery;
    }
    
    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    public abstract String getTopicName() ;

    /**
     * @return the odeLogger
     */
    public abstract ODELogger getOdeLogger();




    /**
     * Constants for Publish Controller endpoints.
     */
    public final class PublishEndpoints {
        public static final String BSM = "publishBSM";
        public static final String VDOT_WEATHER = "publishVDOTWeather";
        public static final String VDOT_TRAVEL_TIME = "publishVDOTTravelTime";
        public static final String VDOT_SPD_VOL_OCC = "publishVDOTSpdVolOcc";
        public static final String RITIS_SPD_VOL_OCC = "publishRITISSpdVolOcc";
        public static final String RITIS_WEATHER = "publishRITISWeather";
        public static final String BLUFAX_LINK = "publishBluFaxLink";
        public static final String BLUFAX_ROUTE = "publishBluFaxRoute";
        public static final String WEATHER = "publishWeather";
        public static final String SPEED = "publishSpeed";
        public static final String VOLUME = "publishVolume";
        public static final String OCCUPANCY = "publishOccupancy";
        public static final String TRAVEL_TIME = "publishTravelTime";
        public static final String INCIDENT = "publishIncident";
    }
}
