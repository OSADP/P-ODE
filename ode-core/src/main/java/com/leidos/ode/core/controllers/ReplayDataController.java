package com.leidos.ode.core.controllers;

import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.ServiceRequest;
import org.bn.CoderFactory;
import org.bn.IEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Controller;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Replay Data Distributor Manager
 *
 * Controls the instantiation and state of ReplayDataDistributor instances.
 */

@Controller
public class ReplayDataController implements DisposableBean {
    private String replayControllerIP = "10.10.10.48";
    private int replayControllerPort = 11025;
    private DatagramSocket socket;
    
    private final String TAG = getClass().getSimpleName();
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TAG);    
    
    public void registerReplayRequest(ServiceRequest serviceRequest, PodeSubscriptionRequest podeSubscriptionRequest) {
        transmitReplayRequest(serviceRequest, podeSubscriptionRequest);
    }
    
    private DatagramSocket getSocket() throws SocketException{
        if(socket == null){
            socket = new DatagramSocket();
        }
        return socket;
    }
    
    public void transmitReplayRequest(ServiceRequest serviceRequest, PodeSubscriptionRequest podeSubscriptionRequest){
        //TODO call command line app over UDP socket.
        
        //Below gives NPE why?
        if(serviceRequest.getRequestID() == null){
            logger.debug("ServiceRequest requestId null creating:");
            serviceRequest.setRequestID(new byte[]{0,0,0,0});
        }else{
            logger.debug("ServiceRequest requestId size: "+serviceRequest.getRequestID().length);
        }
            
        
        try {
            logger.debug("Encoding sub request");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IEncoder encoder = CoderFactory.getInstance().newEncoder("BER");
            encoder.encode(podeSubscriptionRequest, bos);        
            byte[] subRequest = bos.toByteArray();
            
            logger.debug("Encoding service request");
            bos = new ByteArrayOutputStream();
            encoder.encode(serviceRequest, bos);
            byte[] svcRequest = bos.toByteArray();
            String subReqString = DatatypeConverter.printHexBinary(subRequest);
            String svcRequestString = DatatypeConverter.printHexBinary(svcRequest);
            String combined = subReqString+":=:"+svcRequestString;
            
            logger.debug("Constructing datagram");
            byte[] message = combined.getBytes();
            DatagramPacket packet = new DatagramPacket(message, message.length);
            packet.setAddress(InetAddress.getByName(replayControllerIP));
            packet.setPort(replayControllerPort);
            logger.debug("Sending datagram");
            getSocket().send(packet);
            logger.debug("Finished");
            
        } catch (Exception ex) {
            logger.error("Error setting up subscription", ex);
        }
    }

    public void destroy() throws Exception {

    }
}
