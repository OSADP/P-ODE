package com.leidos.ode.agent.registration;

import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.data.ServiceResponse;
import com.leidos.ode.registration.RegistrationMessage;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

public abstract class BasicODERegistration implements ODERegistration {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);
    private String registrationBaseUrl;
    private String registrationEndpoint;
    private String unregisterEndpoint;
    private String serviceRequestEndpoint;
    private ServiceRequest serviceRequest;
    private ServiceResponse serviceResponse;
    
//    @Override
//    public RegistrationResponse register(ODERegistrationRequest registrationRequest) {
//        CloseableHttpClient httpClient = null;
//        CloseableHttpResponse closeableHttpResponse = null;
//        try {
//            StringWriter stringWriter = new StringWriter();
//            JAXBContext registrationInfoContext = JAXBContext.newInstance(ODERegistrationRequest.class);
//            Marshaller marshaller = registrationInfoContext.createMarshaller();
//            marshaller.marshal(registrationRequest, stringWriter);
//            httpClient = HttpClientBuilder.create().build();
//            HttpPost httpPost = new HttpPost(getRegistrationUrl());
//            StringEntity entity = new StringEntity(stringWriter.getBuffer().toString());
//            entity.setContentType("application/xml");
//            
//            httpPost.setEntity(entity);
//            closeableHttpResponse = httpClient.execute(httpPost);
//            logger.debug(closeableHttpResponse.getStatusLine().getReasonPhrase());
//            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
//            //If the request was unsuccessful return
//            if (statusCode != HttpStatus.SC_OK) {
//                getLogger().error("Registration unsuccessful. Status code: " + statusCode);
//                return null;
//            }
//            return marshallRegistrationResponseFromHttpResponse(closeableHttpResponse);
//        } catch (UnsupportedEncodingException e) {
//            getLogger().error(e.getLocalizedMessage());
//        } catch (ClientProtocolException e) {
//            getLogger().error(e.getLocalizedMessage());
//        } catch (IOException e) {
//            getLogger().error(e.getLocalizedMessage());
//        } catch (JAXBException e) {
//            getLogger().error(e.getLocalizedMessage());
//        } finally {
//            try {
//                if (httpClient != null) {
//                    httpClient.close();
//                }
//                if (closeableHttpResponse != null) {
//                    closeableHttpResponse.close();
//                }
//            } catch (IOException e) {
//                getLogger().error(e.getLocalizedMessage());
//            }
//        }
//        return null;
//    }
//    
    
    @Override
    public RegistrationResponse register(ODERegistrationRequest registrationRequest){
        try {
            ServiceRequest sr = buildServiceRequest(registrationRequest);
            this.serviceRequest = sr;
            ServiceResponse response = doServiceRequest(registrationRequest, serviceRequest);
            this.serviceResponse = response;
            
            RegistrationResponse regResponse = doRegistration(registrationRequest, serviceRequest);
            return regResponse;
        } catch (Exception ex) {
            getLogger().error("Error registering",ex);
        }
        return null;
        
    }
    
    
    protected  abstract RegistrationResponse doRegistration(ODERegistrationRequest registrationRequest, ServiceRequest serviceRequest);
    
    protected abstract ServiceRequest buildServiceRequest(ODERegistrationRequest registrationRequest);
    
//    protected abstract void  handleRegistrationResponse(ODERegistrationRequest registrationRequest);
    
    protected CloseableHttpResponse postMessage(StringEntity entity, String url){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);
            entity.setContentType("application/xml");
            
            httpPost.setEntity(entity);
            closeableHttpResponse = httpClient.execute(httpPost);
            logger.debug(closeableHttpResponse.getStatusLine().getReasonPhrase());
            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            //If the request was unsuccessful return
            if (statusCode != HttpStatus.SC_OK) {
                getLogger().error("Registration unsuccessful. Status code: " + statusCode);
                return null;
            }
            return closeableHttpResponse;
        } catch (UnsupportedEncodingException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (ClientProtocolException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        } finally {
//            try {
//                if (httpClient != null) {
//                    httpClient.close();
//                }
////                if (closeableHttpResponse != null) {
////                    closeableHttpResponse.close();
////                }
//            } catch (IOException e) {
//                getLogger().error(e.getLocalizedMessage());
//            }
        }
            
        
        return null;
    }
    
    
    
    protected ServiceResponse doServiceRequest(ODERegistrationRequest registrationRequest, ServiceRequest srvRequest) throws Exception{
        ServiceResponse srvResponse = null;
        
        
        byte[] encodedServiceRequest = encodeMessage(srvRequest);
        RegistrationMessage message = new RegistrationMessage(encodedServiceRequest);
        
        StringEntity entity = createEntityForMessage(message);
        
        CloseableHttpResponse response = postMessage(entity, getServiceRequestUrl());
        
        srvResponse = unmarshellServiceResponse(response);
       
        return srvResponse;
    }

    protected StringEntity createEntityForMessage(RegistrationMessage message) throws JAXBException, UnsupportedEncodingException {
        StringWriter stringWriter = new StringWriter();
        JAXBContext registrationInfoContext = JAXBContext.newInstance(RegistrationMessage.class);
        Marshaller marshaller = registrationInfoContext.createMarshaller();
        marshaller.marshal(message, stringWriter);
        StringEntity entity = new StringEntity(stringWriter.getBuffer().toString());
        entity.setContentType("application/xml");
        return entity;
    }
    
    private ServiceResponse unmarshellServiceResponse(CloseableHttpResponse closeableHttpResponse) throws IOException, ParserConfigurationException, SAXException, JAXBException, Exception {
        ServiceResponse response = null;
            
            
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
        getLogger().debug("Successfully marshalled RegistrationMessage.");

        IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
        ByteArrayInputStream bis = new ByteArrayInputStream(registrationMessage.getEncodedRegistrationMessage());
        response = decoder.decode(bis, ServiceResponse.class);

        return response;
    }
    
    
    private byte[] encodeMessage(ServiceRequest srvRequest) throws Exception{
        IEncoder encoder =  CoderFactory.getInstance().newEncoder("BER");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        encoder.encode(srvRequest, os);
        return os.toByteArray();
          
    }
    
    protected byte[] generateRandomByte(int size){
        Random rand = new Random();
        byte[] bytes = new byte[size];
        rand.nextBytes(bytes);
        return bytes;
    }
    
    

    public String unregister(ODERegistrationRequest registrationRequest) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            StringWriter stringWriter = new StringWriter();
            JAXBContext registrationInfoContext = JAXBContext.newInstance(ODERegistrationRequest.class);
            Marshaller marshaller = registrationInfoContext.createMarshaller();
            marshaller.marshal(registrationRequest, stringWriter);
            httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(getUnRegisterUrl());
            StringEntity entity = new StringEntity(stringWriter.getBuffer().toString());
            entity.setContentType("application/xml");
            
            httpPost.setEntity(entity);
            closeableHttpResponse = httpClient.execute(httpPost);
            logger.debug(closeableHttpResponse.getStatusLine().getReasonPhrase());
            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            //If the request was unsuccessful return
            if (statusCode != HttpStatus.SC_OK) {
                getLogger().error("Unregistration unsuccessful. Status code: " + statusCode);
                return "ERROR "+ statusCode;
            }        
        } catch (UnsupportedEncodingException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (ClientProtocolException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (JAXBException e) {
            getLogger().error(e.getLocalizedMessage());
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
            } catch (IOException e) {
                getLogger().error(e.getLocalizedMessage());
            }
        }
        return "OK";
    }

    
    private ODERegistrationResponse marshallRegistrationResponseFromHttpResponse(CloseableHttpResponse closeableHttpResponse) throws JAXBException, IOException {
        try {
            HttpEntity responseEntity = closeableHttpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
            EntityUtils.consume(responseEntity);
            String responseString = new String(responseBytes);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(responseString)));
            JAXBContext registrationResponseContext = JAXBContext.newInstance(ODERegistrationResponse.class);
            Unmarshaller unmarshaller = registrationResponseContext.createUnmarshaller();
            ODERegistrationResponse odeRegistrationResponse = (ODERegistrationResponse) unmarshaller.unmarshal(document);
            getLogger().debug("Successfully marshalled ODERegistrationResponse.");
            return odeRegistrationResponse;
        } catch (ParserConfigurationException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (SAXException e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }

    protected String getRegistrationUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getRegistrationBaseUrl());
        stringBuilder.append("/");
        stringBuilder.append(getRegistrationEndpoint());
        return stringBuilder.toString();
    }
    private String getServiceRequestUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getRegistrationBaseUrl());
        stringBuilder.append("/");
        stringBuilder.append(getServiceRequestEndpoint());
        return stringBuilder.toString();
    }    
    
    private String getUnRegisterUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getRegistrationBaseUrl());
        stringBuilder.append("/");
        stringBuilder.append(getUnregisterEndpoint());
        return stringBuilder.toString();
    }    

    public String getRegistrationBaseUrl() {
        return registrationBaseUrl;
    }

    public void setRegistrationBaseUrl(String registrationBaseUrl) {
        this.registrationBaseUrl = registrationBaseUrl;
    }

    public String getRegistrationEndpoint() {
        return registrationEndpoint;
    }

    public void setRegistrationEndpoint(String registrationEndpoint) {
        this.registrationEndpoint = registrationEndpoint;
    }

    private Logger getLogger() {
        return logger;
    }

    /**
     * @return the unregisterEndpoint
     */
    public String getUnregisterEndpoint() {
        return unregisterEndpoint;
    }

    /**
     * @param unregisterEndpoint the unregisterEndpoint to set
     */
    public void setUnregisterEndpoint(String unregisterEndpoint) {
        this.unregisterEndpoint = unregisterEndpoint;
    }

    /**
     * @return the serviceRequestEndpoint
     */
    public String getServiceRequestEndpoint() {
        return serviceRequestEndpoint;
    }

    /**
     * @param serviceRequestEndpoint the serviceRequestEndpoint to set
     */
    public void setServiceRequestEndpoint(String serviceRequestEndpoint) {
        this.serviceRequestEndpoint = serviceRequestEndpoint;
    }

    /**
     * @return the serviceRequest
     */
    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    /**
     * @param serviceRequest the serviceRequest to set
     */
    public void setServiceRequest(ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    /**
     * @return the serviceResponse
     */
    public ServiceResponse getServiceResponse() {
        return serviceResponse;
    }

    /**
     * @param serviceResponse the serviceResponse to set
     */
    public void setServiceResponse(ServiceResponse serviceResponse) {
        this.serviceResponse = serviceResponse;
    }
    
    
    

}
