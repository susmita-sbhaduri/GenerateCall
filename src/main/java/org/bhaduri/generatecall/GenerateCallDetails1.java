/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

/**
 *
 * @author sb
 */
import org.bhaduri.datatransfer.DTO.DataStoreNames;
import java.io.IOException;
import static org.apache.commons.io.comparator.LastModifiedFileComparator.*;
import java.io.File;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bhaduri.datatransfer.DTO.*;
import org.bhaduri.minutedataaccess.services.MasterDataServices;


public class GenerateCallDetails1 {

    public void getFileList() {
        
        

        String titleExist;
        MasterDataServices masterDataService = new MasterDataServices();
        List<String> scripIDList = new ArrayList<>();
        scripIDList = masterDataService.readScripData();
        
        List<RecordCallPrice> recordCalls = new ArrayList<>();
//        titleExist = "yes";
//        recordCalls = readCSVCallList(DataStoreNames.INPUT_CALL_DATA_PATH, titleExist);
        titleExist = "no";

        String scripFolderPath = "";
        String[] delimitedString;
        List<ResultData> resultDatas = new ArrayList<ResultData>(); // call list for the last and 
//        previous days file using elliot curve algo

        for (int i = 0; i < scripIDList.size(); i++) {
             String scripId = scripIDList.get(i);

            CsvTickData recordDataLast = new CsvTickData();           
            
            recordDataLast = masterDataService.getLastpricerPerScripID(scripId);
            
            resultDatas.add(fillResult(recordDataLast.getTickData(), scripId, recordDataLast.getDateTime()));
/////////// Update existing call list for today's call
            RecordCallPrice callToAdd = new RecordCallPrice();
            //latest call record
            callToAdd.setScripID(resultDatas.get(resultDatas.size() - 1).getScripID());
            callToAdd.setLastUpdateTime(resultDatas.get(resultDatas.size() - 1).getLastUpdateTime());
            callToAdd.setPrice(resultDatas.get(resultDatas.size() - 1).getPrice());
            callToAdd.setLastCallVersionOne(resultDatas.get(resultDatas.size() - 1).getLastCallVersionOne());
            callToAdd.setLastCallVersionTwo(resultDatas.get(resultDatas.size() - 1).getLastCallVersionTwo());
//            callToAdd.setTallyVersionOne(resultDatas.get(resultDatas.size() - 1).getTallyVersionOne());
//            callToAdd.setTallyVersionTwo(resultDatas.get(resultDatas.size() - 1).getTallyVersionTwo());
            callToAdd.setRetraceVersionOne(resultDatas.get(resultDatas.size() - 1).getRetraceVersionOne());
            callToAdd.setRetraceVersionTwo(resultDatas.get(resultDatas.size() - 1).getRetraceVersionTwo());
            callToAdd.setPriceBrokerageGstOne(resultDatas.get(resultDatas.size() - 1).getPriceBrokerageGstOne());
            callToAdd.setPriceBrokerageGstTwo(resultDatas.get(resultDatas.size() - 1).getPriceBrokerageGstTwo());
            
            masterDataService.insertIntoCalltable(callToAdd);
//            recordCalls.add(callToAdd);

/////////// Update existing call list for today's call
            System.out.println("Done in scrip loop");
        }


        
        recordCalls = masterDataService.readSortCallList();
        printCallList(recordCalls);
        
//        Collections.sort(recordCalls , new SortCallList());
        
        ////////////////////////////////////////////////////////////////////////

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
        String printFile = DataStoreNames.OUTPUT_CALL_DATA_PATH;
        String callOrPrice = "call";
        PrintMatrix printMatrix = new PrintMatrix();
        printMatrix.printResultData(callListFromDbase, printFile, priceHeading, callOrPrice);
        System.out.println("Done");
        
    }
    
    private List<List<String>> readCSV(String csvPath) {
        String line;
        List<List<String>> records = new ArrayList<>();
        try {
            BufferedReader brPrev = new BufferedReader(new FileReader(csvPath));

            while ((line = brPrev.readLine()) != null) {
                // use comma as separator  
                String[] fields = line.split(",");
                //fields will now contain all values    
                records.add(Arrays.asList(fields));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
//                Logger.getLogger(PerMinuteResposeOfNSE.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println("recordTest:" + records.get(records.size() - 1).get(1));
        return records;
    }

    private ResultData fillResult(List<List<Double>> recordData, String scripId, Date lastUpdateDate) {

        int callCount = 3;
        Smoothing smoothing = new Smoothing(recordData, callCount);
        List<SmoothData> smoothData = new ArrayList<SmoothData>();
        smoothData = smoothing.genCall();
        ResultData eachResultData = new ResultData();
        Double thresHold;
        thresHold = (0.5 / 100) * (recordData.get(recordData.size() - 1).get(1))
                + ((0.5 / 100) * recordData.get(recordData.size() - 1).get(1)) * 18 / 100;

        eachResultData.setScripID(scripId);//to be fixed        
        eachResultData.setLastUpdateTime(lastUpdateDate);//to be fixed
        eachResultData.setPrice(recordData.get(recordData.size() - 1).get(1));
        eachResultData.setLastCallVersionOne(smoothData.get(2).getCallArrayOne());
        eachResultData.setLastCallVersionTwo(smoothData.get(2).getCallArrayTwo());
        eachResultData.setTallyVersionOne("");
        eachResultData.setTallyVersionTwo("");
        eachResultData.setRetraceVersionOne(smoothData.get(2).getRetraceOne());
        eachResultData.setRetraceVersionTwo(smoothData.get(2).getRetraceTwo());
        eachResultData.setLastCallOneUpdated("");
        eachResultData.setLastCallTwoUpdated("");
        eachResultData.setTallyOneUpdated("");
        eachResultData.setTallyTwoUpdated("");

        if (smoothData.get(2).getCallArrayOne().equals("buy")) {
            eachResultData.setPriceBrokerageGstOne(recordData.get(recordData.size() - 1).get(1) + thresHold);
        } else {
            if (smoothData.get(2).getCallArrayOne().equals("sell")) {
                eachResultData.setPriceBrokerageGstOne(recordData.get(recordData.size() - 1).get(1) - thresHold);
            } else {
                eachResultData.setPriceBrokerageGstOne(recordData.get(recordData.size() - 1).get(1));
            }
        }

        if (smoothData.get(2).getCallArrayTwo().equals("buy")) {
            eachResultData.setPriceBrokerageGstTwo(recordData.get(recordData.size() - 1).get(1) + thresHold);
        } else {
            if (smoothData.get(2).getCallArrayTwo().equals("sell")) {
                eachResultData.setPriceBrokerageGstTwo(recordData.get(recordData.size() - 1).get(1) - thresHold);
            } else {
                eachResultData.setPriceBrokerageGstTwo(recordData.get(recordData.size() - 1).get(1));
            }
        }

//        System.out.println("recordTest:" + records.get(records.size() - 1).get(1));
        return eachResultData;
    }

    private int prevIndex(List<List<String>> recordPrev, List<List<String>> recordLast) {
        String prevTS = recordPrev.get(recordPrev.size() - 1).get(1);
        int indexLast = 0;
        for (int i = 0; i < recordLast.size(); i++) {
            if (recordLast.get(i).get(1).equals(prevTS)) {
                indexLast = i;
                break;
            }
        }
//        System.out.println("indexLast:" + Integer.toString(indexLast));
        return indexLast;
    }

}
