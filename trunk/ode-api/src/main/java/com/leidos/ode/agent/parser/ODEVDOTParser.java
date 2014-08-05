package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import net.sourceforge.exist.ns.exist.Result;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Class for parsing VDOT Data (Iteris).
 *
 * @author lamde
 */
public class ODEVDOTParser implements ODEDataParser {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        ODEAgentMessage parsedMessage = new ODEAgentMessage();
        parsedMessage.setMessagePayload(bytes);

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Result result = (Result) objectInputStream.readObject();
            parsedMessage.setFormattedMessage(result);
        } catch (IOException e) {
            logger.error("Error parsing message. " + e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            logger.error("Error parsing message. " + e.getLocalizedMessage());
        }

        return parsedMessage;
    }
}
