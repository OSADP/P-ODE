package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.data.ritis.RITISIncidentData;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.agent.data.ritis.RITISWeatherDataClarus;
import com.leidos.ode.agent.data.ritis.RITISWeatherDataNWS;
import com.leidos.ode.agent.parser.JAXBEnabledParser;
import edu.umd.cattlab.schema.ritisFilter.other.AlertsData;
import edu.umd.cattlab.schema.ritisFilter.other.ClarusData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ritis.schema.tmdd_0_0_0.ZoneDetectorDataRITIS;

import javax.xml.bind.JAXBElement;
import org.ritis.schema.atis_3_0_76.AdvisoryInformation;

public class RITISParser extends JAXBEnabledParser {

    private static final String RITIS_SPEED_TAG = "ns8:detectorZoneData-RITIS";
    private static final String RITIS_WEATHER_NWS_TAG = "ns10:alertsData";
    private static final String RITIS_WEATHER_CLARUS_TAG = "ns10:clarusData";
    private static final String RITIS_ERROR_TAG = "errors";
    private static final String RITIS_INCIDENT_TAG = "ns2:advisoryInformation";

    @Override
    protected ODEDataParserResponse parseDocumentByTag(String tag, byte[] bytes) {
        if (tag.equalsIgnoreCase(RITIS_SPEED_TAG)) {
            getLogger().debug("Parsing RITISSpeed data");
            return parseRITISSpeedData(bytes);
        } else if (tag.equalsIgnoreCase(RITIS_WEATHER_NWS_TAG)) {
            getLogger().debug("Parsing RITISWeather data from NWS");
            return parseRITISWeatherNWS(bytes);
        } else if (tag.equalsIgnoreCase(RITIS_WEATHER_CLARUS_TAG)) {
            getLogger().debug("Parsing RITISWeather data from Clarus");
            return parseRITISWeatherClarus(bytes);
        } else if (tag.equalsIgnoreCase(RITIS_ERROR_TAG)) {
            getLogger().debug("Parsing RITIS Error!");
            return parseRITISError(bytes);
        }else if(tag.equalsIgnoreCase(RITIS_INCIDENT_TAG)){
            //parse incident info
            getLogger().debug("Parsing RITIS Incident data");
            return parseRITISIncidentData(bytes);
        } else {
            getLogger().debug("Unknown data type for tag: " + tag);
            return new ODEDataParserResponse(null, ODEDataParserReportCode.DATA_TYPE_UNKNOWN);
        }
    }

    private ODEDataParserResponse parseRITISSpeedData(byte[] bytes) {
        JAXBElement<ZoneDetectorDataRITIS> zoneDetectorDataRITIS = (JAXBElement<ZoneDetectorDataRITIS>) unmarshalBytes(bytes, new Class[]{org.ritis.schema.tmdd_0_0_0.ObjectFactory.class});
        if (zoneDetectorDataRITIS != null) {
            RITISSpeedData ritisSpeedData = new RITISSpeedData();
            ritisSpeedData.setZoneDetectorDataRITIS(zoneDetectorDataRITIS.getValue());
            return new ODEDataParserResponse(ritisSpeedData, ODEDataParserReportCode.PARSE_SUCCESS);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }

    private ODEDataParserResponse parseRITISWeatherNWS(byte[] bytes) {
        JAXBElement<AlertsData> alertsData = (JAXBElement<AlertsData>) unmarshalBytes(bytes, new Class[]{edu.umd.cattlab.schema.ritisFilter.other.ObjectFactory.class});
        if (alertsData != null) {
            RITISWeatherDataNWS ritisWeatherDataNWS = new RITISWeatherDataNWS();
            ritisWeatherDataNWS.setAlertsData(alertsData.getValue());
            return new ODEDataParserResponse(ritisWeatherDataNWS, ODEDataParserReportCode.PARSE_SUCCESS);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }
    
    private ODEDataParserResponse parseRITISIncidentData(byte[] bytes) {
        JAXBElement<AdvisoryInformation> advisoryInformation = (JAXBElement<AdvisoryInformation>) unmarshalBytes(bytes, new Class[]{org.ritis.schema.atis_3_0_76.ObjectFactory.class});
        if(advisoryInformation != null){
            RITISIncidentData incidentData = new RITISIncidentData();
            incidentData.setAdvisoryInformation(advisoryInformation.getValue());
            return new ODEDataParserResponse(incidentData, ODEDataParserReportCode.PARSE_SUCCESS);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }
    

    private ODEDataParserResponse parseRITISWeatherClarus(byte[] bytes) {
        JAXBElement<ClarusData> clarusData = (JAXBElement<ClarusData>) unmarshalBytes(bytes, new Class[]{edu.umd.cattlab.schema.ritisFilter.other.ObjectFactory.class});
        if (clarusData != null) {
            RITISWeatherDataClarus ritisWeatherDataClarus = new RITISWeatherDataClarus();
            ritisWeatherDataClarus.setClarusData(clarusData.getValue());
            return new ODEDataParserResponse(ritisWeatherDataClarus, ODEDataParserReportCode.PARSE_SUCCESS);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }

    private ODEDataParserResponse parseRITISError(byte[] bytes) {
        Document document = getMessageDocument(bytes);
        if (document != null) {
            Element headElement = document.head();
            Element bodyElement = document.body();
            if (bodyElement != null) {
                Elements bodyChildren = bodyElement.children();
                if (bodyChildren != null) {
                    Element error = bodyChildren.first();
                    if (error != null) {
                        Elements errorChildren = error.children();
                        if (errorChildren != null) {
                            Element errorChild = errorChildren.first();
                            if (errorChild != null) {
                                String errorText = errorChild.ownText();
                                getLogger().error("Error requesting RITIS data. Response from server: '" + errorText + "'.");
                                return new ODEDataParserResponse(null, ODEDataParserReportCode.DATA_SOURCE_SERVER_ERROR);
                            }
                        }
                    }
                }
            }
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.UNKNOWN_ERROR);
    }
}