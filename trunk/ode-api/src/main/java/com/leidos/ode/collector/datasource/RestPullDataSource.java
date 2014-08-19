package com.leidos.ode.collector.datasource;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 7/14/14
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class RestPullDataSource extends PullDataSource {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    private String requestURI;
    private String requestParams;
    private WebTarget webTarget;

    @Override
    public void startDataSource() throws DataSourceException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSourceAddress()).append(getRequestURI()).append(getRequestParams());
        String address = stringBuilder.toString();
        logger.debug(TAG + "- Starting source with endpoint address: " + address);

        Client client = ClientBuilder.newClient();
        //TODO Determine best authentication mode. Basic DOES NOT WORK for VDOT, but Digest and Universal do.
        //TODO See 5.9.1. Http Authentication Support of https://jersey.java.net/documentation/latest/client.html#d0e4910 for more info on modes.
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.universal(getUser(), getPass());
        client.register(feature);
        webTarget = client.target(UriBuilder.fromUri(address));
    }

    @Override
    public byte[] getDataFromSource() throws DataSourceException {
        Response response = webTarget.request().get();
        String responseString = response.readEntity(String.class);
        System.out.println(responseString);

        response.close();
        return responseString.getBytes();
//        throw new UnsupportedOperationException("Not supported with REST yet.");
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String... requestParams) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/?");
        int index = 0;
        for (String param : requestParams) {
            stringBuilder.append(param);
            if (index < requestParams.length - 1) {
                stringBuilder.append("&");
            }
            index++;
        }
        this.requestParams = stringBuilder.toString();
    }

    public String getEmulatorWFS(String typeName){
        return "";
    };
}

