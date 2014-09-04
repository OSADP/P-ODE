package com.leidos.ode.logging;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Class representing a log document in Mongo.
 *
 * @author lamde
 */

@Document(collection = "logs")
public class LogBean {

    @Id
    private String id;
    private Date startTime, endTime;
    private ODELogger.ODEStage odeStage;
    private String messageId;

    public LogBean(Date startTime, Date endTime, ODELogger.ODEStage odeStage, String messageId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.odeStage = odeStage;
        this.messageId = messageId;
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

    public void setEndTime(Date endTime){
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Log [id=");
        stringBuilder.append(getId());
        stringBuilder.append(", startTime=");
        stringBuilder.append(getStartTime().getTime());
        stringBuilder.append(", endTime=");
        stringBuilder.append(getEndTime().getTime());
        stringBuilder.append((", odeStage="));
        stringBuilder.append(getOdeStage());
        stringBuilder.append(", messageId=");
        stringBuilder.append(getMessageId());
        return stringBuilder.toString();
    }
}
