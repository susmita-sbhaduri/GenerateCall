package org.bhaduri.validatecall;
import static org.bhaduri.generatecall.DataStoreNames.*;


public class ValidateCall {
    
    public static void main(String[] args) {

        ValidateCallDetails validateCallDetails = new ValidateCallDetails(TICKER_DATA_DETAILS, 
                OUTPUT_CALL_DATA_PATH, INPUT_PRICE_DATA_PATH);
//        ValidateCallDetails.getFileList();

    }
    
}