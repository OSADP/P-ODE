package com.leidos.ode.logging;

import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;

/**
 * The class representing the ODE application logger. Used for logging component events in the database. Includes
 * a Log4J logger instance for logging elsewhere.
 *
 * @author lamde
 */
public final class ODELogger {

    private LogBean logBean;
    private MongoTemplate mongoTemplate;

    /**
     * Marks the starting point of a given ODEStage in the log message.
     *
     * @param odeStage
     * @param messageId
     */
    public void start(ODEStage odeStage, String messageId) throws ODELoggerException {
        Date startTime = new Date();
//        if (logBean == null) {
            logBean = new LogBean(startTime, odeStage, messageId);
//        } else {
//            throw new ODELoggerException("Cannot start a log event when there is one that has not been finished.");
//        }
    }

    /**
     * Cancels the active log message, if one exists.
     */
    public void cancel() {
        if (logBean != null) {
            logBean = null;
        }
    }

    /**
     * Marks the completion point of a given ODEStage in the log message and saves the bean in Mongo.
     */
    public void finish() throws ODELoggerException {
        Date endTime = new Date();
        if (logBean != null) {
            logBean.setEndTime(endTime);
            // save the log bean
            getMongoTemplate().save(logBean);
            logBean = null;
        } else {
            throw new ODELoggerException("Cannot finish a log event that has not been started.");
        }
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Represents current stage of message processing in the ODE.
     * PARSE: Message is being parsed.
     * SANITIZE: Message is being sanitized.
     * ENCRYPT: Message is being encrypted.
     * SEND: Message is being sent to the core.
     */
    public enum ODEStage {
        PARSE, SANITIZE, ENCRYPT, SEND;
    }

    public static class ODELoggerException extends Exception {

        public ODELoggerException() {
        }

        public ODELoggerException(Throwable cause) {
            super(cause);
        }

        public ODELoggerException(String message) {
            super(message);
        }
    }

}
