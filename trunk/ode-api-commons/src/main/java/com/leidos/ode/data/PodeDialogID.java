
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
        name = "PodeDialogID"
    )
    public class PodeDialogID implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "podeDataDistribution", hasTag = true , tag = 192 )
            podeDataDistribution , 
            @ASN1EnumItem ( name = "podeDataSubscriptionRegistration", hasTag = true , tag = 193 )
            podeDataSubscriptionRegistration , 
            @ASN1EnumItem ( name = "podeDataDelivery", hasTag = true , tag = 194 )
            podeDataDelivery , 
            @ASN1EnumItem ( name = "podeArchiveData", hasTag = true , tag = 195 )
            podeArchiveData , 
            @ASN1EnumItem ( name = "podeLogMessage", hasTag = true , tag = 196 )
            podeLogMessage , 
            @ASN1EnumItem ( name = "podeDataPulicationRegistration", hasTag = true , tag = 197 )
            podeDataPulicationRegistration , 
            @ASN1EnumItem ( name = "podeDataArchiveRetrival", hasTag = true , tag = 198 )
            podeDataArchiveRetrival , 
            @ASN1EnumItem ( name = "podeLogData", hasTag = true , tag = 199 )
            podeLogData , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PodeDialogID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            