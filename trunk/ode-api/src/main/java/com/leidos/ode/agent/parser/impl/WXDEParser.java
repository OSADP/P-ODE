package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.data.wxde.WXDEData;
import com.leidos.ode.agent.parser.DateParser;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/10/14
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class WXDEParser extends DateParser {

    private final String WXDE_DATA_TAG = "wxde";
    private final String WXDE_OBSERVATIONS_TAG = "observations";
    private final String WXDE_OBSERVATION_TAG = "obs";
    //Attribute Keys
    private final String WXDE_SOURCE_ID_KEY = "sourceid";
    private final String WXDE_OBS_TYPE_ID_KEY = "obstypeid";
    private final String WXDE_OBS_TYPE_NAME_KEY = "obstypename";
    private final String WXDE_SENSOR_ID_KEY = "sensorid";
    private final String WXDE_SENSOR_INDEX_KEY = "sensorindex";
    private final String WXDE_PLATFORM_ID_KEY = "platformid";
    private final String WXDE_SITE_ID_KEY = "siteid";
    private final String WXDE_CATEGORY_KEY = "category";
    private final String WXDE_CONTRIB_ID_KEY = "contribid";
    private final String WXDE_CONTRIBUTOR_KEY = "contributor";
    private final String WXDE_PLATFORM_CODE_KEY = "platformcode";
    private final String WXDE_TIMESTAMP_KEY = "timestamp";
    private final String WXDE_LATITUDE_KEY = "latitude";
    private final String WXDE_LONGITUDE_KEY = "longitude";
    private final String WXDE_ELEVATION_KEY = "elevation";
    private final String WXDE_OBSERVATION_KEY = "observation";
    private final String WXDE_UNITS_KEY = "units";
    private final String WXDE_ENGLISH_VALUE_KEY = "englishvalue";
    private final String WXDE_ENGLISH_UNITS_KEY = "englishunits";
    private final String WXDE_CONF_VALUE_KEY = "confvalue";
    private final String WXDE_FLAGS_KEY = "flags";
    private final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    protected SimpleDateFormat buildSimpleDateFormat() {
        return new SimpleDateFormat(DATE_PATTERN);
    }

    @Override
    protected ODEDataParserResponse parse(byte[] bytes) {
        getLogger().debug("Parsing WXDE Data.");
        Document document = getMessageDocument(bytes);
        if (document != null) {
            Element body = document.body();
            if (body != null) {
                Elements bodyElements = body.getElementsByTag(WXDE_DATA_TAG);
                if (bodyElements != null) {
                    Element observationsElement = bodyElements.first();
                    if (observationsElement != null) {
                        Elements children = observationsElement.children();
                        if (children != null && !children.isEmpty()) {
                            for (Element element : children) {
                                if (element.tagName().equals(WXDE_OBSERVATIONS_TAG)) {
                                    WXDEData wxdeData = new WXDEData();
                                    List<WXDEData.WXDEDataElement> wxdeDataElements = new ArrayList<WXDEData.WXDEDataElement>();
                                    Elements obsElements = element.getElementsByTag(WXDE_OBSERVATION_TAG);
                                    for (Element obsElement : obsElements) {
                                        WXDEData.WXDEDataElement wxdeDataElement = parseObservationElement(obsElement);
                                        if (wxdeDataElement != null) {
                                            wxdeDataElements.add(wxdeDataElement);
                                        }
                                    }
                                    wxdeData.setWxdeDataElements(wxdeDataElements);
                                    return new ODEDataParserResponse(wxdeData, ODEDataParserReportCode.PARSE_SUCCESS);
                                } else {
                                    return new ODEDataParserResponse(null, ODEDataParserReportCode.UNEXPECTED_DATA_FORMAT);
                                }
                            }
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

    private WXDEData.WXDEDataElement parseObservationElement(Element observationElement) {
        WXDEData.WXDEDataElement wxdeDataElement = null;
        if (observationElement != null) {
            wxdeDataElement = new WXDEData.WXDEDataElement();
            Attributes attributes = observationElement.attributes();
            for (Attribute attribute : attributes) {
                String key = attribute.getKey();
                String value = attribute.getValue();
                if (key.equals(WXDE_SOURCE_ID_KEY)) {
                    wxdeDataElement.setSourceId(value);
                    continue;
                }
                if (key.equals(WXDE_OBS_TYPE_ID_KEY)) {
                    wxdeDataElement.setObsTypeId(value);
                    continue;
                }
                if (key.equals(WXDE_OBS_TYPE_NAME_KEY)) {
                    wxdeDataElement.setObsTypeName(value);
                    continue;
                }
                if (key.equals(WXDE_SENSOR_ID_KEY)) {
                    wxdeDataElement.setSensorId(value);
                    continue;
                }
                if (key.equals(WXDE_SENSOR_INDEX_KEY)) {
                    wxdeDataElement.setSensorIndex(value);
                    continue;
                }
                if (key.equals(WXDE_PLATFORM_ID_KEY)) {
                    wxdeDataElement.setStationId(value);
                    continue;
                }
                if (key.equals(WXDE_SITE_ID_KEY)) {
                    wxdeDataElement.setSiteId(value);
                    continue;
                }
                if (key.equals(WXDE_CATEGORY_KEY)) {
                    wxdeDataElement.setCategory(value);
                    continue;
                }
                if (key.equals(WXDE_CONTRIB_ID_KEY)) {
                    wxdeDataElement.setContribId(value);
                    continue;
                }
                if (key.equals(WXDE_CONTRIBUTOR_KEY)) {
                    wxdeDataElement.setContributor(value);
                    continue;
                }
                if (key.equals(WXDE_PLATFORM_CODE_KEY)) {
                    wxdeDataElement.setStationCode(value);
                    continue;
                }
                if (key.equals(WXDE_TIMESTAMP_KEY)) {
                    Date timestamp = parseDate(value);
                    if (timestamp != null) {
                        wxdeDataElement.setTimestamp(timestamp);
                    } else {
                        getLogger().debug("Unable to set WXDE data element timestamp; was null.");
                    }
                    continue;
                }
                if (key.equals(WXDE_LATITUDE_KEY)) {
                    wxdeDataElement.setLatitude(Double.parseDouble(value));
                    continue;
                }
                if (key.equals(WXDE_LONGITUDE_KEY)) {
                    wxdeDataElement.setLongitude(Double.parseDouble(value));
                    continue;
                }
                if (key.equals(WXDE_ELEVATION_KEY)) {
                    wxdeDataElement.setElevation(Double.parseDouble(value));
                    continue;
                }
                if (key.equals(WXDE_OBSERVATION_KEY)) {
                    wxdeDataElement.setObservation(Double.parseDouble(value));
                    continue;
                }
                if (key.equals(WXDE_UNITS_KEY)) {
                    wxdeDataElement.setUnits(value);
                    continue;
                }
                if (key.equals(WXDE_ENGLISH_VALUE_KEY)) {
                    wxdeDataElement.setEnglishValue(Double.parseDouble(value));
                    continue;
                }
                if (key.equals(WXDE_ENGLISH_UNITS_KEY)) {
                    wxdeDataElement.setEnglishUnits(value);
                    continue;
                }
                if (key.equals(WXDE_CONF_VALUE_KEY)) {
                    wxdeDataElement.setConfValue(Double.parseDouble(value));
                    continue;
                }
                if (key.equals(WXDE_FLAGS_KEY)) {
                    wxdeDataElement.setFlags(value);
                }
            }
        }
        return wxdeDataElement;
    }
}
