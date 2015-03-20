/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.core.distribute;

import java.util.Date;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * @author cassadyja
 */
public abstract class DataDistributor implements Runnable {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);
    protected String topicHostURL;
    protected int topicHostPort;
    protected String connFactName;
    protected String topicName;
    private boolean interrupted = false;
    private boolean stopped = false;
    private Connection connection;
    private Topic topic;
    private MessageConsumer consumer;
    private Date subscriptionEndDate;
    
    
    
    protected abstract void cleanup();
    
    public void run() {
        try {
            logger.info("Starting Data Distributor");
            System.out.println("Starting Data Distributor");
            connectTopic();
            connectTarget();
            while (!isInterrupted()) {
                Message message = consumer.receive(10000);
                
                if (message != null) {
                    if (message instanceof TextMessage) {
                        logger.debug("Distribute found TextMessage, ignoring. " + message);
                    } else {
                        logger.info("Distributor received message: " + message);
                        sendData(message);
                    }
                }
                
                checkSubScriptionEnd();
                
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.debug("Sleep operation interrupted");
                }
            }

        } catch (NamingException ex) {
            logger.error("Naming Exception",ex);
        } catch (JMSException ex) {
            logger.error("Jms Exception",ex);
        } catch (DistributeException ex) {
            logger.error("Distribute Exception",ex);
        } finally {
            try {
                consumer.close();
                connection.close();
            } catch (JMSException ex) {
                java.util.logging.Logger.getLogger(DataDistributor.class.getName()).log(Level.SEVERE, null, ex);
            }
            logger.info("Distributor Stopped.");
            setStopped(true);
            cleanup();
        }
    }

    private void checkSubScriptionEnd() {
        logger.debug("Checking for subscription end sub end date["+subscriptionEndDate+"] Now ["+new Date()+"]");
        logger.debug("Result of Date compare ["+subscriptionEndDate.compareTo(new Date())+"]");
        if(subscriptionEndDate.compareTo(new Date()) < 0){
            logger.debug("Past end date for subscription.");
            setInterrupted(true);
        }
    }

    
    private void connectTopic() throws NamingException, JMSException {
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

        InitialContext ctx = new InitialContext(env);

        logger.info("Looking up Connection Factory: " + connFactName);
        System.out.println("Looking up Connection Factory: " + connFactName);
        ConnectionFactory cf = (ConnectionFactory) ctx.lookup(connFactName);

        logger.info("Looking up Queue: " + topicName);
        topic = (Topic) ctx.lookup(topicName);

        logger.info("Getting connection");
        System.out.println("Getting connection");
        connection = cf.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        consumer = session.createConsumer(topic);
        connection.start();
    }

    protected abstract void connectTarget() throws DistributeException;

    protected abstract void sendData(Message message) throws DistributeException;

    /**
     * @return the connFactName
     */
    public String getConnFactName() {
        return connFactName;
    }

    /**
     * @param connFactName the connFactName to set
     */
    public void setConnFactName(String connFactName) {
        this.connFactName = connFactName;
    }

    /**
     * @return the topicName
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * @param topicName the topicName to set
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * @return the topicHostURL
     */
    public String getTopicHostURL() {
        return topicHostURL;
    }

    /**
     * @param topicHostURL the topicHostURL to set
     */
    public void setTopicHostURL(String topicHostURL) {
        this.topicHostURL = topicHostURL;
    }

    /**
     * @return the topicHostPort
     */
    public int getTopicHostPort() {
        return topicHostPort;
    }

    /**
     * @param topicHostPort the topicHostPort to set
     */
    public void setTopicHostPort(int topicHostPort) {
        this.topicHostPort = topicHostPort;
    }

    /**
     * @return the interrupted
     */
    public boolean isInterrupted() {
        return interrupted;
    }

    /**
     * @param interrupted the interrupted to set
     */
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /**
     * @return the stopped
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * @param stopped the stopped to set
     */
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    /**
     * @return the subscriptionEndDate
     */
    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    /**
     * @param subscriptionEndDate the subscriptionEndDate to set
     */
    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }


    public class DistributeException extends Exception {

        public DistributeException() {
            super();
        }

        public DistributeException(String message) {
            super(message);
        }

        public DistributeException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
