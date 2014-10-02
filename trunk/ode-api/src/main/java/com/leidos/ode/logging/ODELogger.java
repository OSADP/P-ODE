package com.leidos.ode.logging;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;

/**
 * The class representing the ODE application logger. Used for logging component events in the database. Includes
 * a Log4J logger instance for logging elsewhere.
 *
 * @author lamde
 */
public final class ODELogger {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private LogBean logBean;
    private MongoTemplate mongoTemplate;

    /**
     * Marks the starting point of a given ODEStage in the log message.
     *
     * @param odeStage
     * @param messageId
     */
    public void start(ODEStage odeStage, String messageId) {
        Date startTime = new Date();
        logBean = new LogBean(startTime, odeStage, messageId);
        logger.debug(TAG + " Started log event for stage: " + odeStage);
    }

    /**
     * Marks the completion point of a given ODEStage in the log message and saves the bean in Mongo.
     */
    public void finish() {
        Date endTime = new Date();
        if (logBean != null) {
            logBean.setEndTime(endTime);
            getMongoTemplate().save(logBean);
            logger.debug(TAG + " Stored log bean.");
        } else {
            throw new Error("Cannot finish an event that has not been started.");
        }
    }

    private MongoTemplate getMongoTemplate() {
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

}
