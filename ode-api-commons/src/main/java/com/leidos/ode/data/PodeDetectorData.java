
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
    @ASN1Sequence ( name = "PodeDetectorData", isSet = false )
    public class PodeDetectorData implements IASN1PreparedElement {
            
    @ASN1String( name = "", 
        stringType = UniversalTag.UTF8String , isUCS = false )
    
        @ASN1Element ( name = "detectorID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private String detectorID = null;
                
  
    @ASN1String( name = "", 
        stringType = UniversalTag.UTF8String , isUCS = false )
    
        @ASN1Element ( name = "stationID", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private String stationID = null;
                
  
        @ASN1Element ( name = "detectMethod", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private PodeDetectionMethod detectMethod = null;
                
  
        @ASN1Element ( name = "status", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private PodeDetectorStatus status = null;
                
  
        @ASN1Element ( name = "position", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private Position3D position = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "mileMarker", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private byte[] mileMarker = null;
                
  
@ASN1SequenceOf( name = "laneData", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
        @ASN1Element ( name = "laneData", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private java.util.Collection<PodeLaneData>  laneData = null;
                
  
        
        public String getDetectorID () {
            return this.detectorID;
        }

        

        public void setDetectorID (String value) {
            this.detectorID = value;
        }
        
  
        
        public String getStationID () {
            return this.stationID;
        }

        
        public boolean isStationIDPresent () {
            return this.stationID != null;
        }
        

        public void setStationID (String value) {
            this.stationID = value;
        }
        
  
        
        public PodeDetectionMethod getDetectMethod () {
            return this.detectMethod;
        }

        
        public boolean isDetectMethodPresent () {
            return this.detectMethod != null;
        }
        

        public void setDetectMethod (PodeDetectionMethod value) {
            this.detectMethod = value;
        }
        
  
        
        public PodeDetectorStatus getStatus () {
            return this.status;
        }

        
        public boolean isStatusPresent () {
            return this.status != null;
        }
        

        public void setStatus (PodeDetectorStatus value) {
            this.status = value;
        }
        
  
        
        public Position3D getPosition () {
            return this.position;
        }

        
        public boolean isPositionPresent () {
            return this.position != null;
        }
        

        public void setPosition (Position3D value) {
            this.position = value;
        }
        
  
        
        public byte[] getMileMarker () {
            return this.mileMarker;
        }

        
        public boolean isMileMarkerPresent () {
            return this.mileMarker != null;
        }
        

        public void setMileMarker (byte[] value) {
            this.mileMarker = value;
        }
        
  
        
        public java.util.Collection<PodeLaneData>  getLaneData () {
            return this.laneData;
        }

        

        public void setLaneData (java.util.Collection<PodeLaneData>  value) {
            this.laneData = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PodeDetectorData.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            