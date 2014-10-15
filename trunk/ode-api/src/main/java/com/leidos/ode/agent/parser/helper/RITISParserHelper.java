package com.leidos.ode.agent.parser.helper;

import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.agent.data.ritis.RITISWeatherDataClarus;
import com.leidos.ode.agent.data.ritis.RITISWeatherDataNWS;
import edu.umd.cattlab.schema.ritisFilter.other.AlertsData;
import edu.umd.cattlab.schema.ritisFilter.other.ClarusData;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ritis.schema.tmdd_0_0_0.DetectorDataRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneDetectorDataRITIS;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class RITISParserHelper extends ODEParserHelper {

    private static final String TAG = "RITISParserHelper";
    private static final String RITIS_SPEED_TAG = "ns8:detectorZoneData-RITIS";
    private static final String RITIS_WEATHER_NWS_TAG = "ns10:alertsData";
    private static final String RITIS_WEATHER_CLARUS_TAG = "ns10:clarusData";
    private static final String RITIS_ERROR_TAG = "errors";
    private static Logger logger = Logger.getLogger(TAG);
    private static RITISParserHelper instance;

    public static RITISParserHelper getInstance() {
        if (instance == null) {
            instance = new RITISParserHelper();
        }
        return instance;
    }

    @Override
    public ODEHelperResponse parseData(byte[] bytes) {
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
                            if (elementTag.equalsIgnoreCase(RITIS_SPEED_TAG)) {
                                logger.debug("Parsing RITISSpeed data");
                                return parseRITISSpeedData(bytes);

                            } else if (elementTag.equalsIgnoreCase(RITIS_WEATHER_NWS_TAG)) {
                                logger.debug("Parsing RITISWeather data from NWS");
                                return parseRITISWeatherNWS(bytes);

                            } else if (elementTag.equalsIgnoreCase(RITIS_WEATHER_CLARUS_TAG)) {
                                logger.debug("Parsing RITISWeather data from Clarus");
                                return parseRITISWeatherClarus(bytes);
                            } else if (elementTag.equalsIgnoreCase(RITIS_ERROR_TAG)) {
                                logger.debug("Parsing RITIS Error!");
                                return parseRITISError(element);
                            } else {
                                logger.debug("Unknown data type for tag: " + elementTag);
                                return new ODEHelperResponse(null, HelperReport.DATA_TYPE_UNKNOWN);
                            }
                        } else {
                            logger.debug("Unable to parse tag name. Data type unknown.");
                        }
                    } else {
                        return new ODEHelperResponse(null, HelperReport.NO_DATA);
                    }
                } else {
                    logger.debug("Unable to parse body elements. Body element has no children.");
                }
            } else {
                logger.debug("Unable to parse body element. Document body was null..");
            }
        }
        ;
        return new ODEHelperResponse(null, HelperReport.UNEXPECTED_DATA_FORMAT);
    }

    private Object unmarshalBytes(byte[] bytes, Class objectFactoryForContext) {
        if (bytes != null && objectFactoryForContext != null) {
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                JAXBContext jaxbContext = JAXBContext.newInstance(objectFactoryForContext);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                return jaxbUnmarshaller.unmarshal(byteArrayInputStream);
            } catch (JAXBException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        return null;
    }

    private ODEHelperResponse parseRITISSpeedData(byte[] bytes) {
        JAXBElement<ZoneDetectorDataRITIS> zoneDetectorDataRITIS = (JAXBElement<ZoneDetectorDataRITIS>) unmarshalBytes(bytes, org.ritis.schema.tmdd_0_0_0.ObjectFactory.class);
        if (zoneDetectorDataRITIS != null) {
            RITISSpeedData ritisSpeedData = new RITISSpeedData();
            ritisSpeedData.setZoneDetectorDataRITIS(zoneDetectorDataRITIS.getValue());
            return new ODEHelperResponse(ritisSpeedData, HelperReport.PARSE_SUCCESS);
        }
        return new ODEHelperResponse(null, HelperReport.PARSE_ERROR);
    }

    private ODEHelperResponse parseRITISWeatherNWS(byte[] bytes) {
        JAXBElement<AlertsData> alertsData = (JAXBElement<AlertsData>) unmarshalBytes(bytes, edu.umd.cattlab.schema.ritisFilter.other.ObjectFactory.class);
        if (alertsData != null) {
            RITISWeatherDataNWS ritisWeatherDataNWS = new RITISWeatherDataNWS();
            ritisWeatherDataNWS.setAlertsData(alertsData.getValue());
            return new ODEHelperResponse(ritisWeatherDataNWS, HelperReport.PARSE_SUCCESS);
        }
        return new ODEHelperResponse(null, HelperReport.PARSE_ERROR);
    }

    private ODEHelperResponse parseRITISWeatherClarus(byte[] bytes) {
        JAXBElement<ClarusData> clarusData = (JAXBElement<ClarusData>) unmarshalBytes(bytes, edu.umd.cattlab.schema.ritisFilter.other.ObjectFactory.class);
        if (clarusData != null) {
            RITISWeatherDataClarus ritisWeatherDataClarus = new RITISWeatherDataClarus();
            ritisWeatherDataClarus.setClarusData(clarusData.getValue());
            return new ODEHelperResponse(ritisWeatherDataClarus, HelperReport.PARSE_SUCCESS);
        }
        return new ODEHelperResponse(null, HelperReport.PARSE_ERROR);
    }

    private ODEHelperResponse parseRITISError(Element error) {
        if (error != null) {
            Elements errorChildren = error.children();
            if (errorChildren != null) {
                Element errorChild = errorChildren.first();
                if (errorChild != null) {
                    String errorText = errorChild.ownText();
                    logger.error("Error requesting RITIS data. Response from server: '" + errorText + "'.");
                    return new ODEHelperResponse(null, HelperReport.DATA_SOURCE_SERVER_ERROR);
                }
            }
        }
        return new ODEHelperResponse(null, HelperReport.UNKNOWN_ERROR);
    }

}

