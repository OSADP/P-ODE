package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.data.sdc.FundamentalSituationalStatus;
import com.leidos.ode.agent.data.sdc.VehicleSituationDataMessage;
import com.leidos.ode.agent.data.sdc.VehicleSituationRecord;
import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.data.*;
import gov.dot.fhwa.saxton.utils.data.BitStreamUnpacker;
import gov.dot.fhwa.saxton.utils.data.UnpackUtils;
import gov.dot.fhwa.saxton.utils.data.asn1.Asn1Object;
import gov.dot.fhwa.saxton.utils.data.asn1.Asn1ParseException;
import gov.dot.fhwa.saxton.utils.data.asn1.Asn1Unpacker;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parser for Situation Data Clearinghouse messages delivering Vehicle Situation Data message sets
 */
public class SDCParser extends ODEDataParser {
    private final String TAG = getClass().getSimpleName();
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TAG);
    private static final String START_INDICATOR = "8002";
    private static final int GMT_OFFSET_HRS = 4;

    private static double DE_SPEED_SCALING_CONSTANT = 0.02;
    private static int DE_SPEED_UNAVAILABLE_FLAG = 8191;
    private static double DE_HEADING_SCALING_CONSTANT = 0.0125;

    private static final int DIALOG_ID_ID = 0x00;
    private static final int SEQ_ID_ID = 0x01;
    private static final int GROUP_ID_ID = 0x02;
    private static final int REQUEST_ID_ID = 0x03;
    private static final int VSM_TYPE_ID = 0x04;
    private static final int BUNDLE_ID = 0x05;
    private static final int CRC_ID = 0x06;

    @Override
    protected ODEDataParserResponse parse(byte[] bytes) {
        try {
            // Skip forward to the start of the VSDM in the message
            String hexString = new String(bytes);
            int startIdx = hexString.indexOf(START_INDICATOR);
            byte[] vsdmBytes = DatatypeConverter.parseHexBinary(new String(bytes));

            // Process the VSDM itself
            VehicleSituationDataMessage vsdm = parseVSDM(vsdmBytes);

            // Validate it and set our output code
            ODEDataParserReportCode resultCode = validateVSDM(vsdm);
            if (validateVSDM(vsdm).equals(ODEDataParserReportCode.PARSE_SUCCESS)) {
                return new ODEDataParserResponse(vsdm, ODEDataParserReportCode.PARSE_SUCCESS);
            } else {
                return new ODEDataParserResponse(null, resultCode);
            }
        } catch (Asn1ParseException as1pe) {
            return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
        } catch (ODEParseException e) {
            return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
        }
    }

    private VehicleSituationDataMessage parseVSDM(byte[] message) throws Asn1ParseException, ODEParseException {
        VehicleSituationDataMessage out = new VehicleSituationDataMessage();
        Asn1Unpacker unpacker = new Asn1Unpacker(message);
        Asn1Object vsdm = unpacker.unpack();

        out.setDialogID(vsdm.findObjectById(DIALOG_ID_ID).getValue().apply(
                UnpackUtils::unpackU16BigEndian,
                right -> -1));

        out.setSeqID(vsdm.findObjectById(SEQ_ID_ID).getValue().apply(
                left -> UnpackUtils.unpack8(left[0]),
                right -> -1));

        out.setGroupID(vsdm.findObjectById(GROUP_ID_ID).getValue().apply(
                UnpackUtils::unpack32,
                right -> -1));

        out.setRequestID(vsdm.findObjectById(REQUEST_ID_ID).getValue().apply(
                UnpackUtils::unpack32,
                right -> -1));

        out.setType(vsdm.findObjectById(VSM_TYPE_ID).getValue().apply(
                left -> left[0],
                right -> (byte) -1));

        Asn1Object bundle = vsdm.findObjectById(BUNDLE_ID);
        if (bundle != null) {
           List<Asn1Object> parsed = bundle.getValue().apply(
                    p -> null,
                    c -> c
            );

            if (parsed == null) {
                // This should literally never happen.
                throw new Asn1ParseException(Asn1ParseException.Asn1ParseError.MALFORMED_TAG);
            } else {
                out.setBundle(parseBundle(parsed));
            }

        } else {
            throw new ODEParseException("VSDM has no bundle to parse, message might be malformed...");
        }

        out.setCrc(vsdm.findObjectById(CRC_ID).getValue().apply(
                left -> UnpackUtils.unpackU32(left),
                right -> -1L));

        return out;
    }

    private List<VehicleSituationRecord> parseBundle(List<Asn1Object> bundle) {
        List<VehicleSituationRecord> out = new ArrayList<>();
        for (Asn1Object record : bundle) {
            VehicleSituationRecord tmp = record.getValue().apply(
                    left -> {
                        return null; // Shouldn't happen ever, record is composite.
                    },
                    right -> parseRecord(record)
            );
            out.add(tmp);
        }

        return out;
    }

    private VehicleSituationRecord parseRecord(Asn1Object record) {
        return record.getValue().apply(
                left -> {
                    return null; // Shouldn't happen ever, record is composite.
                },
                right -> {
                    VehicleSituationRecord out = new VehicleSituationRecord();

                    Asn1Object tempId = right.get(0);
                    byte[] rawTempId = getNullablePrimitive(tempId);
                    if (rawTempId != null) {
                        out.setTempID(rawTempId);
                    }

                    // Destructure DSRC.DDateTime
                    Asn1Object time = right.get(1);
                    byte[] rawDYear = getNullablePrimitive(getNullableChild(time, 0));
                    byte[] rawDMonth = getNullablePrimitive(getNullableChild(time, 1));
                    byte[] rawDDay = getNullablePrimitive(getNullableChild(time, 2));
                    byte[] rawDHour = getNullablePrimitive(getNullableChild(time, 3));
                    byte[] rawDMinute = getNullablePrimitive(getNullableChild(time, 4));
                    byte[] rawDSecond = getNullablePrimitive(getNullableChild(time, 5));

                    // Copy into DDateTimeInstance
                    DDateTime dDateTime = new DDateTime();
                    if (rawDYear != null) {
                        short year = ByteBuffer.wrap(rawDYear).order(ByteOrder.BIG_ENDIAN).getShort();
                        dDateTime.setYear(new DYear((int) year));
                    }
                    if (rawDMonth != null) {
                        dDateTime.setMonth(new DMonth((int) rawDMonth[0]));
                    }
                    int dayOffset = 0;
                    if (rawDHour != null) {
                        if (rawDHour.length == 2) {
                            int hour = UnpackUtils.unpack16BigEndian(rawDHour);

                            // Account for GMT/Eastern Time offset
                            hour -= GMT_OFFSET_HRS;
                            if (hour < 0) {
                                hour += 24;
                                dayOffset = -1;
                            }
                            if (hour >= 24) {
                                hour -= 24;
                                dayOffset = 1;
                            }

                            dDateTime.setHour(new DHour(hour));
                        } else {
                            dDateTime.setHour(new DHour((int) rawDHour[0]));
                        }
                    }
                    if (rawDDay != null) {
                        dDateTime.setDay(new DDay(((int) rawDDay[0]) + dayOffset));
                    }

                    if (rawDMinute != null) {
                        dDateTime.setMinute(new DMinute((int) rawDMinute[0]));
                    }
                    if (rawDSecond != null) {

                        // NOTE: Converted to seconds to preserve consistency with other uses of the DSecond object
                        // Should be in ms, but everywhere else uses it as seconds

                        if (rawDSecond.length == 3) {
                            dDateTime.setSecond(new DSecond(UnpackUtils.unpackU16BigEndian(Arrays.copyOfRange(rawDSecond, 1, 3))/1000));
                        } else if (rawDSecond.length == 2) {
                            dDateTime.setSecond(new DSecond(UnpackUtils.unpackU16BigEndian(rawDSecond)/1000));
                        } else {
                            dDateTime.setSecond(new DSecond((int) rawDSecond[0]/ 1000));
                        }
                    }
                    out.setTime(dDateTime);

                    // Destructure DSRC.Position3D
                    Asn1Object pos = right.get(2);
                    byte[] rawLat = getNullablePrimitive(getNullableChild(pos, 0));
                    byte[] rawLon = getNullablePrimitive(getNullableChild(pos, 1));
                    byte[] rawElev = getNullablePrimitive(getNullableChild(pos, 2));

                    // Copy into Position3D instance
                    Position3D pos3d = new Position3D();
                    if (rawLat != null) {
                        pos3d.setLat(new Latitude(UnpackUtils.unpack32BigEndian(rawLat)));
                    }
                    if (rawLon != null) {
                        pos3d.setLon(new Longitude(UnpackUtils.unpack32BigEndian(rawLon)));
                    }
                    if (rawElev != null) {
                        pos3d.setElevation(new Elevation(rawElev));
                    }
                    out.setPos(pos3d);


                    Asn1Object fundamental = right.get(3);
                    byte[] rawSpeed = getNullablePrimitive(getNullableChild(fundamental, 0));
                    byte[] rawHeading = getNullablePrimitive(getNullableChild(fundamental, 1));

                    FundamentalSituationalStatus fund = new FundamentalSituationalStatus();
                    if (rawSpeed != null) {
                        BitStreamUnpacker bsu = new BitStreamUnpacker(rawSpeed);
                        int transmission = bsu.readBits(3);

                        int speed = bsu.readBits(13);
                        if (speed != DE_SPEED_UNAVAILABLE_FLAG) {
                            fund.setSpeed(speed * DE_SPEED_SCALING_CONSTANT);
                        }
                    }
                    if (rawHeading != null) {
                        fund.setHeading(UnpackUtils.unpackU16(rawHeading) * DE_HEADING_SCALING_CONSTANT);
                    }



                    out.setFundamental(fund);

                    return out;
                }
        );
    }


    private ODEDataParserReportCode validateVSDM(VehicleSituationDataMessage vsdm) {
        if (vsdm.getDialogID() == -1
                || vsdm.getSeqID() == -1
                || vsdm.getGroupID() == -1
                || vsdm.getRequestID() == -1
                || vsdm.getType() == -1
                || vsdm.getBundle() == null
                || vsdm.getBundle().size() == 0) {
            return ODEDataParserReportCode.UNEXPECTED_DATA_FORMAT;
        } else {
            return ODEDataParserReportCode.PARSE_SUCCESS;
        }
    }

    public String convertBytesToHex(byte[] bytes) {
        String hexString = DatatypeConverter.printHexBinary(bytes);
        return hexString;
    }


    private Asn1Object getNullableChild(Asn1Object obj, int index) {
        if (obj == null) {
            return null; // Propagate the null onwards
        }

        return obj.getValue().apply(
                p -> null,
                c -> {
                    if (c.size() >= index) {
                        return c.get(index);
                    } else {
                        return null;
                    }
                }

        );
    }

    private byte[] getNullablePrimitive(Asn1Object obj) {
        if (obj != null) {
            return obj.getValue().apply(
                    p -> p,
                    c -> null
            );
        } else {
            return null;
        }
    }
}
