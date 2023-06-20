/////*
//// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//// */
package org.bhaduri.validatecall;
//
///**
// *
// * @author sb
// */
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


public class ValidateCallDetails {

    String latestTickDataPath;  
    String updatedCallDataPath;
    String inputPriceDataPath;
    String outputPriceDataPath;
    String statusString;
    
//    String previousCall;
//    CsvTickData recordDataLast = new CsvTickData();
//    List<RecordCallPrice> callListInput = new ArrayList<>();
//    RecordCallPrice prevCallUpdated = new RecordCallPrice();;
//    int ob;

    public ValidateCallDetails(String latestTickDataPath, String updatedCallDataPath,
            String inputPriceDataPath, String outputPriceDataPath) {
        
        this.latestTickDataPath = latestTickDataPath;
        this.updatedCallDataPath=updatedCallDataPath;
        this.inputPriceDataPath=inputPriceDataPath;
        this.outputPriceDataPath=outputPriceDataPath;
        processInput();
    }
//
    private void processInput() {
        File directory = new File(latestTickDataPath);
        List listFileArray = Arrays.asList(directory.list());
        Collections.sort(listFileArray); //directories are sorted as per their name
        int dirCount = listFileArray.size();
        
        String scripFolderPath = "";
        String[] delimitedString;
        List<RecordCallPrice> updatedCalls;
        List<RecordCallPrice> updatedPrice;
        List<RecordCallPrice> inputPriceList = new ArrayList<>();
        
        CsvTickData lastCsvTickData;
//        String callOrPrice = "price";
//        String scripId = "none";
        String callOrPrice;
        String scripId;
//        inputPriceList = readCSVCallList(inputPriceDataPath, callOrPrice, scripId);
        
        for (int i = 0; i < dirCount; i++) {
            scripFolderPath = latestTickDataPath.concat(listFileArray.get(i).toString());
            scripFolderPath = scripFolderPath.concat("/");
            File fileListPerScrip = new File(scripFolderPath);
            File[] arrayPerScrip = fileListPerScrip.listFiles();
            int fileCount = arrayPerScrip.length;
            Arrays.sort(arrayPerScrip, LASTMODIFIED_COMPARATOR);

            String scripLast = arrayPerScrip[fileCount - 1].getAbsolutePath();
            delimitedString = scripFolderPath.split("/");
            scripId = delimitedString[6];//to be fixed
            
            updatedCalls = new ArrayList<>();
            callOrPrice = "call";
            updatedCalls = readCSVCallList(updatedCallDataPath, callOrPrice, scripId);

            updatedPrice = new ArrayList<>();
            callOrPrice = "price";
            updatedPrice = readCSVCallList(inputPriceDataPath, callOrPrice, scripId);
            
            String lastCallOne = updatedCalls.get(updatedCalls.size() - 1).getLastCallVersionOne();
            String lastCallTwo = updatedCalls.get(updatedCalls.size() - 1).getLastCallVersionTwo();
            Double lastCallPrice = updatedCalls.get(updatedCalls.size() - 1).getPrice();
            List<String> lastCalls = new ArrayList<>();
            lastCalls = validateLastSellCall(updatedPrice, lastCallOne, 
                    lastCallTwo, lastCallPrice);

            lastCsvTickData = new CsvTickData();
//            lastCsvTickData = readLastTickerData(scripLast,
//                    updatedCalls.get(updatedCalls.size() - 2).getLastUpdateTime());
            MasterDataServices masterDataService = new MasterDataServices();
            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                Date lastupdateDate = targetFormat.parse(updatedCalls.get(updatedCalls.size() - 2).getLastUpdateTime());
                lastCsvTickData = masterDataService.getLatestDataScripID(scripId, lastupdateDate);
            } catch (ParseException ex) {
                System.out.println(ex + " has occurred for ParseException.");
            }
            
            
            String tally = "";
            tally = fillTally(updatedPrice.get(updatedPrice.size() - 1).getLastCallVersionOne(), lastCsvTickData.getTickData(),
                    updatedPrice.get(updatedPrice.size() - 1).getPrice());
            updatedPrice.get(updatedPrice.size() - 1).setTallyVersionOne(tally);

            tally = "";
            tally = fillTally(updatedPrice.get(updatedPrice.size() - 1).getLastCallVersionTwo(), lastCsvTickData.getTickData(),
                    updatedPrice.get(updatedPrice.size() - 1).getPrice());
            updatedPrice.get(updatedPrice.size() - 1).setTallyVersionTwo(tally);

            updatedPrice.add(updatedCalls.get(updatedCalls.size()-1));
            if(lastCalls.get(0).equals("no-sell")){
               updatedPrice.get(updatedPrice.size()-1).setLastCallVersionOne("no-sell");
               updatedPrice.get(updatedPrice.size()-1).setTallyVersionOne("");
            }
            if(lastCalls.get(1).equals("no-sell")){
               updatedPrice.get(updatedPrice.size()-1).setLastCallVersionTwo("no-sell");
               updatedPrice.get(updatedPrice.size()-1).setTallyVersionTwo("");
            }
            
            inputPriceList.addAll(updatedPrice);

        }
        
        String priceHeading = "EQ,Date,Price,CallOne,CallTwo,TallyOne,TallyTwo,RetraceOne,RetraceTwo,"
                + "PriceGSTOne,PriceGSTTwo";
        Collections.sort(inputPriceList , new SortCallList());
        PrintMatrix printMatrix = new PrintMatrix();
        String typeToPrint = "price";
        printMatrix.printResultData(inputPriceList, outputPriceDataPath, 
                priceHeading,typeToPrint);
        statusString = "done";      
   
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
    
    private String fillTally(String resultTallyData, List<List<Double>> dataNext, Double lastData) {
        double threshold = (0.5 / 100) * lastData;
        String tally = "";
        if (resultTallyData.equals("buy")) {
            for (int ii = 0; ii < dataNext.size(); ii++) {
//                System.out.println(dataNext.get(ii).get(1));
                if (dataNext.get(ii).get(1) < lastData) {
                    tally = "success";
                    break;
                }
            }
            if (tally.equals("")) {
                for (int ii = 0; ii < dataNext.size(); ii++) {
                    if (dataNext.get(ii).get(1) < (lastData + threshold)) {
                        tally = "success";
                        break;
                    } else {
                        tally = "failure";
                        System.out.println(dataNext.get(ii).get(1));
                    }
                }
            }
        }
        if (resultTallyData.equals("sell")) {
            for (int ii = 0; ii < dataNext.size(); ii++) {
                if (dataNext.get(ii).get(1) > lastData) {
                    tally = "success";
                    break;
                }
            }
            if (tally.equals("")) {
                for (int ii = 0; ii < dataNext.size(); ii++) {
                    if (dataNext.get(ii).get(1) > (lastData - threshold)) {
                        tally = "success";
                        break;
                    } else {
                        tally = "failure";
                        System.out.println(dataNext.get(ii).get(1));
                    }
                }
            }
        }
        if (resultTallyData.equals("no-sell")) {
            tally = "";
        }
        return tally;
    }
    
    private List<RecordCallPrice> readCSVCallList(String csvPath, String callOrPrice, String scripID) {
        String line;
        int flag = 0;
        List<RecordCallPrice> recordList = new ArrayList<>();
        RecordCallPrice record = new RecordCallPrice();
        try {
            BufferedReader brPrev = new BufferedReader(new FileReader(csvPath));
            line = brPrev.readLine();
            flag = 1;
            while ((line = brPrev.readLine()) != null) {
                // use comma as separator  
                String[] fields = line.split(",");
                if (fields[0].equals(scripID)) {
                    record.setScripID(fields[0]);

                    DateFormat originalFormat = new SimpleDateFormat("MMM-dd-yyyy", Locale.ENGLISH);
                    DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    try {
                        Date date = originalFormat.parse(fields[1]);
                        String formattedDate = targetFormat.format(date);
                        record.setLastUpdateTime(formattedDate);
                    } catch (ParseException ex) {
                        record.setLastUpdateTime(fields[1]);
                    }
//                    record.setLastUpdateTime(fields[1]);
                    record.setPrice(Double.valueOf(fields[2]));
                    record.setLastCallVersionOne(fields[3]);
                    record.setLastCallVersionTwo(fields[4]);
                    if (callOrPrice.equals("price")) {
                        record.setTallyVersionOne(fields[5]);
                        record.setTallyVersionTwo(fields[6]);
                        record.setRetraceVersionOne(Double.valueOf(fields[7]));
                        record.setRetraceVersionTwo(Double.valueOf(fields[8]));
                        record.setPriceBrokerageGstOne(Double.valueOf(fields[9]));
                        record.setPriceBrokerageGstTwo(Double.valueOf(fields[10]));
                    } else {
                        record.setTallyVersionOne("");
                        record.setTallyVersionTwo("");
                        record.setRetraceVersionOne(Double.valueOf(fields[5]));
                        record.setRetraceVersionTwo(Double.valueOf(fields[6]));
                        record.setPriceBrokerageGstOne(Double.valueOf(fields[7]));
                        record.setPriceBrokerageGstTwo(Double.valueOf(fields[8]));
                    }
                    //fields will now contain all values    
                    recordList.add(record);
                    record = new RecordCallPrice();
                    flag = 2;
                }
                if (flag == 2 && (fields[0].equals(scripID) == false)) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
//                Logger.getLogger(PerMinuteResposeOfNSE.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println("recordTest:" + records.get(records.size() - 1).get(1));
        return recordList;
    }
    
    private List<String> validateLastSellCall(List<RecordCallPrice> lastPriceList, String latestCallOne,
            String latestCallTwo, Double lastCallPrice) {
        List<String> returnValues = new ArrayList<>();
        returnValues.add("");
        returnValues.add("");
        double buyAccumulated = 0.0;
        int buyCount = 0;
        if (latestCallOne.equals("sell")) {
            for (int ii = lastPriceList.size() - 1; ii >= 0; ii--) {
                if (lastPriceList.get(ii).getLastCallVersionOne().equals("buy")) {
                    buyCount = buyCount + 1;
                    buyAccumulated = buyAccumulated
                            + lastPriceList.get(ii).getPriceBrokerageGstOne();
                }
                if (lastPriceList.get(ii).getLastCallVersionOne().equals("sell")) {
                    if (buyCount > 0) {
                        if ((buyAccumulated / buyCount) > lastCallPrice) {
//                            lastPriceList.get(lastPriceList.size() - 1).setLastCallVersionOne("no-sell");
//                            lastPriceList.get(lastPriceList.size() - 1).setTallyVersionOne("");
//                                pricePerScrip.get(pricePerScrip.size() - 1).set(3, "no-sell");
//                                pricePerScrip.get(pricePerScrip.size() - 1).set(5, "");
                            returnValues.set(0,"no-sell");
//                            returnValues.add("");
                        }
                    }
                    break;
                }
                if (ii == 0 && !lastPriceList.get(ii).getLastCallVersionOne().equals("sell")
                        && buyCount > 0) {
                    if ((buyAccumulated / buyCount) > lastCallPrice) {
//                        lastPriceList.get(lastPriceList.size() - 1).setLastCallVersionOne("no-sell");
//                        lastPriceList.get(lastPriceList.size() - 1).setTallyVersionOne("");
//                                pricePerScrip.get(pricePerScrip.size() - 1).set(3, "no-sell");
//                                pricePerScrip.get(pricePerScrip.size() - 1).set(5, "");
                        returnValues.set(0,"no-sell");
//                        returnValues.add("");
                    }
                }
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////         
        buyAccumulated = 0.0;
        buyCount = 0;
        if (latestCallTwo.equals("sell")) {
            for (int ii = lastPriceList.size() - 1; ii >= 0; ii--) {
                if (lastPriceList.get(ii).getLastCallVersionTwo().equals("buy")) {
                    buyCount = buyCount + 1;
                    buyAccumulated = buyAccumulated
                            + lastPriceList.get(ii).getPriceBrokerageGstTwo();
                }
                if (lastPriceList.get(ii).getLastCallVersionTwo().equals("sell")) {
                    if (buyCount > 0) {
                        if ((buyAccumulated / buyCount) > lastCallPrice) {
//                            lastPriceList.get(lastPriceList.size() - 1).setLastCallVersionTwo("no-sell");
//                            lastPriceList.get(lastPriceList.size() - 1).setTallyVersionTwo("");
//                                pricePerScrip.get(pricePerScrip.size() - 1).set(3, "no-sell");
//                                pricePerScrip.get(pricePerScrip.size() - 1).set(5, "");
                            returnValues.set(1,"no-sell");
//                            returnValues.add("");
                        }
                    }
                    break;
                }
                if (ii == 0 && !lastPriceList.get(ii).getLastCallVersionTwo().equals("sell")
                        && buyCount > 0) {
                    if ((buyAccumulated / buyCount) > lastCallPrice) {
//                        lastPriceList.get(lastPriceList.size() - 1).setLastCallVersionTwo("no-sell");
//                        lastPriceList.get(lastPriceList.size() - 1).setTallyVersionTwo("");
//                                pricePerScrip.get(pricePerScrip.size() - 1).set(3, "no-sell");
//                                pricePerScrip.get(pricePerScrip.size() - 1).set(5, "");
                        returnValues.set(1,"no-sell");
//                        returnValues.add("");
                    }
                }
            }
        }
        return returnValues;
//########################################################################
    }
}
