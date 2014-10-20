/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.distribute;

import com.leidos.ode.agent.data.ODEAgentMessage;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.io.IOException;
import java.net.*;

/**
 * @author cassadyja
 */
public class UDPDataDistributor extends DataDistributor {

    private String targetURL;
    private int targetPort;
    private DatagramSocket socket;

    public UDPDataDistributor(String topicHostURL, int topicHostPort,
                              String connFactName, String topicName, String targetURL, int targetPort) {

        setTopicHostURL(topicHostURL);
        setTopicHostPort(topicHostPort);
        setConnFactName(connFactName);
        setTopicName(topicName);

        this.targetURL = targetURL;
        this.targetPort = targetPort;
    }

    @Override
    protected void connectTarget() throws DistributeException {
        try {
            System.out.println("~~~~~~ UDP Distributor Connecting to socket.");
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
        try {
            ODEAgentMessage msg = (ODEAgentMessage) ((ObjectMessage) message).getObject();
            DatagramPacket packet = new DatagramPacket(msg.getMessagePayload(), msg.getMessagePayload().length);
            InetAddress address = InetAddress.getByName(targetURL);
            packet.setAddress(address);
            packet.setPort(targetPort);
            System.out.println("Distributor sending message.");
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
