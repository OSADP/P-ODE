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
public class ODELogger{

    private final String TAG = getClass().getSimpleName();

    @Autowired
    private LogDAO logDAO;

    public ODELogger(){

    }

    /**
     * Log event to the database for AAR.
     * @param component The component that generated the event.
     * @param message The event message.
     */
    public void odeLogEvent(String component, String message) {
        getLogDAO().storeLogBean(new LogBean(new Date(), component, message));
    }

    private LogDAO getLogDAO() {
        return logDAO;
    }

}
