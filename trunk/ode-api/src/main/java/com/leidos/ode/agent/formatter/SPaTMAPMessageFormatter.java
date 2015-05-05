package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ODEMessageType;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rushk1 on 3/27/2015.
 */
public class SPaTMAPMessageFormatter extends ODEMessageFormatter {
    @Override
    public Map<ODEMessageType, List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        PodeSpatMap spatMap = (PodeSpatMap) agentMessage.getFormattedMessage();
        PodeDataDistribution message = new PodeDataDistribution();

        PodeDialogID dialog = new PodeDialogID();
        dialog.setValue(PodeDialogID.EnumType.podeDataDistribution);
        message.setDialogID(dialog);

        message.setGroupID(serviceRequst.getGroupID());
        message.setRequestID(serviceRequst.getRequestID());
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        message.setSeqID(seqId);

        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.spat);
        Sha256Hash hash = new Sha256Hash(DatatypeConverter.parseHexBinary(agentMessage.getMessageId()));
        message.setMessageID(hash);

        PodeDetectorData detector = new PodeDetectorData();

        PodeDetectionMethod method = new PodeDetectionMethod();
        method.setValue(PodeDetectionMethod.EnumType.unknown);
        detector.setDetectMethod(method);

        PodeDataRecord.PodeDataChoiceType data = new PodeDataRecord.PodeDataChoiceType();
        data.selectSpatMap(spatMap);

        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);

        message.setPodeData(record);

        Map<ODEMessageType, List<PodeDataDistribution>> out = new HashMap<ODEMessageType, List<PodeDataDistribution>>();
        List<PodeDataDistribution> msgList = new ArrayList<PodeDataDistribution>();
        out.put(ODEMessageType.SPATMAP, msgList);

        return out;
    }
}
