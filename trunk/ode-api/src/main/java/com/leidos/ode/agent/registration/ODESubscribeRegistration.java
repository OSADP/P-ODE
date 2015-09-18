/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.registration;

import com.leidos.ode.data.ConnectionPoint;
import com.leidos.ode.data.DDay;
import com.leidos.ode.data.DFullTime;
import com.leidos.ode.data.DHour;
import com.leidos.ode.data.DMinute;
import com.leidos.ode.data.DMonth;
import com.leidos.ode.data.DYear;
import com.leidos.ode.data.DataSubscriptionResponse;
import com.leidos.ode.data.GroupID;
import com.leidos.ode.data.IPv4Address;
import com.leidos.ode.data.IpAddress;
import com.leidos.ode.data.PodeDataTypes;
import com.leidos.ode.data.PodeDialogID;
import com.leidos.ode.data.PodeProtocol;
import com.leidos.ode.data.PodeRealTimeData;
import com.leidos.ode.data.PodeReplayData;
import com.leidos.ode.data.PodeSubData;
import com.leidos.ode.data.PodeSubReqType;
import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.PortNumber;
import com.leidos.ode.data.SemiSequenceID;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.registration.RegistrationMessage;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.util.ODEMessageType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *
 * @author cassadyja
 */
public class ODESubscribeRegistration extends BasicODERegistration{

    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy Hh:mm:ss");
    
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
            PodeReplayData rpData = new PodeReplayData();
            DFullTime start = getFullTimeForDateString(registrationRequest.getReplayStartDate());
            rpData.setStartTime(start);
            DFullTime end = getFullTimeForDateString(registrationRequest.getReplayEndDate());
            rpData.setEndTIme(end);
            type.selectReplayData(rpData);
        }
        subRequest.setType(type);
        
        
        PodeSubData subData = new PodeSubData();
        int i = Integer.parseInt(registrationRequest.getDataTypes(),2);
        BigInteger bi = new  BigInteger(Integer.toString(i));
        PodeDataTypes dataTypes = new PodeDataTypes(bi.toByteArray());
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
    
    private DFullTime getFullTimeForDateString(String dateString){
        DFullTime fullTime = new DFullTime();
        
        
        
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
