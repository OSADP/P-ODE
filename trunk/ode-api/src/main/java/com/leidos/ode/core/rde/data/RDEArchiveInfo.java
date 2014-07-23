package com.leidos.ode.core.rde.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;

@Document(collection = "archives")
public class RDEArchiveInfo {

    @Id
	private String id;
	private String msgType;
	private String region;
	private String metadataLoc;
	private Date archiveDate;

    public RDEArchiveInfo(){
        this.id = id;
    }

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
