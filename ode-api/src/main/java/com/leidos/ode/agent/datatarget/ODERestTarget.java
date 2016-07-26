package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.registration.RegistrationResponse;
import com.leidos.ode.data.PodeDataDestination;
import com.leidos.ode.data.PodeDataDistribution;
import com.leidos.ode.util.ODEMessageType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IEncoder;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author cassadyja, lamde
 */
public class ODERestTarget implements ODEDataTarget {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private CloseableHttpClient httpClient;
    private CloseableHttpResponse httpResponse;
    private HttpPost httpPost;
    private Map<ODEMessageType,String> targetURLs;
    private Map<ODEMessageType,HttpPost> httpPosts;
    
    @Override
    public void configure(RegistrationResponse registrationResponse) throws DataTargetException {
        populateURLs(registrationResponse);
        httpClient = HttpClientBuilder.create().build();
        
//        String hostURL = registrationResponse.getQueueHostURL();
//        int hostPort = registrationResponse.getQueueHostPort();
//        String queueName = registrationResponse.getPublishWebServiceAddress();
//
//        String address = new StringBuilder()
//                .append("http://")
//                .append(hostURL)
//                .append(":")
//                .append(hostPort)
//                .append(queueName)
//                .toString();
//
//        logger.debug("Configuring ODERegistrationResponse with endpoint address: " + address);
//
//        httpPost = new HttpPost(address);
//        httpPost.addHeader("Content-Type", "application/xml");

    }

    private void populateURLs(RegistrationResponse registrationResponse){
        PodeDataDestination dest = registrationResponse.getPubResponse().getDestination();
        targetURLs = new HashMap<ODEMessageType, String>();
        httpPosts = new HashMap<ODEMessageType, HttpPost>();
        
        if(dest.getOccupancyURL() != null){
            targetURLs.put(ODEMessageType.OCCUPANCY, dest.getOccupancyURL());
            HttpPost post = new HttpPost(dest.getOccupancyURL());
            post.addHeader("Content-Type", "application/xml");
            httpPosts.put(ODEMessageType.OCCUPANCY,post);
        }
        if(dest.getSpeedURL() != null){
            targetURLs.put(ODEMessageType.SPEED, dest.getSpeedURL());
            HttpPost post = new HttpPost(dest.getSpeedURL());
            post.addHeader("Content-Type", "application/xml");
            httpPosts.put(ODEMessageType.SPEED,post);
        }
        if(dest.getTravelTimeURL() != null){
            targetURLs.put(ODEMessageType.TRAVEL, dest.getTravelTimeURL());
            HttpPost post = new HttpPost(dest.getTravelTimeURL());
            post.addHeader("Content-Type", "application/xml");
            httpPosts.put(ODEMessageType.TRAVEL,post);
        }
        if(dest.getVolumeURL() != null){
            targetURLs.put(ODEMessageType.VOLUME, dest.getVolumeURL());
            HttpPost post = new HttpPost(dest.getVolumeURL());
            post.addHeader("Content-Type", "application/xml");
            httpPosts.put(ODEMessageType.VOLUME,post);
        }
        if(dest.getWeatherURL()!= null){
            targetURLs.put(ODEMessageType.WEATHER, dest.getWeatherURL());
            HttpPost post = new HttpPost(dest.getWeatherURL());
            post.addHeader("Content-Type", "application/xml");
            httpPosts.put(ODEMessageType.WEATHER,post);
        }
        if (dest.getIncidentURL() != null) {
            targetURLs.put(ODEMessageType.INCIDENT, dest.getIncidentURL());
            HttpPost post = new HttpPost(dest.getIncidentURL());
            post.addHeader("Content-Type", "application/xml");
            httpPosts.put(ODEMessageType.INCIDENT, post);
        }
        
        
    }
    
    @Override
    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        //Going to continue using the ODEAgentMessage, but only going to send 
        //the raw bytes of the new message.
        Map<ODEMessageType, List<PodeDataDistribution>> messages = message.getPodeMessageList();
        if (messages != null) {

            Iterator<ODEMessageType> it = messages.keySet().iterator();
            while (it.hasNext()) {
                ODEMessageType messageType = it.next();
                httpPost = httpPosts.get(messageType);
                List<PodeDataDistribution> list = messages.get(messageType);
                logger.debug("Sending [" + list.size() + "] messages of Type [" + messageType.name() + "]");
                for (PodeDataDistribution data : list) {
                    try {
                        ODEAgentMessage agentMessage = new ODEAgentMessage();
                        agentMessage.setMessageId(message.getMessageId());
                        agentMessage.setMessagePayload(encodePodeMessage(data));
                        agentMessage.setMessagePayloadBase64(DatatypeConverter.printBase64Binary(agentMessage.getMessagePayload()));
                        StringEntity entity = createEntity(agentMessage);
                        httpPost.setEntity(entity);
                        logger.debug("Sending message to target.");
                        httpResponse = httpClient.execute(httpPost);
                        HttpEntity responseEntity = httpResponse.getEntity();
                        String responseString = EntityUtils.toString(responseEntity);
                        logger.debug("Target response: " + responseString);
                        Thread.sleep(100);
                    } catch (IOException e) {
                        logger.error(e.getLocalizedMessage());
                    } catch (JAXBException e) {
                        logger.error(e.toString());
                    } catch (Exception e) {
                        logger.error(e.toString(), e);
                    }
                }
            }
        }
    }
    
    private byte[] encodePodeMessage(PodeDataDistribution data) throws Exception{
        IEncoder encoder = CoderFactory.getInstance().newEncoder("BER");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        encoder.encode(data, bos);
        return bos.toByteArray();
    }

    private StringEntity createEntity(ODEAgentMessage message) throws JAXBException, UnsupportedEncodingException {
        StringWriter stringWriter = new StringWriter();
//        JAXBContext registrationInfoContext = JAXBContext.newInstance(ODEAgentMessage.class);
        JAXBContext registrationInfoContext = JAXBContext.newInstance(
                    "com.leidos.ode.agent.data:com.leidos.ode.agent.data.vdot:com.leidos.ode.agent.data.ritis:com.leidos.ode.agent.data.bsm:com.leidos.ode.agent.data.blufax:com.fastlanesw.bfw:com.leidos.ode.agent.data.wxde");

        Marshaller marshaller = registrationInfoContext.createMarshaller();
        marshaller.marshal(message, stringWriter);
//        httpClient = HttpClientBuilder.create().build();
        StringEntity entity = new StringEntity(stringWriter.getBuffer().toString());
        return entity;
    }

    @Override
    public void close() {
        try {
            logger.debug("Closing target resources.");

            if (httpClient != null) {
                httpClient.close();
            }
            if (httpResponse != null) {
                httpResponse.close();
            }
        } catch (IOException e) {
            logger.error("Failed to close target resources.");
            logger.error(e.getLocalizedMessage());
        }
    }
}
