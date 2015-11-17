package com.leidos.ode.collector.datasource.pull;

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

import java.io.IOException;

/**
 * VDOT data source. Handles
 * requests to ITERIS for VDOT data using the Digest Authentication scheme required by the site.
 * Polls for VDOT data using Apache HTTP libraries.
 *
 * @author lamde
 */
public class VDOTDataSource extends RestPullDataSource {

    private Header firstHeader;//Necessary to keep track of for Digest authentication

    @Override
    public void startDataSource() {
        super.startDataSource();
        try {
            CloseableHttpResponse closeableHttpResponse = getHttpClient().execute(getHttpGet());

            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
//            getLogger().debug("Using digest scheme. Initial status code: " + statusCode);

            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                firstHeader = closeableHttpResponse.getFirstHeader(AUTH.WWW_AUTH);
//                getLogger().debug("authHeader: " + firstHeader);
                closeableHttpResponse.close();
                executeDataSourceThread();
            }
        } catch (ClientProtocolException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        }
    }

    @Override
    public byte[] pollDataSource() {
        try {
            DigestScheme digestScheme = new DigestScheme();
            digestScheme.processChallenge(firstHeader);

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
            Thread.sleep(getRequestLimit());
            return responseBytes;
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (MalformedChallengeException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected String buildWfsFilter() {
        return new StringBuilder()
                .append("&typeName=orci:")
                .append(getFeedName())
                .append(getWfsFilter())
                .toString();
    }
}
