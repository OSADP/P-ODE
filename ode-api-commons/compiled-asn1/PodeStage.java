
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
        name = "PodeStage"
    )
    public class PodeStage implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "reserved", hasTag = true , tag = 0 )
            reserved , 
            @ASN1EnumItem ( name = "parse", hasTag = true , tag = 1 )
            parse , 
            @ASN1EnumItem ( name = "sanitize", hasTag = true , tag = 2 )
            sanitize , 
            @ASN1EnumItem ( name = "encrypt", hasTag = true , tag = 3 )
            encrypt , 
            @ASN1EnumItem ( name = "send", hasTag = true , tag = 4 )
            send , 
            @ASN1EnumItem ( name = "received", hasTag = true , tag = 5 )
            received , 
            @ASN1EnumItem ( name = "unknown", hasTag = true , tag = 6 )
            unknown , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PodeStage.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            