package com.leidos.ode.core.controllers.publish;

import com.leidos.ode.core.controllers.*;
import com.leidos.ode.agent.data.ODEAgentMessage;
import java.util.Properties;
import java.util.logging.Level;
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
    private StoreDataController storeDataController;
    @Autowired
    private DistributeDataController distributeDataController;
    private int i = 0;
    
    private Topic topic;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer = null;
    private String hostURL;
    private int hostPort;
    private String connectionFactName;
    private String topicName;
    
    
    public PublishBSMDataController() {

    }

    @RequestMapping(value = "publishBSM", method = RequestMethod.POST)
    public @ResponseBody String publishData(@RequestBody ODEAgentMessage odeAgentMessage) {
        System.out.println("~~~~~~~Received message ."+ ++i);
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
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(odeAgentMessage);
            messageProducer.send(msg);
        } catch (JMSException ex) {
            logger.error("Error creating message.", ex);
        }
        
        
    }
    
    private MessageProducer getMessageProducer() throws JMSException{
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        return session.createProducer(topic);
    }
    
    public StoreDataController getStoreDataController() {
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
