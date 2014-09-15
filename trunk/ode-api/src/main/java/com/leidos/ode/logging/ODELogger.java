package com.leidos.ode.logging;

import com.leidos.ode.core.dao.LogDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * The class representing the ODE application logger. Used for logging component events in the database. Includes
 * a Log4J logger instance for logging elsewhere.
 *
 * @author lamde
 */
@Component
public final class ODELogger {

    private final String TAG = getClass().getSimpleName();
    private LogBean logBean;
    @Autowired
    private LogDAO logDAO;

    /**
     * Marks the starting point of a given ODEStage in the log message.
     *
     * @param odeStage
     * @param messageId
     */
    public void start(ODEStage odeStage, String messageId) {
        Date startTime = new Date();
        logBean = new LogBean(startTime, odeStage, messageId);
    }

    /**
     * Marks the completion point of a given ODEStage in the log message.
     */
    public void finish() {
        Date endTime = new Date();
        if (logBean != null) {
            logBean.setEndTime(endTime);
            getLogDAO().storeLogBean(logBean);
        } else {
            throw new Error("Cannot finish an event that has not been started.");
        }
    }

    private LogDAO getLogDAO() {
        return logDAO;
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
