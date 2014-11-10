package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.data.bsm.BSM;
import com.leidos.ode.agent.data.bsm.SafetyExtData;
import com.leidos.ode.agent.data.bsm.VehStatusData;
import com.leidos.ode.agent.parser.ODEDataParser;
import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.sql.Timestamp;

public class BSMParser extends ODEDataParser {

    private static final String BSM_START_CHARS = "80010281";
    private static final int MESSAGE_COUNT_START = 6;
    private static final int MESSAGE_COUNT_END = 8;
    private static final int ID_START = 8;
    private static final int ID_END = 16;
    private static final int SECMARK_START = 16;
    private static final int SECMARK_END = 20;
    private static final int LAT_START = 20;
    private static final int LAT_END = 28;
    private static final int LONG_START = 28;
    private static final int LONG_END = 36;
    private static final int ELEVATION_START = 36;
    private static final int ELEVATION_END = 40;
    private static final int ACCURACY_START = 40;
    private static final int ACCURACY_END = 48;
    private static final int SPEED_TRANS_START = 48;
    private static final int SPEED_TRANS_END = 52;
    private static final int HEADING_START = 52;
    private static final int HEADING_END = 56;
    private static final int ANGLE_START = 56;
    private static final int ANGLE_END = 58;
    private static final int ACCEL_LONG_START = 58;
    private static final int ACCEL_LONG_END = 62;
    private static final int ACCEL_LAT_START = 62;
    private static final int ACCEL_LAT_END = 66;
    private static final int ACCEL_VERT_START = 66;
    private static final int ACCEL_VERT_END = 68;
    private static final int ACCEL_YAW_START = 68;
    private static final int ACCEL_YAW_END = 72;
    private static final int BRAKES_START = 72;
    private static final int BRAKES_END = 76;
    private static final int WIDTH_LENGTH_START = 76;
    private static final int WIDTH_LENGTH_END = 82;
    private static final int SAFETY_EXT_START = 82;
    private static final int SAFETY_EXT_END = 84;
    private static Logger logger = Logger.getLogger(BSMParser.class);

    @Override
    public ODEDataParserResponse parse(byte[] bytes) {
        BSM bsm = decodeBSM(bytes);
        if (bsm != null) {
            return new ODEDataParserResponse(bsm, ODEDataParserReportCode.PARSE_SUCCESS);
        } else {
            return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
        }
    }

    private BSM decodeBSM(byte[] inputBytes) {
        BSM bsm = null;
        String hex = convertBytesToHex(inputBytes);
        log("Processing BSM: " + hex);

        int bsmStart = hex.indexOf("80010281");
        if (bsmStart > -1) {
            bsm = new BSM();
            bsmStart = bsmStart + 4;
            bsm.setDateReceived(new Timestamp(System.currentTimeMillis()));

            bsm.setBsmMessageId(hex.substring(bsmStart, bsmStart + 2));

            int blobLen = Integer.parseInt(hex.substring(bsmStart + 4, bsmStart + 6), 16);
            bsm.setMessageCount(Integer.parseInt(hex.substring(bsmStart + MESSAGE_COUNT_START, bsmStart + MESSAGE_COUNT_END), 16));

            bsm.setId(hex.substring(bsmStart + ID_START, bsmStart + ID_END));

            bsm.setSecMark(Integer.parseInt(hex.substring(bsmStart + SECMARK_START, bsmStart + SECMARK_END), 16));

            int lat = new BigInteger(hex.substring(bsmStart + LAT_START, bsmStart + LAT_END), 16).intValue();
            bsm.setLatitude((lat / 10000000.0));

            int longitude = new BigInteger(hex.substring(bsmStart + LONG_START, bsmStart + LONG_END), 16).intValue();
            bsm.setLongitude((longitude / 10000000.0));

            int ele = new BigInteger(hex.substring(bsmStart + ELEVATION_START, bsmStart + ELEVATION_END), 16).intValue();
            bsm.setElevation((ele / 10.0));

            bsm.setAccuracy(hex.substring(bsmStart + ACCURACY_START, bsmStart + ACCURACY_END));

            String speedTrans = hex.substring(bsmStart + SPEED_TRANS_START, bsmStart + SPEED_TRANS_END);

            int st = new BigInteger(speedTrans, 16).intValue();
            String stBin = Integer.toBinaryString(st);
            while (stBin.length() < 16) {
                stBin += "0";
            }
            String transBin = stBin.substring(0, 3);
            int trans = Integer.parseInt(transBin, 2);
            bsm.setTrans(trans);

            String speedBin = stBin.substring(3, 16);

            int speed = Integer.parseInt(speedBin, 2);
            bsm.setSpeed(speed * .02);

            String heading = hex.substring(bsmStart + HEADING_START, bsmStart + HEADING_END);
            int headingInt = new BigInteger(heading, 16).intValue();
            bsm.setHeading(headingInt * .0125);

            int angle = new BigInteger(hex.substring(bsmStart + ANGLE_START, bsmStart + ANGLE_END), 16).intValue();
            if (angle == 127) {
            } else {
                bsm.setAngle(angle);
            }

            String accelLong = hex.substring(bsmStart + ACCEL_LONG_START, bsmStart + ACCEL_LONG_END);
            int accelLongInt = new BigInteger(accelLong, 16).intValue();
            if (accelLongInt == 2001) {
            } else {
                bsm.setAccelLong(Integer.valueOf(accelLong, 16).shortValue() * .01);
            }

            String accelLat = hex.substring(bsmStart + ACCEL_LAT_START, bsmStart + ACCEL_LAT_END);
            int accelLatInt = new BigInteger(accelLat, 16).intValue();
            if (accelLatInt == 2001) {
            } else {
                bsm.setAccelLat(Integer.valueOf(accelLat, 16).shortValue() * .01);
            }

            String accelVert = hex.substring(bsmStart + ACCEL_VERT_START, bsmStart + ACCEL_VERT_END);
            int accelVertInt = Integer.parseInt(accelVert, 16);
            double accelVertCon = (50 - accelVertInt) * .02;
            if (accelVertInt < 50) {
                accelVertCon = accelVertCon * -1;
            }
            bsm.setAccelVert(accelVertCon);

            String accelYaw = hex.substring(bsmStart + ACCEL_YAW_START, bsmStart + ACCEL_YAW_END);
            int accelYawInt = Integer.parseInt(accelYaw, 16);
            bsm.setAccelYaw(accelYawInt * .01);

            String brakes = hex.substring(bsmStart + BRAKES_START, bsmStart + BRAKES_END);
            bsm.setBrakes(brakes);

            String widthLength = hex.substring(bsmStart + WIDTH_LENGTH_START, bsmStart + WIDTH_LENGTH_END);

            int wl = new BigInteger(widthLength, 16).intValue();
            String wlBin = Integer.toBinaryString(wl);
            while (wlBin.length() < 24) {
                wlBin = "0" + wlBin;
            }

            String widthBin = wlBin.substring(0, 10);
            int width = Integer.parseInt(widthBin, 2);
            bsm.setWidth(width);

            String lenBin = wlBin.substring(10, 24);
            int length = Integer.parseInt(lenBin, 2);
            bsm.setLength(length);

            if (hex.length() > bsmStart + SAFETY_EXT_END) {
//				SafetyExtData extData = new SafetyExtData();

                String nextContext = hex.substring(bsmStart + SAFETY_EXT_START, bsmStart + SAFETY_EXT_END);
                int nextContextStart = bsmStart + SAFETY_EXT_START;
                if (nextContext.equalsIgnoreCase("A2")) {
                    nextContextStart = handleExtData(hex, nextContextStart + 2, bsm);
                }

                if (hex.length() > nextContextStart + 2) {
                    nextContext = hex.substring(nextContextStart, nextContextStart + 2);

                    if (nextContext.equalsIgnoreCase("A3")) {
                        nextContextStart = handleStatusData(hex, nextContextStart, bsm);
                    }
                }
            }
            log("-----------------------------------------------------------------");

        }

        return bsm;

    }

    private int handleExtData(String hex, int start, BSM bsm) {
        SafetyExtData extData = new SafetyExtData();
        String safetyExtLength = null;
        int safetyExtStart = start + 2;
        //Not sure if this section is right.  If there are flags, we need to
        //take the length of the flags into consideration.
        if (hex.substring(start, start + 2).equalsIgnoreCase("81")) {
//			String safetyExtTag = hex.substring(start, start+2);
            safetyExtLength = hex.substring(start + 2, start + 4);
            safetyExtStart = start + 4;
        } else {
            safetyExtLength = hex.substring(start, start + 2);
            safetyExtStart = start + 2;

        }
        int safetyExtLengthInt = new BigInteger(safetyExtLength, 16).intValue();
        int nextContextStart = safetyExtStart;
        int prevNextContextStart = nextContextStart;
        String nextContext = "";
        if (safetyExtLengthInt > 0) {
            while (nextContextStart < safetyExtStart + (2 * safetyExtLengthInt)) {

                nextContext = hex.substring(nextContextStart, nextContextStart + 2);
                if (nextContext.equalsIgnoreCase("80")) {
                    nextContextStart += 2;
                    String len = hex.substring(nextContextStart, nextContextStart += 2);
                    int lenInt = Integer.parseInt(len, 16);
                    nextContextStart += (2 * lenInt);
                }

                if (nextContext.equalsIgnoreCase("A1")) {
                    nextContextStart = handlePathHistory(hex, nextContextStart + 2, extData);
                }

                if (nextContext.equalsIgnoreCase("A2")) {
                    nextContextStart = handlePathPrediction(hex, nextContextStart, extData);
                }

                if (nextContext.equalsIgnoreCase("A3")) {
                    nextContextStart = handleRTCMPackage(hex, nextContextStart + 2, extData);
                }
                if (nextContextStart == prevNextContextStart) {
                    nextContextStart += 2;
                }

            }

        }

        bsm.setExtData(extData);

        return nextContextStart;

    }

    private int handleRTCMPackage(String hex, int start, SafetyExtData extData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        //TODO: Implement

        return start + (2 * lenInt);
    }

    private int handlePathPrediction(String hex, int start, SafetyExtData extData) {
        //Skip path predict main tag
        int a2NextTag = start + 2;
        String pathPredictLength = hex.substring(a2NextTag, a2NextTag += 2);
        int pathPredictLengthInt = Integer.parseInt(pathPredictLength, 16);
        if (pathPredictLengthInt > 0) {
            a2NextTag += 2;
            //get radius length
            String curveRadiusLength = hex.substring(a2NextTag, a2NextTag += 2);
            int curveRadiusLengthInt = Integer.parseInt(curveRadiusLength, 16);
            String curveRadius = hex.substring(a2NextTag, a2NextTag += (2 * curveRadiusLengthInt));
            int curveRadiusInt = Integer.parseInt(curveRadius, 16);
            extData.setCurveRadius(curveRadiusInt);

            //Skip path predict tag
            a2NextTag += 2;
            String confidenceLength = hex.substring(a2NextTag, a2NextTag += 2);
            int confidenceLengthInt = Integer.parseInt(confidenceLength, 16);
            String confidence = hex.substring(a2NextTag, a2NextTag += (2 * confidenceLengthInt));
            int confidenceInt = Integer.parseInt(confidence, 16);
            extData.setCurveConfidence(confidenceInt);
        }
        return a2NextTag;
    }

    private int handlePathHistory(String hex, int start, SafetyExtData extData) {
        int a1NextTag = start;
        String pathHistoryLength = null;
        int a1End = a1NextTag;
        int pathHistoryEnd = a1End;

        if (hex.substring(a1NextTag, a1NextTag + 2).equals("81")) {
            a1NextTag += 2;
            pathHistoryLength = hex.substring(a1NextTag, a1NextTag += 2);
            a1End = a1NextTag;
        } else {
            pathHistoryLength = hex.substring(a1NextTag, a1NextTag += 2);
            a1End = a1NextTag;
        }

        int pathHistoryLengthInt = new BigInteger(pathHistoryLength, 16).intValue();

        if (pathHistoryLengthInt > 0) {
            int nextContextStart = a1End;
            String nextContext = hex.substring(nextContextStart, nextContextStart + 2);

            if (nextContext.equalsIgnoreCase("A0")) {
                nextContextStart = handlePathHistFullPositionVector(hex, nextContextStart + 2, extData);
            }

            nextContext = hex.substring(nextContextStart, nextContextStart + 2);
            if (nextContext.equalsIgnoreCase("A1")) {
                //do GPSStatus
                //TODO: Implement

            }

            nextContext = hex.substring(nextContextStart, nextContextStart + 2);
            if (nextContext.equalsIgnoreCase("82")) {
                String itemCountTag = hex.substring(nextContextStart, nextContextStart + 2);
                String itemCountLength = hex.substring(nextContextStart + 2, nextContextStart + 4);
                int itemCountLengthInt = Integer.parseInt(itemCountLength, 16);
                int itemCountStart = nextContextStart + 4;
                String itemCount = hex.substring(itemCountStart, itemCountStart + (2 * itemCountLengthInt));
                itemCountStart = itemCountStart + (2 * itemCountLengthInt);

                int itemCountInt = new BigInteger(itemCount, 16).intValue();
                extData.setItemCount(itemCountInt);
                nextContextStart = itemCountStart;
            }

            nextContext = hex.substring(nextContextStart, nextContextStart + 2);
            if (nextContext.equalsIgnoreCase("A3")) {

                int a3NextTagStart = nextContextStart + 2;
                if (hex.substring(a3NextTagStart, a3NextTagStart + 2).equalsIgnoreCase("81")) {
                    a3NextTagStart += 6;
                } else {
                    a3NextTagStart += 2;
                }
                //skip crumbdata tag
                a3NextTagStart += 2;
                String crumbDataLength = hex.substring(a3NextTagStart, a3NextTagStart += 2);
                int crumbDataLengthInt = Integer.parseInt(crumbDataLength, 16);
                String pathHistory = hex.substring(a3NextTagStart, a3NextTagStart + (2 * crumbDataLengthInt));
                extData.setPathHistory(pathHistory);

                nextContextStart = a3NextTagStart + (2 * crumbDataLengthInt);
            }
            pathHistoryEnd = nextContextStart;
        }

        return pathHistoryEnd;
    }

    private int handlePathHistFullPositionVector(String hex, int start, SafetyExtData extData) {
        //Skip Inital Pos Len
        int nextContextStart = start + 2;
        String nextContext = hex.substring(nextContextStart, nextContextStart + 2);

        if (nextContext.equalsIgnoreCase("A0")) {
            nextContextStart += 2;
            String dateTimeLen = hex.substring(nextContextStart, nextContextStart += 2);
            int dateTimeLenInt = Integer.parseInt(dateTimeLen, 16);
            if (dateTimeLenInt > 0) {
                //Skip UTC time tag, utc len, year tag
                nextContextStart += 2;
                String yearLen = hex.substring(nextContextStart, nextContextStart += 2);
                int yearLenInt = Integer.parseInt(yearLen, 16);
                String year = hex.substring(nextContextStart, nextContextStart += (2 * yearLenInt));
                int yearInt = new BigInteger(year, 16).intValue();
                extData.setInitPosYear(yearInt);

                //Skip month tag
                nextContextStart += 2;
                String monthLen = hex.substring(nextContextStart, nextContextStart += 2);
                int monthLenInt = Integer.parseInt(monthLen, 16);
                String month = hex.substring(nextContextStart, nextContextStart += (2 * monthLenInt));
                int monthInt = new BigInteger(month, 16).intValue();
                extData.setInitPosMonth(monthInt);

                //Skip day tag
                nextContextStart += 2;
                String dayLen = hex.substring(nextContextStart, nextContextStart += 2);
                int dayLenInt = Integer.parseInt(dayLen, 16);
                String day = hex.substring(nextContextStart, nextContextStart += (2 * dayLenInt));
                int dayInt = new BigInteger(day, 16).intValue();
                extData.setInitPosDay(dayInt);

                //Skip hour tag
                nextContextStart += 2;
                String hourLen = hex.substring(nextContextStart, nextContextStart += 2);
                int hourLenInt = Integer.parseInt(hourLen, 16);
                String hour = hex.substring(nextContextStart, nextContextStart += (2 * hourLenInt));
                int hourInt = new BigInteger(hour, 16).intValue();
                extData.setInitPosHour(hourInt);

                //Skip minute tag
                nextContextStart += 2;
                String minuteLen = hex.substring(nextContextStart, nextContextStart += 2);
                int minuteLenInt = Integer.parseInt(minuteLen, 16);
                String minute = hex.substring(nextContextStart, nextContextStart += (2 * minuteLenInt));
                int minuteInt = new BigInteger(minute, 16).intValue();
                extData.setInitPosMinute(minuteInt);

                //Skip second tag
                String secondTag = hex.substring(nextContextStart, nextContextStart += 2);
                String secondLen = hex.substring(nextContextStart, nextContextStart += 2);
                int secondLenInt = Integer.parseInt(secondLen, 16);
                String second = hex.substring(nextContextStart, nextContextStart += (2 * secondLenInt));
                int secondInt = new BigInteger(second, 16).intValue();
                extData.setInitPosSecond(secondInt);
            }
        }

        nextContext = hex.substring(nextContextStart, nextContextStart + 2);
        if (nextContext.equalsIgnoreCase("81")) {
            //skip long tag
            nextContextStart += 2;
            String longLen = hex.substring(nextContextStart, nextContextStart += 2);
            int longLenInt = Integer.parseInt(longLen, 16);
            String lon = hex.substring(nextContextStart, nextContextStart += (2 * longLenInt));
            int lonInt = new BigInteger(lon, 16).intValue();
            extData.setInitPosLong((lonInt / 10000000.0));
        }

        nextContext = hex.substring(nextContextStart, nextContextStart + 2);
        if (nextContext.equalsIgnoreCase("82")) {
            //skip lat tag
            nextContextStart += 2;
            String latLen = hex.substring(nextContextStart, nextContextStart += 2);
            int latLenInt = Integer.parseInt(latLen, 16);
            String a0lat = hex.substring(nextContextStart, nextContextStart += (2 * latLenInt));
            int a0latInt = new BigInteger(a0lat, 16).intValue();
            extData.setInitPosLat((a0latInt / 10000000.0));
        }

        nextContext = hex.substring(nextContextStart, nextContextStart + 2);
        if (nextContext.equalsIgnoreCase("83")) {
            nextContextStart += 2;
            nextContextStart += 2;
            String elevation = hex.substring(nextContextStart, nextContextStart += 4);
            int ele = new BigInteger(elevation, 16).intValue();
            extData.setElevation((ele / 10.0));
        }

        nextContext = hex.substring(nextContextStart, nextContextStart + 2);
        if (nextContext.equalsIgnoreCase("84")) {
            nextContextStart += 2;
            nextContextStart += 2;
            String heading = hex.substring(nextContextStart, nextContextStart += 4);
            int headingInt = new BigInteger(heading, 16).intValue();
            extData.setHeading(headingInt * .0125);

        }

        nextContext = hex.substring(nextContextStart, nextContextStart + 2);
        if (nextContext.equalsIgnoreCase("85")) {
            nextContextStart += 2;
            nextContextStart += 2;
            String speedTrans = hex.substring(nextContextStart, nextContextStart += 4);

            int st = new BigInteger(speedTrans, 16).intValue();
            String stBin = Integer.toBinaryString(st);
            while (stBin.length() < 16) {
                stBin += "0";
            }
            String transBin = stBin.substring(0, 3);
            int trans = Integer.parseInt(transBin, 2);
            extData.setTrans(trans);

            String speedBin = stBin.substring(3, 16);
            int speed = Integer.parseInt(speedBin, 2);
            extData.setSpeed(speed * .02);

        }

        nextContext = hex.substring(nextContextStart, nextContextStart + 2);
        if (nextContext.equalsIgnoreCase("86")) {
            nextContextStart += 2;
            String accLen = hex.substring(nextContextStart, nextContextStart += 2);
            int accLenInt = Integer.parseInt(accLen, 16);
            String acc = hex.substring(nextContextStart, nextContextStart += (2 * accLenInt));
            extData.setPosAccuracy(acc);
        }

        nextContext = hex.substring(nextContextStart, nextContextStart + 2);
        if (nextContext.equalsIgnoreCase("87")) {
            nextContextStart += 2;
            String len = hex.substring(nextContextStart, nextContextStart += 2);
            int lenInt = Integer.parseInt(len, 16);
            String val = hex.substring(nextContextStart, nextContextStart += (2 * lenInt));
            extData.setTimeConfidence(val);
        }

        nextContext = hex.substring(nextContextStart, nextContextStart + 2);
        if (nextContext.equalsIgnoreCase("88")) {
            nextContextStart += 2;
            String len = hex.substring(nextContextStart, nextContextStart += 2);
            int lenInt = Integer.parseInt(len, 16);
            String val = hex.substring(nextContextStart, nextContextStart += (2 * lenInt));
            extData.setPosConfidence(val);
        }

        nextContext = hex.substring(nextContextStart, nextContextStart + 2);
        if (nextContext.equalsIgnoreCase("89")) {
            nextContextStart += 2;
            String len = hex.substring(nextContextStart, nextContextStart += 2);
            int lenInt = Integer.parseInt(len, 16);
            String val = hex.substring(nextContextStart, nextContextStart += (2 * lenInt));
            extData.setSpeedConfidence(val);
        }

        return nextContextStart;
    }

    private int handleLights(String hex, int start, VehStatusData statusData) {
        String lightsLen = hex.substring(start, start += 2);
        int lightsLenInt = Integer.parseInt(lightsLen, 16);
        String lights = hex.substring(start, start += (2 * lightsLenInt));
        int lightsInt = new BigInteger(lights, 16).intValue();
        statusData.setVehStatusLights(lightsInt);
        return start;
    }

    private int handleLightBar(String hex, int start, VehStatusData statusData) {
        String lightsLen = hex.substring(start, start += 2);
        int lightsLenInt = Integer.parseInt(lightsLen, 16);
        String lights = hex.substring(start, start += (2 * lightsLenInt));
        int lightsInt = new BigInteger(lights, 16).intValue();
        statusData.setVehStatusLightBar(lightsInt);
        return start;
    }

    private int handleWipers(String hex, int start, VehStatusData statusData) {
        String wipersLen = hex.substring(start, start += 2);
        int wipersLenInt = Integer.parseInt(wipersLen, 16);
        if (wipersLenInt > 0) {
            String wiperString = hex.substring(start, start + (2 * wipersLenInt));
            int wipersStart = 0;
            while (wipersStart < (2 * wipersLenInt)) {
                String tag = wiperString.substring(wipersStart, wipersStart + 2);
                ;
                String value = wiperString.substring(wipersStart + 4, wipersStart + 6);
                ;
                int valInt = Integer.parseInt(value, 16);
                if (tag.equalsIgnoreCase("80")) {
                    //80 front stat
                    statusData.setWiperStatFront(valInt);
                }
                if (tag.equalsIgnoreCase("81")) {
                    //81 front rate
                    statusData.setWiperRateFront(valInt);
                }
                if (tag.equalsIgnoreCase("82")) {
                    //82 rear stat
                    statusData.setWiperStatRear(valInt);
                }
                if (tag.equalsIgnoreCase("83")) {
                    statusData.setWiperRateRear(valInt);
                }
                wipersStart += 6;
            }
        }

        return start + (wipersLenInt * 2);
    }

    private int handleBrakeSystem(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        String brakes = hex.substring(start, start += (2 * lenInt));
        statusData.setBrakes(brakes);

        return start;
    }

    private int handleBrakePressure(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        String valString = hex.substring(start, start += (2 * lenInt));
        int val = Integer.parseInt(valString, 2);
        statusData.setBreakPressure(val);
        return start;
    }

    private int handleCoefficientOfFriction(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        String value = hex.substring(start, start += (2 * lenInt));
        int valInt = new BigInteger(value, 16).intValue();
        statusData.setCoefficientOfFriction(valInt);
        return start;
    }

    private int handleSunSensor(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        String value = hex.substring(start, start += (2 * lenInt));
        int valInt = new BigInteger(value, 16).intValue();
        statusData.setSunSensor(valInt);
        return start;
    }

    private int handleRainSensor(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        String value = hex.substring(start, start += (2 * lenInt));
        int valInt = new BigInteger(value, 16).intValue();
        statusData.setRainSensor(valInt);
        return start;
    }

    private int handleAirTemp(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        String value = hex.substring(start, start += (2 * lenInt));
        int valInt = new BigInteger(value, 16).intValue();
        statusData.setAirTemp(valInt);
        return start;
    }

    private int handleAirPressure(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        String value = hex.substring(start, start += (2 * lenInt));
        int valInt = new BigInteger(value, 16).intValue();
        statusData.setAirPressure(valInt);
        return start;
    }

    private int handleSteering(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        if (lenInt > 0) {
            String string = hex.substring(start, start + (2 * lenInt));
            int sectionStart = 0;
            while (start < (2 * lenInt)) {
                String tag = string.substring(sectionStart, sectionStart + 2);
                ;
                String value = string.substring(sectionStart + 4, sectionStart + 6);
                ;
                int valInt = Integer.parseInt(value, 16);
                if (tag.equalsIgnoreCase("80")) {
                    //80 angle
                    if (valInt != 127) {
                        statusData.setSteeringWheelAngle(valInt);
                    }
                }
                if (tag.equalsIgnoreCase("81")) {
                    //81 angle conf
                    statusData.setSteeringWheelAngleConf(valInt);
                }
                if (tag.equalsIgnoreCase("82")) {
                    //82 rate of change
                    statusData.setSteeringWheelAngleRateOfChange(valInt);
                }
                if (tag.equalsIgnoreCase("83")) {
                    //Driving wheel angle
                    statusData.setDrivingWheelAngle(valInt);
                }
                sectionStart += 6;
            }
        }

        return start + (lenInt * 2);

    }

    private int handleAccelSets(String hex, int start, VehStatusData statusData) {
        //TODO: Implement
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        if (lenInt > 0) {
            String string = hex.substring(start, start + (2 * lenInt));
            int sectionStart = 0;
            while (start < (2 * lenInt)) {
                String tag = string.substring(sectionStart, sectionStart += 2);
                int valLen = Integer.parseInt(string.substring(sectionStart, sectionStart += 2), 16);
                String value = string.substring(sectionStart, sectionStart + (2 * valLen));
                int valInt = Integer.parseInt(value, 16);
                if (tag.equalsIgnoreCase("80")) {
                    //80 Accel Set 4 way
//					statusData.setSteeringWheelAngle(valInt);
                }
                if (tag.equalsIgnoreCase("81")) {
                    //Vert Accel Threshold
//					statusData.setSteeringWheelAngleConf(valInt);
                }
                if (tag.equalsIgnoreCase("82")) {
                    //82 Yaw Rate Conf
//					statusData.setSteeringWheelAngleRateOfChange(valInt);
                }
                if (tag.equalsIgnoreCase("83")) {
                    //Accel Conf
//					statusData.setDrivingWheelAngle(valInt);
                }
                if (tag.equalsIgnoreCase("84")) {
                    //Conf Set
//					statusData.setDrivingWheelAngle(valInt);
                }
                sectionStart += (2 * valLen);
            }
        }

        return start + (lenInt * 2);
    }

    private int handleObstacle(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        if (lenInt > 0) {
            String string = hex.substring(start, start + (2 * lenInt));
            int sectionStart = 0;
            while (start < (2 * lenInt)) {
                String tag = string.substring(sectionStart, sectionStart += 2);
                int valLen = Integer.parseInt(string.substring(sectionStart, sectionStart += 2), 16);
                String value = string.substring(sectionStart, sectionStart + (2 * valLen));
                int valInt = Integer.parseInt(value, 16);
                if (tag.equalsIgnoreCase("80")) {
                    //Obstacle Distance
//					statusData.setSteeringWheelAngle(valInt);
                }
                if (tag.equalsIgnoreCase("81")) {
                    //Obstacle Direction
//					statusData.setSteeringWheelAngleConf(valInt);
                }
                if (tag.equalsIgnoreCase("82")) {
                    //DDateTime
//					statusData.setSteeringWheelAngleRateOfChange(valInt);
                }

                sectionStart += (2 * valLen);
            }
        }

        return start + (lenInt * 2);
    }

    private int handleWeather(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        //TODO: Implement

        return start + (2 * lenInt);
    }

    private int handleJ1939(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        //TODO: Implement

        return start + (2 * lenInt);
    }

    private int handleGPS(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        //TODO: Implement

        return start + (2 * lenInt);
    }

    private int handleFullPosVector(String hex, int start, VehStatusData statusData) {
        String vectLen = hex.substring(start, start += 2);
        int vectLenInt = Integer.parseInt(vectLen, 16);
        String vectString = hex.substring(start, start + (2 * vectLenInt));
        String len = null;
        int lenInt = 0;
        if (vectLenInt > 0) {
            int nextContextStart = 0;
            while (nextContextStart < (2 * vectLenInt)) {
                String nextContext = vectString.substring(nextContextStart, nextContextStart += 2);

                if (nextContext.equalsIgnoreCase("A0")) {
                    len = vectString.substring(nextContextStart, nextContextStart += 2);
                    lenInt = Integer.parseInt(len, 16);

                    if (lenInt > 0) {
                        String dateString = vectString.substring(nextContextStart, nextContextStart + (2 * lenInt));
                        int nextDateContextStart = 0;
                        while (nextDateContextStart < (2 * lenInt)) {
                            String nextDateContext = dateString.substring(nextDateContextStart, nextDateContextStart += 2);

                            if (nextDateContext.equalsIgnoreCase("80")) {
                                String yearLen = dateString.substring(nextDateContextStart, nextDateContextStart += 2);
                                int yearLenInt = Integer.parseInt(yearLen, 16);
                                String year = dateString.substring(nextDateContextStart, nextDateContextStart += (2 * yearLenInt));
                                int yearInt = new BigInteger(year, 16).intValue();
                                statusData.setInitPosYear(yearInt);
                            }
                            if (nextDateContext.equalsIgnoreCase("81")) {
                                String monthLen = dateString.substring(nextDateContextStart, nextDateContextStart += 2);
                                int monthLenInt = Integer.parseInt(monthLen, 16);
                                String month = dateString.substring(nextDateContextStart, nextDateContextStart += (2 * monthLenInt));
                                int monthInt = new BigInteger(month, 16).intValue();
                                statusData.setInitPosMonth(monthInt);
                            }
                            if (nextDateContext.equalsIgnoreCase("82")) {
                                String dayLen = dateString.substring(nextDateContextStart, nextDateContextStart += 2);
                                int dayLenInt = Integer.parseInt(dayLen, 16);
                                String day = dateString.substring(nextDateContextStart, nextDateContextStart += (2 * dayLenInt));
                                int dayInt = new BigInteger(day, 16).intValue();
                                statusData.setInitPosDay(dayInt);
                            }
                            if (nextDateContext.equalsIgnoreCase("83")) {
                                String hourLen = dateString.substring(nextDateContextStart, nextDateContextStart += 2);
                                int hourLenInt = Integer.parseInt(hourLen, 16);
                                String hour = dateString.substring(nextDateContextStart, nextDateContextStart += (2 * hourLenInt));
                                int hourInt = new BigInteger(hour, 16).intValue();
                                statusData.setInitPosHour(hourInt);
                            }
                            if (nextDateContext.equalsIgnoreCase("84")) {
                                String minuteLen = dateString.substring(nextDateContextStart, nextDateContextStart += 2);
                                int minuteLenInt = Integer.parseInt(minuteLen, 16);
                                String minute = dateString.substring(nextDateContextStart, nextDateContextStart += (2 * minuteLenInt));
                                int minuteInt = new BigInteger(minute, 16).intValue();
                                statusData.setInitPosMinute(minuteInt);
                            }
                            if (nextDateContext.equalsIgnoreCase("85")) {
                                String secondLen = dateString.substring(nextDateContextStart, nextDateContextStart += 2);
                                int secondLenInt = Integer.parseInt(secondLen, 16);
                                String second = dateString.substring(nextDateContextStart, nextDateContextStart += (2 * secondLenInt));
                                int secondInt = new BigInteger(second, 16).intValue();
                                statusData.setInitPosSecond(secondInt);
                            }
                        }
                    }
                    nextContextStart += (2 * lenInt);
                }

                if (nextContext.equalsIgnoreCase("81")) {
                    String longLen = vectString.substring(nextContextStart, nextContextStart += 2);
                    int longLenInt = Integer.parseInt(longLen, 16);
                    String lon = vectString.substring(nextContextStart, nextContextStart += (2 * longLenInt));
                    int lonInt = new BigInteger(lon, 16).intValue();
                    statusData.setInitPosLong((lonInt / 10000000.0));
                }

                if (nextContext.equalsIgnoreCase("82")) {
                    String latLen = vectString.substring(nextContextStart, nextContextStart += 2);
                    int latLenInt = Integer.parseInt(latLen, 16);
                    String a0lat = vectString.substring(nextContextStart, nextContextStart += (2 * latLenInt));
                    int a0latInt = new BigInteger(a0lat, 16).intValue();
                    statusData.setInitPosLat((a0latInt / 10000000.0));
                }

                if (nextContext.equalsIgnoreCase("83")) {
                    len = vectString.substring(nextContextStart, nextContextStart += 2);
                    lenInt = Integer.parseInt(len, 16);
                    String elevation = vectString.substring(nextContextStart, nextContextStart += 4);
                    int ele = new BigInteger(elevation, 16).intValue();
                    statusData.setElevation((ele / 10.0));
                }

                if (nextContext.equalsIgnoreCase("84")) {
                    len = vectString.substring(nextContextStart, nextContextStart += 2);
                    lenInt = Integer.parseInt(len, 16);
                    String heading = vectString.substring(nextContextStart, nextContextStart += 4);
                    int headingInt = new BigInteger(heading, 16).intValue();
                    statusData.setHeading(headingInt * .0125);

                }

                if (nextContext.equalsIgnoreCase("85")) {
                    len = vectString.substring(nextContextStart, nextContextStart += 2);
                    lenInt = Integer.parseInt(len, 16);
                    String speedTrans = vectString.substring(nextContextStart, nextContextStart += 4);
                    int st = new BigInteger(speedTrans, 16).intValue();
                    String stBin = Integer.toBinaryString(st);
                    while (stBin.length() < 16) {
                        stBin += "0";
                    }
                    String transBin = stBin.substring(0, 3);
                    int trans = Integer.parseInt(transBin, 2);
                    statusData.setTrans(trans);

                    String speedBin = stBin.substring(3, 16);
                    int speed = Integer.parseInt(speedBin, 2);
                    statusData.setSpeed(speed * .02);

                }

                if (nextContext.equalsIgnoreCase("86")) {
                    String accLen = vectString.substring(nextContextStart, nextContextStart += 2);
                    int accLenInt = Integer.parseInt(accLen, 16);
                    String acc = vectString.substring(nextContextStart, nextContextStart += (2 * accLenInt));
                    statusData.setPosAccuracy(acc);
                }

                if (nextContext.equalsIgnoreCase("87")) {
                    len = vectString.substring(nextContextStart, nextContextStart += 2);
                    lenInt = Integer.parseInt(len, 16);
                    String val = vectString.substring(nextContextStart, nextContextStart += (2 * lenInt));
                    statusData.setTimeConfidence(val);
                }

                if (nextContext.equalsIgnoreCase("88")) {
                    len = vectString.substring(nextContextStart, nextContextStart += 2);
                    lenInt = Integer.parseInt(len, 16);
                    String val = vectString.substring(nextContextStart, nextContextStart += (2 * lenInt));
                    statusData.setPosConfidence(val);
                }

                if (nextContext.equalsIgnoreCase("89")) {
                    len = vectString.substring(nextContextStart, nextContextStart += 2);
                    lenInt = Integer.parseInt(len, 16);
                    String val = vectString.substring(nextContextStart, nextContextStart += (2 * lenInt));
                    statusData.setSpeedConfidence(val);
                }

            }

        }

        return start + (2 * vectLenInt);
    }

    private int handleThrottle(String hex, int start, VehStatusData statusData) {
        String throttleLen = hex.substring(start, start += 2);
        int throttleLenInt = Integer.parseInt(throttleLen, 16);
        String throttle = hex.substring(start, start += (2 * throttleLenInt));
        int throttleInt = new BigInteger(throttle, 16).intValue();
        statusData.setVehStatusThrottle(throttleInt);
        return start;
    }

    private int handleSpeedHeadingThrottleConf(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        //TODO: Implement

        return start + (2 * lenInt);
    }

    private int handleSpeedConfidence(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        //TODO: Implement

        return start + (2 * lenInt);
    }

    private int handleVehicleData(String hex, int start, VehStatusData statusData) {

        String vehDataLen = hex.substring(start, start += 2);
        int vehDataLenInt = Integer.parseInt(vehDataLen, 16);
        if (vehDataLenInt > 0) {
            //skip vehicle height tag
            start += 2;

            String vehHeightLen = hex.substring(start, start += 2);
            int vehHeightLenInt = Integer.parseInt(vehHeightLen, 16);
            String vehHeight = hex.substring(start, start += (2 * vehHeightLenInt));
            int vehHeightInt = new BigInteger(vehHeight, 16).intValue();
            statusData.setVehStatusHeight(vehHeightInt);

            //Skip bumper tag,bumper len, bumper front tag
            start += 6;
            String bumperFrontLen = hex.substring(start, start += 2);
            int bumperFrontLenInt = Integer.parseInt(bumperFrontLen, 16);
            String bumperFront = hex.substring(start, start += (2 * bumperFrontLenInt));
            int bumperFrontInt = new BigInteger(bumperFront, 16).intValue();
            statusData.setVehStatusBumperFront(bumperFrontInt);

            //Skip bumper rear tag
            start += 2;
            String bumperRearLen = hex.substring(start, start += 2);
            int bumperRearLenInt = Integer.parseInt(bumperRearLen, 16);
            String bumperRear = hex.substring(start, start += (2 * bumperRearLenInt));
            int bumperRearInt = new BigInteger(bumperRear, 16).intValue();
            statusData.setVehStatusBumperRear(bumperRearInt);

            //Skip veh mass tag
            start += 2;
            String vehMassLen = hex.substring(start, start += 2);
            int vehMassLenInt = Integer.parseInt(vehMassLen, 16);
            String vehMass = hex.substring(start, start += (2 * vehMassLenInt));
            int vehMassInt = new BigInteger(vehMass, 16).intValue();
            statusData.setVehStatusMass(vehMassInt);

            //skip trailer weight tag
            start += 2;
            String trailerWeightLen = hex.substring(start, start += 2);
            int trailerWeightLenInt = Integer.parseInt(trailerWeightLen, 16);
            String trailerWeight = hex.substring(start, start += (2 * trailerWeightLenInt));
            int trailerWeightInt = new BigInteger(trailerWeight, 16).intValue();
            statusData.setVehStatusTrailerWeight(trailerWeightInt);

            //skip veh type tag
            start += 2;
            String vehTypeLen = hex.substring(start, start += 2);
            int vehTypeLenInt = Integer.parseInt(vehTypeLen, 16);
            String vehType = hex.substring(start, start += (2 * vehTypeLenInt));
            int vehTypeInt = new BigInteger(vehType, 16).intValue();
            statusData.setVehStatusType(vehTypeInt);

        }

        return start;
    }

    private int handleVehicleIdent(String hex, int start, VehStatusData statusData) {
        String len = hex.substring(start, start += 2);
        int lenInt = Integer.parseInt(len, 16);
        //TODO: Implement
        return start + (2 * lenInt);
    }

    private int handleV2VData(String hex, int start, VehStatusData statusData) {
        //skip next 3 bytes and v2v height tag
        start += 8;

        String v2vHeightLen = hex.substring(start, start += 2);
        int v2vHeightLenInt = Integer.parseInt(v2vHeightLen, 16);
        String v2vHeight = hex.substring(start, start += (2 * v2vHeightLenInt));
        int v2vHeightInt = new BigInteger(v2vHeight, 16).intValue();
        statusData.setV2vHeight(v2vHeightInt);

        String bfa1 = hex.substring(start, start + 2);
        if (bfa1.equalsIgnoreCase("A1")) {
            // bumper and other vehicle data
            //skip v2v bumpers tag, v2v bumpers len, v2v bumper frnt tag
            start += 6;
            String bumerFrntLen = hex.substring(start, start += 2);
            int bumerFrntLenInt = Integer.parseInt(bumerFrntLen, 16);
            String bumperFrnt = hex.substring(start, start += (2 * bumerFrntLenInt));
            int bumperFrntInt = Integer.parseInt(bumperFrnt, 16);
            statusData.setV2vBumperFront(bumperFrntInt);

            //skip v2v bumper rear tag
            start += 2;
            String bumerRearLen = hex.substring(start, start += 2);
            int bumerRearLenInt = Integer.parseInt(bumerRearLen, 16);
            String bumperRear = hex.substring(start, start += (2 * bumerRearLenInt));
            int bumperRearInt = Integer.parseInt(bumperRear, 16);
            statusData.setV2vBumperRear(bumperRearInt);

            //skip v2v mass tag
            start += 2;
            String massLen = hex.substring(start, start += 2);
            int massLenInt = Integer.parseInt(massLen, 16);
            String mass = hex.substring(start, start += (2 * massLenInt));
            int massInt = Integer.parseInt(mass, 16);
            statusData.setV2vMass(massInt);

            //skip v2v type tag
            start += 2;
            String typeLen = hex.substring(start, start += 2);
            int typeLenInt = Integer.parseInt(typeLen, 16);
            String type = hex.substring(start, start += (2 * typeLenInt));
            int typeInt = Integer.parseInt(type, 16);
            statusData.setV2vType(typeInt);

        }

        return start;
    }

    private int handleStatusData(String hex, int start, BSM bsm) {

        VehStatusData statusData = new VehStatusData();

        int nextContextStart = start + 2;
        String vehicleStatusLength = hex.substring(nextContextStart, nextContextStart += 2);
        int vehicleStatusLengthInt = Integer.parseInt(vehicleStatusLength, 16);
        int prevNextContextStart = nextContextStart;
        String nextContext = "";
        if (vehicleStatusLengthInt > 0) {
            while (nextContextStart < start + (2 * vehicleStatusLengthInt)) {

                nextContext = hex.substring(nextContextStart, nextContextStart + 2);

                if (nextContext.equalsIgnoreCase("80")) {
                    //handleLights
                    nextContextStart = handleLights(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("81")) {
                    //handleLightBar
                    nextContextStart = handleLightBar(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("A2")) {
                    nextContextStart = handleWipers(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("83")) {
                    nextContextStart = handleBrakeSystem(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("84")) {
                    //handle Brake Pressure
                    nextContextStart = handleBrakePressure(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("85")) {
                    //handle Co of Friction
                    nextContextStart = handleCoefficientOfFriction(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("86")) {
                    //handle Sun Sensor
                    nextContextStart = handleSunSensor(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("87")) {
                    //handle Rain Sensor
                    nextContextStart = handleRainSensor(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("88")) {
                    //handle Air Temp
                    nextContextStart = handleAirTemp(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("89")) {
                    //handle Air Pressure
                    nextContextStart = handleAirPressure(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("AA")) {
                    //handle Steering
                    nextContextStart = handleSteering(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("AB")) {
                    //handle Accel Sets
                    nextContextStart = handleAccelSets(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("AC")) {
                    //handle Obstacle
                    nextContextStart = handleObstacle(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("AD")) {
                    nextContextStart = handleFullPosVector(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("8E")) {
                    nextContextStart = handleThrottle(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("8F")) {
                    //handle Speed heading throttle confidence
                    nextContextStart = handleSpeedHeadingThrottleConf(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("B0")) {
                    //handle Speed confidence
                    nextContextStart = handleSpeedConfidence(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("B1")) {
                    nextContextStart = handleVehicleData(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("B2")) {
                    //vehicle ident
                    nextContextStart = handleVehicleIdent(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("B3")) {
                    //j1939 data
                    nextContextStart = handleJ1939(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("B4")) {
                    //weather report
                    nextContextStart = handleWeather(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("B5")) {
                    //gpsStatus
                    nextContextStart = handleGPS(hex, nextContextStart + 2, statusData);
                }

                if (nextContext.equalsIgnoreCase("BF")) {
                    nextContextStart = handleV2VData(hex, nextContextStart + 2, statusData);
                }

                if (nextContextStart == prevNextContextStart) {
                    String lenHex = hex.substring(nextContextStart + 2, nextContextStart + 4);
//					int unknownContextLen = Integer.parseInt(lenHex,16);

                    nextContextStart += 2;
                    return start * vehicleStatusLengthInt;
                }
            }
        }
        bsm.setVehStatusData(statusData);
        return nextContextStart;
    }

    private void log(String s) {
        logger.debug(s);
    }

    public String convertBytesToHex(byte[] bytes) {
        String hexString = DatatypeConverter.printHexBinary(bytes);
        return hexString;
    }

    public byte[] hexToBinary(String hex) {
        byte[] byteArray = DatatypeConverter.parseHexBinary(hex);
        return byteArray;
    }

    public byte[] removeHeaderBytes(byte[] bytes) {
        String hex = convertBytesToHex(bytes);
        int index = hex.indexOf(BSM_START_CHARS);
        if (index > -1) {
            String bsmHex = hex.substring(index);
            log(bsmHex);
            return hexToBinary(bsmHex);
        } else {
            return null;
        }

    }

}
