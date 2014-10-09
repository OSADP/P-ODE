package com.leidos.ode.agent.parser.jsoup;

import com.leidos.ode.agent.data.vdot.VDOTData;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.data.vdot.VDOTTravelTimeData;
import com.leidos.ode.agent.data.vdot.VDOTWeatherData;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
public class VDOTJsoup extends ODEJsoup {

    private static final String TAG = "VDOTJsoup";
    private static final String VDOT_SPEED_TAG = "orci:tss_detector_status";
    private static final String VDOT_TRAVEL_TIME_TAG = "orci:traveltimesegment";
    private static final String VDOT_WEATHER_TAG = "orci:vat_road_cond_point";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String TIME_ZONE_GMT = "GMT";
    private static Logger logger = Logger.getLogger(TAG);

    public static List<VDOTData> parseVDOTData(byte[] bytes) {
        Document document = getMessageDocument(bytes);
        Elements featureMembers = document.getElementsByTag("gml:featureMembers");
        if (featureMembers != null) {
            if (!featureMembers.isEmpty()) {

                Element firstChild = featureMembers.first().child(0);
                String firstChildTag = firstChild.tagName();

                if (firstChildTag.equalsIgnoreCase(VDOT_WEATHER_TAG)) {
                    logger.debug("Parsing VDOTWeather data");
                    return parseVDOTWeatherData(document);

                } else if (firstChildTag.equalsIgnoreCase(VDOT_SPEED_TAG)) {
                    logger.debug("Parsing VDOTSpeed data");
                    return parseVDOTSpeedData(document);

                } else if (firstChildTag.equalsIgnoreCase(VDOT_TRAVEL_TIME_TAG)) {
                    logger.debug("Parsing VDOTTravelTime data");
                    return parseVDOTTravelTimeData(document);
                }
            } else {
                logger.debug("There is no data to be parsed.");
            }
        } else {
            logger.debug("Error parsing feature members. Members were null.");
        }
        return null;
    }

    private static List<VDOTData> parseVDOTWeatherData(Document document) {
        List<VDOTData> vdotWeatherDataList = new ArrayList<VDOTData>();

        Elements messageElements = document.getElementsByTag(VDOT_WEATHER_TAG);

        for (Element messageElement : messageElements) {

            VDOTWeatherData vdotWeatherData = new VDOTWeatherData();

            Elements elementChildren = messageElement.children();

            for (Element childElement : elementChildren) {

                String tagName = childElement.tagName();
                String value = childElement.ownText();

            }
            vdotWeatherDataList.add(vdotWeatherData);
        }
        return vdotWeatherDataList;
    }

    private static List<VDOTData> parseVDOTSpeedData(Document document) {
        List<VDOTData> vdotSpeedDataList = new ArrayList<VDOTData>();

        Elements messageElements = document.getElementsByTag(VDOT_SPEED_TAG);

        for (Element messageElement : messageElements) {

            VDOTSpeedData vdotSpeedData = new VDOTSpeedData();

            Elements elementChildren = messageElement.children();

            for (Element childElement : elementChildren) {

                String tagName = childElement.tagName();
                String value = childElement.ownText();

                if (tagName.equals("orci:detectorid")) {
                    vdotSpeedData.setDetectorId(value);
                    continue;
                }
                if (tagName.equals("orci:stationid")) {
                    vdotSpeedData.setStationId(value);
                    continue;
                }
                if (tagName.equals("orci:device_name")) {
                    vdotSpeedData.setDeviceName(value);
                    continue;
                }
                if (tagName.equals("orci:lane_direction")) {
                    vdotSpeedData.setLaneDirection(value);
                    continue;
                }
                if (tagName.equals("orci:lanetype")) {
                    vdotSpeedData.setLaneType(value);
                    continue;
                }
                if (tagName.equals("orci:lanenum")) {
                    vdotSpeedData.setLaneNum(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:last_updated")) {
                    vdotSpeedData.setLastUpdated(parseDate(value));
                    continue;
                }
                if (tagName.equals("orci:mile_marker")) {
                    vdotSpeedData.setMileMarker(Double.parseDouble(value));
                    continue;
                }
                if (tagName.equals("orci:occupancy")) {
                    vdotSpeedData.setOccupancy(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:routename")) {
                    vdotSpeedData.setRouteName(value);
                    continue;
                }
                if (tagName.equals("orci:speed")) {
                    vdotSpeedData.setSpeed(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:the_geom")) {
                    vdotSpeedData.setGeometry(parseGeometry(childElement));
                    continue;
                }
                if (tagName.equals("orci:volume")) {
                    vdotSpeedData.setVolume(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:nodeid")) {
                    vdotSpeedData.setNodeId(Integer.parseInt(value));
                    continue;
                }
            }
            vdotSpeedDataList.add(vdotSpeedData);
        }
        return vdotSpeedDataList;
    }

    private static List<VDOTData> parseVDOTTravelTimeData(Document document) {
        List<VDOTData> vdotTravelTimeDataList = new ArrayList<VDOTData>();

        Elements messageElements = document.getElementsByTag(VDOT_SPEED_TAG);

        for (Element messageElement : messageElements) {

            VDOTTravelTimeData vdotTravelTimeData = new VDOTTravelTimeData();

            Elements elementChildren = messageElement.children();

            for (Element childElement : elementChildren) {

                String tagName = childElement.tagName();
                String value = childElement.ownText();

                if (tagName.equals("orci:segmentid")) {
                    vdotTravelTimeData.setSegmentId(value);
                    continue;
                }
                if (tagName.equals("orci:lasttimeupdated")) {
                    vdotTravelTimeData.setLastTimeUpdated(parseDate(value));
                    continue;
                }
                if (tagName.equals("orci:segmentname")) {
                    vdotTravelTimeData.setSegmentName(value);
                    continue;
                }
                if (tagName.equals("orci:traveltime")) {
                    vdotTravelTimeData.setTravelTime(Integer.parseInt(value));
                    continue;
                }
                if (tagName.equals("orci:the_geom")) {
                    vdotTravelTimeData.setGeometry(parseGeometry(childElement));
                    continue;
                }
            }
            vdotTravelTimeDataList.add(vdotTravelTimeData);
        }
        return vdotTravelTimeDataList;
    }

    private static float[] parseGeometry(Element element) {
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

    protected static Date parseDate(String dateString) {
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
