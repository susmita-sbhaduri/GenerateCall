package org.bhaduri.validatecall;
import static org.bhaduri.datatransfer.DTO.DataStoreNames.*;


public class ValidateCall {
    
    public static void main(String[] args) {

        ValidateCallDetails validateCallDetails = new ValidateCallDetails(VALIDATE_SCRIPID);
         System.out.println(validateCallDetails.getValidateStatus());

    }
    
}