package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.data.ODERegistrationResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author cassadyja, lamde
 */
public class ODERestTarget implements ODEDataTarget {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private CloseableHttpClient httpClient;
    private CloseableHttpResponse httpResponse;
    private HttpPost httpPost;

    public void configure(ODERegistrationResponse regInfo) throws DataTargetException {
        String hostURL = regInfo.getQueueHostURL();
        int hostPort = regInfo.getQueueHostPort();
        String queueName = regInfo.getQueueName();

        String address = new StringBuilder()
                .append("http://")
                .append(hostURL)
                .append(":")
                .append(hostPort)
                .append("/")
                .append(queueName)
                .toString();

        logger.debug("Configuring ODERegistrationResponse with endpoint address: " + address);

        httpClient = HttpClientBuilder.create().build();
        httpPost = new HttpPost(address);
    }

    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        if (httpPost != null) {
            try {
                logger.debug("Sending message to target.");
                httpResponse = httpClient.execute(httpPost);
                HttpEntity responseEntity = httpResponse.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                logger.debug("Target response: " + responseString);
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        }else{
            logger.warn("Unable to send message. Target has not been configured.");
        }
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
