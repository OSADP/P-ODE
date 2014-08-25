package com.leidos.ode.collector;

import com.leidos.ode.collector.datasource.DataSourceException;
import com.leidos.ode.collector.datasource.RestPullDataSource;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

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

    private String wfsBaseUrl;
    private String xmlBaseUrl;

    private WebTarget webTarget;

    public VDOTDataSource() {
    }

    @Override
    public void startDataSource() throws DataSourceException {
        if (webTarget == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getSourceAddress()).append(getWfsBaseUrl());
            String address = stringBuilder.toString();
            setSourceAddress(address);
            logger.debug(TAG + "- Starting source with endpoint address: " + address);

            Client client = ClientBuilder.newClient();
            //TODO Determine best authentication mode. Basic DOES NOT WORK for VDOT, but Digest and Universal do.
            //TODO See 5.9.1. Http Authentication Support of https://jersey.java.net/documentation/latest/client.html#d0e4910 for more info on modes.
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.universal(getUser(), getPass());
            client.register(feature);
            webTarget = client.target(UriBuilder.fromUri(address));
        }
    }

    public String retrieveData(String recordName) {
        return getSourceAddress() + getWFSFilter(recordName);
    }

    private String getRequestURIXML(String recordName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getXmlBaseUrl()).append("/").append(recordName).append("/");
        return stringBuilder.toString();
    }

    private String getWFSFilter(String recordName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&typeName=");
        stringBuilder.append("orci:");
        stringBuilder.append(recordName);
        stringBuilder.append("&");
        stringBuilder.append("bbox=");
        stringBuilder.append(getEmulatorWFSbbox());
        return stringBuilder.toString();
    }

    private String getEmulatorWFSbbox() {
        return "38.856259,-77.35548,38.882853,-77.259612";
    }

    public String getWfsBaseUrl() {
        return wfsBaseUrl;
    }

    public void setWfsBaseUrl(String wfsBaseUrl) {
        this.wfsBaseUrl = wfsBaseUrl;
    }

    public String getXmlBaseUrl() {
        return xmlBaseUrl;
    }

    public void setXmlBaseUrl(String xmlBaseUrl) {
        this.xmlBaseUrl = xmlBaseUrl;
    }
}
