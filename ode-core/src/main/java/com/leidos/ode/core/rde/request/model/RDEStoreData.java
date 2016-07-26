package com.leidos.ode.core.rde.request.model;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/21/14
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class RDEStoreData implements RDEData {

    private String type;
    private String date;
    private byte[] data;

    public RDEStoreData(String type, byte[] data, String date) {
        this.type = type;
        this.data = data;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
