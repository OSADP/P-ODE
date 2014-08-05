package com.leidos.ode.agent.datatarget;

import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.data.ODERegistrationResponse;
import java.util.logging.Level;


public class ODEQueueTarget implements ODEDataTarget {

    private static Logger logger = Logger.getLogger(ODEQueueTarget.class);

    private Connection connection;
    private Queue queue;

    private Session session;
    private MessageProducer messageProducer;

    private ODERegistrationResponse regInfo;

    @Override
    public void configure(ODERegistrationResponse regInfo) throws DataTargetException {
        try {
            this.regInfo = regInfo;
            connect();
            createProducer();
        } catch (NamingException ex) {
            throw new DataTargetException("Error connecting to queue", ex);
        } catch (JMSException ex) {
            throw new DataTargetException("Error connecting to queue", ex);
        }
    }

    @Override
    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        try{
            BytesMessage bytesMessage = session.createBytesMessage();
//		message.writeBytes(message.get);
            messageProducer.send(bytesMessage);
        }catch(JMSException e){
            throw new DataTargetException("Error sending vdotdata", e);
        }
    }

    private void createProducer() throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        messageProducer = session.createProducer(queue);

    }

    private void connect() throws NamingException, JMSException  {
        Properties env = new Properties();
        env.put(Context.SECURITY_PRINCIPAL, "admin");
        env.put(Context.SECURITY_CREDENTIALS, "admin");
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.appserv.naming.S1ASCtxFactory");
        env.put(Context.PROVIDER_URL, "iiop://" + regInfo.getQueueHostURL() + ":" + regInfo.getQueueHostPort());

        env.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
        env.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        env.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        env.setProperty("org.omg.CORBA.ORBInitialHost", regInfo.getQueueHostURL());

        // optional. Defaults to 3700. Only needed if target orb port is not 3700.
        env.setProperty("org.omg.CORBA.ORBInitialPort", "3700");

        InitialContext ctx = new InitialContext(env);

        logger.info("Looking up Connection Factory: " + regInfo.getQueueConnFact());
        ConnectionFactory cf = (ConnectionFactory) ctx.lookup(regInfo.getQueueConnFact());

        logger.info("Looking up Queue: " + regInfo.getQueueName());
        queue = (Queue) ctx.lookup(regInfo.getQueueName());

        logger.info("Getting connection");
        connection = cf.createConnection();

    }

    public void close() {
        try {
            logger.info("Closing queue connection");
            messageProducer.close();
            connection.close();
        } catch (JMSException ex) {
            java.util.logging.Logger.getLogger(ODEQueueTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
