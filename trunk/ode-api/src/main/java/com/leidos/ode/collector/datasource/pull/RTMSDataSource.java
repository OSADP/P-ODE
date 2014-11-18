package com.leidos.ode.collector.datasource.pull;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/17/14
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class RTMSDataSource extends RestPullDataSource {

    private String service;

    @Override
    protected String buildWfsFilter() {
        return getService();
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
