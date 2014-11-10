package com.leidos.ode.collector.datasource.pull;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/17/14
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class BluFaxDataSource extends RestPullDataSource {

    private String clientId;
    private String token;

    @Override
    public void startDataSource() {
        super.startDataSource();
        executeDataSourceThread();
    }

    @Override
    public byte[] pollDataSource() {
        try {
            getLogger().debug("Polling data source for feed: '" + getFeedName() + "'.");
            CloseableHttpResponse closeableHttpResponse = getHttpClient().execute(getHttpGet());
            HttpEntity responseEntity = closeableHttpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
            EntityUtils.consume(responseEntity);
            closeableHttpResponse.close();
            Thread.sleep(getRequestLimit());
            return responseBytes;
        } catch (ClientProtocolException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected String buildWfsFilter() {
        return new StringBuilder()
                .append(getFeedName())
                .append(".do")
                .append("?")
                .append("clientID=")
                .append(getClientId())
                .append("&")
                .append("token=")
                .append(getToken())
                .toString();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
