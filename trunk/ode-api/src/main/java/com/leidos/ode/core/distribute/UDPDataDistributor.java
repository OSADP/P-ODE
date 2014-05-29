/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.distribute;

import java.net.DatagramSocket;
import java.net.SocketException;
import javax.jms.Message;

/**
 *
 * @author cassadyja
 */
public class UDPDataDistributor extends DataDistributor {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UDPDataDistributor.class);

    private String targetURL;
    private int targetPort;

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
            DatagramSocket socket = new DatagramSocket(targetPort);
//        String ipv6Address = "SVRWIN-DECRYPT5.intellidrive.local";
//        InetAddress tmp = InetAddress.getByName(ipv6Address);
//        InetAddress address = Inet6Address.getByAddress(ipv6Address, tmp.getAddress());
//
//        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
//        packet.setAddress(address);
//			
//        socket.send(packet);        
        } catch (SocketException ex) {
            throw new DistributeException("Error connecting to UDP socket", ex);
        }
    }

    @Override
    protected void sendData(Message message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
