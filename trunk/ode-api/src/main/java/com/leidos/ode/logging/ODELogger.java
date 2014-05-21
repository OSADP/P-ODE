package com.leidos.ode.logging;

import com.leidos.ode.core.dao.LogDAO;
import org.apache.log4j.Logger;
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
public class ODELogger{

    private final String TAG = getClass().getSimpleName();

    @Autowired
    private LogDAO logDAO;
    private Logger logger;

    public ODELogger(){
        logger = Logger.getLogger(TAG);
    }

    /**
     * Log event to the database for AAR.
     * @param component The component that generated the event.
     * @param message The event message.
     */
    public void odeLogEvent(String component, String message) {
        getLogDAO().storeLogBean(new LogBean(new Date(), component, message));
    }

    /**
     * Returns a Log4J logger instance.
     * @return
     */
    public Logger getLogger(){
        return logger;
    }

    private LogDAO getLogDAO() {
        return logDAO;
    }

}
