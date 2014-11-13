/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.core.distribute;

import com.leidos.ode.agent.data.ODEAgentMessage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

/**
 *
 * @author cassadyja
 */
public class TCPDataDistributor extends DataDistributor{
    private final String TAG = getClass().getSimpleName();
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TAG);    
    
    private Socket socket = null;
    
    
    private String targetURL;
    private int targetPort;
    
    
    public TCPDataDistributor(String topicHostURL, int topicHostPort,
                              String connFactName, String topicName, String targetURL, int targetPort){
        
        setTopicHostURL(topicHostURL);
        setTopicHostPort(topicHostPort);
        setConnFactName(connFactName);
        setTopicName(topicName);
        
        this.targetURL = targetURL;
        this.targetPort = targetPort;        
    }

    @Override
    protected void cleanup() {
        if(socket != null){
            try {
                socket.close();
            } catch (IOException ex) {
                logger.warn("Error with cleanup",ex);
            }
        }
    }

    @Override
    protected void connectTarget() throws DistributeException {

    }

    @Override
    protected void sendData(Message message) throws DistributeException {
        try {
            logger.debug("Sending TCP Message");
            socket = new Socket(targetURL, targetPort);
            logger.debug("Have Socket: "+socket);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());
            logger.debug("Getting ODEAgentMessage");
            ODEAgentMessage msg = (ODEAgentMessage) ((ObjectMessage) message).getObject();
            logger.debug("Writing TCP Message Length: "+msg.getMessagePayload().length);

            dos.writeInt(msg.getMessagePayload().length);
            dos.flush();
            logger.debug("Writing TCP Message");
            dos.write(msg.getMessagePayload());
            logger.debug("Finished Writing TCP Message");
            dos.flush();
            logger.debug("Finished Writing TCP Message");
            int ret = is.readInt();
            logger.debug("Distributor received: "+ret);
            
        } catch (JMSException ex) {
            throw new DistributeException("Error sending message", ex);
        } catch (IOException ex) {
            throw new DistributeException("Error sending message", ex);
        }finally{
            try {
                socket.close();
            } catch (IOException ex) {
                logger.warn("Error with close",ex);
            }
        }


    }
    
}
