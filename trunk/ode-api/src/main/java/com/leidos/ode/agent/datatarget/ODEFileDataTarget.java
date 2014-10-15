package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author cassadyja
 */
public class ODEFileDataTarget implements ODEDataTarget {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private String filePath;
    private PrintStream out;

    public void configure(ODERegistrationResponse registrationResponse) throws DataTargetException {
        try {
            out = new PrintStream(new FileOutputStream(filePath));
        } catch (IOException ex) {
            throw new DataTargetException("Error creating file", ex);
        }
    }

    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        for (int i = 0; i < message.getMessagePayload().length; i++) {
            out.printf("%02X ", message.getMessagePayload()[i]);
        }
    }

    public void close() {
        out.flush();
        out.close();
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
