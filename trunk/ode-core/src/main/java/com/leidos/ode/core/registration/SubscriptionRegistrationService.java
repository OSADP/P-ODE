package com.leidos.ode.core.registration;

import com.leidos.ode.core.controllers.DistributeDataController;
import com.leidos.ode.core.controllers.ReplayDataController;
import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.DDay;
import com.leidos.ode.data.DHour;
import com.leidos.ode.data.DMinute;
import com.leidos.ode.data.DMonth;
import com.leidos.ode.data.DYear;
import com.leidos.ode.data.DataSubscriptionResponse;
import com.leidos.ode.data.PodeDataConfirmation;
import com.leidos.ode.data.PodeDataPulicationRegistration;
import com.leidos.ode.data.PodeDialogID;
import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.SemiSequenceID;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.data.ServiceResponse;
import com.leidos.ode.data.Sha256Hash;
import com.leidos.ode.registration.RegistrationMessage;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import com.leidos.ode.util.SHAHasher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SubscriptionRegistrationService {
    private final String TAG = getClass().getSimpleName();
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TAG);
    
    
    @Autowired
    private DistributeDataController distributeDataController;

    @Autowired
    private ReplayDataController replayDataController;

    @Autowired
    private RegistrationDAO registrationDAO;

    @RequestMapping(value = "registerSubscribeServiceRequest", method = RequestMethod.POST, consumes  = "application/xml", produces = "application/xml")
    public
    @ResponseBody
    RegistrationMessage registerServiceRequest(@RequestBody RegistrationMessage registrationRequest) {
        RegistrationMessage registrationResponse = null;
        
        ByteArrayInputStream bis = new ByteArrayInputStream(registrationRequest.getEncodedRegistrationMessage());
        try {
            IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
            ServiceRequest serviceRequest = decoder.decode(bis, ServiceRequest.class);
            
            registrationDAO.storeServiceRequest(serviceRequest, true);
            
            ServiceResponse response = new ServiceResponse();
            PodeDialogID dId = new PodeDialogID();
            dId.setValue(PodeDialogID.EnumType.podeDataSubscriptionRegistration);            
            
            response.setDialogID(dId);
            
            SemiSequenceID seqId = new SemiSequenceID();
            seqId.setValue(SemiSequenceID.EnumType.svcResp);
            response.setSeqID(seqId);
            
            response.setGroupID(serviceRequest.getGroupID());
            response.setRequestID(serviceRequest.getRequestID());
            
            DDateTime exp = new DDateTime();
            Calendar cal = Calendar.getInstance();
            
            DHour hour = new DHour(cal.get(Calendar.HOUR_OF_DAY));
            exp.setHour(hour);
            DMinute minute = new DMinute(cal.get(Calendar.MINUTE));
            exp.setMinute(minute);
            DMonth month = new DMonth(cal.get(Calendar.MONTH)+2);
            exp.setMonth(month);
            DDay day = new DDay(cal.get(Calendar.DAY_OF_MONTH));
            exp.setDay(day);
            DYear year = new DYear(cal.get(Calendar.YEAR));
            exp.setYear(year);            
            response.setExpiration(exp);

            
            String hash = SHAHasher.sha256Hash(registrationRequest.getEncodedRegistrationMessage());
            Sha256Hash shaHash = new Sha256Hash(hash.getBytes());
            response.setHash(shaHash);
            
            registrationResponse = new RegistrationMessage(encodeMessage(response));
                    
        } catch (Exception ex) {
            Logger.getLogger(PublicationRegistrationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return registrationResponse;
    }
    
    
    
    private byte[] encodeMessage(ServiceResponse response) throws Exception{
        IEncoder encoder =  CoderFactory.getInstance().newEncoder("BER");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        encoder.encode(response, os);
        return os.toByteArray();
          
    } 
      
    private byte[] encodeMessage(DataSubscriptionResponse response) throws Exception{
        IEncoder encoder =  CoderFactory.getInstance().newEncoder("BER");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        encoder.encode(response, os);
        return os.toByteArray();
          
    } 
   
    @RequestMapping(value = "registerSubscription", method = RequestMethod.POST, consumes  = "application/xml", produces = "application/xml")
    public
    @ResponseBody
    RegistrationMessage registerSubscription(@RequestBody RegistrationMessage registrationRequest) {
        RegistrationMessage registrationResponse = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(registrationRequest.getEncodedRegistrationMessage());
        try {
            IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
            PodeSubscriptionRequest subRegistration = decoder.decode(bis, PodeSubscriptionRequest.class);

            DataSubscriptionResponse response = new DataSubscriptionResponse();
            
            PodeDialogID dId = new PodeDialogID();
            dId.setValue(PodeDialogID.EnumType.podeDataSubscriptionRegistration);
            response.setDialogID(dId);
            SemiSequenceID seqId = new SemiSequenceID();
            seqId.setValue(SemiSequenceID.EnumType.subscriptinoResp);            
            response.setSeqID(seqId);
            response.setGroupID(subRegistration.getGroupID());
            response.setRequestID(subRegistration.getRequestID());
            response.setSubID(generateRandomByte(4));
            

            
            
            
            
            getRegistrationDAO().updateRegistration(subRegistration, response);
            ServiceRequest serviceRequest = getRegistrationDAO().getServiceRequestForRequestId(subRegistration.getRequestID());

            if (subRegistration.getType().getReplayData() != null) {
                // Handle requests for replay data
                replayDataController.registerReplayRequest(serviceRequest, subRegistration);
            } else {
                // Handle normal real-time data requests
                distributeDataController.subscriptionReceived(serviceRequest, subRegistration);
            }
                    
            
            registrationResponse = new RegistrationMessage(encodeMessage(response));
            
        } catch (Exception ex) {
            logger.error("Error with subscription",ex);
        }

        return registrationResponse;
    }
        
    private byte[] generateRandomByte(int size){
        Random rand = new Random();
        byte[] bytes = new byte[size];
        rand.nextBytes(bytes);
        return bytes;
    }    
    
    
    @RequestMapping(value = "registerSubscribe", method = RequestMethod.POST)
    public
    @ResponseBody
    ODERegistrationResponse registerSubscriptionIntent(@RequestBody ODERegistrationRequest registrationRequest) {
        ODERegistrationResponse response = null;
        
        registrationRequest = getRegistrationDAO().storeRegistration(registrationRequest);
        QueueInfo queueInfo = getQueueNameForRegistration(registrationRequest);
        if (queueInfo != null) {
            response = createRegResponse(registrationRequest, queueInfo);
            notifiyDistribute(response);
        }
        return response;
    }

    private void notifiyDistribute(ODERegistrationResponse registrationResponse) {
        getDistributeDataController().receiveSubscriptionNotification(registrationResponse);
    }

    private QueueInfo getQueueNameForRegistration(ODERegistrationRequest registrationRequest) {
        return getRegistrationDAO().getQueueForRegistration(registrationRequest);
    }

    private ODERegistrationResponse createRegResponse(ODERegistrationRequest registrationRequest, QueueInfo qInfo) {
        ODERegistrationResponse resp = new ODERegistrationResponse();
        resp.setAgentId(registrationRequest.getAgentId());
        resp.setMessageType(registrationRequest.getMessageType());
        resp.setRegion(registrationRequest.getRegion());
        resp.setRegistrationId(registrationRequest.getRegistrationId());
        resp.setRegistrationType(registrationRequest.getRegistrationType());
        resp.setSubscriptionProtocol(registrationRequest.getSubscriptionProtocol());
        
        resp.setStartDate(registrationRequest.getStartDate());
        resp.setEndDate(registrationRequest.getEndDate());
        
        resp.setQueueConnFact(qInfo.getQueueConnectionFactory());
        resp.setQueueName(qInfo.getQueueName());
        resp.setQueueHostURL(qInfo.getTargetAddress());
        resp.setQueueHostPort(qInfo.getTargetPort());
        resp.setTargetAddress(registrationRequest.getSubscriptionReceiveAddress());
        resp.setTargetPort(registrationRequest.getSubscriptionReceivePort());

        return resp;
    }

    private DistributeDataController getDistributeDataController() {
        return distributeDataController;
    }

    private RegistrationDAO getRegistrationDAO() {
        return registrationDAO;
    }
}
