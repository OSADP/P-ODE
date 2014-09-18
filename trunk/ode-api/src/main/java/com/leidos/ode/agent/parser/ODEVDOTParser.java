package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import net.sourceforge.exist.ns.exist.Result;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class ODEVDOTParser implements ODEDataParser {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        try {
            String messageString = new String(bytes);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(messageString)));
            JAXBContext jaxbContext = JAXBContext.newInstance(Result.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Result result = (Result) unmarshaller.unmarshal(document);
            logger.debug(TAG + ": Sucessfully parsed result.");
            return new ODEAgentMessage().setFormattedMessage(result).setMessagePayload(bytes);
        } catch (ParserConfigurationException e) {
            logger.error(e.getLocalizedMessage());
        } catch (SAXException e) {
            logger.error(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        } catch (JAXBException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

}
