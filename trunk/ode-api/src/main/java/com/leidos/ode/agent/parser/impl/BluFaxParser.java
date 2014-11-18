package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.data.blufax.BluFaxErrorData;
import com.leidos.ode.agent.data.blufax.BluFaxLinkData;
import com.leidos.ode.agent.data.blufax.BluFaxNodeData;
import com.leidos.ode.agent.data.blufax.BluFaxRouteData;
import com.leidos.ode.agent.parser.JAXBEnabledParser;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.pull.BluFaxDataSource;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.tmdd._3.messages.*;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/17/14
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class BluFaxParser extends JAXBEnabledParser {

    private static final String BLUFAX_LINK_TAG = "tmdd:linkStatusMsg";
    private static final String BLUFAX_ROUTE_TAG = "tmdd:routeStatusMsg";
    private static final String BLUFAX_ERROR_TAG = "ns2:errorReportMsg";
//    private BluFaxDataSource bluFaxNodeDataSource;
//    private String protocol;
//    private String sourceAddress;
//    private String baseUrl;
//    private String nodeDetailsUrl;
//    private String username;
//    private String password;

    @Override
    protected ODEDataParserResponse parseDocumentByTag(String tag, byte[] bytes) {
        if (tag.equalsIgnoreCase(BLUFAX_LINK_TAG)) {
            getLogger().debug("Parsing BluFax Link Status Message.");
            return parseBluFaxLinkStatusMessage(bytes);
        } else if (tag.equalsIgnoreCase(BLUFAX_ROUTE_TAG)) {
            getLogger().debug("Parsing BluFax Route Status Message.");
            return parseBluFaxRouteStatusMessage(bytes);
        } else if (tag.equalsIgnoreCase(BLUFAX_ERROR_TAG)) {
            getLogger().debug("Parsing BluFax Error Message.");
            return parseBluFaxErrorMessage(bytes);
        } else {
            getLogger().debug("Unknown data type for tag: " + tag);
            return new ODEDataParserResponse(null, ODEDataParserReportCode.DATA_TYPE_UNKNOWN);
        }
    }

    private ODEDataParserResponse parseBluFaxLinkStatusMessage(byte[] bytes) {
        LinkStatusMsg linkStatusMsg = (LinkStatusMsg) unmarshalBytes(bytes, new Class[]{org.tmdd._3.messages.ObjectFactory.class, com.fastlanesw.bfw.ObjectFactory.class});
        if (linkStatusMsg != null) {
            BluFaxLinkData bluFaxLinkData = new BluFaxLinkData();
            bluFaxLinkData.setLinkStatusMsg(linkStatusMsg);
            return new ODEDataParserResponse(bluFaxLinkData, ODEDataParserReportCode.PARSE_SUCCESS);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }

    private ODEDataParserResponse parseBluFaxRouteStatusMessage(byte[] bytes) {
        RouteStatusMsg routeStatusMsg = (RouteStatusMsg) unmarshalBytes(bytes, new Class[]{org.tmdd._3.messages.ObjectFactory.class, com.fastlanesw.bfw.ObjectFactory.class});
        if (routeStatusMsg != null) {
            BluFaxRouteData bluFaxRouteData = new BluFaxRouteData();
            bluFaxRouteData.setRouteStatusMsg(routeStatusMsg);
            return new ODEDataParserResponse(bluFaxRouteData, ODEDataParserReportCode.PARSE_SUCCESS);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }

    private ODEDataParserResponse parseBluFaxNodeInventoryMessage(byte[] bytes) {
        NodeInventoryMsg nodeInventoryMsg = (NodeInventoryMsg) unmarshalBytes(bytes, new Class[]{org.tmdd._3.messages.ObjectFactory.class, com.fastlanesw.bfw.ObjectFactory.class});
        if (nodeInventoryMsg != null) {
            BluFaxNodeData bluFaxNodeData = new BluFaxNodeData();
            bluFaxNodeData.setNodeInventoryMsg(nodeInventoryMsg);
            return new ODEDataParserResponse(bluFaxNodeData, ODEDataParserReportCode.PARSE_SUCCESS);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }

    private ODEDataParserResponse parseBluFaxErrorMessage(byte[] bytes) {
        JAXBElement<ErrorReport> errorReportMsg = (JAXBElement<ErrorReport>) unmarshalBytes(bytes, new Class[]{org.tmdd._3.messages.ObjectFactory.class});
        if (errorReportMsg != null) {
            BluFaxErrorData bluFaxErrorData = new BluFaxErrorData();
            bluFaxErrorData.setErrorReportMsg(errorReportMsg.getValue());
            getLogger().debug("BluFax ErrorReportMsg parsed: " + bluFaxErrorData.getErrorReportMsg().getErrorText());
            return new ODEDataParserResponse(bluFaxErrorData, ODEDataParserReportCode.DATA_SOURCE_SERVER_ERROR);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);

    }

//    private void parseNodes(List<String> nodeIds) {
//        if (nodeIds != null) {
//            for (String nodeId : nodeIds) {
//                byte[] bytes = examineNode(nodeId);
//                parseNodeDetails(bytes);
//            }
//        }
//        getBluFaxNodeDataSource().stopDataSource();
//    }
//
//    private void getBluFaxNodeInventory() {
//        //Get Node Inventory
//        getBluFaxNodeDataSource().setCollectorDataSourceListener(new CollectorDataSource.CollectorDataSourceListener() {
//            @Override
//            public void onDataReceived(byte[] receivedData) {
//                ODEDataParserResponse response = parseBluFaxNodeInventoryMessage(receivedData);
//                if (response != null) {
//                    Object data = response.getData();
//                    if (data != null) {
//                        BluFaxNodeData bluFaxNodeData = (BluFaxNodeData) data;
//                        NodeInventoryMsg nodeInventoryMsg = bluFaxNodeData.getNodeInventoryMsg();
//                        if (nodeInventoryMsg != null) {
//                            List<NodeInventory> nodeInventoryList = nodeInventoryMsg.getNodeInventoryItem();
//                            if (nodeInventoryList != null) {
//                                final List<String> nodeIdList = new ArrayList<String>();
//                                for (NodeInventory nodeInventory : nodeInventoryList) {
//                                    NodeInventory.NodeList nodeList = nodeInventory.getNodeList();
//                                    List<NodeInventoryList> nodeInventoryListList = nodeList.getNode();
//                                    for (NodeInventoryList nodeInventoryList1 : nodeInventoryListList) {
//                                        nodeIdList.add(nodeInventoryList1.getNodeId());
//                                    }
//                                }
//                                parseNodes(nodeIdList);
//                            }
//                        }
//                    }
//                }
//            }
//        });
//        getBluFaxNodeDataSource().startDataSource();
//    }
//
//    private byte[] examineNode(String nodeId) {
//        String nodeRequestString = buildNodeRequest(nodeId);
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//        HttpGet httpGet = new HttpGet(nodeRequestString);
//        getLogger().debug("Examining node: " + nodeId);
//        try {
//            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(getUsername(), getPassword());
//            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
//            HttpClientContext httpClientContext = HttpClientContext.create();
//
//            httpClientContext.setCredentialsProvider(credentialsProvider);
//            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet, httpClientContext);
//            HttpEntity responseEntity = closeableHttpResponse.getEntity();
//            byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
//            EntityUtils.consume(responseEntity);
//            closeableHttpResponse.close();
//            httpClient.close();
//            return responseBytes;
//        } catch (IOException e) {
//            getLogger().error(e.getLocalizedMessage());
//        }
//        return null;
//    }
//
//    private String buildNodeRequest(String nodeId) {
//        return new StringBuilder()
//                .append(getProtocol())
//                .append("://")
//                .append(getSourceAddress())
//                .append("/")
//                .append(getBaseUrl())
//                .append(getNodeDetailsUrl())
//                .append(".hdo")
//                .append("?")
//                .append("id=")
//                .append(nodeId)
//                .toString();
//    }
//
//    private void parseNodeDetails(byte[] bytes) {
//        Document document = getMessageDocument(bytes);
//        if (document != null) {
//            getLogger().debug("Successfully used Jsoup to parse Node details!");
//        }
//    }
//
//    public BluFaxDataSource getBluFaxNodeDataSource() {
//        return bluFaxNodeDataSource;
//    }
//
//    public void setBluFaxNodeDataSource(BluFaxDataSource bluFaxNodeDataSource) {
//        this.bluFaxNodeDataSource = bluFaxNodeDataSource;
//    }
//
//    public String getSourceAddress() {
//        return sourceAddress;
//    }
//
//    public void setSourceAddress(String sourceAddress) {
//        this.sourceAddress = sourceAddress;
//    }
//
//    public String getProtocol() {
//        return protocol;
//    }
//
//    public void setProtocol(String protocol) {
//        this.protocol = protocol;
//    }
//
//    public String getBaseUrl() {
//        return baseUrl;
//    }
//
//    public void setBaseUrl(String baseUrl) {
//        this.baseUrl = baseUrl;
//    }
//
//    public String getNodeDetailsUrl() {
//        return nodeDetailsUrl;
//    }
//
//    public void setNodeDetailsUrl(String nodeDetailsUrl) {
//        this.nodeDetailsUrl = nodeDetailsUrl;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
}
