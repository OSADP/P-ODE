
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
        name = "DelayType"
    )
    public class DelayType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "potential", hasTag = true , tag = 0 )
            potential , 
            @ASN1EnumItem ( name = "minor", hasTag = true , tag = 1 )
            minor , 
            @ASN1EnumItem ( name = "major", hasTag = true , tag = 2 )
            major , 
            @ASN1EnumItem ( name = "other", hasTag = true , tag = 3 )
            other , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DelayType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            