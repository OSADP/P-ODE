/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.data.PodeDataDelivery;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;

/**
 *
 * @author cassadyja
 */
public class ODEJ2735DataParser extends ODEDataParser{
    private final String TAG = getClass().getSimpleName();
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TAG);

    @Override
    protected ODEDataParserResponse parse(byte[] bytes) {
        try {
            IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            PodeDataDelivery message = decoder.decode(bis, PodeDataDelivery.class);
            return new ODEDataParserResponse(message, ODEDataParserReportCode.PARSE_SUCCESS);
        } catch (Exception ex) {
            logger.error("Error Parsing message", ex);
        }
        return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
    }
    
    
    
}
