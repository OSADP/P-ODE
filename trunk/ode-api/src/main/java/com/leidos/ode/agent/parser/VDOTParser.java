package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.vdot.VDOTData;
import com.leidos.ode.agent.parser.jsoup.VDOTJsoup;

import java.util.List;

public class VDOTParser extends ODEDataParser {

    @Override
    public ODEAgentMessage parseMessage(byte[] bytes) throws ODEParseException {
//        try {
//            String messageString = new String(bytes);
//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//            Document document = documentBuilder.parse(new InputSource(new StringReader(messageString)));
//            JAXBContext jaxbContext = JAXBContext.newInstance(Result.class);
//            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//            Result result = (Result) unmarshaller.unmarshal(document);
//            logger.debug("Sucessfully parsed result.");
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
        getLogger().debug("Parsing VDOT data.");
        List<VDOTData> vdotData = VDOTJsoup.parseVDOTData(bytes);
        getLogger().debug("Successfully parsed VDOT data.");
        getLogger().debug("Total data elements parsed into objects: " + vdotData.size());
        return new ODEAgentMessage().setFormattedMessage(vdotData).setMessagePayload(bytes);
    }
}
