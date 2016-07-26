/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.parser.impl;

import com.leidos.ode.agent.data.vdotbsm.VDOTBSMList;
import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.collector.datasource.pull.VDOTBSMDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author cassadyja
 */
public class VDOTBSMParser extends ODEDataParser{

    @Override
    protected ODEDataParserResponse parse(byte[] bytes) {
        if(bytes != null){
            ObjectMapper mapper = new ObjectMapper();
            try {
                VDOTBSMList data = mapper.readValue(new ByteArrayInputStream(bytes), VDOTBSMList.class);
                if(data.getBsm().length > 0){
                    return new ODEDataParserResponse(data, ODEDataParserReportCode.PARSE_SUCCESS);
                }else{
                    return new ODEDataParserResponse(null, ODEDataParserReportCode.NO_DATA);
                }
            } catch (IOException ex) {
                Logger.getLogger(VDOTBSMDataSource.class.getName()).log(Level.SEVERE, null, ex);
                return new ODEDataParserResponse(null, ODEDataParserReportCode.PARSE_ERROR);
            }
        }else{
            return new ODEDataParserResponse(null, ODEDataParserReportCode.NO_DATA);
        }
    }
    
}
