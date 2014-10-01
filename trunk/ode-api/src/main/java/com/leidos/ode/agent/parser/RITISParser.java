package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.util.ODEMessageType;
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
        if (getODEMessageType().equals(ODEMessageType.UNDEFINED) || getODEMessageType() == null) {
            throw new ODEParseException("Cannot parse message. Message type is undefined.");
        }
        try {
            String messageString = new String(bytes);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(messageString)));
            JAXBContext jaxbContext = null;
            if (getODEMessageType().equals(ODEMessageType.RITISSpeed)) {
                jaxbContext = JAXBContext.newInstance(CollectionPeriod.class);
            }
            if (getODEMessageType().equals(ODEMessageType.RITISWeather)) {
                jaxbContext = JAXBContext.newInstance(Point.class);
            }

            if (jaxbContext != null) {
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                Result result = (Result) unmarshaller.unmarshal(document);
                logger.debug(TAG + ": Sucessfully parsed result.");
                return new ODEAgentMessage().setFormattedMessage(result).setMessagePayload(bytes);
            } else {
                throw new ODEParseException("JAXB Context was not initialized. Not a valid RITISParser message type.");
            }
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