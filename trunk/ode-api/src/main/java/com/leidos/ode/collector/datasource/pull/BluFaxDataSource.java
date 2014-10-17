package com.leidos.ode.collector.datasource.pull;

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
    public byte[] pollDataSource() {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
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
