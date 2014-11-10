package com.leidos.ode.agent.parser.helper;

import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.data.vdot.VDOTTravelTimeData;
import com.leidos.ode.agent.data.vdot.VDOTWeatherData;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class VDOTParserHelper extends ODEParserHelper {

    private static final String TAG = "VDOTParserHelper";
    private static final String VDOT_SPEED_TAG = "orci:tss_detector_status";
    private static final String VDOT_TRAVEL_TIME_TAG = "orci:traveltimesegment";
    private static final String VDOT_WEATHER_SHORT_TERM_TAG = "orci:vat_road_cond_point";
    private static final String VDOT_WEATHER_LONG_TERM_TAG = "orci:vat_road_cond_line";
    private static final String VDOT_WEATHER_LONG_TERM_DEFAULTS_TAG = "orci:vat_road_cond_area";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String VDOT_FEATURE_MEMBERS_TAG = "gml:featureMembers";
    private static final String VDOT_EXCEPTION_REPORT_TAG = "ows:ExceptionReport";
    private static final String TIME_ZONE_GMT = "GMT";
    private static Logger logger = Logger.getLogger(TAG);
    private static VDOTParserHelper instance;

    public static VDOTParserHelper getInstance() {
        if (instance == null) {
            instance = new VDOTParserHelper();
        }
        return instance;
    }

    @Override
    public ODEHelperResponse parseData(byte[] bytes) {
        Document document = getMessageDocument(bytes);
        if (document != null) {
            Elements exceptionReport = document.getElementsByTag(VDOT_EXCEPTION_REPORT_TAG);
            if (exceptionReport != null && exceptionReport.size() > 0) {
                return parseDocumentForExceptions(exceptionReport);
            } else {
                return parseDocumentForFeatureMembers(document);
            }
        } else {
            logger.debug("Error parsing Document with Jsoup.");
            return new ODEHelperResponse(null, HelperReport.UNKNOWN_ERROR);
        }
    }

    @Override
    protected ODEHelperResponse parseDocumentByTag(String tag, byte[] bytes) {
        return null;
    }

    private ODEHelperResponse parseDocumentForExceptions(Elements exceptionReport) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Error parsing VDOT Data Exception: '");
        Element exceptionReports = exceptionReport.first();
        if (exceptionReports != null) {
            Elements exceptions = exceptionReports.children();
            if (exceptions != null && exceptions.size() > 0) {
                Element firstException = exceptions.first();
                if (firstException.tagName().equalsIgnoreCase("ows:Exception")) {
                    Elements exceptionChildren = firstException.children();
                    if (exceptionChildren != null && exceptionChildren.size() > 0) {
                        for (Element child : exceptionChildren) {
                            if (child.tagName().equalsIgnoreCase("ows:exceptionText")) {
                                List<TextNode> textNodes = child.textNodes();
                                if (textNodes != null && textNodes.size() > 0) {
                                    TextNode textNode = textNodes.get(0);
                                    String exceptionText = textNode.text();
                                    stringBuilder.append(exceptionText);
                                    stringBuilder.append("'. ");
                                } else {
                                    stringBuilder.append("Unable to parse exception text nodes. There were none.");
                                }
                            } else {
                                stringBuilder.append("Unable to parse type 'ows:exceptionText'. Tag not found.");
                            }
                        }
                    } else {
                        stringBuilder.append("Expected a list of type 'ows:Exception', but there were none.");
                    }
                    Attributes exceptionAttributes = firstException.attributes();
                    if (exceptionAttributes != null && exceptionAttributes.size() > 0) {
                        String exceptionCodeValue = exceptionAttributes.get("exceptioncode");
                        String locatorValue = exceptionAttributes.get("locator");
                        stringBuilder.append("Exception code: '");
                        stringBuilder.append(exceptionCodeValue);
                        stringBuilder.append("'. ");
                        stringBuilder.append("Locator: '");
                        stringBuilder.append(locatorValue);
                        stringBuilder.append("'. ");
                    } else {
                        stringBuilder.append("Exception attributes were null or empty.");
                    }
                } else {
                    stringBuilder.append("Expected type 'ows:Exception', but was not found.");
                }
            } else {
                stringBuilder.append("Exceptions were empty or null.");
            }
        } else {
            stringBuilder.append("Exception reports was empty or null.");
        }

        logger.debug(stringBuilder.toString());
        return new ODEHelperResponse(null, HelperReport.DATA_SOURCE_SERVER_ERROR);
    }

    private ODEHelperResponse parseDocumentForFeatureMembers(Document document) {
        Elements featureMembers = document.getElementsByTag(VDOT_FEATURE_MEMBERS_TAG);
        if (featureMembers != null) {
            Element firstChild = featureMembers.first();
            if (firstChild != null) {
                Elements elementChildren = firstChild.children();
                if (elementChildren != null) {
                    if (elementChildren.size() > 0) {
                        Element features = elementChildren.first();
                        if (features != null) {
                            String featuresTag = features.tagName();
                            if (featuresTag != null) {
                                return determineDataTypeAndParse(document, featuresTag);
                            }
                        } else {
                            logger.debug("Unable to parse features tag.");
                        }
                    } else {
                        logger.debug("There is no data to be parsed.");
                        return new ODEHelperResponse(null, HelperReport.NO_DATA);
                    }
                } else {
                    logger.debug("Unable to parse feature elements.");
                }
            } else {
                logger.debug("Unable to parse feature elements.");
            }
        } else {
            logger.debug("Error parsing feature members. Could not find a tag: " + VDOT_FEATURE_MEMBERS_TAG);
        }
        return new ODEHelperResponse(null, HelperReport.UNEXPECTED_DATA_FORMAT);
    }

    private ODEHelperResponse determineDataTypeAndParse(Document document, String featureMembersTag) {
        if (featureMembersTag.equalsIgnoreCase(VDOT_WEATHER_SHORT_TERM_TAG)) {
            logger.debug("Parsing VDOTWeather short term data");
            return parseVDOTWeatherDataShortTerm(document);
        } else if (featureMembersTag.equalsIgnoreCase(VDOT_WEATHER_LONG_TERM_TAG)) {
            logger.debug("Parsing VDOTWeather long term data");
            return parseVDOTWeatherDataLongTerm(document);
        } else if (featureMembersTag.equalsIgnoreCase(VDOT_WEATHER_LONG_TERM_DEFAULTS_TAG)) {
            logger.debug("Parsing VDOTWeather long term defaults data");
            return parseVDOTWeatherDataLongTermDefaults(document);
        } else if (featureMembersTag.equalsIgnoreCase(VDOT_SPEED_TAG)) {
            logger.debug("Parsing VDOTSpeed data");
            return parseVDOTSpeedData(document);
        } else if (featureMembersTag.equalsIgnoreCase(VDOT_TRAVEL_TIME_TAG)) {
            logger.debug("Parsing VDOTTravelTime data");
            return parseVDOTTravelTimeData(document);
        }
        logger.debug("Unknown data type for tag: " + featureMembersTag);
        return new ODEHelperResponse(null, HelperReport.DATA_TYPE_UNKNOWN);
    }

    //TODO Figure out what weather data looks like
    private ODEHelperResponse parseVDOTWeatherDataShortTerm(Document document) {
        VDOTWeatherData vdotWeatherData = new VDOTWeatherData();
        List<VDOTWeatherData.VDOTWeatherDataElement> vdotWeatherDataElements = new ArrayList<VDOTWeatherData.VDOTWeatherDataElement>();

        Elements messageElements = document.getElementsByTag(VDOT_WEATHER_SHORT_TERM_TAG);

        for (Element messageElement : messageElements) {

            VDOTWeatherData.VDOTWeatherDataElement vdotWeatherDataElement = new VDOTWeatherData.VDOTWeatherDataElement();

            Elements elementChildren = messageElement.children();

            for (Element childElement : elementChildren) {

                String tagName = childElement.tagName();
                String value = childElement.ownText();

            }
            vdotWeatherDataElements.add(vdotWeatherDataElement);
        }
        vdotWeatherData.setVdotWeatherDataElements(vdotWeatherDataElements);

        return new ODEHelperResponse(vdotWeatherData, HelperReport.PARSE_SUCCESS);
    }

    //TODO Figure out what weather data looks like
    private ODEHelperResponse parseVDOTWeatherDataLongTerm(Document document) {
        VDOTWeatherData vdotWeatherData = new VDOTWeatherData();
        List<VDOTWeatherData.VDOTWeatherDataElement> vdotWeatherDataElements = new ArrayList<VDOTWeatherData.VDOTWeatherDataElement>();

        Elements messageElements = document.getElementsByTag(VDOT_WEATHER_LONG_TERM_TAG);

        for (Element messageElement : messageElements) {

            VDOTWeatherData.VDOTWeatherDataElement vdotWeatherDataElement = new VDOTWeatherData.VDOTWeatherDataElement();

            Elements elementChildren = messageElement.children();

            for (Element childElement : elementChildren) {

                String tagName = childElement.tagName();
                String value = childElement.ownText();

            }
            vdotWeatherDataElements.add(vdotWeatherDataElement);
        }
        vdotWeatherData.setVdotWeatherDataElements(vdotWeatherDataElements);

        return new ODEHelperResponse(vdotWeatherData, HelperReport.PARSE_SUCCESS);
    }

    //TODO Figure out what weather data looks like
    private ODEHelperResponse parseVDOTWeatherDataLongTermDefaults(Document document) {
        VDOTWeatherData vdotWeatherData = new VDOTWeatherData();
        List<VDOTWeatherData.VDOTWeatherDataElement> vdotWeatherDataElements = new ArrayList<VDOTWeatherData.VDOTWeatherDataElement>();

        Elements messageElements = document.getElementsByTag(VDOT_WEATHER_LONG_TERM_DEFAULTS_TAG);

        for (Element messageElement : messageElements) {

            VDOTWeatherData.VDOTWeatherDataElement vdotWeatherDataElement = new VDOTWeatherData.VDOTWeatherDataElement();

            Elements elementChildren = messageElement.children();

            for (Element childElement : elementChildren) {

                String tagName = childElement.tagName();
                String value = childElement.ownText();

            }
            vdotWeatherDataElements.add(vdotWeatherDataElement);
        }
        vdotWeatherData.setVdotWeatherDataElements(vdotWeatherDataElements);

        return new ODEHelperResponse(vdotWeatherData, HelperReport.PARSE_SUCCESS);
    }

    private ODEHelperResponse parseVDOTSpeedData(Document document) {
        VDOTSpeedData vdotSpeedData = new VDOTSpeedData();
        List<VDOTSpeedData.VDOTSpeedDataElement> vdotSpeedDataElements = new ArrayList<VDOTSpeedData.VDOTSpeedDataElement>();

        Elements messageElements = document.getElementsByTag(VDOT_SPEED_TAG);

        for (Element messageElement : messageElements) {

            VDOTSpeedData.VDOTSpeedDataElement vdotSpeedDataElement = new VDOTSpeedData.VDOTSpeedDataElement();

            Elements elementChildren = messageElement.children();

            for (Element childElement : elementChildren) {

                String tagName = childElement.tagName();
                String value = childElement.ownText();

                if (tagName.equals("orci:detectorid")) {
                    vdotSpeedDataElement.setDetectorId(value);
                    continue;
                }
                if (tagName.equals("orci:stationid")) {
                    vdotSpeedDataElement.setStationId(value);
                    continue;
                }
                if (tagName.equals("orci:device_name")) {
                    vdotSpeedDataElement.setDeviceName(value);
                    continue;
                }
                if (tagName.equals("orci:lane_direction")) {
                    vdotSpeedDataElement.setLaneDirection(value);
                    continue;
                }
                if (tagName.equals("orci:lanetype")) {
                    vdotSpeedDataElement.setLaneType(value);
                    continue;
                }
                if (tagName.equals("orci:lanenum")) {
                    vdotSpeedDataElement.setLaneNum(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:last_updated")) {
                    vdotSpeedDataElement.setLastUpdated(parseDate(value));
                    continue;
                }
                if (tagName.equals("orci:mile_marker")) {
                    vdotSpeedDataElement.setMileMarker(Double.parseDouble(value));
                    continue;
                }
                if (tagName.equals("orci:occupancy")) {
                    vdotSpeedDataElement.setOccupancy(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:routename")) {
                    vdotSpeedDataElement.setRouteName(value);
                    continue;
                }
                if (tagName.equals("orci:speed")) {
                    vdotSpeedDataElement.setSpeed(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:the_geom")) {
                    vdotSpeedDataElement.setGeometry(parseGeometry(childElement));
                    continue;
                }
                if (tagName.equals("orci:volume")) {
                    vdotSpeedDataElement.setVolume(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:nodeid")) {
                    vdotSpeedDataElement.setNodeId(Integer.parseInt(value));
                }
            }
            vdotSpeedDataElements.add(vdotSpeedDataElement);
        }
        vdotSpeedData.setVdotSpeedDataElements(vdotSpeedDataElements);

        return new ODEHelperResponse(vdotSpeedData, HelperReport.PARSE_SUCCESS);
    }

    private ODEHelperResponse parseVDOTTravelTimeData(Document document) {
        VDOTTravelTimeData vdotTravelTimeData = new VDOTTravelTimeData();
        List<VDOTTravelTimeData.VDOTTravelTimeDataElement> vdotTravelTimeDataElements = new ArrayList<VDOTTravelTimeData.VDOTTravelTimeDataElement>();

        Elements messageElements = document.getElementsByTag(VDOT_TRAVEL_TIME_TAG);

        for (Element messageElement : messageElements) {

            VDOTTravelTimeData.VDOTTravelTimeDataElement vdotTravelTimeDataElement = new VDOTTravelTimeData.VDOTTravelTimeDataElement();

            Elements elementChildren = messageElement.children();

            for (Element childElement : elementChildren) {

                String tagName = childElement.tagName();
                String value = childElement.ownText();

                if (tagName.equals("orci:segmentid")) {
                    vdotTravelTimeDataElement.setSegmentId(value);
                    continue;
                }
                if (tagName.equals("orci:lasttimeupdated")) {
                    vdotTravelTimeDataElement.setLastTimeUpdated(parseDate(value));
                    continue;
                }
                if (tagName.equals("orci:segmentname")) {
                    vdotTravelTimeDataElement.setSegmentName(value);
                    continue;
                }
                if (tagName.equals("orci:traveltime")) {
                    vdotTravelTimeDataElement.setTravelTime(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:the_geom")) {
                    vdotTravelTimeDataElement.setGeometry(parseGeometry(childElement));
                }
            }
            vdotTravelTimeDataElements.add(vdotTravelTimeDataElement);
        }
        vdotTravelTimeData.setVdotTravelTimeDataElements(vdotTravelTimeDataElements);

        return new ODEHelperResponse(vdotTravelTimeData, HelperReport.PARSE_SUCCESS);
    }

    private float[] parseGeometry(Element element) {
        Elements geometryElements = element.getElementsByTag("gml:Point");

        Elements geometryChildren = geometryElements.first().children();

        float[] geometry = new float[2];

        for (Element geometryChild : geometryChildren) {

            String tagName = geometryChild.tagName();
            String value = geometryChild.ownText();

            if (tagName.equals("gml:pos")) {
                String[] position = value.split(" ");
                geometry[0] = Float.parseFloat(position[0]);
                geometry[1] = Float.parseFloat(position[1]);
                break;
            }
        }
        return geometry;
    }

    protected Date parseDate(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_GMT));
        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }
}
