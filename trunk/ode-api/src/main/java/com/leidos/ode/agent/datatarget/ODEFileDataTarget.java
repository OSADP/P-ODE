package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.registration.RegistrationResponse;

import java.io.File;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author cassadyja
 */
public class ODEFileDataTarget implements ODEDataTarget {
    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private String filePath;
    private PrintStream out;
    private File outputFile;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    @Override
    public void configure(RegistrationResponse registrationResponse) throws DataTargetException {
        try {
            logger.debug("Configuring file output.");
            outputFile = new File(getFilePath());
            if(outputFile.exists()){
                backupFile();
                outputFile = new File(getFilePath());
            }
            out = new PrintStream(outputFile);
            out.println("File start: "+sdf.format(new Date()));
        } catch (IOException ex) {
            logger.error("Error creating file", ex);
            throw new DataTargetException("Error creating file", ex);
        }
    }

    @Override
    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        for (int i = 0; i < message.getMessagePayload().length; i++) {
            out.printf("%02X ", message.getMessagePayload()[i]);
        }
        out.println();
        out.flush();
        if(outputFile.length() > 20000000){
            close();
            configure(null);            
        }
    }

    @Override
    public void close() {
        out.flush();
        out.close();
        backupFile();
 
    }
    
    private void backupFile(){
        File rename = new File(filePath+sdf.format(new Date()));
        outputFile.renameTo(rename);
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
