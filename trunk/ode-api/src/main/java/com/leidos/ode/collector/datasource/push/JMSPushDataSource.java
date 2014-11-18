package com.leidos.ode.collector.datasource.push;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author cassadyja
 */
public class JMSPushDataSource extends PushDataSource implements ExceptionListener {

    private Connection connection;
    private MessageConsumer consumer;

    @Override
    public void startDataSource() {
        try {
//            Properties env = new Properties();
//            env.put(Context.SECURITY_PRINCIPAL, user);
//            env.put(Context.SECURITY_CREDENTIALS, pass);
//            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.appserv.naming.S1ASCtxFactory");
//            env.put(Context.PROVIDER_URL, "tcp://" + getHostURL() + ":" + getHostPort());
//
//            env.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
//            env.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
//            env.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
//            env.setProperty("org.omg.CORBA.ORBInitialHost", getHostURL());
//
//            // optional. Defaults to 3700. Only needed if target orb port is not 3700.
//            env.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
//
//            InitialContext ctx = new InitialContext(env);
            ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(getUsername(), getPassword(), getHostProtocol() + getHostAddress() + ":" + getHostPort());

            // Create a Connection
            connection = cf.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(getQueueName());

            // Create a MessageConsumer from the Session to the Topic or Queue
            consumer = session.createConsumer(destination);

//        } catch (NamingException e) {
//            throw new DataSourceException("Error connecting", e);
        } catch (JMSException e) {
            getLogger().error(e.getLocalizedMessage());
        }
    }

    public void onException(JMSException jmse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] pollDataSource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean canPoll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void cleanUpConnections() {
        if (connection != null) {
            try {
                connection.close();
                getLogger().debug("Closed connection.");
            } catch (JMSException e) {
                getLogger().error(e.getLocalizedMessage());
            }
        } else {
            getLogger().warn("Unable to close connection. Connection was null.");
        }
    }
}
