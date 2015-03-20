/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.wxde.WXDEData;
import com.leidos.ode.data.AmbientAirTemperature;
import com.leidos.ode.data.Distance;
import com.leidos.ode.data.Latitude;
import com.leidos.ode.data.Longitude;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.PodeDataDistribution;
import com.leidos.ode.data.PodeDataRecord;
import com.leidos.ode.data.PodeDialogID;
import com.leidos.ode.data.PodeSource;
import com.leidos.ode.data.PodeWeatherData;
import com.leidos.ode.data.PodeWeatherinfo;
import com.leidos.ode.data.Position3D;
import com.leidos.ode.data.PrecipRate;
import com.leidos.ode.data.SemiSequenceID;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.data.Sha256Hash;
import com.leidos.ode.util.ODEMessageType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author cassadyja
 */
public class WxDEMessageFormatter extends ODEMessageFormatter{

    private static final String TEMP_READING = "essSurfaceTemperature";
    private static final String PRECIP_READING = "essPrecipRate";
    private static final String VIS_READING = "essVisibility";
    
    
    @Override
    public Map<ODEMessageType,List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        Map<ODEMessageType,List<PodeDataDistribution>> messages = new HashMap<ODEMessageType,List<PodeDataDistribution>>();
        WXDEData wxdeData = (WXDEData)agentMessage.getFormattedMessage();
        
        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.wxde);
        List<PodeDataDistribution> messageList = new ArrayList<PodeDataDistribution>();
        for(WXDEData.WXDEDataElement element:wxdeData.getWxdeDataElements()){
            PodeDataDistribution dataDelivery = createMessage(agentMessage, element, source, serviceRequst);
            messageList.add(dataDelivery);
        }
        messages.put(ODEMessageType.WEATHER, messageList);

        
        
        return messages;
    }    

    private PodeDataDistribution createMessage(ODEAgentMessage agentMessage, WXDEData.WXDEDataElement element, PodeSource source, ServiceRequest serviceRequst) {
        PodeDataDistribution dataDelivery = new PodeDataDistribution();
        PodeDialogID dialog = new PodeDialogID();
        dialog.setValue(PodeDialogID.EnumType.podeDataDistribution);
        dataDelivery.setDialogID(dialog);
        
        dataDelivery.setGroupID(serviceRequst.getGroupID());
        dataDelivery.setRequestID(serviceRequst.getRequestID());
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        dataDelivery.setSeqID(seqId);
                
        
        Sha256Hash hash = new Sha256Hash(DatatypeConverter.parseHexBinary(agentMessage.getMessageId()));
        dataDelivery.setMessageID(hash);        
        PodeDataRecord record = new PodeDataRecord();
        record.setSource(source);
        PodeDataRecord.PodeDataChoiceType dataChoice = new PodeDataRecord.PodeDataChoiceType();
        
        
        PodeWeatherinfo weatherInfo = new PodeWeatherinfo();
        weatherInfo.setCategory(null);
        weatherInfo.setContribID(element.getContribId().getBytes());
        weatherInfo.setContributor(element.getContributor());
        weatherInfo.setObsTypeID(element.getObsTypeId().getBytes());
        weatherInfo.setPlatformCode(element.getStationCode());
        weatherInfo.setPlatformID(element.getStationId().getBytes());
        weatherInfo.setSensorID(element.getSensorId().getBytes());
        weatherInfo.setSensorIndex(Integer.parseInt(element.getSensorIndex()));
        weatherInfo.setSiteID(element.getSiteId().getBytes());
        weatherInfo.setSourceID(element.getSourceId().getBytes());
        Position3D position = new Position3D();
        
        position.setLat(new Latitude(getLatitudeValue(element.getLatitude())));
        position.setLon(new Longitude(getLongitudeValue(element.getLongitude())));
        weatherInfo.setPosition(position);
        
        
        
        PodeWeatherData weatherData = createWeatherData(element);
                
        weatherInfo.setWeatherSenorData(weatherData);
        
        
        dataChoice.selectWeather(weatherInfo);
        record.setPodeData(dataChoice);
        
        dataDelivery.setPodeData(record);
        
        return dataDelivery;
    }    
    
    
    private PodeWeatherData createWeatherData(WXDEData.WXDEDataElement element){
        PodeWeatherData weatherData = new PodeWeatherData();
        String type = element.getObsTypeName();
        if(TEMP_READING.equalsIgnoreCase(type)){
            double value = element.getObservation();
            value += 40;
            weatherData.setSurfaceTemperature(new AmbientAirTemperature((int)value));
        }else if(VIS_READING.equalsIgnoreCase(type)){
            int value = (int)element.getObservation();
            String valueString = Integer.toString(value);
            weatherData.setVisibility(new Distance(valueString.getBytes()));
        }else if(PRECIP_READING.equalsIgnoreCase(type)){
            weatherData.setPrecipRate(new PrecipRate((int)element.getObservation()));
        }
        
        return weatherData;
    }
    
    private int getLatitudeValue(double lat){
        int base = 10000000;
        
        return (int)Math.round(base*lat);
    }
    
    private int getLongitudeValue(double lon){
        int base = 10000000;
        return (int)Math.round(base*lon);
    }
}

