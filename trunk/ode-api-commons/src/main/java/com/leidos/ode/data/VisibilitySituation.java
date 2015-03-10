
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
        name = "VisibilitySituation"
    )
    public class VisibilitySituation implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "other", hasTag = true , tag = 1 )
            other , 
            @ASN1EnumItem ( name = "unknown", hasTag = true , tag = 2 )
            unknown , 
            @ASN1EnumItem ( name = "clear", hasTag = true , tag = 3 )
            clear , 
            @ASN1EnumItem ( name = "fogNotPatchy", hasTag = true , tag = 4 )
            fogNotPatchy , 
            @ASN1EnumItem ( name = "patchyFog", hasTag = true , tag = 5 )
            patchyFog , 
            @ASN1EnumItem ( name = "blowingSnow", hasTag = true , tag = 6 )
            blowingSnow , 
            @ASN1EnumItem ( name = "smoke", hasTag = true , tag = 7 )
            smoke , 
            @ASN1EnumItem ( name = "seaSpray", hasTag = true , tag = 8 )
            seaSpray , 
            @ASN1EnumItem ( name = "vehicleSpray", hasTag = true , tag = 9 )
            vehicleSpray , 
            @ASN1EnumItem ( name = "blowingDustOrSand", hasTag = true , tag = 10 )
            blowingDustOrSand , 
            @ASN1EnumItem ( name = "sunGlare", hasTag = true , tag = 11 )
            sunGlare , 
            @ASN1EnumItem ( name = "swarmsOfInsects", hasTag = true , tag = 12 )
            swarmsOfInsects , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(VisibilitySituation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            