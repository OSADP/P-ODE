package com.leidos.ode.logging;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Serialized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 4/22/14
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
@Document(collection = "Log")
public class LogBean {

    @Id private ObjectId objectId;

    @Serialized private Date date;
    private String component;
    private String message;

    public LogBean(Date date, String component, String message) {
        this.date = date;
        this.component = component;
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    private void setDate(Date date) {
        this.date = date;
    }

    public String getComponent() {
        return component;
    }

    private void setComponent(String component) {
        this.component = component;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Log [objectId=");
        stringBuilder.append(objectId);
        stringBuilder.append(", timestamp=");
        stringBuilder.append(getDate().getTime());
        stringBuilder.append((", component="));
        stringBuilder.append(getComponent());
        stringBuilder.append(", message=");
        stringBuilder.append(getMessage());
        return stringBuilder.toString();
    }

}
