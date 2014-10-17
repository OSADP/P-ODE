package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;

public class ODEQueueTarget implements ODEDataTarget {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private Connection connection;
    private Queue queue;
    private Session session;
    private MessageProducer messageProducer;
    private ODERegistrationResponse registrationResponse;

    @Override
    public void configure(ODERegistrationResponse registrationResponse) throws DataTargetException {
        try {
            this.registrationResponse = registrationResponse;
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
        try {
            BytesMessage bytesMessage = session.createBytesMessage();
            messageProducer.send(bytesMessage);
        } catch (JMSException e) {
            throw new DataTargetException("Error sending data", e);
        }
    }

    private void createProducer() throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        messageProducer = session.createProducer(queue);
    }

    private void connect() throws NamingException, JMSException {
        Properties env = new Properties();
        env.put(Context.SECURITY_PRINCIPAL, "admin");
        env.put(Context.SECURITY_CREDENTIALS, "admin");
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.appserv.naming.S1ASCtxFactory");
        env.put(Context.PROVIDER_URL, "iiop://" + registrationResponse.getQueueHostURL() + ":" + registrationResponse.getQueueHostPort());

        env.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
        env.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        env.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        env.setProperty("org.omg.CORBA.ORBInitialHost", registrationResponse.getQueueHostURL());

        // optional. Defaults to 3700. Only needed if target orb port is not 3700.
        env.setProperty("org.omg.CORBA.ORBInitialPort", "3700");

        InitialContext ctx = new InitialContext(env);

        logger.info("Looking up Connection Factory: " + registrationResponse.getQueueConnFact());
        ConnectionFactory cf = (ConnectionFactory) ctx.lookup(registrationResponse.getQueueConnFact());

        logger.info("Looking up Queue: " + registrationResponse.getQueueName());
        queue = (Queue) ctx.lookup(registrationResponse.getQueueName());

        logger.info("Getting connection");
        connection = cf.createConnection();

    }

    @Override
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
