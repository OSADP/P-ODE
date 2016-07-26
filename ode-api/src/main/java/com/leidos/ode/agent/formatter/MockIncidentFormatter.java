package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ODEMessageType;

import javax.xml.bind.DatatypeConverter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rushk1 on 5/19/2016.
 */
public class MockIncidentFormatter extends ODEMessageFormatter {
    @Override
    public Map<ODEMessageType, List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequest) {
        Map<ODEMessageType, List<PodeDataDistribution>> out = new HashMap<>();
        List<PodeDataDistribution> records = new ArrayList<>();
        out.put(ODEMessageType.INCIDENT, records);

        PodeDataDistribution rec = new PodeDataDistribution();

        PodeDialogID dialog = new PodeDialogID();
        dialog.setValue(PodeDialogID.EnumType.podeDataDistribution);
        rec.setDialogID(dialog);

        rec.setGroupID(serviceRequest.getGroupID());
        rec.setRequestID(serviceRequest.getRequestID());
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        rec.setSeqID(seqId);

        Sha256Hash hash = new Sha256Hash(DatatypeConverter.parseHexBinary(agentMessage.getMessageId()));

        rec.setMessageID(hash);


        PodeDetectionMethod method = new PodeDetectionMethod();
        method.setValue(PodeDetectionMethod.EnumType.unknown);

        PodeIncidentData data = new PodeIncidentData();
        IncidentEvent event = new IncidentEvent();
        event.setDelayMiles(1);
        DelayType delay = new DelayType();
        delay.setValue(DelayType.EnumType.minor);
        event.setDelayType(delay);
        IncidentType type = new IncidentType();
        type.setValue(IncidentType.EnumType.congestion_Delay);
        event.setIncidentType(type);
        data.setEvent(event);

        data.setDescription("TEST EVENT, PLEASE IGNORE.");
        data.setEventID("TEST");
        data.setIssueAgency("STOL TEST FACILITY");
        IncidentLocation loc = new IncidentLocation();
        Position3D pos = new Position3D();
        Latitude lat = new Latitude(389554674);
        Longitude lon = new Longitude(-771542682);
        Elevation elev = new Elevation();
        elev.setValue(new byte[]{0x00, 0x00});
        pos.setElevation(elev);
        pos.setLat(lat);
        pos.setLon(lon);

        loc.setGeoLocation(pos);
        Area area = new Area();
        area.setJurisdiction("VDOT");
        area.setRegion("NOVA");
        area.setState("VA");
        loc.setArea(area);

        PodeLaneDirection direction = new PodeLaneDirection();
        direction.setValue(PodeLaneDirection.EnumType.west);

        loc.setDirection(direction);

        loc.setNoOfLanesAffected(2);

        RoadType roadType = new RoadType();
        roadType.setValue(RoadType.EnumType.other);
        loc.setRoadType(roadType);

        loc.setLaneCount(4);

        List<IncidentLane> lanes = new ArrayList<IncidentLane>();
        IncidentLane lane = new IncidentLane();
        PodeLaneType laneType = new PodeLaneType();
        laneType.setValue(PodeLaneType.EnumType.normal);
        LaneStatus status = new LaneStatus();
        status.setValue(LaneStatus.EnumType.open);
        lane.setDirection(direction);
        lane.setStatus(status);
        lane.setDirection(direction);
        lane.setLaneType(laneType);

        IncidentLane lane1 = new IncidentLane();
        lane1.setDirection(direction);
        lane1.setStatus(status);
        lane1.setDirection(direction);
        lane1.setLaneType(laneType);

        lanes.add(lane);
        lanes.add(lane1);


        loc.setLanesAffected(lanes);
        data.setLocation(loc);

        PodeDataRecord podeRecord = new PodeDataRecord();
        PodeDataRecord.PodeDataChoiceType dataType = new PodeDataRecord.PodeDataChoiceType();
        dataType.selectIncident(data);
        podeRecord.setPodeData(dataType);

        DDateTime dateTime = new DDateTime();
        LocalDateTime now = LocalDateTime.now();
        dateTime.setYear(new DYear(now.getYear()));
        dateTime.setMonth(new DMonth(now.getMonthValue()));
        dateTime.setDay(new DDay(now.getDayOfMonth()));
        dateTime.setHour(new DHour(now.getHour()));
        dateTime.setMinute(new DMinute(now.getMinute()));
        dateTime.setSecond(new DSecond(now.getSecond()));

        podeRecord.setLastupdatetime(dateTime);

        podeRecord.setRoutename("GW Parkway");

        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.dms);
        podeRecord.setSource(source);
        podeRecord.setMeasduration(1);

        rec.setPodeData(podeRecord);


        records.add(rec);

        return out;
    }
}
