/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.core.rde;

import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.util.ByteUtils;
import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author cassadyja
 */
public class RDESubscribeController implements Runnable{
    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);    
    
    private static final String DELIM = ":=:";    

    private int listenerPort = 11025;
    
    private int consumerAdminPort = 11111;
    private boolean shuttingDown = false;
    
    
    private DatagramSocket ss;
    
    private Map<String, ReplayDataDistributor> distributors = new HashMap<String, ReplayDataDistributor>();
    
    
    public static void main(String[] args){
        new Thread(new RDESubscribeController()).start();
    }
    
    
    private class AdminThread implements Runnable{
        public void run() {
                try {
                        logger.info("ADMIND: Starting Admin Thread");
                        ServerSocket ss = new ServerSocket(consumerAdminPort);
                        logger.info("ADMIN: Waiting for admin command");
                        while(true){
                                Socket socket = ss.accept();
                                logger.info("ADMIN: Received connection");
                                InputStream is = socket.getInputStream();
                                byte[] bytes = new byte[2048];
                                int read = is.read(bytes);
                                is.close();
                                byte[] resized = Arrays.copyOf(bytes,read);
                                String command = new String(resized);
                                logger.info("ADMIN: Command is: "+command);
                                if(command.equalsIgnoreCase("shutdown")){
                                        shuttingDown = true;
                                        sendShutdown();
                                }
                        }
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

        }        
    }
    
    
    
    
    public void run() {
        try {
            logger.info("Starting up...");
            new Thread(new AdminThread()).start();
            InetAddress tmpAddress = InetAddress.getByName("[REDACTED]");
            ss = new DatagramSocket(listenerPort,tmpAddress);
            ss.setSoTimeout(10000);
            
            while(!shuttingDown){
                logger.info("Listening");
                try{
                    byte[] receiveData = new byte[5120];
                    DatagramPacket packet = new DatagramPacket(receiveData,receiveData.length);
                    ss.receive(packet);
                    if(packet.getLength() > 0){
                        byte[] resized = Arrays.copyOf(packet.getData(), packet.getLength());
                        //process message;
                        logger.info("Message received size: "+ packet.getLength());
                        String s = new String(resized);
                        if(s.indexOf(DELIM) > -1){
                            String subReqString = s.substring(0,s.indexOf(DELIM));
                            String svcReqString = s.substring(s.indexOf(DELIM)+3,s.length());
                            byte[] svcRequest = DatatypeConverter.parseHexBinary(svcReqString);
                            byte[] subRequest = DatatypeConverter.parseHexBinary(subReqString);
                            IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
                            ByteArrayInputStream bis = new ByteArrayInputStream(subRequest);
                            PodeSubscriptionRequest podeSubscriptionRequest = decoder.decode(bis, PodeSubscriptionRequest.class);

                            bis = new ByteArrayInputStream(svcRequest);
                            ServiceRequest serviceRequest = decoder.decode(bis, ServiceRequest.class);

                            ReplayDataDistributor dist = new UDPReplayDataDistributor(podeSubscriptionRequest, serviceRequest);
                            new Thread(dist).start();
                            distributors.put(ByteUtils.convertBytesToHex(podeSubscriptionRequest.getRequestID()), dist);

                        }
                    }
                }catch (IOException e) {
                        if(! (e instanceof SocketTimeoutException)){
                                logger.warn("Error receiving Packet from socket",e);
                        }
                }
            }
            
            
        } catch (UnknownHostException ex) {
            java.util.logging.Logger.getLogger(RDESubscribeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            java.util.logging.Logger.getLogger(RDESubscribeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(RDESubscribeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
        
    
    
    private void sendShutdown(){
        Iterator<String> it = distributors.keySet().iterator();
        while(it.hasNext()){
            String s = it.next();
            distributors.get(s).setInterrupted(true);
        }
    }
    
    
    
    
    
    
    
    
    
}
