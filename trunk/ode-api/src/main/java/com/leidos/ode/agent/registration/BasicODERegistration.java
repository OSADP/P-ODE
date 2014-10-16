package com.leidos.ode.agent.registration;

import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.registration.response.ODERegistrationResponse;
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
import java.nio.charset.Charset;
import org.apache.http.entity.ContentType;

public class BasicODERegistration implements ODERegistration {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);
    private String registrationBaseUrl;
    private String registrationEndpoint;

    @Override
    public ODERegistrationResponse register(ODERegistrationRequest registrationRequest) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            StringWriter stringWriter = new StringWriter();
            JAXBContext registrationInfoContext = JAXBContext.newInstance(ODERegistrationRequest.class);
            Marshaller marshaller = registrationInfoContext.createMarshaller();
            marshaller.marshal(registrationRequest, stringWriter);
            httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(getRegistrationUrl());
//            httpPost.addHeader("Content-Type", "application/xml");
//            httpPost.addHeader("dataType", "application/xml");
//            httpPost.addHeader("Accept", "application/xml");
//            StringEntity entity = new StringEntity(stringWriter.getBuffer().toString(),ContentType.APPLICATION_XML);
            StringEntity entity = new StringEntity(stringWriter.getBuffer().toString());
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
            return marshallRegistrationResponseFromHttpResponse(closeableHttpResponse);
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
        return null;
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

    private String getRegistrationUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getRegistrationBaseUrl());
        stringBuilder.append("/");
        stringBuilder.append(getRegistrationEndpoint());
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
}
