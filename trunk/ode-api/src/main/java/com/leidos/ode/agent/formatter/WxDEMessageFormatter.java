/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.data.wxde.WXDEData;
import com.leidos.ode.data.AmbientAirTemperature;
import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.DDay;
import com.leidos.ode.data.DHour;
import com.leidos.ode.data.DMinute;
import com.leidos.ode.data.DMonth;
import com.leidos.ode.data.DSecond;
import com.leidos.ode.data.DYear;
import com.leidos.ode.data.Distance;
import com.leidos.ode.data.Latitude;
import com.leidos.ode.data.Longitude;
import com.leidos.ode.data.PodeCategory;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author cassadyja
 */
public class WxDEMessageFormatter extends ODEMessageFormatter{

    private static final String TEMP_READING = "essSurfaceTemperature";
    private static final String PRECIP_READING = "essPrecipRate";
    private static final String VIS_READING = "essVisibility";
    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    
    
    @Override
    public Map<ODEMessageType,List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        Map<ODEMessageType,List<PodeDataDistribution>> messages = new HashMap<ODEMessageType,List<PodeDataDistribution>>();
        WXDEData wxdeData = (WXDEData)agentMessage.getFormattedMessage();
        if(wxdeData != null){
            PodeSource source = new PodeSource();
            source.setValue(PodeSource.EnumType.wxde);
            List<PodeDataDistribution> messageList = new ArrayList<PodeDataDistribution>();
            for(WXDEData.WXDEDataElement element:wxdeData.getWxdeDataElements()){
                PodeDataDistribution dataDelivery = createMessage(agentMessage, element, source, serviceRequst);
                if(dataDelivery != null){
                    messageList.add(dataDelivery);
                }
            }
            messages.put(ODEMessageType.WEATHER, messageList);
        }
        
        
        return messages;
    }    

    private PodeDataDistribution createMessage(ODEAgentMessage agentMessage, WXDEData.WXDEDataElement element, PodeSource source, ServiceRequest serviceRequst) {
        PodeWeatherData weatherData = createWeatherData(element);        
        if(weatherData != null){
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

            record.setLastupdatetime(getDateTimeForElement());


            record.setSource(source);
            PodeDataRecord.PodeDataChoiceType dataChoice = new PodeDataRecord.PodeDataChoiceType();


            PodeWeatherinfo weatherInfo = new PodeWeatherinfo();
            PodeCategory category = new PodeCategory();
            category.setValue(PodeCategory.EnumType.t);
            weatherInfo.setCategory(category);
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





            weatherInfo.setWeatherSenorData(weatherData);


            dataChoice.selectWeather(weatherInfo);
            record.setPodeData(dataChoice);

            dataDelivery.setPodeData(record);

            return dataDelivery;
        }else{
            return null;
        }
    }    
    
    
    private PodeWeatherData createWeatherData(WXDEData.WXDEDataElement element){
        PodeWeatherData weatherData = new PodeWeatherData();
        String type = element.getObsTypeName();
        if(TEMP_READING.equalsIgnoreCase(type)){
            logger.debug("Formatting temp message");
            double value = element.getObservation();
            value += 40;
            weatherData.setSurfaceTemperature(new AmbientAirTemperature((int)value));
        }else if(VIS_READING.equalsIgnoreCase(type)){
            logger.debug("Formatting Visibility message");
            Integer i = new Integer((int)element.getObservation());
            weatherData.setVisibility(new Distance(i.toString().getBytes()));
        }else if(PRECIP_READING.equalsIgnoreCase(type)){
            logger.debug("Formatting Precip message");
            weatherData.setPrecipRate(new PrecipRate((int)element.getObservation()));
        }else{
            logger.debug("Ignoring "+type);
            return null;
        }
        
        return weatherData;
    }
    
    private DDateTime getDateTimeForElement(){
        Calendar cal = Calendar.getInstance();
        
        DDateTime dateTime = new DDateTime();
        DHour hour = new DHour(cal.get(Calendar.HOUR_OF_DAY));
        dateTime.setHour(hour);
        DMinute min = new DMinute(cal.get(Calendar.MINUTE));
        dateTime.setMinute(min);
        DSecond sec = new DSecond(cal.get(Calendar.SECOND));
        dateTime.setSecond(sec);
        DMonth month = new DMonth(cal.get(Calendar.MONTH)+1);
        dateTime.setMonth(month);
        DDay day = new DDay(cal.get(Calendar.DAY_OF_MONTH));
        dateTime.setDay(day);
        DYear year = new DYear(cal.get(Calendar.YEAR));
        dateTime.setYear(year);
        return dateTime;
    }    
    
}

