package com.leidos.ode.core.rde.data;

import java.sql.Date;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 9/18/14
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class RDEArchiveInfo {

    private String id;
    private String msgType;
    private String region;
    private String metadataLoc;
    private Date archiveDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMetadataLoc() {
        return metadataLoc;
    }

    public void setMetadataLoc(String metadataLoc) {
        this.metadataLoc = metadataLoc;
    }

    public Date getArchiveDate() {
        return archiveDate;
    }

    public void setArchiveDate(Date archiveDate) {
        this.archiveDate = archiveDate;
    }
}
