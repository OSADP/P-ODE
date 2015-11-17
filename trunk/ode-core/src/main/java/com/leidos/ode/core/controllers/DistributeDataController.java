package com.leidos.ode.core.controllers;

import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.core.distribute.DataDistributor;
import com.leidos.ode.core.distribute.TCPDataDistributor;
import com.leidos.ode.core.distribute.UDPDataDistributor;
import com.leidos.ode.data.ConnectionPoint;
import com.leidos.ode.data.DFullTime;
import com.leidos.ode.data.PodeDataTypes;
import com.leidos.ode.data.PodeProtocol;
import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.logging.ODELogger;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import com.leidos.ode.util.ByteUtils;
import com.leidos.ode.util.ODEMessageType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Controller
public class DistributeDataController implements  DisposableBean{
    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);

    private static final int SPEED_MASK = Integer.parseInt("00000001", 2);
    private static final int OCC_MASK = Integer.parseInt("00000010", 2);
    private static final int VOLUME_MASK = Integer.parseInt("00000100", 2);
    private static final int TRAVEL_MASK = Integer.parseInt("00001000", 2);
    private static final int WEATHER_MASK = Integer.parseInt("00010000", 2);    
    
    private Map<String, DataDistributor> distributors = new HashMap<String, DataDistributor>();
    
    @Autowired
    private RegistrationDAO regDao;
    
    
    
    
    public DistributeDataController() {

    }


    @RequestMapping(value = "subscriptionNotification", method = RequestMethod.POST)
    public void receiveSubscriptionNotification(@RequestBody ODERegistrationResponse registrationResponse) {
        //Create a new DataDistributor with the info from the subscription.
        logger.debug("New Subscription creating Distributor");
        DataDistributor dist = null;
        if("UDP".equalsIgnoreCase(registrationResponse.getSubscriptionProtocol())){
            logger.debug("Creating UDP Distributor");
            dist = new UDPDataDistributor(registrationResponse.getQueueHostURL(), registrationResponse.getQueueHostPort(), registrationResponse.getQueueConnFact(), registrationResponse.getQueueName(), registrationResponse.getTargetAddress(), registrationResponse.getTargetPort(), registrationResponse.getEndDate());    
        }else if("TCP".equalsIgnoreCase(registrationResponse.getSubscriptionProtocol())){
            logger.debug("Creating TCP Distributor");
            dist = new TCPDataDistributor(registrationResponse.getQueueHostURL(), registrationResponse.getQueueHostPort(), registrationResponse.getQueueConnFact(), registrationResponse.getQueueName(), registrationResponse.getTargetAddress(), registrationResponse.getTargetPort(), registrationResponse.getEndDate());    
        }
        //Start up the distributor
        new Thread(dist).start();
        distributors.put(registrationResponse.getAgentId(), dist);
    }
    
    
    public void subscriptionReceived(ServiceRequest srvRequest, PodeSubscriptionRequest subRequest){
        logger.debug("New Subscription creating Distributor");
        DataDistributor dist = null;
        
        List<QueueInfo> queueInfoList = getQueueInformationForSubscription(subRequest);
        ConnectionPoint connPoint = srvRequest.getDestination();
        byte[] ipAddress = connPoint.getAddress().getIpv4Address().getValue();
        String ip = ByteUtils.buildIpAddressFromBytes(ipAddress);
        int port = connPoint.getPort().getValue();
        Date endDate = getDateFromFullDate(subRequest.getEndTime());
        
        for(QueueInfo queueInfo:queueInfoList){
            if(subRequest.getProtocol().getValue().equals(PodeProtocol.EnumType.upd)){
                logger.debug("Creating UDP Distributor to IP: ["+ip+"] Port: ["+port+"]");
                
                dist = new UDPDataDistributor(queueInfo.getTargetAddress(), queueInfo.getTargetPort(), queueInfo.getQueueConnectionFactory(), queueInfo.getQueueName(), ip, port, endDate);    
            }else if(subRequest.getProtocol().getValue().equals(PodeProtocol.EnumType.tcp)){
                logger.debug("Creating TCP Distributor to IP: ["+ip+"] Port: ["+port+"]");
                
                dist = new TCPDataDistributor(queueInfo.getTargetAddress(), queueInfo.getTargetPort(), queueInfo.getQueueConnectionFactory(), queueInfo.getQueueName(), ip, port, endDate);    
            }
        }
        
        
        new Thread(dist).start();
        distributors.put(ByteUtils.convertBytesToHex(subRequest.getRequestID()), dist);
        
    }
    
    private Date getDateFromFullDate(DFullTime fullTime){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, fullTime.getHour().getValue());
        cal.set(Calendar.MINUTE, fullTime.getMinute().getValue());
        cal.set(Calendar.MONTH, fullTime.getMonth().getValue()-1);
        cal.set(Calendar.DAY_OF_MONTH, fullTime.getDay().getValue());
        cal.set(Calendar.YEAR, fullTime.getYear().getValue());
        
        return cal.getTime();
    }
    
    private List<QueueInfo> getQueueInformationForSubscription(PodeSubscriptionRequest subRequest){
        List<QueueInfo> queueInfoList = new ArrayList<QueueInfo>();
        PodeDataTypes dataTypes = subRequest.getSubData().getDataElements();
        byte[] value = dataTypes.getValue();
        if(value.length == 1){
            Integer i = new Integer(value[0]);
            if((i & SPEED_MASK) == SPEED_MASK){
                String messageType = ODEMessageType.SPEED.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                queueInfoList.add(queueInfo);
            }
            if((i & OCC_MASK) == OCC_MASK){
                String messageType = ODEMessageType.OCCUPANCY.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                queueInfoList.add(queueInfo);
            }
            if((i & VOLUME_MASK) == VOLUME_MASK){
                String messageType = ODEMessageType.VOLUME.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                queueInfoList.add(queueInfo);
            }
            if((i & TRAVEL_MASK) == TRAVEL_MASK){
                String messageType = ODEMessageType.TRAVEL.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                queueInfoList.add(queueInfo);
            }
            if((i & WEATHER_MASK) == WEATHER_MASK){
                String messageType = ODEMessageType.WEATHER.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                queueInfoList.add(queueInfo);
            }
        }
        return queueInfoList;
    }
    
    private QueueInfo getQueueForMessageType(String messageType){
        return getRegDao().getQueueForMessageType(messageType);
    }    
    

    @RequestMapping(value = "subscriptionCancel", method = RequestMethod.POST)
    public void stopSubscription(@RequestBody String agentId) {
        if(distributors.get(agentId) != null){
            distributors.get(agentId).setInterrupted(true);
            distributors.remove(agentId);
        }
        
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Closing Distributors.");
        Iterator<String> it = distributors.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            distributors.get(key).setInterrupted(true);
        }
    }

    public void destroy() throws Exception {
        cleanup();
    }

    /**
     * @return the regDao
     */
    public RegistrationDAO getRegDao() {
        return regDao;
    }

    /**
     * @param regDao the regDao to set
     */
    public void setRegDao(RegistrationDAO regDao) {
        this.regDao = regDao;
    }

}
