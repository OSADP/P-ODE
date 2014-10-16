package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.http.entity.StringEntity;

/**
 * @author cassadyja, lamde
 */
public class ODERestTarget implements ODEDataTarget {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private CloseableHttpClient httpClient;
    private CloseableHttpResponse httpResponse;
    private HttpPost httpPost;

    public void configure(ODERegistrationResponse registrationResponse) throws DataTargetException {
        String hostURL = registrationResponse.getQueueHostURL();
        int hostPort = registrationResponse.getQueueHostPort();
        String queueName = registrationResponse.getPublishWebServiceAddress();

        String address = new StringBuilder()
                .append("http://")
                .append(hostURL)
                .append(":")
                .append(hostPort)
                .append(queueName)
                .toString();

        logger.debug("Configuring ODERegistrationResponse with endpoint address: " + address);

        httpClient = HttpClientBuilder.create().build();
        httpPost = new HttpPost(address);
        httpPost.addHeader("Content-Type", "application/xml");

    }

    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        if (httpPost != null) {
            try {
                StringEntity entity = createEntity(message);
                httpPost.setEntity(entity);
                logger.debug("Sending message to target.");
                httpResponse = httpClient.execute(httpPost);
                HttpEntity responseEntity = httpResponse.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                logger.debug("Target response: " + responseString);
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            } catch (JAXBException e) {
                logger.error(e.toString());
            } 
        }else{
            logger.warn("Unable to send message. Target has not been configured.");
        }
    }
    
    private StringEntity createEntity(ODEAgentMessage message) throws JAXBException, UnsupportedEncodingException{
        StringWriter stringWriter = new StringWriter();
//        JAXBContext registrationInfoContext = JAXBContext.newInstance(ODEAgentMessage.class);
        JAXBContext registrationInfoContext = JAXBContext.newInstance("com.leidos.ode.agent.data:com.leidos.ode.agent.data.vdot:com.leidos.ode.agent.data.ritis:com.leidos.ode.agent.data.bsm");
        
        Marshaller marshaller = registrationInfoContext.createMarshaller();
        marshaller.marshal(message, stringWriter);
        httpClient = HttpClientBuilder.create().build();
        StringEntity entity = new StringEntity(stringWriter.getBuffer().toString());
        return entity;
    }

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
