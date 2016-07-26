/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.registration;

import com.leidos.ode.data.*;
import com.leidos.ode.registration.RegistrationMessage;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.util.ODEMessageType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;
import org.bn.types.BitString;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author cassadyja
 */
public class ODESubscribeRegistration extends BasicODERegistration{

    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy Hh:mm:ss");
    private SimpleDateFormat replaySDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String TAG = getClass().getSimpleName();
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TAG);    
    
    @Override
    protected RegistrationResponse doRegistration(ODERegistrationRequest registrationRequest, ServiceRequest serviceRequest) {
         RegistrationResponse response = null;
        try {
            RegistrationMessage message = new RegistrationMessage();
            
            PodeSubscriptionRequest subRequest = populateSubscriptionRequest(registrationRequest, serviceRequest);
            message.setEncodedRegistrationMessage(encodeSubscriptionRequest(subRequest));
            
            StringEntity entity = createEntityForMessage(message);
            
            CloseableHttpResponse closeableHttpResponse = postMessage(entity, getRegistrationUrl());
            
            DataSubscriptionResponse subResp = unmarshallSubscriptionResponse(closeableHttpResponse);
            response = new RegistrationResponse();                    
            response.setSubResponse(subResp);
            
        } catch (Exception ex) {
            Logger.getLogger(ODESubscribeRegistration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
    
    private PodeSubscriptionRequest populateSubscriptionRequest(ODERegistrationRequest registrationRequest, ServiceRequest serviceRequest){
        PodeSubscriptionRequest subRequest = new PodeSubscriptionRequest();
        
        
        PodeDialogID dId = new PodeDialogID();
        dId.setValue(PodeDialogID.EnumType.podeDataSubscriptionRegistration);
        subRequest.setDialogID(dId);
        
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.subscriptionReq);
        subRequest.setSeqID(seqId);
        
        subRequest.setGroupID(serviceRequest.getGroupID());
        subRequest.setRequestID(serviceRequest.getRequestID());

        PodeSubReqType type = new PodeSubReqType();
        if(registrationRequest.getSubscriptionType().equalsIgnoreCase("RealTime")){
            PodeRealTimeData rtData = new PodeRealTimeData(new byte[]{0x01});
            type.selectRealTimeData(rtData);
        }else{
            try {
                PodeReplayData rpData = new PodeReplayData();
                DFullTime start = getFullTimeForDateString(registrationRequest.getReplayStartDate());
                rpData.setStartTime(start);
                DFullTime end = getFullTimeForDateString(registrationRequest.getReplayEndDate());
                rpData.setEndTIme(end);
                type.selectReplayData(rpData);
                logger.debug("Sending Replay Dates Start: "+type.getReplayData().getStartTime().getYear().getValue()+"-"+
                                            type.getReplayData().getStartTime().getMonth().getValue()+"-"+
                                            type.getReplayData().getStartTime().getDay().getValue()+
                                " End: "+type.getReplayData().getEndTIme().getYear().getValue()+"-"+
                                            type.getReplayData().getEndTIme().getMonth().getValue()+"-"+
                                            type.getReplayData().getEndTIme().getDay().getValue());
                
                GeoRegion region = new GeoRegion();
                Position3D nw = new Position3D();
                
                Latitude nwlat = new Latitude(getLatitudeValue(registrationRequest.getReplayNorthWestCornerLat()));
                Longitude nwlon = new Longitude(getLongitudeValue(registrationRequest.getReplayNorthWestCornerLon()));
                nw.setLat(nwlat);
                nw.setLon(nwlon);
                region.setNwCorner(nw);
                Position3D se = new Position3D();
                Latitude selat = new Latitude(getLatitudeValue(registrationRequest.getReplaySouthEastCornerLat()));
                Longitude selon = new Longitude(getLongitudeValue(registrationRequest.getReplaySouthEastCornerLon()));
                se.setLat(selat);
                se.setLon(selon);
                region.setSeCorner(se);
                subRequest.setServiceRegion(region);
            } catch (ParseException ex) {
                logger.error("Error parsing Dates", ex);
            }
        }
        subRequest.setType(type);
        
        
        PodeSubData subData = new PodeSubData();
        int i = Integer.parseInt(registrationRequest.getDataTypes(),2);
        BigInteger bi = new  BigInteger(Integer.toString(i));
        byte[] typeMask = bi.toByteArray();

        // Ugly workaround because for some reason BigIntger(128) encodes as a 2 byte string
        // 2 byte strings here cause breakage in the core, so I've special cased this.
        if (i == 128) {
            typeMask = new byte[]{ (byte) 128 };
        }

        PodeDataTypes dataTypes = new PodeDataTypes();
        dataTypes.setValue(new BitString(typeMask));

        subData.setDataElements(dataTypes);
        subRequest.setSubData(subData);
        
        
        DFullTime end = new DFullTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(registrationRequest.getEndDate());
        
        
        int theDay = cal.get(Calendar.DAY_OF_MONTH);
        int theYear = cal.get(Calendar.YEAR);
        int theMonth = cal.get(Calendar.MONTH)+1;
        int theHour = cal.get(Calendar.HOUR_OF_DAY);
        int theMinute = cal.get(Calendar.MINUTE);
        
        
        
        DHour hour = new DHour(theHour);
        end.setHour(hour);
        DMinute minute = new DMinute(theMinute);
        end.setMinute(minute);
        DMonth month = new DMonth(theMonth);
        end.setMonth(month);
        DDay day = new DDay(theDay);                
        end.setDay(day);
        DYear year = new DYear(theYear);
        end.setYear(year);
        
        subRequest.setEndTime(end);
        
        PodeProtocol protocol = new PodeProtocol();
        if(registrationRequest.getSubscriptionProtocol().equalsIgnoreCase("udp")){
            protocol.setValue(PodeProtocol.EnumType.upd);
        }else{
            protocol.setValue(PodeProtocol.EnumType.tcp);
        }
        subRequest.setProtocol(protocol);
        
        return subRequest;
    }
    
    protected int getLatitudeValue(double lat){
        int base = 10000000;
        
        return (int)Math.round(base*lat);
    }
    
    protected int getLongitudeValue(double lon){
        int base = 10000000;
        return (int)Math.round(base*lon);
    }      
    
    private DFullTime getFullTimeForDateString(String dateString) throws ParseException{
        Calendar cal = Calendar.getInstance();
        Date d = replaySDF.parse(dateString);
        logger.debug("Parsed Date String of: "+dateString+" to Date Object: "+d);
        cal.setTime(d);
        DFullTime fullTime = new DFullTime();
        DHour hour = new DHour(cal.get(Calendar.HOUR_OF_DAY));
        fullTime.setHour(hour);
        DMinute min = new DMinute(cal.get(Calendar.MINUTE));
        fullTime.setMinute(min);
//        DSecond sec = new DSecond(cal.get(Calendar.SECOND));
//        fullTime.setSecond(sec);
        DMonth month = new DMonth(cal.get(Calendar.MONTH)+1);
        fullTime.setMonth(month);
        DDay day = new DDay(cal.get(Calendar.DAY_OF_MONTH));
        fullTime.setDay(day);
        DYear year = new DYear(cal.get(Calendar.YEAR));
        logger.debug("DYear Object Value: "+year.getValue());
        fullTime.setYear(year); 
        
        
        
        return fullTime;
    }
    
    private byte[] encodeSubscriptionRequest(PodeSubscriptionRequest subRequest) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IEncoder encoder = CoderFactory.getInstance().newEncoder("BER");
        encoder.encode(subRequest, bos);
        return bos.toByteArray();
    }
    
    
    private DataSubscriptionResponse unmarshallSubscriptionResponse(CloseableHttpResponse closeableHttpResponse) throws IOException, ParserConfigurationException, SAXException, JAXBException, Exception{
        DataSubscriptionResponse response = null;
        
        HttpEntity responseEntity = closeableHttpResponse.getEntity();
        byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
        EntityUtils.consume(responseEntity);
        String responseString = new String(responseBytes);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(responseString)));
        JAXBContext registrationResponseContext = JAXBContext.newInstance(RegistrationMessage.class);
        Unmarshaller unmarshaller = registrationResponseContext.createUnmarshaller();
        RegistrationMessage registrationMessage = (RegistrationMessage) unmarshaller.unmarshal(document);
      

        IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
        ByteArrayInputStream bis = new ByteArrayInputStream(registrationMessage.getEncodedRegistrationMessage());
        response = decoder.decode(bis, DataSubscriptionResponse.class);
        
        return response;        
    }

    @Override
    protected ServiceRequest buildServiceRequest(ODERegistrationRequest registrationRequest) {
        ServiceRequest srvRequest = new ServiceRequest();
        PodeDialogID dId = new PodeDialogID();
        dId.setValue(PodeDialogID.EnumType.podeDataSubscriptionRegistration);
        srvRequest.setDialogID(dId);
        
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.svcReq);
        srvRequest.setSeqID(seqId);
        
        
        GroupID gId = new GroupID(ODEMessageType.valueOf(registrationRequest.getMessageType()).getGroupId());
        srvRequest.setGroupID(gId);
        
        
        srvRequest.setRequestID(generateRandomByte(4));
        
        
        
        ConnectionPoint cp = new ConnectionPoint();
        IpAddress ip = new IpAddress();
        IPv4Address ip4 = new IPv4Address();
        String subIpAddress = registrationRequest.getSubscriptionReceiveAddress();
        byte[] addressBytes = null;
        if(subIpAddress.equalsIgnoreCase("localhost")){
            addressBytes = new byte[]{127,0,0,1};
        }else{
            addressBytes = new byte[4];
            String[] split = subIpAddress.split("\\.");
            for(int i=0;i<split.length;i++){
                addressBytes[i] = Byte.parseByte(split[i]);
            }
        }
        ip4.setValue(addressBytes);
        ip.selectIpv4Address(ip4);
                
        cp.setAddress(ip);
        PortNumber pn = new PortNumber(new Integer(registrationRequest.getSubscriptionReceivePort()));
        cp.setPort(pn);
        srvRequest.setDestination(cp);        
        return srvRequest;
    }
    
    
}
