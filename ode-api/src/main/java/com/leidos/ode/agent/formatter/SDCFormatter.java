package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.sdc.VehicleSituationDataMessage;
import com.leidos.ode.agent.data.sdc.VehicleSituationRecord;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rushk1 on 2/23/2016.
 */
public class SDCFormatter extends ODEMessageFormatter {

    private Logger log = LogManager.getLogger(SDCFormatter.class);
    private static final String SDC_ROUTE_NAME = "I-66";


    @Override
    public Map<ODEMessageType, List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        Map<ODEMessageType, List<PodeDataDistribution>> out = new HashMap<>();

        Sha256Hash hash = new Sha256Hash(DatatypeConverter.parseHexBinary(agentMessage.getMessageId()));

        VehicleSituationDataMessage vsdm = (VehicleSituationDataMessage) agentMessage.getFormattedMessage();
        List<PodeDataDistribution> data = new ArrayList<>();

        for (VehicleSituationRecord record : vsdm.getBundle()) {
            data.add(createMessage(record, serviceRequst, hash));
        }

        out.put(ODEMessageType.SPEED, data);
        return out;
    }


    private PodeDataDistribution createMessage(VehicleSituationRecord record, ServiceRequest serviceRequest, Sha256Hash hash){
        PodeDataDistribution message = new PodeDataDistribution();

        PodeDialogID dialog = new PodeDialogID();
        dialog.setValue(PodeDialogID.EnumType.podeDataDistribution);
        message.setDialogID(dialog);

        message.setGroupID(serviceRequest.getGroupID());
        message.setRequestID(serviceRequest.getRequestID());
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        message.setSeqID(seqId);

        message.setMessageID(hash);

        PodeDataRecord.PodeDataChoiceType data = new PodeDataRecord.PodeDataChoiceType();
        PodeDetectorData detector = new PodeDetectorData();

        PodeDetectionMethod method = new PodeDetectionMethod();
        method.setValue(PodeDetectionMethod.EnumType.vehicleProbe);
        detector.setDetectMethod(method);
        detector.setDetectorID(DatatypeConverter.printHexBinary(record.getTempID()));
        detector.setPosition(record.getPos());

        PodeLaneData laneData = new PodeLaneData();
        PodeDataElementList laneDataList = new PodeDataElementList();
        Speed speed = new Speed((int) Math.round(record.getFundamental().getSpeed()));
        laneDataList.setSpeed(speed);
        log.debug("!!! VSD SPEED: " + speed.getValue() + " !!!");

        laneData.setData(laneDataList);

        PodeLaneInfo laneInfo = new PodeLaneInfo();
        PodeLaneDirection direction = new PodeLaneDirection();
        //For Prototype only concerned with East or West.
        if(record.getFundamental().getHeading() >=0 && record.getFundamental().getHeading() < 180){
            direction.setValue(PodeLaneDirection.EnumType.east);
        }else{
            direction.setValue(PodeLaneDirection.EnumType.west);
        }
        //hardcoded for Prototype.
        laneInfo.setZoneNum(1);

        laneInfo.setLaneDirection(direction);
        laneData.setLane(laneInfo);
        List<PodeLaneData> dataList = new ArrayList<>();
        dataList.add(laneData);
        detector.setLaneData(dataList);

        data.selectDetector(detector);


        PodeDataRecord podeRecord = new PodeDataRecord();
        podeRecord.setPodeData(data);

        podeRecord.setLastupdatetime(record.getTime());

        podeRecord.setRoutename(SDC_ROUTE_NAME);

        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.dms);
        podeRecord.setSource(source);

        message.setPodeData(podeRecord);

        return message;

    }
}
