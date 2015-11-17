package com.leidos.ode.logging;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Class representing a LogBean in MongoDB.
 *
 * @author lamde
 */
public class LogBean {

    @Id
    private String id;
    private Date startTime, endTime;
    private ODELogger.ODEStage odeStage;
    private String messageId;
    private String component;
    private String system;
    
    public LogBean(Date startTime, Date endTime, ODELogger.ODEStage odeStage, String messageId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.odeStage = odeStage;
        this.messageId = messageId;
    }

    public LogBean(Date startTime, Date endTime, ODELogger.ODEStage odeStage, String messageId, String system) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.odeStage = odeStage;
        this.messageId = messageId;
        this.system = system;
    }
    
    
    public LogBean(Date startTime, ODELogger.ODEStage odeStage, String messageId, String system) {
        this(startTime, null, odeStage, messageId, system);
    }    
    
    public LogBean(Date startTime, ODELogger.ODEStage odeStage, String messageId) {
        this(startTime, null, odeStage, messageId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public ODELogger.ODEStage getOdeStage() {
        return odeStage;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Log [id=")
                .append(getId())
                .append(", startTime=")
                .append(getStartTime().getTime())
                .append(", endTime=")
                .append(getEndTime().getTime())
                .append((", odeStage="))
                .append(getOdeStage())
                .append(", messageId=")
                .append(getMessageId())
                .append(", component=")
                .append(getComponent())
                .toString();
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return component;
    }

    /**
     * @param component the component to set
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system the system to set
     */
    public void setSystem(String system) {
        this.system = system;
    }
}
