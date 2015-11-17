package com.leidos.ode.agent.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/10/14
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JAXBEnabledParser extends ODEDataParser {

    @Override
    protected ODEDataParserResponse parse(byte[] bytes) {
        return parseDocumentByTag(bytes);
    }

    protected abstract ODEDataParserResponse parseDocumentByTag(String tag, byte[] bytes);

    protected final ODEDataParserResponse parseDocumentByTag(byte[] bytes) {
        Document document = getMessageDocument(bytes);
        if (document != null) {
            Element headElement = document.head();
            Element bodyElement = document.body();
            if (bodyElement != null) {
                Elements bodyChildren = bodyElement.children();
                if (bodyChildren != null) {
                    Element element = bodyChildren.first();
                    if (element != null) {
                        String elementTag = element.tagName();
                        if (elementTag != null) {
                            return parseDocumentByTag(elementTag, bytes);
                        }
                    } else {
                        return new ODEDataParserResponse(null, ODEDataParserReportCode.NO_DATA);
                    }
                } else {
                    getLogger().debug("Unable to parse body elements. Body element has no children.");
                }
            } else {
                getLogger().debug("Unable to parse body element. Document body was null.");
            }
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.UNEXPECTED_DATA_FORMAT);
    }

    protected final Object unmarshalBytes(byte[] bytes, Class[] objectFactoryForContext) {
        if (bytes != null && objectFactoryForContext != null) {
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                JAXBContext jaxbContext = JAXBContext.newInstance(objectFactoryForContext);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                return jaxbUnmarshaller.unmarshal(byteArrayInputStream);
            } catch (JAXBException e) {
                getLogger().error(e.getLocalizedMessage());
            }
        }
        return null;
    }
}
