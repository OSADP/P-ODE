/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.collector.datasource;

import java.util.Properties;
import java.util.logging.Level;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author cassadyja
 */
public class JMSPushDataSource extends PushDataSource implements ExceptionListener {

    private static Logger logger = Logger.getLogger(JMSPushDataSource.class);

    private String hostURL;
    private String hostPort;
    private String queueName;
    private String user;
    private String pass;

//    private Queue queue;
    private Connection connection;
    private MessageConsumer consumer;
    
    
    public void startDataSource() throws DataSourceException {
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
            ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(user, pass, "tcp://" + getHostURL() + ":" + getHostPort());

            // Create a Connection
            connection = cf.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(queueName);

            // Create a MessageConsumer from the Session to the Topic or Queue
            consumer = session.createConsumer(destination);

//        } catch (NamingException e) {
//            throw new DataSourceException("Error connecting", e);
        } catch (JMSException ex) {
            throw new DataSourceException("Error connecting",ex);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public byte[] getDataFromSource() throws DataSourceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the hostURL
     */
    public String getHostURL() {
        return hostURL;
    }

    /**
     * @param hostURL the hostURL to set
     */
    public void setHostURL(String hostURL) {
        this.hostURL = hostURL;
    }

    /**
     * @return the hostPort
     */
    public String getHostPort() {
        return hostPort;
    }

    /**
     * @param hostPort the hostPort to set
     */
    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    /**
     * @return the queueName
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * @param queueName the queueName to set
     */
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    public void onException(JMSException jmse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void stop(){
        try {
            connection.close();
        } catch (JMSException ex) {
            java.util.logging.Logger.getLogger(JMSPushDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
