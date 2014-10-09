package com.leidos.ode.agent.data.ritis;

import generated.CollectionPeriod;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class RITISSpeedData implements RITISData {

    private String organizationId;
    private String networkId;
    private XMLGregorianCalendar timeStamp;
    private CollectionPeriod collectionPeriod;

    public CollectionPeriod getCollectionPeriod() {
        return collectionPeriod;
    }

    public void setCollectionPeriod(CollectionPeriod collectionPeriod) {
        this.collectionPeriod = collectionPeriod;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(XMLGregorianCalendar timeStamp) {
        this.timeStamp = timeStamp;
    }
}
