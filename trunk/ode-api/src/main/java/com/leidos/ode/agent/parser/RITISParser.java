package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import generated.CollectionPeriod;
import net.opengis.gml._3.Point;
import net.sourceforge.exist.ns.exist.Result;
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

public class RITISParser extends ODEDataParser {

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
        try {
            String messageString = new String(bytes);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(messageString)));
            JAXBContext jaxbContext = JAXBContext.newInstance(CollectionPeriod.class);
            Unmarshaller unmarshaller;
            Result result;
            try {
                unmarshaller = jaxbContext.createUnmarshaller();
                result = (Result) unmarshaller.unmarshal(document);
                logger.debug("Unmarshalled bytes into a RITISSpeed object.");
            } catch (JAXBException e) {
                logger.error("Unable to unmarshal bytes into a RITISSpeed object.");
                logger.debug("Attempting to unmarshal bytes into a RITISWeather object instead.");
                jaxbContext = JAXBContext.newInstance(Point.class);
                unmarshaller = jaxbContext.createUnmarshaller();
                result = (Result) unmarshaller.unmarshal(document);
                logger.debug("Unmarshalled bytes into a RITISWeather object.");
            }
            if (result == null) {
                logger.warn("Unmarshalling of bytes was unsuccessful. ODEAgentMessage formatted message will be null.");
            }
            return new ODEAgentMessage().setFormattedMessage(result).setMessagePayload(bytes);
        } catch (ParserConfigurationException e) {
            throw new ODEParseException(e.getMessage(), e);
        } catch (JAXBException e) {
            throw new ODEParseException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new ODEParseException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ODEParseException(e.getMessage(), e);
        }
    }
}