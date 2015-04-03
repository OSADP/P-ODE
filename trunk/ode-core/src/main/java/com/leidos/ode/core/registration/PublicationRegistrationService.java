package com.leidos.ode.core.registration;

import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.DDay;
import com.leidos.ode.data.DHour;
import com.leidos.ode.data.DMinute;
import com.leidos.ode.data.DMonth;
import com.leidos.ode.data.DYear;
import com.leidos.ode.data.PodeDataConfirmation;
import com.leidos.ode.data.PodeDataDestination;
import com.leidos.ode.data.PodeDataPulicationRegistration;
import com.leidos.ode.data.PodeDataTypes;
import com.leidos.ode.data.PodeDialogID;
import com.leidos.ode.data.SemiSequenceID;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.data.ServiceResponse;
import com.leidos.ode.data.Sha256Hash;
import com.leidos.ode.registration.RegistrationMessage;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import com.leidos.ode.util.ODEMessageType;
import com.leidos.ode.util.SHAHasher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import org.apache.log4j.Logger;
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
public class PublicationRegistrationService {
    private String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    
    @Autowired
    private RegistrationDAO regDao;

    private static final int SPEED_MASK = Integer.parseInt("00000001", 2);
    private static final int OCC_MASK = Integer.parseInt("00000010", 2);
    private static final int VOLUME_MASK = Integer.parseInt("00000100", 2);
    private static final int TRAVEL_MASK = Integer.parseInt("00001000", 2);
    private static final int WEATHER_MASK = Integer.parseInt("00010000", 2);
    
    
    
    @RequestMapping(value = "registerPublishServiceRequest", method = RequestMethod.POST, consumes  = "application/xml", produces = "application/xml")
    public
    @ResponseBody
    RegistrationMessage registerServiceRequest(@RequestBody RegistrationMessage registrationRequest) {
        RegistrationMessage registrationResponse = null;
        
        ByteArrayInputStream bis = new ByteArrayInputStream(registrationRequest.getEncodedRegistrationMessage());
        try {
            IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
            ServiceRequest serviceRequest = decoder.decode(bis, ServiceRequest.class);
            
            regDao.storeServiceRequest(serviceRequest, false);
            
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
//            response.setServiceRegion(null);
            
            String hash = SHAHasher.sha256Hash(registrationRequest.getEncodedRegistrationMessage());
            logger.debug("Service Response Hash: "+hash);
            logger.debug("Service Response Hash Length: "+hash.getBytes().length);
            Sha256Hash shaHash = new Sha256Hash(hash.getBytes());
            response.setHash(shaHash);
            
            registrationResponse = new RegistrationMessage();
            registrationResponse.setEncodedRegistrationMessage(encodeMessage(response));
                    
        } catch (Exception ex) {
            logger.error("Error processing Service Request", ex);
        }
        
        return registrationResponse;
    }
    
    
    
    private byte[] encodeMessage(ServiceResponse response) throws Exception{
        IEncoder encoder =  CoderFactory.getInstance().newEncoder("BER");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        encoder.encode(response, os);
        return os.toByteArray();
          
    } 
    
    private byte[] encodeMessage(PodeDataConfirmation response) throws Exception{
        IEncoder encoder =  CoderFactory.getInstance().newEncoder("BER");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        encoder.encode(response, os);
        return os.toByteArray();
          
    } 
    
    @RequestMapping(value = "registerPublication", method = RequestMethod.POST, consumes  = "application/xml", produces = "application/xml")
    public
    @ResponseBody
    RegistrationMessage registerPublication(@RequestBody RegistrationMessage registrationRequest) {
        RegistrationMessage registrationResponse = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(registrationRequest.getEncodedRegistrationMessage());
        try {
            IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
            PodeDataPulicationRegistration pubRegistration = decoder.decode(bis, PodeDataPulicationRegistration.class);
            
            
            PodeDataConfirmation confirmation = new PodeDataConfirmation();
            PodeDialogID dId = new PodeDialogID();
            dId.setValue(PodeDialogID.EnumType.podeDataPulicationRegistration);
            confirmation.setDialogID(dId);
            
            SemiSequenceID seqId = new SemiSequenceID();
            seqId.setValue(SemiSequenceID.EnumType.dataConf);
            confirmation.setSeqID(seqId);
            
            confirmation.setGroupID(pubRegistration.getGroupID());
            confirmation.setRequestID(pubRegistration.getRequestID());
            
            PodeDataDestination destination = populateDestinations(pubRegistration.getRegData());
            
            confirmation.setDestination(destination);
            
            String hash = SHAHasher.sha256Hash(registrationRequest.getEncodedRegistrationMessage());
            Sha256Hash shaHash = new Sha256Hash(hash.getBytes());
            confirmation.setHash(shaHash);
            
                    
            getRegDao().updateRegistration(pubRegistration);
            
            registrationResponse = new RegistrationMessage();
            registrationResponse.setEncodedRegistrationMessage(encodeMessage(confirmation));
            
        } catch (Exception ex) {
            logger.error("Error processing registration: ",ex);
        }

        return registrationResponse;
    }
    
    
    private PodeDataDestination populateDestinations(PodeDataTypes regData){
        PodeDataDestination destination = new PodeDataDestination();
        byte[] value = regData.getValue();
        if(value.length == 1){
            Integer i = new Integer(value[0]);
            if((i & SPEED_MASK) == SPEED_MASK){
                String messageType = ODEMessageType.SPEED.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                destination.setSpeedURL(queueInfo.getWsHost()+queueInfo.getWsURL());
            }
            if((i & OCC_MASK) == OCC_MASK){
                String messageType = ODEMessageType.OCCUPANCY.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                destination.setOccupancyURL(queueInfo.getWsHost()+queueInfo.getWsURL());
            }
            if((i & VOLUME_MASK) == VOLUME_MASK){
                String messageType = ODEMessageType.VOLUME.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                destination.setVolumeURL(queueInfo.getWsHost()+queueInfo.getWsURL());
            }
            if((i & TRAVEL_MASK) == TRAVEL_MASK){
                String messageType = ODEMessageType.TRAVEL.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                destination.setTravelTimeURL(queueInfo.getWsHost()+queueInfo.getWsURL());
            }
            if((i & WEATHER_MASK) == WEATHER_MASK){
                String messageType = ODEMessageType.WEATHER.dataSource();
                QueueInfo queueInfo = getQueueForMessageType(messageType);
                destination.setWeatherURL(queueInfo.getWsHost()+queueInfo.getWsURL());
            }
        }
        return destination;
    }
    
    private QueueInfo getQueueForMessageType(String messageType){
        return getRegDao().getQueueForMessageType(messageType);
    }
    
    
    @RequestMapping(value = "registerPublish", method = RequestMethod.POST, consumes  = "application/xml", produces = "application/xml")
    public
    @ResponseBody
    ODERegistrationResponse registerPublicationIntent(@RequestBody ODERegistrationRequest registrationRequest) {
        ODERegistrationResponse registrationResponse = null;
        registrationRequest = getRegDao().storeRegistration(registrationRequest);
        QueueInfo qInfo = getQueueNameForRegistration(registrationRequest);
        if (qInfo != null) {
            registrationResponse = createRegResponse(registrationRequest, qInfo);
        }

        return registrationResponse;
    }

    private ODERegistrationResponse createRegResponse(ODERegistrationRequest registrationRequest, QueueInfo qInfo) {
        ODERegistrationResponse resp = new ODERegistrationResponse();
        resp.setAgentId(registrationRequest.getAgentId());
        resp.setMessageType(registrationRequest.getMessageType());
        resp.setRegion(registrationRequest.getRegion());
        resp.setRegistrationId(registrationRequest.getRegistrationId());
        resp.setRegistrationType(registrationRequest.getRegistrationType());

        resp.setQueueConnFact(qInfo.getQueueConnectionFactory());
        resp.setQueueName(qInfo.getQueueName());
        resp.setQueueHostURL(qInfo.getTargetAddress());
        resp.setQueueHostPort(qInfo.getTargetPort());
        resp.setPublishWebServiceAddress(qInfo.getWsURL());
        return resp;
    }

    private QueueInfo getQueueNameForRegistration(ODERegistrationRequest registrationRequest) {
        return getRegDao().getQueueForRegistration(registrationRequest);
    }

    public RegistrationDAO getRegDao() {
        return regDao;
    }

    public void setRegDao(RegistrationDAO regDao) {
        this.regDao = regDao;
    }

}
