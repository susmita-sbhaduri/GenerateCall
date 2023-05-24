/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

/**
 *
 * @author sb
 */
import java.io.IOException;
import static org.apache.commons.io.comparator.LastModifiedFileComparator.*;
import java.io.File;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class GenerateCallDetails1 {

    public void getFileList() {
        String fullDataPath = "/home/sb/Documents/java_testing/EQ_test/";
        String nifty50Path = "/home/sb/Documents/java_testing/EQ_test_data/";
        String callDataPath = "/home/sb/Documents/java_testing/calls23thmayjava.csv";
        String priceDataPath = "/home/sb/Documents/java_testing/price23thmayjava.csv";

        File directory = new File(nifty50Path);
        List listFileArray = Arrays.asList(directory.list());
        Collections.sort(listFileArray); //directories are sorted as per their name
        int dirCount = listFileArray.size();
        ////////for extracting lastupdate date
//        System.out.println("listFileArray[1]:" + fullDataPath.concat(listFileArray.get(0).toString()).concat("/"));
        String firstScripFolder = fullDataPath.concat(listFileArray.get(0).toString()).concat("/");
        File fileListFirstScrip = new File(firstScripFolder);
        File[] arrayFirstScrip = fileListFirstScrip.listFiles();
        Arrays.sort(arrayFirstScrip, LASTMODIFIED_COMPARATOR);
        String firstScripPrev = arrayFirstScrip[arrayFirstScrip.length - 2].getAbsolutePath();
        String[] delimitedString = firstScripPrev.split("_"); // to be fixed as per the path
         ////////for extracting lastupdate date from the file of previous day for the first scrip id in EQ_test/
         ////////folder here its "ADANIENT"        
        String titleExist;
        List<RecordCallPrice> recordCalls = new ArrayList<>();
        titleExist="yes";
        recordCalls = readCSVCallList(callDataPath, titleExist); 
        titleExist="no";
        
        List<RecordCallPrice> recordCallUpdated = new ArrayList<>();//input call list
        List<RecordCallPrice> recordPrice = new ArrayList<>(); //input price validation list
/****************input call list is read into recordCallUpdated except the last record */
        for (int ii = 0; ii < recordCalls.size(); ii++) {
            System.out.println("recordCalls.get(ii).getLastUpdateTime()" + 
                    recordCalls.get(ii).getLastUpdateTime());
            if (!recordCalls.get(ii).getLastUpdateTime().equals(delimitedString[3])) {
                recordCallUpdated.add(recordCalls.get(ii));
            }
        }
/****************input price list is read into recordPrice  */
        titleExist="yes";
        recordPrice = readCSVCallList(priceDataPath, titleExist);
        titleExist="no";
        List<RecordCallPrice> pricePerScrip; //each record of validated price list
        List<RecordCallPrice> printUpdatedList = new ArrayList<>();//updated validated price list
        
/****************input price list is read into recordPrice  */
       
        String scripFolderPath = "";
        List<ResultData> resultDatas = new ArrayList<ResultData>(); // call list for the last and 
//        previous days file using elliot curve algo
        
        for (int i = 0; i < dirCount; i++) {
            scripFolderPath = fullDataPath.concat(listFileArray.get(i).toString());
            scripFolderPath = scripFolderPath.concat("/");
            File fileListPerScrip = new File(scripFolderPath);
            File[] arrayPerScrip = fileListPerScrip.listFiles();
            int fileCount = arrayPerScrip.length;
            Arrays.sort(arrayPerScrip, LASTMODIFIED_COMPARATOR);

            String scripPrev = arrayPerScrip[fileCount - 2].getAbsolutePath();
            String scripLast = arrayPerScrip[fileCount - 1].getAbsolutePath();

            System.out.println("scripid" + listFileArray.get(i));
// ***********           price data is extracted for for current scripid 
            pricePerScrip = new ArrayList<>();
            
            for (int ii = 0; ii < recordPrice.size(); ii++) {
                if (recordPrice.get(ii).getScripID().equals(listFileArray.get(i))) {
                    pricePerScrip.add(recordPrice.get(ii));
                }
            }
//            System.out.println("Previous file:" + scripPrev);
//            System.out.println("Previous file:" + scripLast);
            List<List<String>> recordPrev = new ArrayList<>();
            List<List<String>> recordLast = new ArrayList<>();

            recordPrev = readCSV(scripPrev);
            recordLast = readCSV(scripLast);

            delimitedString = scripFolderPath.split("/");
            String scripId = delimitedString[6];//to be fixed
            delimitedString = scripPrev.split("_");
            String lastUpdateDate = delimitedString[3];//to be fixed            
            List<List<Double>> recordDataPrev = new ArrayList<>();
            recordDataPrev = readCSVData(recordPrev);
            resultDatas.add(fillResult(recordDataPrev, scripId, lastUpdateDate));

            List<List<Double>> recordDataLast = new ArrayList<>();
            recordDataLast = readCSVData(recordLast);
            delimitedString = scripLast.split("_");
            lastUpdateDate = delimitedString[3];//to be fixed
            resultDatas.add(fillResult(recordDataLast, scripId, lastUpdateDate));

            List<List<String>> recordLastNext = new ArrayList<>();
            int indexL = prevIndex(recordPrev, recordLast);
            List<List<Double>> recordDataLastNext = new ArrayList<>();

            List<Double> row = new ArrayList<>();
            for (int ii = indexL; ii < recordDataLast.size(); ii++) {
                row.add(0, recordDataLast.get(ii).get(0));
                row.add(1, recordDataLast.get(ii).get(1));
                recordDataLastNext.add(row);
                row = new ArrayList<>();
            }
            double lastDataFromPrev = recordDataPrev.get(recordDataPrev.size() - 1).get(1);
            String tally = "";

            tally = fillTally(resultDatas.get(resultDatas.size() - 2).getLastCallVersionOne(), recordDataLastNext, lastDataFromPrev);
            resultDatas.get(resultDatas.size() - 2).setTallyVersionOne(tally);
            tally = "";
            tally = fillTally(resultDatas.get(resultDatas.size() - 2).getLastCallVersionTwo(), recordDataLastNext, lastDataFromPrev);
            resultDatas.get(resultDatas.size() - 2).setTallyVersionTwo(tally);
/////////// Update existing call list for the previous days call
            RecordCallPrice callToAdd = new RecordCallPrice(); //previous day's call record 
            callToAdd.setScripID(resultDatas.get(resultDatas.size() - 2).getScripID());
            callToAdd.setLastUpdateTime(resultDatas.get(resultDatas.size() - 2).getLastUpdateTime());
            callToAdd.setPrice(resultDatas.get(resultDatas.size() - 2).getPrice());
            callToAdd.setLastCallVersionOne(resultDatas.get(resultDatas.size() - 2).getLastCallVersionOne());
            callToAdd.setLastCallVersionTwo(resultDatas.get(resultDatas.size() - 2).getLastCallVersionTwo());
            callToAdd.setTallyVersionOne(resultDatas.get(resultDatas.size() - 2).getTallyVersionOne());
            callToAdd.setTallyVersionTwo(resultDatas.get(resultDatas.size() - 2).getTallyVersionTwo());
            callToAdd.setRetraceVersionOne(resultDatas.get(resultDatas.size() - 2).getRetraceVersionOne());
            callToAdd.setRetraceVersionTwo(resultDatas.get(resultDatas.size() - 2).getRetraceVersionTwo());
            callToAdd.setPriceBrokerageGstOne(resultDatas.get(resultDatas.size() - 2).getPriceBrokerageGstOne());
            callToAdd.setPriceBrokerageGstTwo(resultDatas.get(resultDatas.size() - 2).getPriceBrokerageGstTwo());
            recordCallUpdated.add(callToAdd);
/////////// Update existing call list
///////////Update price list for the previous days call
            String noSellCallOne = pricePerScrip.get(pricePerScrip.size() - 1).getLastCallVersionOne();
            String noSellCallTwo = pricePerScrip.get(pricePerScrip.size() - 1).getLastCallVersionTwo();            

            if (noSellCallOne.equals("no-sell")) {
                pricePerScrip.get(pricePerScrip.size() - 1).setLastCallVersionOne( "no-sell");
                pricePerScrip.get(pricePerScrip.size() - 1).setTallyVersionOne("");
            }else{
                pricePerScrip.get(pricePerScrip.size() - 1).setTallyVersionOne( callToAdd.getTallyVersionOne());
            }
            if (noSellCallTwo.equals("no-sell")) {
                pricePerScrip.get(pricePerScrip.size() - 1).setLastCallVersionTwo( "no-sell");
                pricePerScrip.get(pricePerScrip.size() - 1).setTallyVersionTwo( "");
            }else{
                pricePerScrip.get(pricePerScrip.size() - 1).setTallyVersionTwo(callToAdd.getTallyVersionTwo());
            }
///////////Update price list for the previous days call
/////////// Update existing call list for today's call
            callToAdd = new RecordCallPrice();
             //latest call record
            callToAdd.setScripID(resultDatas.get(resultDatas.size() - 1).getScripID());
            callToAdd.setLastUpdateTime(resultDatas.get(resultDatas.size() - 1).getLastUpdateTime());
            callToAdd.setPrice(resultDatas.get(resultDatas.size() - 1).getPrice());
            callToAdd.setLastCallVersionOne(resultDatas.get(resultDatas.size() - 1).getLastCallVersionOne());
            callToAdd.setLastCallVersionTwo(resultDatas.get(resultDatas.size() - 1).getLastCallVersionTwo());
            callToAdd.setTallyVersionOne(resultDatas.get(resultDatas.size() - 1).getTallyVersionOne());
            callToAdd.setTallyVersionTwo(resultDatas.get(resultDatas.size() - 1).getTallyVersionTwo());
            callToAdd.setRetraceVersionOne(resultDatas.get(resultDatas.size() - 1).getRetraceVersionOne());
            callToAdd.setRetraceVersionTwo(resultDatas.get(resultDatas.size() - 1).getRetraceVersionTwo());
            callToAdd.setPriceBrokerageGstOne(resultDatas.get(resultDatas.size() - 1).getPriceBrokerageGstOne());
            callToAdd.setPriceBrokerageGstTwo(resultDatas.get(resultDatas.size() - 1).getPriceBrokerageGstTwo());
            
            recordCallUpdated.add(callToAdd);
            
/////////// Update existing call list for today's call
///////////Update price list for today's call
            RecordCallPrice priceCallToAdd = new RecordCallPrice();            
            pricePerScrip.add(callToAdd);
 ///////////Update price list for today's call
 //########################################################################
//          call list ends here
//########################################################################
/*latest call is taken from  resultDatas and if it's "sell" it's checked from previous buys for each 
scripid stored in pricePerScrip first for version 1 call next for version 2 call*/
//########################################################################
            double buyAccumulated = 0.0;
            int buyCount = 0;
            if (resultDatas.get(resultDatas.size() - 1).getLastCallVersionOne().equals("sell")) {
                for (int ii = pricePerScrip.size() - 2; ii > 0; ii--) {
                    if (pricePerScrip.get(ii).getLastCallVersionOne().equals("buy")) {
                        buyCount = buyCount + 1;
                        buyAccumulated = buyAccumulated
                                + pricePerScrip.get(ii).getPriceBrokerageGstOne();
                    }
                    if (pricePerScrip.get(ii).getLastCallVersionOne().equals("sell")) {
                        if (buyCount > 0) {
                            if ((buyAccumulated / buyCount) > resultDatas.get(resultDatas.size() - 1).getPrice()) {
                                resultDatas.get(resultDatas.size() - 1).setLastCallOneUpdated("no-sell");
                                resultDatas.get(resultDatas.size() - 1).setTallyOneUpdated("");
                                pricePerScrip.get(pricePerScrip.size() - 1).setLastCallVersionOne("no-sell");
                                pricePerScrip.get(pricePerScrip.size() - 1).setTallyVersionOne("");
                            }
                        }
                        break;
                    }
                    if (ii == 1 && !pricePerScrip.get(ii).getLastCallVersionOne().equals("sell")
                            && buyCount > 0) {
                        if ((buyAccumulated / buyCount) > resultDatas.get(resultDatas.size() - 1).getPrice()) {
                            resultDatas.get(resultDatas.size() - 1).setLastCallOneUpdated("no-sell");
                            resultDatas.get(resultDatas.size() - 1).setTallyOneUpdated("");
                            pricePerScrip.get(pricePerScrip.size() - 1).setLastCallVersionOne("no-sell");
                            pricePerScrip.get(pricePerScrip.size() - 1).setTallyVersionOne("");
                        }
                    }
                }
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////         
            buyAccumulated = 0.0;
            buyCount = 0;
            if (resultDatas.get(resultDatas.size() - 1).getLastCallVersionTwo().equals("sell")) {
                for (int ii = pricePerScrip.size() - 2; ii > 0; ii--) {
                    if (pricePerScrip.get(ii).getLastCallVersionTwo().equals("buy")) {
                        buyCount = buyCount + 1;
                        buyAccumulated = buyAccumulated
                                + pricePerScrip.get(ii).getPriceBrokerageGstTwo();
                    }
                    if (pricePerScrip.get(ii).getLastCallVersionTwo().equals("sell")) {
                        if (buyCount > 0) {
//                        selling price should be more than last buy price+0.5% commision+GST
                            if ((buyAccumulated / buyCount) > resultDatas.get(resultDatas.size() - 1).getPrice()) {
                                resultDatas.get(resultDatas.size() - 1).setLastCallTwoUpdated("no-sell");
                                resultDatas.get(resultDatas.size() - 1).setTallyVersionTwo("");
                                pricePerScrip.get(pricePerScrip.size() - 1).setLastCallVersionTwo("no-sell");
                                pricePerScrip.get(pricePerScrip.size() - 1).setTallyVersionTwo("");
                            }
                        }
                        break;
                    }
                    if (ii == 1 && !pricePerScrip.get(ii).getLastCallVersionTwo().equals("sell")
                            && buyCount > 0) {
                        if ((buyAccumulated / buyCount) > resultDatas.get(resultDatas.size() - 1).getPrice()) {
                            resultDatas.get(resultDatas.size() - 1).setLastCallTwoUpdated("no-sell");
                            resultDatas.get(resultDatas.size() - 1).setTallyVersionTwo("");
                            pricePerScrip.get(pricePerScrip.size() - 1).setLastCallVersionTwo("no-sell");
                            pricePerScrip.get(pricePerScrip.size() - 1).setTallyVersionTwo("");
                        }
                    }
                }
            }
//########################################################################
/*latest call is taken from  resultDatas and if it's "sell" it's checked from previous buys for each 
scripid stored in pricePerScrip first for version 1 call next for version 2 call*/
//########################################################################
            System.out.println("Done in scrip loop");
            printUpdatedList.addAll(pricePerScrip);
        }

//        ////////////////////////////////////////////////////////////////////////
//        int indexL = prevIndex(recordPrev, recordLast);
//        System.out.println("indexL:" +  Integer.toString(indexL));
//        https://www.geeksforgeeks.org/arraylist-sublist-method-in-java-with-examples/
//        String printFile = "/home/sb/Documents/java_testing/calls10thmayjava.csv";
        String priceHeading = "EQ,Date,Price,CallOne,CallTwo,TallyOne,TallyTwo,RetraceOne,RetraceTwo,"
                + "PriceGSTOne,PriceGSTTwo";
                
        String printFile = "/home/sb/Documents/java_testing/calls24thmayjava1.csv";
        PrintMatrix printMatrix = new PrintMatrix();
        printMatrix.printResultData(recordCallUpdated, printFile, priceHeading);
        
        String priceFile = "/home/sb/Documents/java_testing/price24thmayjava1.csv";
        printMatrix = new PrintMatrix();
//        printMatrix.saveListData(printUpdatedList, priceFile,priceHeading);
        printMatrix.printResultData(printUpdatedList, priceFile, priceHeading);
        
        System.out.println("Done");
        ////////////////////////////////////////////////////////////////////////

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
        return tally;
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
    
    private List<RecordCallPrice> readCSVCallList(String csvPath, String titleFlag) {
        String line;        
        List<RecordCallPrice> recordList = new ArrayList<>();
        RecordCallPrice record = new RecordCallPrice();
        try {
            BufferedReader brPrev = new BufferedReader(new FileReader(csvPath));
            if (titleFlag.equals("yes")) {
                line = brPrev.readLine();
            }
            while ((line = brPrev.readLine()) != null) {
                // use comma as separator  
                String[] fields = line.split(",");
                record.setScripID(fields[0]);
                record.setLastUpdateTime(fields[1]);
                record.setPrice(Double.valueOf(fields[2]));
                record.setLastCallVersionOne(fields[3]);
                record.setLastCallVersionTwo(fields[4]);
                record.setTallyVersionOne(fields[5]);
                record.setTallyVersionTwo(fields[6]);
                record.setRetraceVersionOne(Double.valueOf(fields[7]));
                record.setRetraceVersionTwo(Double.valueOf(fields[8]));
                record.setPriceBrokerageGstOne(Double.valueOf(fields[9]));
                record.setPriceBrokerageGstTwo(Double.valueOf(fields[10]));
                
                //fields will now contain all values    
                recordList.add(record);
                record = new RecordCallPrice();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
//                Logger.getLogger(PerMinuteResposeOfNSE.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println("recordTest:" + records.get(records.size() - 1).get(1));
        return recordList;
    }
    

    private List<List<Double>> readCSVData(List<List<String>> csvStringData) {
        List<List<Double>> recordData = new ArrayList<>();
        List<Double> row = new ArrayList<>();
        for (int ii = 1; ii < csvStringData.size(); ii++) {
            row.add(Double.valueOf(ii));
            row.add(Double.valueOf(csvStringData.get(ii).get(3)));
            recordData.add(row);
            row = new ArrayList<>();
        }
        return recordData;
    }

    private ResultData fillResult(List<List<Double>> recordData, String scripId, String lastUpdateDate) {

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
