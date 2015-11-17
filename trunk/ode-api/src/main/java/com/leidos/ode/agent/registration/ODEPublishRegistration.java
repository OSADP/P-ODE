/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.registration;

import com.leidos.ode.data.GroupID;
import com.leidos.ode.data.PodeDataConfirmation;
import com.leidos.ode.data.PodeDataPulicationRegistration;
import com.leidos.ode.data.PodeDataTypes;
import com.leidos.ode.data.PodeDialogID;
import com.leidos.ode.data.SemiSequenceID;
import com.leidos.ode.data.ServiceProviderID;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.registration.RegistrationMessage;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.util.ODEMessageType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Arrays;
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
public class ODEPublishRegistration extends BasicODERegistration{

    @Override
    protected RegistrationResponse doRegistration(ODERegistrationRequest registrationRequest, ServiceRequest serviceRequest) {
        RegistrationResponse response = null;
        try {
            RegistrationMessage message = new RegistrationMessage();
            
            PodeDataPulicationRegistration registration = populatePublishRegistration(registrationRequest, serviceRequest);
            message.setEncodedRegistrationMessage(encodePublishRegistration(registration));
            
            StringEntity entity = createEntityForMessage(message);
            
            CloseableHttpResponse closeableHttpResponse = postMessage(entity, getRegistrationUrl());
            PodeDataConfirmation confirm = unmarshallConfirmation(closeableHttpResponse);
            response = new RegistrationResponse();
            response.setPubResponse(confirm);
            
        } catch (Exception ex) {
            Logger.getLogger(ODEPublishRegistration.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        return response;
    }
    
    private PodeDataPulicationRegistration populatePublishRegistration(ODERegistrationRequest registrationRequest, ServiceRequest serviceRequest){
        PodeDataPulicationRegistration registration = new PodeDataPulicationRegistration();
        
        PodeDialogID dId = new PodeDialogID();
        dId.setValue(PodeDialogID.EnumType.podeDataPulicationRegistration);
        registration.setDialogID(dId);
        
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        registration.setSeqID(seqId);
        registration.setGroupID(serviceRequest.getGroupID());
        registration.setRequestID(serviceRequest.getRequestID());
        
        
        ServiceProviderID sourceId = new ServiceProviderID();
        if(registrationRequest.getAgentId().getBytes().length >=4){
            sourceId.setValue(Arrays.copyOfRange(registrationRequest.getAgentId().getBytes(), 0, 4));
        }else{
            byte[] bytes = new byte[4];
            for(int i=0;i<registrationRequest.getAgentId().getBytes().length;i++){
                bytes[i] = registrationRequest.getAgentId().getBytes()[i];
            }
            for(int i=registrationRequest.getAgentId().getBytes().length;i< 4;i++){
                bytes[i] = 0x00;
            }
            sourceId.setValue(bytes);
        }
        registration.setSource(sourceId);
        
        //TODO: set this.  Need to have a list of messages based on source feed.
        int i = Integer.parseInt(registrationRequest.getDataTypes(),2);
        BigInteger bi = new  BigInteger(Integer.toString(i));
        PodeDataTypes dataTypes = new PodeDataTypes(bi.toByteArray());        
        registration.setRegData(dataTypes);
        
        return registration;
    }
    
    
    private byte[] encodePublishRegistration(PodeDataPulicationRegistration registration) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IEncoder encoder = CoderFactory.getInstance().newEncoder("BER");
        encoder.encode(registration, bos);
        return bos.toByteArray();
    }
    
    
    private PodeDataConfirmation unmarshallConfirmation(CloseableHttpResponse closeableHttpResponse) throws IOException, ParserConfigurationException, JAXBException, SAXException, Exception{
        PodeDataConfirmation confirmation = null;
        
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
        confirmation = decoder.decode(bis, PodeDataConfirmation.class);
        return confirmation;
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
        return srvRequest;
    }
    
    
}
