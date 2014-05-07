package com.leidos.ode.core.rde.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;

@Document(collection = "RDEArchive")
public class RDEArchiveInfo {

    @Id
	private ObjectId objectId;
	private String msgType;
	private String region;
	private String metadataLoc;
	private Date archiveDate;

	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
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
