package com.leidos.ode.core.controllers;

import com.leidos.ode.agent.data.ODEAgentMessage;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

@Controller
public abstract class PublishDataController {

    private String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private Topic topic;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private String hostAddress;
    private int hostPort;
    private String connectionFactoryName;
    private String topicName;

    protected final String publish(ODEAgentMessage odeAgentMessage) {
        try {
            if (messageProducer == null) {
                initMessageProducer();
            }
            sendMessage(odeAgentMessage);
        } catch (JMSException e) {
            logger.error("Error connecting to Topic", e);
        }
        return "OK";
    }

    @PostConstruct
    private void initTopicConnection() {
        try {
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
            topic = (Topic) ctx.lookup(getTopicName());

            logger.info("Getting connection");
            connection = cf.createConnection();


        } catch (JMSException e) {
            // TODO Auto-generated catch block
            logger.error("Error connecting to Topic", e);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            logger.error("Error connecting to Topic", e);
        }

    }

    private void sendMessage(ODEAgentMessage odeAgentMessage) {
        try {
            //puts the message on the topic.
            logger.info("Preparing message for topic");
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(odeAgentMessage);
            logger.info("Placing message on topic");
            messageProducer.send(msg);

        } catch (JMSException ex) {
            logger.error("Error creating message.", ex);
        } catch (Exception e) {
            logger.error("Error creating message.", e);
        }
    }

    private void initMessageProducer() throws JMSException {
        logger.info("Getting session");
        session = (connection.createSession(false, Session.AUTO_ACKNOWLEDGE));
        logger.info("Getting producer");
        messageProducer = session.createProducer(topic);
    }

    private String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    private int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    private String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    private String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

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
    }
}
