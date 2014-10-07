package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;

public class RITISParser extends ODEDataParser {

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
//        try {
//            String messageString = new String(bytes);
//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//            Document document = documentBuilder.parse(new InputSource(new StringReader(messageString)));
//            JAXBContext jaxbContext = JAXBContext.newInstance(CollectionPeriod.class);
//            Unmarshaller unmarshaller;
//            Result result;
//            try {
//                unmarshaller = jaxbContext.createUnmarshaller();
//                result = (Result) unmarshaller.unmarshal(document);
//                logger.debug("Unmarshalled bytes into a RITISSpeed object.");
//            } catch (JAXBException e) {
//                logger.error("Unable to unmarshal bytes into a RITISSpeed object.");
//                logger.debug("Attempting to unmarshal bytes into a RITISWeather object instead.");
//                jaxbContext = JAXBContext.newInstance(Header.class);
//                unmarshaller = jaxbContext.createUnmarshaller();
//                result = (Result) unmarshaller.unmarshal(document);
//                logger.debug("Unmarshalled bytes into a RITISWeather object.");
//            }
//            if (result == null) {
//                logger.warn("Unmarshalling of bytes was unsuccessful. ODEAgentMessage formatted message will be null.");
//            }
//            return new ODEAgentMessage().setFormattedMessage(result).setMessagePayload(bytes);
//        } catch (ParserConfigurationException e) {
//            throw new ODEParseException(e.getMessage(), e);
//        } catch (JAXBException e) {
//            throw new ODEParseException(e.getMessage(), e);
//        } catch (SAXException e) {
//            throw new ODEParseException(e.getMessage(), e);
//        } catch (IOException e) {
//            throw new ODEParseException(e.getMessage(), e);
//        }
        return null;
    }
}