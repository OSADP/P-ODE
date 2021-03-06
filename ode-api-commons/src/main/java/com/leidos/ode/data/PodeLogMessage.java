
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
    @ASN1Sequence ( name = "PodeLogMessage", isSet = false )
    public class PodeLogMessage implements IASN1PreparedElement {
            
        @ASN1Element ( name = "dialogID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private PodeDialogID dialogID = null;
                
  
        @ASN1Element ( name = "seqID", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private SemiSequenceID seqID = null;
                
  
        @ASN1Element ( name = "groupID", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private GroupID groupID = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 4L )
        
        @ASN1Element ( name = "requestID", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private byte[] requestID = null;
                
  
        @ASN1Element ( name = "dataSource", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private PodeSource dataSource = null;
                
  
        @ASN1Element ( name = "dataType", isOptional =  false , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private PodeDataTypes dataType = null;
                
  
        @ASN1Element ( name = "startTime", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private DDateTime startTime = null;
                
  
        @ASN1Element ( name = "endTime", isOptional =  false , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private DDateTime endTime = null;
                
  
        @ASN1Element ( name = "stage", isOptional =  false , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private PodeStage stage = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 4L )
        
        @ASN1Element ( name = "msgSize", isOptional =  false , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private byte[] msgSize = null;
                
  
        @ASN1Element ( name = "messageID", isOptional =  false , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private Sha256Hash messageID = null;
                
  
        
        public PodeDialogID getDialogID () {
            return this.dialogID;
        }

        

        public void setDialogID (PodeDialogID value) {
            this.dialogID = value;
        }
        
  
        
        public SemiSequenceID getSeqID () {
            return this.seqID;
        }

        

        public void setSeqID (SemiSequenceID value) {
            this.seqID = value;
        }
        
  
        
        public GroupID getGroupID () {
            return this.groupID;
        }

        

        public void setGroupID (GroupID value) {
            this.groupID = value;
        }
        
  
        
        public byte[] getRequestID () {
            return this.requestID;
        }

        

        public void setRequestID (byte[] value) {
            this.requestID = value;
        }
        
  
        
        public PodeSource getDataSource () {
            return this.dataSource;
        }

        

        public void setDataSource (PodeSource value) {
            this.dataSource = value;
        }
        
  
        
        public PodeDataTypes getDataType () {
            return this.dataType;
        }

        

        public void setDataType (PodeDataTypes value) {
            this.dataType = value;
        }
        
  
        
        public DDateTime getStartTime () {
            return this.startTime;
        }

        

        public void setStartTime (DDateTime value) {
            this.startTime = value;
        }
        
  
        
        public DDateTime getEndTime () {
            return this.endTime;
        }

        

        public void setEndTime (DDateTime value) {
            this.endTime = value;
        }
        
  
        
        public PodeStage getStage () {
            return this.stage;
        }

        

        public void setStage (PodeStage value) {
            this.stage = value;
        }
        
  
        
        public byte[] getMsgSize () {
            return this.msgSize;
        }

        

        public void setMsgSize (byte[] value) {
            this.msgSize = value;
        }
        
  
        
        public Sha256Hash getMessageID () {
            return this.messageID;
        }

        

        public void setMessageID (Sha256Hash value) {
            this.messageID = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PodeLogMessage.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            