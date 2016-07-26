package com.leidos.ode.collector.datasource.pull.sdc;

/**
 * SDC query object
 */
public class SDCQuery {
    private String systemSubName;
    private Integer dialogID;
    private Integer vsmType;
    private Double nwLat;
    private Double nwLon;
    private Double seLat;
    private Double seLon;
    private String resultEncoding;

    public String getSystemSubName() {
        return systemSubName;
    }

    public void setSystemSubName(String systemSubName) {
        this.systemSubName = systemSubName;
    }

    public Integer getDialogID() {
        return dialogID;
    }

    public void setDialogID(Integer dialogID) {
        this.dialogID = dialogID;
    }

    public Integer getVsmType() {
        return vsmType;
    }

    public void setVsmType(Integer vsmType) {
        this.vsmType = vsmType;
    }

    public Double getNwLat() {
        return nwLat;
    }

    public void setNwLat(Double nwLat) {
        this.nwLat = nwLat;
    }

    public Double getNwLon() {
        return nwLon;
    }

    public void setNwLon(Double nwLon) {
        this.nwLon = nwLon;
    }

    public Double getSeLat() {
        return seLat;
    }

    public void setSeLat(Double seLat) {
        this.seLat = seLat;
    }

    public Double getSeLon() {
        return seLon;
    }

    public void setSeLon(Double seLon) {
        this.seLon = seLon;
    }

    public String getResultEncoding() {
        return resultEncoding;
    }

    public void setResultEncoding(String resultEncoding) {
        this.resultEncoding = resultEncoding;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        /*
         * NOTE: The online query builder appears to encode numbers as strings, but the actual SDC software appears
         * to accept numerical values in the JSON anyway. If issues are encountered with the SDC subscription, check
         * that possibility first.
         */

        sb.append("SUBSCRIBE:{\"systemSubName\":");
        sb.append("\"");
        sb.append(systemSubName);
        sb.append("\",");
        sb.append("\"dialogID\":");
        sb.append("\"");
        sb.append(dialogID);
        sb.append("\",");
        sb.append("\"vsmType\":");
        sb.append(vsmType);
        sb.append(",");
        sb.append("\"nwLat\":");
        sb.append("\"");
        sb.append(nwLat);
        sb.append("\",");
        sb.append("\"nwLon\":");
        sb.append("\"");
        sb.append(nwLon);
        sb.append("\",");
        sb.append("\"seLat\":");
        sb.append("\"");
        sb.append(seLat);
        sb.append("\",");
        sb.append("\"seLon\":");
        sb.append("\"");
        sb.append(seLon);
        sb.append("\",");
        sb.append("\"resultEncoding\":");
        sb.append("\"");
        sb.append(resultEncoding);
        sb.append("\"");
        sb.append("}");

        return sb.toString();
    }

}
