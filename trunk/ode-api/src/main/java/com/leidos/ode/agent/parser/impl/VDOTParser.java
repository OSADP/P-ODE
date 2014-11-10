package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.data.vdot.VDOTTravelTimeData;
import com.leidos.ode.agent.data.vdot.VDOTWeatherData;
import com.leidos.ode.agent.parser.ODEDataParser;
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

public class VDOTParser extends ODEDataParser {

    private static final String VDOT_SPEED_TAG = "orci:tss_detector_status";
    private static final String VDOT_TRAVEL_TIME_TAG = "orci:traveltimesegment";
    private static final String VDOT_WEATHER_SHORT_TERM_TAG = "orci:vat_road_cond_point";
    private static final String VDOT_WEATHER_LONG_TERM_TAG = "orci:vat_road_cond_line";
    private static final String VDOT_WEATHER_LONG_TERM_DEFAULTS_TAG = "orci:vat_road_cond_area";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String VDOT_FEATURE_MEMBERS_TAG = "gml:featureMembers";
    private static final String VDOT_EXCEPTION_REPORT_TAG = "ows:ExceptionReport";
    private static final String TIME_ZONE_GMT = "GMT";

    @Override
    public ODEDataParserResponse parse(byte[] bytes) {
        Document document = getMessageDocument(bytes);
        if (document != null) {
            Elements exceptionReport = document.getElementsByTag(VDOT_EXCEPTION_REPORT_TAG);
            if (exceptionReport != null && exceptionReport.size() > 0) {
                return parseDocumentForExceptions(exceptionReport);
            } else {
                return parseDocumentForFeatureMembers(document);
            }
        } else {
            getLogger().debug("Error parsing Document with Jsoup.");
            return new ODEDataParserResponse(null, ODEDataParserReportCode.UNKNOWN_ERROR);
        }
    }

    private ODEDataParserResponse parseDocumentForExceptions(Elements exceptionReport) {
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

        getLogger().debug(stringBuilder.toString());
        return new ODEDataParserResponse(null, ODEDataParserReportCode.DATA_SOURCE_SERVER_ERROR);
    }

    private ODEDataParserResponse parseDocumentForFeatureMembers(Document document) {
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
                            getLogger().debug("Unable to parse features tag.");
                        }
                    } else {
                        getLogger().debug("There is no data to be parsed.");
                        return new ODEDataParserResponse(null, ODEDataParserReportCode.NO_DATA);
                    }
                } else {
                    getLogger().debug("Unable to parse feature elements.");
                }
            } else {
                getLogger().debug("Unable to parse feature elements.");
            }
        } else {
            getLogger().debug("Error parsing feature members. Could not find a tag: " + VDOT_FEATURE_MEMBERS_TAG);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.UNEXPECTED_DATA_FORMAT);
    }

    private ODEDataParserResponse determineDataTypeAndParse(Document document, String featureMembersTag) {
        if (featureMembersTag.equalsIgnoreCase(VDOT_WEATHER_SHORT_TERM_TAG)) {
            getLogger().debug("Parsing VDOTWeather short term data");
            return parseVDOTWeatherDataShortTerm(document);
        } else if (featureMembersTag.equalsIgnoreCase(VDOT_WEATHER_LONG_TERM_TAG)) {
            getLogger().debug("Parsing VDOTWeather long term data");
            return parseVDOTWeatherDataLongTerm(document);
        } else if (featureMembersTag.equalsIgnoreCase(VDOT_WEATHER_LONG_TERM_DEFAULTS_TAG)) {
            getLogger().debug("Parsing VDOTWeather long term defaults data");
            return parseVDOTWeatherDataLongTermDefaults(document);
        } else if (featureMembersTag.equalsIgnoreCase(VDOT_SPEED_TAG)) {
            getLogger().debug("Parsing VDOTSpeed data");
            return parseVDOTSpeedData(document);
        } else if (featureMembersTag.equalsIgnoreCase(VDOT_TRAVEL_TIME_TAG)) {
            getLogger().debug("Parsing VDOTTravelTime data");
            return parseVDOTTravelTimeData(document);
        }
        getLogger().debug("Unknown data type for tag: " + featureMembersTag);
        return new ODEDataParserResponse(null, ODEDataParserReportCode.DATA_TYPE_UNKNOWN);
    }

    //TODO Figure out what weather data looks like
    private ODEDataParserResponse parseVDOTWeatherDataShortTerm(Document document) {
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

        return new ODEDataParserResponse(vdotWeatherData, ODEDataParserReportCode.PARSE_SUCCESS);
    }

    //TODO Figure out what weather data looks like
    private ODEDataParserResponse parseVDOTWeatherDataLongTerm(Document document) {
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

        return new ODEDataParserResponse(vdotWeatherData, ODEDataParserReportCode.PARSE_SUCCESS);
    }

    //TODO Figure out what weather data looks like
    private ODEDataParserResponse parseVDOTWeatherDataLongTermDefaults(Document document) {
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

        return new ODEDataParserResponse(vdotWeatherData, ODEDataParserReportCode.PARSE_SUCCESS);
    }

    private ODEDataParserResponse parseVDOTSpeedData(Document document) {
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

        return new ODEDataParserResponse(vdotSpeedData, ODEDataParserReportCode.PARSE_SUCCESS);
    }

    private ODEDataParserResponse parseVDOTTravelTimeData(Document document) {
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

        return new ODEDataParserResponse(vdotTravelTimeData, ODEDataParserReportCode.PARSE_SUCCESS);
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
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }
}
