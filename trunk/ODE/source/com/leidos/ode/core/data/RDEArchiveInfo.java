package com.leidos.ode.core.data;

import java.sql.Date;

public class RDEArchiveInfo {
	private int id;
	private String msgType;
	private String region;
	private String metadataLoc;
	private Date archiveDate;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
