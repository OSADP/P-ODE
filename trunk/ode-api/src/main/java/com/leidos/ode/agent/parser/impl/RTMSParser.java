package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.data.rtms.RTMSData;
import com.leidos.ode.agent.parser.DateParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/17/14
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class RTMSParser extends DateParser {

    private final String RTMS_DATA_TAG = "rtms";
    private final String RTMS_DATA_ELEMENT_TAG = "ode_rtms_view";

    private final String RTMS_NETWORK_ID_KEY = "RTMS_NETWORK_ID";
    private final String RTMS_NAME_KEY = "RTMS_NAME";
    private final String RTMS_ZONE_KEY = "Zone";
    private final String RTMS_ZONE_LABEL_KEY = "ZoneLabel";
    private final String RTMS_STATION_NAME_KEY = "Station_Name";
    private final String RTMS_SPEED_KEY = "Speed";
    private final String RTMS_FWDLK_SPEED_KEY = "FWDLK_Speed";
    private final String RTMS_VOLUME_KEY = "Volume";
    private final String RTMS_VOL_MID_KEY = "Vol_Mid";
    private final String RTMS_VOL_LONG_KEY = "Vol_Long";
    private final String RTMS_VOL_EXTRA_LONG_KEY = "Vol_Extra_Long";
    private final String RTMS_OCCUPANCY_KEY = "Occupancy";
    private final String RTMS_MSG_NUMBER_KEY = "MsgNumber";
    private final String RTMS_DATE_TIME_STAMP_KEY = "DateTimeStamp";
    private final String RTMS_SENSOR_ERR_RATE_KEY = "SensorErrRate";
    private final String RTMS_HEALTH_BYTE_KEY = "HealthByte";
    private final String RTMS_SPEED_UNITS_KEY = "SpeedUnits";
    private final String RTMS_VOL_MID2_KEY = "Vol_Mid2";
    private final String RTMS_VOL_LONG2_KEY = "Vol_Long2";
    private final String DATE_PATTERN = "yyyy-MM-dd'T'hh:mm:ss";

    @Override
    protected SimpleDateFormat buildSimpleDateFormat() {
        return new SimpleDateFormat(DATE_PATTERN);
    }

    @Override
    protected ODEDataParserResponse parse(byte[] bytes) {
        getLogger().debug("Parsing RTMS Data.");
        Document document = getMessageDocument(bytes);
        if (document != null) {
            Element body = document.body();
            if (body != null) {
                Elements bodyElements = body.getElementsByTag(RTMS_DATA_TAG);
                if (bodyElements != null) {
                    Element observationsElement = bodyElements.first();
                    if (observationsElement != null) {
                        Elements children = observationsElement.children();
                        if (children != null && !children.isEmpty()) {
                            RTMSData rtmsData = new RTMSData();
                            List<RTMSData.RTMSDataElement> rtmsDataElements = new ArrayList<RTMSData.RTMSDataElement>();
                            for (Element element : children) {
                                if (element.tagName().equals(RTMS_DATA_ELEMENT_TAG)) {
                                    RTMSData.RTMSDataElement rtmsDataElement = parseRTMSDataElement(element);
                                    if (rtmsDataElement != null) {
                                        rtmsDataElements.add(rtmsDataElement);
                                    }
                                }
                            }
                            rtmsData.setRtmsDataElements(rtmsDataElements);
                            return new ODEDataParserResponse(rtmsData, ODEDataParserReportCode.PARSE_SUCCESS);
                        } else {
                            return new ODEDataParserResponse(null, ODEDataParserReportCode.NO_DATA);
                        }
                    } else {
                        return new ODEDataParserResponse(null, ODEDataParserReportCode.UNEXPECTED_DATA_FORMAT);
                    }
                } else {
                    return new ODEDataParserResponse(null, ODEDataParserReportCode.UNEXPECTED_DATA_FORMAT);
                }
            } else {
                return new ODEDataParserResponse(null, ODEDataParserReportCode.UNEXPECTED_DATA_FORMAT);
            }
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }

    private RTMSData.RTMSDataElement parseRTMSDataElement(Element element) {
        RTMSData.RTMSDataElement rtmsDataElement = null;
        if (element != null) {
            rtmsDataElement = new RTMSData.RTMSDataElement();
            Elements children = element.children();
            for (Element attribute : children) {
                String key = attribute.tagName();
                String value;
                List<Node> childNodes = attribute.childNodes();
                if (childNodes != null && !childNodes.isEmpty()) {
                    Node valueNode = childNodes.get(0);
                    if (valueNode != null) {
                        value = valueNode.attr("text");
                        if (value != null) {
                            if (key.equalsIgnoreCase(RTMS_NETWORK_ID_KEY)) {
                                rtmsDataElement.setRtmsNetworkId(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_NAME_KEY)) {
                                rtmsDataElement.setRtmsName(value);
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_ZONE_KEY)) {
                                rtmsDataElement.setZone(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_ZONE_LABEL_KEY)) {
                                rtmsDataElement.setZoneLabel(value);
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_STATION_NAME_KEY)) {
                                rtmsDataElement.setStationName(value);
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_SPEED_KEY)) {
                                rtmsDataElement.setSpeed(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_FWDLK_SPEED_KEY)) {
                                rtmsDataElement.setFwdlkSpeed(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_VOLUME_KEY)) {
                                rtmsDataElement.setVolume(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_VOL_MID_KEY)) {
                                rtmsDataElement.setVolMid(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_VOL_LONG_KEY)) {
                                rtmsDataElement.setVolLong(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_VOL_EXTRA_LONG_KEY)) {
                                rtmsDataElement.setVolExtraLong(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_OCCUPANCY_KEY)) {
                                rtmsDataElement.setOccupancy(value);
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_MSG_NUMBER_KEY)) {
                                rtmsDataElement.setMsgNumber(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_DATE_TIME_STAMP_KEY)) {
                                rtmsDataElement.setDateTimeStamp(parseDate(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_SENSOR_ERR_RATE_KEY)) {
                                rtmsDataElement.setSensorErrRate(Float.parseFloat(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_HEALTH_BYTE_KEY)) {
                                rtmsDataElement.setHealthByte(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_SPEED_UNITS_KEY)) {
                                rtmsDataElement.setSpeedUnits(value);
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_VOL_MID2_KEY)) {
                                rtmsDataElement.setVolMid2(Integer.parseInt(value));
                                continue;
                            }
                            if (key.equalsIgnoreCase(RTMS_VOL_LONG2_KEY)) {
                                rtmsDataElement.setVolLong2(Integer.parseInt(value));
                            }
                        }
                    }
                }
            }
        }
        return rtmsDataElement;
    }
}


