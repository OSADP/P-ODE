
package com.leidos.ode.data;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.bn.*;
import org.bn.annotations.*;
import org.bn.annotations.constraints.*;
import org.bn.coders.*;
import org.bn.types.*;




    @ASN1PreparedElement
    @ASN1Choice ( name = "PodeSubReqType" )
    public class PodeSubReqType implements IASN1PreparedElement {
            
        @ASN1Element ( name = "realTimeData", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private PodeRealTimeData realTimeData = null;
                
  
        @ASN1Element ( name = "replayData", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private PodeReplayData replayData = null;
                
  
        
        public PodeRealTimeData getRealTimeData () {
            return this.realTimeData;
        }

        public boolean isRealTimeDataSelected () {
            return this.realTimeData != null;
        }

        private void setRealTimeData (PodeRealTimeData value) {
            this.realTimeData = value;
        }

        
        public void selectRealTimeData (PodeRealTimeData value) {
            this.realTimeData = value;
            
                    setReplayData(null);
                            
        }

        
  
        
        public PodeReplayData getReplayData () {
            return this.replayData;
        }

        public boolean isReplayDataSelected () {
            return this.replayData != null;
        }

        private void setReplayData (PodeReplayData value) {
            this.replayData = value;
        }

        
        public void selectReplayData (PodeReplayData value) {
            this.replayData = value;
            
                    setRealTimeData(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PodeSubReqType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            