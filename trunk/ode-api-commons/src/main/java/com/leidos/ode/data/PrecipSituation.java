
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
    @ASN1Enum (
        name = "PrecipSituation"
    )
    public class PrecipSituation implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "other", hasTag = true , tag = 1 )
            other , 
            @ASN1EnumItem ( name = "unknown", hasTag = true , tag = 2 )
            unknown , 
            @ASN1EnumItem ( name = "noPrecipitation", hasTag = true , tag = 3 )
            noPrecipitation , 
            @ASN1EnumItem ( name = "unidentifiedSlight", hasTag = true , tag = 4 )
            unidentifiedSlight , 
            @ASN1EnumItem ( name = "unidentifiedModerate", hasTag = true , tag = 5 )
            unidentifiedModerate , 
            @ASN1EnumItem ( name = "unidentifiedHeavy", hasTag = true , tag = 6 )
            unidentifiedHeavy , 
            @ASN1EnumItem ( name = "snowSlight", hasTag = true , tag = 7 )
            snowSlight , 
            @ASN1EnumItem ( name = "snowModerate", hasTag = true , tag = 8 )
            snowModerate , 
            @ASN1EnumItem ( name = "snowHeavy", hasTag = true , tag = 9 )
            snowHeavy , 
            @ASN1EnumItem ( name = "rainSlight", hasTag = true , tag = 10 )
            rainSlight , 
            @ASN1EnumItem ( name = "rainModerate", hasTag = true , tag = 11 )
            rainModerate , 
            @ASN1EnumItem ( name = "rainHeavy", hasTag = true , tag = 12 )
            rainHeavy , 
            @ASN1EnumItem ( name = "frozenPrecipitationSlight", hasTag = true , tag = 13 )
            frozenPrecipitationSlight , 
            @ASN1EnumItem ( name = "frozenPrecipitationModerate", hasTag = true , tag = 14 )
            frozenPrecipitationModerate , 
            @ASN1EnumItem ( name = "frozenPrecipitationHeavy", hasTag = true , tag = 15 )
            frozenPrecipitationHeavy , 
        }
        
        private EnumType value;
        private Integer integerForm;
        
        public EnumType getValue() {
            return this.value;
        }
        
        public void setValue(EnumType value) {
            this.value = value;
        }
        
        public Integer getIntegerForm() {
            return integerForm;
        }
        
        public void setIntegerForm(Integer value) {
            integerForm = value;
        }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PrecipSituation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            