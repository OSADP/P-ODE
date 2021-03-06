
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
    @ASN1Sequence ( name = "Area", isSet = false )
    public class Area implements IASN1PreparedElement {
            
    @ASN1String( name = "", 
        stringType = UniversalTag.UTF8String , isUCS = false )
    
        @ASN1Element ( name = "jurisdiction", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private String jurisdiction = null;
                
  
    @ASN1String( name = "", 
        stringType = UniversalTag.UTF8String , isUCS = false )
    
        @ASN1Element ( name = "region", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private String region = null;
                
  
    @ASN1String( name = "", 
        stringType = UniversalTag.UTF8String , isUCS = false )
    
        @ASN1Element ( name = "state", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private String state = null;
                
  
        
        public String getJurisdiction () {
            return this.jurisdiction;
        }

        

        public void setJurisdiction (String value) {
            this.jurisdiction = value;
        }
        
  
        
        public String getRegion () {
            return this.region;
        }

        
        public boolean isRegionPresent () {
            return this.region != null;
        }
        

        public void setRegion (String value) {
            this.region = value;
        }
        
  
        
        public String getState () {
            return this.state;
        }

        
        public boolean isStatePresent () {
            return this.state != null;
        }
        

        public void setState (String value) {
            this.state = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Area.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            