package com.leidos.ode.agent.parser.jsoup;

import com.leidos.ode.agent.data.ritis.RITISData;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.agent.data.ritis.RITISWeatherData;
import generated.Alerts;
import generated.CollectionPeriod;
import generated.Header;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class RITISJsoup extends ODEJsoup<RITISData> {

    private static final String TAG = "RITISJsoup";
    private static final String RITIS_SPEED_TAG = "ns8:detectorZoneData-RITIS";
    private static final String RITIS_WEATHER_TAG = "ns10:alertsData";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.'Z'";
    private static Logger logger = Logger.getLogger(TAG);
    private static RITISJsoup instance;

    public static RITISJsoup getInstance() {
        if (instance == null) {
            instance = new RITISJsoup();
        }
        return instance;
    }

    @Override
    public ODEJsoupResponse<RITISData> parseData(byte[] bytes) {
        Document document = getMessageDocument(bytes);
        if (document != null) {
            Element bodyElement = document.body();
            if (bodyElement != null) {
                Elements elementChildren = bodyElement.children();
                if (elementChildren != null) {
                    if (!elementChildren.isEmpty()) {
                        Element firstChild = elementChildren.first();
                        if (firstChild != null) {
                            String firstChildTag = firstChild.tagName();
                            if (firstChildTag.equalsIgnoreCase(RITIS_SPEED_TAG)) {
                                logger.debug("Parsing RITISSpeed data");
                                //Parse the RITISSpeed data using the CollectionPeriods which are the 3rd element
                                return parseRITISSpeedData(document, firstChild.child(3));

                            } else if (firstChildTag.equalsIgnoreCase(RITIS_WEATHER_TAG)) {
                                logger.debug("Parsing RITISWeather data");
                                return parseRITISWeatherData(elementChildren);
                            }
                        } else {
                            logger.debug("Error parsing first child. First child was null.");
                        }
                    } else {
                        logger.debug("There is no data to be parsed.");
                        return new ODEJsoupResponse<RITISData>(null, JsoupReport.NO_RESULTS);
                    }
                } else {
                    logger.debug("Error parsing elements. Body's children elements were null.");
                }
            } else {
                logger.debug("Error parsing body element. Body element was null.");
            }
        } else {
            return new ODEJsoupResponse<RITISData>(null, JsoupReport.DOCUMENT_PARSE_ERROR);
        }
        return new ODEJsoupResponse<RITISData>(null, JsoupReport.UNKNOWN);
    }

    private ODEJsoupResponse<RITISData> parseRITISSpeedData(Document document, Element collectionPeriodElement) {
        List<RITISData> ritisSpeedDataList = new ArrayList<RITISData>();

        List<CollectionPeriod> collectionPeriodList = parseCollectionPeriods(collectionPeriodElement);

        Element documentBody = document.body().child(0);

        String organizationId = documentBody.child(0).ownText();
        String networkId = documentBody.child(1).ownText();
        XMLGregorianCalendar timeStamp = null;
        try {
            timeStamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(documentBody.child(2).ownText());
        } catch (DatatypeConfigurationException e) {
            logger.error(e.getLocalizedMessage());
        }
        RITISSpeedData ritisSpeedData;

        for (CollectionPeriod collectionPeriod : collectionPeriodList) {
            ritisSpeedData = new RITISSpeedData();
            ritisSpeedData.setOrganizationId(organizationId);
            ritisSpeedData.setNetworkId(networkId);
            ritisSpeedData.setTimeStamp(timeStamp);
            ritisSpeedData.setCollectionPeriod(collectionPeriod);
            ritisSpeedDataList.add(ritisSpeedData);
        }
        return buildJsoupResponseFromParsedData(ritisSpeedDataList);
    }

    private List<CollectionPeriod> parseCollectionPeriods(Element collectionPeriodElement) {
        List<CollectionPeriod> collectionPeriodList = new ArrayList<CollectionPeriod>();
        String tagName = collectionPeriodElement.tagName();
        if (tagName.equals("collection-period")) {
            Elements collectionPeriodItems = collectionPeriodElement.children();
            if (collectionPeriodItems != null) {
                for (Element collectionPeriodItem : collectionPeriodItems) {
                    try {
                        String html = new StringBuilder()
                                .append("<collection-period>")
                                .append(System.lineSeparator())
                                .append(collectionPeriodItem.outerHtml())
                                .append(System.lineSeparator())
                                .append("</collection-period>")
                                .toString();
                        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                        org.w3c.dom.Document document = documentBuilder.parse(new InputSource(new StringReader(html)));
                        JAXBContext jaxbContext = JAXBContext.newInstance(CollectionPeriod.class);
                        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                        CollectionPeriod collectionPeriod = (CollectionPeriod) unmarshaller.unmarshal(document);
                        if (collectionPeriod != null) {
                            collectionPeriodList.add(collectionPeriod);
                        }
                    } catch (ParserConfigurationException e) {
                        logger.error(e.getLocalizedMessage());
                    } catch (SAXException e) {
                        logger.error(e.getLocalizedMessage());
                    } catch (IOException e) {
                        logger.error(e.getLocalizedMessage());
                    } catch (JAXBException e) {
                        logger.error(e.getLocalizedMessage());
                    }
                }
            }
        }
        return collectionPeriodList;
    }

    private ODEJsoupResponse<RITISData> parseRITISWeatherData(Elements element) {
        List<RITISData> ritisWeatherDataList = new ArrayList<RITISData>();

        Elements elements = element.first().children();
        if (elements != null) {
            Header header = parseHeader(elements.get(0));
            List<Alerts> alertsList = parseAlerts(elements.get(1));

            RITISWeatherData ritisWeatherData;

            for (Alerts alert : alertsList) {
                ritisWeatherData = new RITISWeatherData();
                ritisWeatherData.setHeader(header);
                ritisWeatherData.setAlerts(alert);
                ritisWeatherDataList.add(ritisWeatherData);
            }
        }
        return buildJsoupResponseFromParsedData(ritisWeatherDataList);
    }

    /**
     * Parses the Header element of RITISWeather data.
     *
     * @param headerElement
     * @return
     */
    private Header parseHeader(Element headerElement) {
        if (headerElement != null) {
            try {
                String html = headerElement.outerHtml();
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                org.w3c.dom.Document document = documentBuilder.parse(new InputSource(new StringReader(html)));
                JAXBContext jaxbContext = JAXBContext.newInstance(Header.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                return (Header) unmarshaller.unmarshal(document);
            } catch (ParserConfigurationException e) {
                logger.error(e.getLocalizedMessage());
            } catch (SAXException e) {
                logger.error(e.getLocalizedMessage());
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            } catch (JAXBException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        return null;
    }

    /**
     * Parses the Alerts element of RITISWeather data.
     *
     * @param alertsElement
     * @return
     */
    private List<Alerts> parseAlerts(Element alertsElement) {
        List<Alerts> alertsList = new ArrayList<Alerts>();
        if (alertsElement != null) {
            String tagName = alertsElement.tagName();
            if (tagName.equals("alerts")) {
                Elements alertsChildren = alertsElement.children();
                if (alertsChildren != null) {
                    for (Element alertElement : alertsChildren) {
                        try {
                            String html = new StringBuilder()
                                    .append("<").append(tagName).append(">")
                                    .append(System.lineSeparator())
                                    .append(alertElement.outerHtml())
                                    .append(System.lineSeparator())
                                    .append("</").append(tagName).append(">")
                                    .toString();
                            //Fix the unclosed tag 'area'. This bug seems to be Jsoup related, because the same url in a browser returns valid xml
                            html = fixHtmlWithUnclosedTag(html, "area", "arealocation");

                            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                            org.w3c.dom.Document document = documentBuilder.parse(new InputSource(new StringReader(html)));
                            JAXBContext jaxbContext = JAXBContext.newInstance(Alerts.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                            Alerts alerts = (Alerts) unmarshaller.unmarshal(document);
                            if (alerts != null) {
                                alertsList.add(alerts);
                            }
                        } catch (ParserConfigurationException e) {
                            logger.error(e.getLocalizedMessage());
                        } catch (SAXException e) {
                            logger.error(e.getLocalizedMessage());
                        } catch (IOException e) {
                            logger.error(e.getLocalizedMessage());
                        } catch (JAXBException e) {
                            logger.error(e.getLocalizedMessage());
                        }
                    }
                }
            }
        }
        return alertsList;
    }

    /**
     * Fixes an html string that contains an unclosed tag.
     *
     * @param html
     * @param unclosedTag
     * @param followingTag
     * @return
     */
    private String fixHtmlWithUnclosedTag(String html, String unclosedTag, String followingTag) {
        if (html != null && unclosedTag != null && followingTag != null) {

            String unclosedEndTagHtml = new StringBuilder()
                    .append("</").append(unclosedTag).append(">")
                    .toString();

            String followingEndTagHtml = new StringBuilder()
                    .append("</").append(followingTag).append(">")
                    .toString();

            String fixedHtml = new StringBuilder()
                    .append(unclosedEndTagHtml)
                    .append(System.lineSeparator())
                    .append(followingEndTagHtml)
                    .toString();

            return html.replaceAll("(?i)" + followingEndTagHtml, fixedHtml);
        }
        return null;
    }
}

