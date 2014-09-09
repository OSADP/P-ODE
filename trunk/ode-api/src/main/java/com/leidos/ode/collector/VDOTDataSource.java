package com.leidos.ode.collector;

import com.leidos.ode.collector.datasource.RestPullDataSource;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 8/18/14
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class VDOTDataSource extends RestPullDataSource {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private CloseableHttpResponse response;

    @Override
    public void startDataSource() throws DataSourceException {
        super.startDataSource();
        try {
            //Initial request without credentials returns "HTTP/1.1 401 Unauthorized"
            response = getHttpClient().execute(getHttpGet());

            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("Status code: " + statusCode);

            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                new Thread(new RestPullListener()).start();
            }
        } catch (ClientProtocolException e) {
            logger.error(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        } finally {
            if (getHttpClient() != null) {
                try {
                    getHttpClient().close();
                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    protected String getWFSFilter() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&typeName=");
        stringBuilder.append("orci:");
        stringBuilder.append(getFeedName());
        stringBuilder.append("&");
        stringBuilder.append("bbox=");
        stringBuilder.append(getEmulatorWFSbbox());
//        stringBuilder.append("&propertyName=");
//        stringBuilder.append(getEmulatorPropertyNames());
        return stringBuilder.toString();
    }

    /**
     * Returns the coordinate bounding box filter for the request.
     *
     * @return String representing bounding box.
     */
    private String getEmulatorWFSbbox() {
        return "38.856259,-77.35548,38.882853,-77.259612";
    }

    private class RestPullListener implements Runnable {
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    //Get current current "WWW-Authenticate" header from response
                    // WWW-Authenticate:Digest realm="My Test Realm", qop="auth",
                    //nonce="cdcf6cbe6ee17ae0790ed399935997e8", opaque="ae40d7c8ca6a35af15460d352be5e71c"
                    Header authHeader = response.getFirstHeader(AUTH.WWW_AUTH);
                    response.close();

                    logger.debug("authHeader: " + authHeader);

                    DigestScheme digestScheme = new DigestScheme();

                    //Parse realm, nonce sent by server.
                    digestScheme.processChallenge(authHeader);

                    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(getUsername(), getPassword());
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(AuthScope.ANY, credentials);
                    HttpClientContext httpClientContext = HttpClientContext.create();
                    httpClientContext.setCredentialsProvider(credentialsProvider);

                    CloseableHttpResponse closeableHttpResponse = getHttpClient().execute(getHttpGet(), httpClientContext);
//                String responseString = EntityUtils.toString(responseBody.getEntity());
                    HttpEntity responseEntity = closeableHttpResponse.getEntity();
                    byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
                    EntityUtils.consume(responseEntity);
                    closeableHttpResponse.close();

                    getCollectorDataSourceListener().dataReceived(responseBytes);
                }
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            } catch (MalformedChallengeException e) {
                logger.error(e.getLocalizedMessage());
            } finally {
                if (getHttpClient() != null) {
                    try {
                        getHttpClient().close();
                        response.close();
                    } catch (IOException e) {
                        logger.error(e.getLocalizedMessage());
                    }
                }
            }
        }
    }

//    /**
//     * Returns the list of properties to return in the request.
//     *
//     * @return String representing properties to return
//     */
//    private String getEmulatorPropertyNames() {
//        return "orci:speed,orci:volume,orci:occupancy";
//    }
}
