/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.distribute;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.util.ByteUtils;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.io.IOException;
import java.net.*;
import java.util.Date;
import javax.jms.BytesMessage;
import org.apache.log4j.Logger;

/**
 * @author cassadyja
 */
public class UDPDataDistributor extends DataDistributor {
    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);

    private String targetURL;
    private int targetPort;
    private DatagramSocket socket;
    

    public UDPDataDistributor(String topicHostURL, int topicHostPort,
                              String connFactName, String topicName, String targetURL, int targetPort, Date endDate) {

        setTopicHostURL(topicHostURL);
        setTopicHostPort(topicHostPort);
        setConnFactName(connFactName);
        setTopicName(topicName);
        setSubscriptionEndDate(endDate);
        
        this.targetURL = targetURL;
        this.targetPort = targetPort;

    }

    @Override
    protected void connectTarget() throws DistributeException {
        try {
            System.out.println("~~~~~~ UDP Distributor Connecting to socket.");
            socket = new DatagramSocket();
//            InetAddress address = InetAddress.getByName(targetURL);
//            socket.connect();
        } catch (SocketException ex) {
            throw new DistributeException("Error connecting to UDP socket", ex);
//        } catch (UnknownHostException ex) {
//            throw new DistributeException("Error connecting to UDP socket", ex);
        }
    }

    @Override
    protected void sendData(Message message) throws DistributeException {
        try {
            BytesMessage msg = (BytesMessage) message;
            logger.debug("Distributor reading bytes.");
            byte[] bytes = new byte[(int)msg.getBodyLength()];
            logger.debug("Distributor: bytes size ["+bytes.length+"]");
            msg.readBytes(bytes);
            logger.debug("Distributor: bytes read ["+ByteUtils.convertBytesToHex(bytes)+"]");
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
            logger.info("Distributor: Constructed Datagram Packet.");
            InetAddress address = InetAddress.getByName(targetURL);
            packet.setAddress(address);
            packet.setPort(targetPort);
            logger.info("Distributor sending message.");
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

    @Override
    protected void cleanup() {
        if(socket != null){
            socket.disconnect();
            socket.close();
        }
    }

}
