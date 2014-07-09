/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.controllers.topics;

import com.leidos.ode.agent.data.AgentInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

/**
 *
 * @author cassadyja
 */
public class MessageProducerFactory {
    private static Logger logger = Logger.getLogger(MessageProducerFactory.class);
    
    private static MessageProducerFactory theFactory;
    
    private String topicHostURL = "";
    private String topicHostPort = "";
    private String connFactName = "";
    
    private ConnectionFactory cf = null;
    private InitialContext ctx = null;
    
    private Map<String, Connection> connectionMap = new HashMap<String, Connection>();
    
    
    
    
    private MessageProducerFactory(){
        //probably should have the producer factory make connections
        //to the server for each available topic, then creating a producer
        //will be faster.
    }
    
    public static MessageProducerFactory getInstance(){
        if(theFactory == null){
            theFactory = new MessageProducerFactory();
        }
        return theFactory;
    }
    
    
    public MessageProducer getMessageProducer(AgentInfo agentInfo){
        //determine which topic the message should go to based on
        //the data in agent info.  
        //This will include: message type, and region.
        //Then create a message producer for that topic.
        
        
//        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        consumer = session.createConsumer(topic);

        return null;
    }
    
    
    
    private void createConnections() throws NamingException, JMSException{
        createConnectionFactory();
        //for all topics
        Connection conn = createConnection("");
        connectionMap.put("", conn);
        
        
    }
    
    private void createConnectionFactory() throws NamingException{
        Properties env = new Properties();
        env.put(Context.SECURITY_PRINCIPAL, "admin");
        env.put(Context.SECURITY_CREDENTIALS, "admin");
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.appserv.naming.S1ASCtxFactory");
        env.put(Context.PROVIDER_URL, "iiop://" + topicHostURL + ":" + topicHostPort);

        env.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
        env.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        env.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        env.setProperty("org.omg.CORBA.ORBInitialHost", topicHostURL);

        // optional. Defaults to 3700. Only needed if target orb port is not 3700.
        env.setProperty("org.omg.CORBA.ORBInitialPort", "3700");

        ctx = new InitialContext(env);

        logger.info("Looking up Connection Factory: " + connFactName);
        cf = (ConnectionFactory) ctx.lookup(connFactName);        
        
    }
    
    
    private Connection createConnection(String topicName) throws NamingException, JMSException{
        Topic topic = null;
        logger.info("Looking up topic: " + topicName);
        topic = (Topic) ctx.lookup(topicName);

        logger.info("Getting connection");
        Connection connection = cf.createConnection();
        return connection;
    }
    
}
