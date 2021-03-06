
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
    @ASN1Sequence ( name = "IncidentEvent", isSet = false )
    public class IncidentEvent implements IASN1PreparedElement {
            
        @ASN1Element ( name = "incidentType", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private IncidentType incidentType = null;
                
  
        @ASN1Element ( name = "delayType", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private DelayType delayType = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 20L 
		
	   )
	   
        @ASN1Element ( name = "delayMiles", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Integer delayMiles = null;
                
  
        
        public IncidentType getIncidentType () {
            return this.incidentType;
        }

        

        public void setIncidentType (IncidentType value) {
            this.incidentType = value;
        }
        
  
        
        public DelayType getDelayType () {
            return this.delayType;
        }

        
        public boolean isDelayTypePresent () {
            return this.delayType != null;
        }
        

        public void setDelayType (DelayType value) {
            this.delayType = value;
        }
        
  
        
        public Integer getDelayMiles () {
            return this.delayMiles;
        }

        
        public boolean isDelayMilesPresent () {
            return this.delayMiles != null;
        }
        

        public void setDelayMiles (Integer value) {
            this.delayMiles = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(IncidentEvent.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            