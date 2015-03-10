/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.core.dao;

import java.sql.Date;

/**
 *
 * @author cassadyja
 */
public class RegistrationRow {

    private int id;
    private String dialogId;
    private String sequenceId;
    private String groupId;
    private String requestId;
    private String subAddress;
    private String subPort;
    private String regType;
    private String dataTypes;
    private Date startDate;
    private Date endDate;
    private String subId;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the dialogId
     */
    public String getDialogId() {
        return dialogId;
    }

    /**
     * @param dialogId the dialogId to set
     */
    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    /**
     * @return the sequenceId
     */
    public String getSequenceId() {
        return sequenceId;
    }

    /**
     * @param sequenceId the sequenceId to set
     */
    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return the subAddress
     */
    public String getSubAddress() {
        return subAddress;
    }

    /**
     * @param subAddress the subAddress to set
     */
    public void setSubAddress(String subAddress) {
        this.subAddress = subAddress;
    }

    /**
     * @return the subPort
     */
    public String getSubPort() {
        return subPort;
    }

    /**
     * @param subPort the subPort to set
     */
    public void setSubPort(String subPort) {
        this.subPort = subPort;
    }

    /**
     * @return the regType
     */
    public String getRegType() {
        return regType;
    }

    /**
     * @param regType the regType to set
     */
    public void setRegType(String regType) {
        this.regType = regType;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the dataTypes
     */
    public String getDataTypes() {
        return dataTypes;
    }

    /**
     * @param dataTypes the dataTypes to set
     */
    public void setDataTypes(String dataTypes) {
        this.dataTypes = dataTypes;
    }

    /**
     * @return the subId
     */
    public String getSubId() {
        return subId;
    }

    /**
     * @param subId the subId to set
     */
    public void setSubId(String subId) {
        this.subId = subId;
    }
    
    
    
    
    
}
