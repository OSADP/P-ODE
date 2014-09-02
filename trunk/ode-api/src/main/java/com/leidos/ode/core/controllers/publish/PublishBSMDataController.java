package com.leidos.ode.core.controllers.publish;

import com.leidos.ode.core.controllers.*;
import com.leidos.ode.agent.data.ODEAgentMessage;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.leidos.ode.core.rde.controllers.RDEStoreController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class representing the Publish Data controller. Responsible for retrieving data from the Publisher ODE
 * Agents and verifying the data published is in accordance with publication registrations. Verified data
 * is sent to the Store Data component and made available to subscribers via the Distribute Data component.
 *
 * @author cassadyja, lamde
 *
 */
@Controller
public class PublishBSMDataController   {
    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    @Autowired
    private RDEStoreController storeDataController;
    @Autowired
    private DistributeDataController distributeDataController;
    private int i = 0;
    
    private Topic topic;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer = null;
    private String hostURL = "cassadyja2";
    private int hostPort = 7676;
    private String connectionFactName = "ODETopicConnFact";
    private String topicName = "BSMR1Topic";
    
    
    public PublishBSMDataController() {

    }

    @RequestMapping(value = "publishBSM", method = RequestMethod.POST)
    public @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
        logger.info("~~~~~~~Received message ."+ ++i);
        try {
            if(messageProducer == null){
                messageProducer = getMessageProducer();
            }
            sendMessage(odeAgentMessage);
        } catch (JMSException e) {
            logger.error("Error connecting to Topic",e);
        }
        
        return "OK";
    }

    private void sendMessage(ODEAgentMessage odeAgentMessage){
        try {
            //puts the message on the topic.
            logger.info("Preparing message for topic");
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(odeAgentMessage);
            logger.info("Placing message on topic");
            messageProducer.send(msg);
            
        } catch (JMSException ex) {
            logger.error("Error creating message.", ex);
        } catch(Exception e){
            logger.error("Error creating message.", e);
        }
        
        
    }
    
    private MessageProducer getMessageProducer() throws JMSException{
        logger.info("Getting session");
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        logger.info("getting producer");
        return session.createProducer(topic);
    }
    
    public RDEStoreController getStoreDataController() {
        return storeDataController;
    }

    public DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }
    
    @PostConstruct
    public void initTopicConnection(){
        try {
            Properties env = new Properties();
            env.put(Context.SECURITY_PRINCIPAL, "admin");  
            env.put(Context.SECURITY_CREDENTIALS, "admin");
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.appserv.naming.S1ASCtxFactory");
            env.put(Context.PROVIDER_URL, "iiop://"+hostURL+":"+hostPort);

            env .setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
            env .setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
            env .setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
            env.setProperty("org.omg.CORBA.ORBInitialHost", hostURL);

            // optional. Defaults to 3700. Only needed if target orb port is not 3700.
            env.setProperty("org.omg.CORBA.ORBInitialPort", "3700");	   

            logger.info("Getting Context");
            InitialContext ctx = new InitialContext(env);


            logger.info("Looking up Connection Factory: "+connectionFactName);
            ConnectionFactory cf = (ConnectionFactory)ctx.lookup(connectionFactName);


            logger.info("Looking up Queue: "+topicName);
            topic = (Topic) ctx.lookup(topicName);

            logger.info("Getting connection");
            connection = cf.createConnection();
        } catch (JMSException e) {
                // TODO Auto-generated catch block
                logger.error("Error connecting to Topic",e);
        } catch (NamingException e) {
                // TODO Auto-generated catch block
                logger.error("Error connecting to Topic",e);
        }
        
    }

}
