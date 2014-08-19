package com.leidos.ode.collector;

import com.leidos.ode.collector.datasource.DataSourceException;
import com.leidos.ode.collector.datasource.RestPullDataSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 8/18/14
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class VDOTDataSource extends RestPullDataSource {

    private final String TAG = getClass().getSimpleName();

    private Logger logger = Logger.getLogger(TAG);

    private String wfsBaseUrl;
    private String xmlBaseUrl;

    StringBuilder stringBuilder;

    public VDOTDataSource() {
        stringBuilder = new StringBuilder();
    }

    private void retrieveData(String recordName) {
        setRequestURI(getRequestURIXML(recordName));
        try {
            startDataSource();
            byte[] data = getDataFromSource();
            System.out.println("Received vdotdata of length: " + data.length);
        } catch (DataSourceException e) {
            logger.equals(e.getLocalizedMessage());
        }
    }

    private String getRequestURIXML(String recordName) {
        stringBuilder = new StringBuilder();
        stringBuilder.append(getXmlBaseUrl()).append("/").append(recordName).append("/");
        return stringBuilder.toString();
    }

    private String getRequestURIWFS(String recordName) {
        stringBuilder = new StringBuilder();
        stringBuilder.append(getWfsBaseUrl()).append(recordName);
        return stringBuilder.toString();
    }

    public String getWfsBaseUrl() {
        return wfsBaseUrl;
    }

    public void setWfsBaseUrl(String wfsBaseUrl) {
        this.wfsBaseUrl = wfsBaseUrl;
    }

    public String getXmlBaseUrl() {
        return xmlBaseUrl;
    }

    public void setXmlBaseUrl(String xmlBaseUrl) {
        this.xmlBaseUrl = xmlBaseUrl;
    }

    @Override
    public String getEmulatorWFS(String typeName) {
        stringBuilder = new StringBuilder();
        stringBuilder.append(getWfsBaseUrl());
        stringBuilder.append("&typeName=");
        stringBuilder.append(typeName);
        stringBuilder.append("&");
        stringBuilder.append(getFilter());

        return stringBuilder.toString();
//        String boundingBoxKML = "<Polygon><outerBoundaryIs><coordinates>-77.3544727652,38.8599860006 -77.2634035056,38.8599860006 -77.2634035056,38.8795318015 -77.3544727652,38.8795318015</coordinates></outerBoundaryIs></Polygon>";
//        String boundingBoxGeoJSON = "[[[-77.3544727652,38.8599860006],[-77.3544727652,38.8795318015],[-77.2634035056,38.8795318015],[-77.2634035056,38.8599860006],[-77.3544727652,38.8599860006]]]";

    }

    private String getPropertyEqualToLiteral(String propertyName, String literal){
        stringBuilder.append("<ogc:PropertyIsEqualTo>");
        stringBuilder.append("<ogc:PropertyName>");
        stringBuilder.append(propertyName);
        stringBuilder.append("</ogc:PropertyName>");
        stringBuilder.append("<ogc:Literal>");
        stringBuilder.append(literal);
        stringBuilder.append("</ogc:Literal>");
        stringBuilder.append("</ogc:PropertyIsEqualTo>");

        return stringBuilder.toString();
    }


    private String getFilter(){
        stringBuilder = new StringBuilder();
        stringBuilder.append("Filter=<Filter>");
        stringBuilder.append("<ogc:And>");
        stringBuilder.append("<ogc:BBOX>");
        stringBuilder.append("<ogc:PropertyName>the_geom</ogc:PropertyName>");
        stringBuilder.append("<gml:Box srcName=\"\">");
        stringBuilder.append("<gml:coordinates>");
        stringBuilder.append(getEmulatorBBOX());
        stringBuilder.append("</gml:coordinates>");
        stringBuilder.append("</gml:Box>");
        stringBuilder.append("</ogc:BBOX>");
        stringBuilder.append(getPropertyEqualToLiteral("orci:route_name", "I-66"));
        stringBuilder.append("</ogc:And>");
        stringBuilder.append("</ogc:Filter>");
        stringBuilder.append("</wfs:Query>");
        stringBuilder.append("</wfs:GetFeature>");

        return stringBuilder.toString();
    }

    /**
     * Returns bounding box representing I-66, outside of the beltway (I-495), between Rt 243 and Rt 50 in Virginia.
     */
    private String getEmulatorBBOX(){
        return "38.85611,-77.353099,38.882238,-77.26306";
    }

    private String getRouteName(){
        return "I-66";
    }
}
