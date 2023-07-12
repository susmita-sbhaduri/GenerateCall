///////*
////// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
////// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
////// */
package org.bhaduri.validatecall;
////
/////**
//// *
//// * @author sb
//// */
import org.bhaduri.datatransfer.DTO.*;;
import org.bhaduri.generatecall.*;
import org.bhaduri.minutedataaccess.services.MasterDataServices;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import static org.apache.commons.io.comparator.LastModifiedFileComparator.LASTMODIFIED_COMPARATOR;
//
//
public class ValidateCallDetails {

    String inputScripID;
    String statusString;
 
    public ValidateCallDetails(String inputScripID) {
        
        this.inputScripID = inputScripID;
        processInput();
    }
    private void processInput() {
        
        List<String> scripIDList = new ArrayList<>();
        MasterDataServices masterDataService = new MasterDataServices();
        scripIDList = masterDataService.readScripData();
        
        String scripFolderPath = "";
        String[] delimitedString;
        List<RecordCallPrice> updatedCalls;
        List<RecordCallPrice> reverseCallList = null;
        updatedCalls = masterDataService.callListPerScrip(inputScripID);
        int i = 286; //user input
        if(updatedCalls.get(i).getLastCallVersionTwo().equals("buy")){
            reverseCallList = masterDataService.listReverseCalls(inputScripID, updatedCalls.get(i).getLastUpdateTime(), "sell");
            if(reverseCallList.isEmpty()){
                System.out.println("No previous Sell call for this scripid" + inputScripID);
            }
            else {
//                printCallList(reverseCallList);
            }
        }
        if(updatedCalls.get(i).getLastCallVersionTwo().equals("sell")){
            reverseCallList = masterDataService.listReverseCalls(inputScripID, updatedCalls.get(i).getLastUpdateTime(), "buy");
            if(reverseCallList.isEmpty()){
                System.out.println("No previous Buy call for this scripid" + inputScripID);
            }
            else {
//                printCallList(reverseCallList);
            }
        }
        if(updatedCalls.get(i).getLastCallVersionTwo().equals("buy")){
            int ii = 17; //user input
            List<RecordMinute> minuteDataForRange = new ArrayList<>();
            List<RecordMinute> minuteDataValid = new ArrayList<>();;
            RecordMinute record = new RecordMinute();
            if(reverseCallList.isEmpty()==false){
                minuteDataForRange = masterDataService.getMindataForRange(inputScripID,
                        reverseCallList.get(ii).getLastUpdateTime(), updatedCalls.get(i).getLastUpdateTime());
                Double sellPrice = reverseCallList.get(ii).getPrice();
                
                for (int k = 0; k < minuteDataForRange.size(); k++) {
                    if (minuteDataForRange.get(k).getDaylastprice() < sellPrice) {
                        record.setScripID(minuteDataForRange.get(k).getScripID());
                        record.setLastUpdateTime(minuteDataForRange.get(k).getLastUpdateTime());
                        record.setOpenprice(minuteDataForRange.get(k).getOpenprice());
                        record.setDaylastprice(minuteDataForRange.get(k).getDaylastprice());
                        record.setDayhighprice(minuteDataForRange.get(k).getDayhighprice());
                        record.setDaylowprice(minuteDataForRange.get(k).getDaylowprice());
                        record.setPrevcloseprice(minuteDataForRange.get(k).getPrevcloseprice());
                        record.setTotaltradedvolume(minuteDataForRange.get(k).getTotaltradedvolume());
                        minuteDataValid.add(record);
                        record = new RecordMinute();
                    }
                }
                if(minuteDataValid.size()==0){
                    System.out.println("Buy price is not satisfied w.r.t selected Sell call for this scripid=" + inputScripID);
                }
                else{
                    String titleString = "scripid,lastupdateminute,openprice,daylastprice,dayhighprice,daylowprice,prevcloseprice,totaltradedvolume";
                    PrintMatrix printMatrix = new PrintMatrix();
                    printMatrix.printMinuteData(minuteDataValid, "/home/sb/Documents/java_testing/valid_buy_"+inputScripID+".csv", titleString);
                }                
            }             
        }
        
        if(updatedCalls.get(i).getLastCallVersionTwo().equals("sell")){
            int ii = 149; //user input
            List<RecordMinute> minuteDataForRange = new ArrayList<>();
            List<RecordMinute> minuteDataValid = new ArrayList<>();;
            RecordMinute record = new RecordMinute();
            if(reverseCallList.isEmpty()==false){
                minuteDataForRange = masterDataService.getMindataForRange(inputScripID,
                        reverseCallList.get(ii).getLastUpdateTime(), updatedCalls.get(i).getLastUpdateTime());
                Double buyPrice = reverseCallList.get(ii).getPrice();
                
                for (int k = 0; k < minuteDataForRange.size(); k++) {
                    if (minuteDataForRange.get(k).getDaylastprice() > buyPrice) {
                        record.setScripID(minuteDataForRange.get(k).getScripID());
                        record.setLastUpdateTime(minuteDataForRange.get(k).getLastUpdateTime());
                        record.setOpenprice(minuteDataForRange.get(k).getOpenprice());
                        record.setDaylastprice(minuteDataForRange.get(k).getDaylastprice());
                        record.setDayhighprice(minuteDataForRange.get(k).getDayhighprice());
                        record.setDaylowprice(minuteDataForRange.get(k).getDaylowprice());
                        record.setPrevcloseprice(minuteDataForRange.get(k).getPrevcloseprice());
                        record.setTotaltradedvolume(minuteDataForRange.get(k).getTotaltradedvolume());
                        minuteDataValid.add(record);
                        record = new RecordMinute();
                    }
                }
                if(minuteDataValid.size()==0){
                    System.out.println("Buy price is not satisfied w.r.t selected Buy call for this scripid=" + inputScripID);
                }
                else{
                    String titleString = "scripid,lastupdateminute,openprice,daylastprice,dayhighprice,daylowprice,prevcloseprice,totaltradedvolume";
                    PrintMatrix printMatrix = new PrintMatrix();
                    printMatrix.printMinuteData(minuteDataValid, "/home/sb/Documents/java_testing/valid_sell_"+inputScripID+".csv", titleString);
                }
            }             
        }
        List<RecordCallPrice> inputPriceList = new ArrayList<>();
        
        CsvTickData lastCsvTickData;
        String callOrPrice;
        String scripId;        
        
    }

    public String getValidateStatus() {
        return statusString;
    }
    private CsvTickData readLastTickerData(String filePath, String fromDate) {
        CsvTickData retCsvTickData = new CsvTickData();
        String line;
        double index = 0;
        double indexLast = 0;
        List<List<Double>> recordData = new ArrayList<>();
        List<Double> row = null;
        String[] fields = null;
        try {
            BufferedReader brPrev = new BufferedReader(new FileReader(filePath));
            line = brPrev.readLine();
            while ((line = brPrev.readLine()) != null) {
                row = new ArrayList<>();
                fields = line.split(",");
                if(fields[1].equals(fromDate)){
                    indexLast = index;
                }
                if(index>indexLast && indexLast > 0){
                    row.add(index);
                    row.add(Double.valueOf(fields[3]));
                    recordData.add(row); 
                }              
                index = index+1;
            }
            retCsvTickData.setTickData(recordData);             
        } catch (IOException ex) {
            ex.printStackTrace();
//                Logger.getLogger(PerMinuteResposeOfNSE.class.getName()).log(Level.SEVERE, null, ex);
        }       
        return retCsvTickData;
    }
    
    private void printCallList(List<RecordCallPrice> callListFromDbase) {
        String priceHeading = "EQ,Date,Price,CallOne,CallTwo,RetraceOne,RetraceTwo,"
                + "PriceGSTOne,PriceGSTTwo";
        Double thresHold;
        for (int i = 0; i < callListFromDbase.size(); i++) {
            thresHold = (0.5 / 100) * (callListFromDbase.get(i).getPrice())
                    + ((0.5 / 100) * callListFromDbase.get(i).getPrice()) * 18 / 100;
            if (callListFromDbase.get(i).getLastCallVersionOne().equals("buy")) {
                callListFromDbase.get(i).setPriceBrokerageGstOne(callListFromDbase.get(i).getPrice() + thresHold);
            } else {
                if (callListFromDbase.get(i).getLastCallVersionOne().equals("sell")) {
                    callListFromDbase.get(i).setPriceBrokerageGstOne(callListFromDbase.get(i).getPrice() - thresHold);
                } else {
                    callListFromDbase.get(i).setPriceBrokerageGstOne(callListFromDbase.get(i).getPrice());
                }
            }
            
            if (callListFromDbase.get(i).getLastCallVersionTwo().equals("buy")) {
                callListFromDbase.get(i).setPriceBrokerageGstTwo(callListFromDbase.get(i).getPrice() + thresHold);
            } else {
                if (callListFromDbase.get(i).getLastCallVersionTwo().equals("sell")) {
                    callListFromDbase.get(i).setPriceBrokerageGstTwo(callListFromDbase.get(i).getPrice() - thresHold);
                } else {
                    callListFromDbase.get(i).setPriceBrokerageGstTwo(callListFromDbase.get(i).getPrice());
                }
            }      

        }
        String printFile = DataStoreNames.REV_CALL_DATA_SCRIPID;
        String callOrPrice = "call";
        PrintMatrix printMatrix = new PrintMatrix();
        printMatrix.printResultData(callListFromDbase, printFile, priceHeading, callOrPrice);
        System.out.println("Done");
        
    }
}
