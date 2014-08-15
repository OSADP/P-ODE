/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.distribute;

import com.leidos.ode.agent.data.ODEAgentMessage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

/**
 *
 * @author cassadyja
 */
public class UDPDataDistributor extends DataDistributor {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UDPDataDistributor.class);

    private String targetURL;
    private int targetPort;

    private DatagramSocket socket;
    
    public UDPDataDistributor(String topicHostURL, int topicHostPort, 
            String connFactName, String topicName, String targetURL, int targetPort){
        
        setTopicHostURL(topicHostURL);
        setTopicHostPort(topicHostPort);
        setConnFactName(connFactName);
        setTopicName(topicName);
        
        this.targetURL = targetURL;
        this.targetPort = targetPort;
    }
    
    
    @Override
    protected void connectTarget() throws DistributeException{
        try {
            socket = new DatagramSocket(targetPort);
            InetAddress address = InetAddress.getByName(targetURL);
            socket.connect(address, targetPort);
        } catch (SocketException ex) {
            throw new DistributeException("Error connecting to UDP socket", ex);
        } catch (UnknownHostException ex) {
            throw new DistributeException("Error connecting to UDP socket", ex);
        }
    }

    @Override
    protected void sendData(Message message) throws DistributeException {
        //TODO: finish
        try {
            ODEAgentMessage msg = (ODEAgentMessage)((ObjectMessage)message).getObject();
            DatagramPacket packet = new DatagramPacket(msg.getMessagePayload(), msg.getMessagePayload().length);
            InetAddress address = InetAddress.getByName(targetURL);
            packet.setAddress(address);
            packet.setPort(targetPort);
        
            socket.send(packet);
        } catch (IOException ex) {
            throw new DistributeException("Error sending message", ex);
        } catch (JMSException ex) {
            throw new DistributeException("Error sending message", ex);
        }
    }

    /**
     * @return the targetURL
     */
    public String getTargetURL() {
        return targetURL;
    }

    /**
     * @param targetURL the targetURL to set
     */
    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }

    /**
     * @return the targetPort
     */
    public int getTargetPort() {
        return targetPort;
    }

    /**
     * @param targetPort the targetPort to set
     */
    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }
    
}
