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
import java.util.stream.Collectors;

public class GenerateCallDetails {

    public void getFileList() {
        String fullDataPath = "/home/sb/Documents/java_testing/EQ_test/";
        String nifty50Path = "/home/sb/Documents/java_testing/EQ_test_data/";
        String callDataPath = "/home/sb/Documents/java_testing/call12thmay_matlab.csv";
        String priceDataPath = "/home/sb/Documents/java_testing/price12thmay_matlab.csv";

        File directory = new File(nifty50Path);
//        String[] fileArray = directory.list();
        List listFileArray = Arrays.asList(directory.list());
        Collections.sort(listFileArray);
        int dirCount = listFileArray.size();
        ////////for extracting lastupdate date
//        System.out.println("listFileArray[1]:" + fullDataPath.concat(listFileArray.get(0).toString()).concat("/"));
        String firstScripFolder = fullDataPath.concat(listFileArray.get(0).toString()).concat("/");
        File fileListFirstScrip = new File(firstScripFolder);
        File[] arrayFirstScrip = fileListFirstScrip.listFiles();
        Arrays.sort(arrayFirstScrip, LASTMODIFIED_COMPARATOR);
        String firstScripPrev = arrayFirstScrip[arrayFirstScrip.length - 2].getAbsolutePath();
        String[] delimitedString = firstScripPrev.split("_"); // to be fixed as per the path

        List<List<String>> recordCalls = new ArrayList<>();
        recordCalls = readCSV(callDataPath);
        List<List<String>> recordCallUpdated = new ArrayList<>();
        List<List<String>> recordPrice = new ArrayList<>();

        for (int ii = 0; ii < recordCalls.size(); ii++) {
            if (!recordCalls.get(ii).get(1).equals(delimitedString[3])) {
                recordCallUpdated.add(recordCalls.get(ii));
            }
        }
        recordPrice = readCSV(priceDataPath);
        List<List<String>> pricePerScrip;
        List<List<String>> printUpdatedList = new ArrayList<>();
        List<List<String>> priceHeading = new ArrayList<>();
        List<String> innerList = new ArrayList<>();

        innerList.add("EQ");
        innerList.add("Date");
        innerList.add("Price");
        innerList.add("CallOne");
        innerList.add("CallTwo");
        innerList.add("TallyOne");
        innerList.add("TallyTwo");
        innerList.add("RetraceOne");
        innerList.add("RetraceTwo");
        innerList.add("PriceGSTOne");
        innerList.add("PriceGSTTwo");
        priceHeading.add(innerList);
        ////////for extracting lastupdate date
        String scripFolderPath = "";
        List<ResultData> resultDatas = new ArrayList<ResultData>();
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
            pricePerScrip = new ArrayList<>();
            for (int ii = 0; ii < recordPrice.size(); ii++) {
                if (recordPrice.get(ii).get(0).equals(listFileArray.get(i))) {
                    pricePerScrip.add(recordPrice.get(ii));
                }
            }
//            priceHeading.addAll(pricePerScrip);
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
/////////// Update existing call list 
            List<String> callToAdd = updateCallsFile(resultDatas.get(resultDatas.size() - 2)); //previous day's call record 
            recordCallUpdated.add(callToAdd);
/////////// Update existing call list
///////////Update price list
//            List<String> priceCallToAdd = new ArrayList<>();
//            priceCallToAdd=callToAdd;
            String noSellCallOne = pricePerScrip.get(pricePerScrip.size() - 1).get(3);
            String noSellCallTwo = pricePerScrip.get(pricePerScrip.size() - 1).get(4);
//            pricePerScrip.set(pricePerScrip.size() - 1, priceCallToAdd);

//            pricePerScrip.get(pricePerScrip.size() - 1).set(0, callToAdd.get(0));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(1, callToAdd.get(1));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(2, callToAdd.get(2));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(3, callToAdd.get(3));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(4, callToAdd.get(4));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(5, callToAdd.get(5));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(6, callToAdd.get(6));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(7, callToAdd.get(7));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(8, callToAdd.get(8));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(9, callToAdd.get(9));
//            pricePerScrip.get(pricePerScrip.size() - 1).set(10, callToAdd.get(10));
            

            if (noSellCallOne.equals("no-sell")) {
                pricePerScrip.get(pricePerScrip.size() - 1).set(3, "no-sell");
                pricePerScrip.get(pricePerScrip.size() - 1).set(5, "");
            }else{
                pricePerScrip.get(pricePerScrip.size() - 1).set(5, callToAdd.get(5));
            }
            if (noSellCallTwo.equals("no-sell")) {
                pricePerScrip.get(pricePerScrip.size() - 1).set(4, "no-sell");
                pricePerScrip.get(pricePerScrip.size() - 1).set(6, "");
            }else{
                pricePerScrip.get(pricePerScrip.size() - 1).set(6, callToAdd.get(6));
            }
///////////Update price list
/////////// Update existing call list
//            callToAdd = new ArrayList<>();
            callToAdd = updateCallsFile(resultDatas.get(resultDatas.size() - 1)); //latest call record
            recordCallUpdated.add(callToAdd);
/////////// Update existing call list
///////////Update price list
            List<String> priceCallToAdd = new ArrayList<>();
            
            priceCallToAdd.add(0, callToAdd.get(0));
            priceCallToAdd.add(1, callToAdd.get(1));
            priceCallToAdd.add(2, callToAdd.get(2));
            priceCallToAdd.add(3, callToAdd.get(3));
            priceCallToAdd.add(4, callToAdd.get(4));
            priceCallToAdd.add(5, callToAdd.get(5));
            priceCallToAdd.add(6, callToAdd.get(6));
            priceCallToAdd.add(7, callToAdd.get(7));
            priceCallToAdd.add(8, callToAdd.get(8));
            priceCallToAdd.add(9, callToAdd.get(9));
            priceCallToAdd.add(10, callToAdd.get(10));
            pricePerScrip.add(priceCallToAdd);
 ///////////Update price list
//          call list ends here
/////////////////////////////////////////////////////////////////////////////////////////////////////////

            double buyAccumulated = 0.0;
            int buyCount = 0;
            if (resultDatas.get(resultDatas.size() - 1).getLastCallVersionOne().equals("sell")) {
                for (int ii = pricePerScrip.size() - 2; ii > 0; ii--) {
//                    if (pricePerScrip.get(ii).get(0).equals(listFileArray.get(i))) {
                    if (pricePerScrip.get(ii).get(3).equals("buy")) {
                        buyCount = buyCount + 1;
                        buyAccumulated = buyAccumulated
                                + Double.parseDouble(pricePerScrip.get(ii).get(9));
                    }
                    if (pricePerScrip.get(ii).get(3).equals("sell")) {
                        if (buyCount > 0) {
//                        selling price should be more than last buy price+0.5% commision+GST
                            if ((buyAccumulated / buyCount) > resultDatas.get(resultDatas.size() - 1).getPrice()) {
                                resultDatas.get(resultDatas.size() - 1).setLastCallOneUpdated("no-sell");
                                resultDatas.get(resultDatas.size() - 1).setTallyOneUpdated("");
                                pricePerScrip.get(pricePerScrip.size() - 1).set(3, "no-sell");
                                pricePerScrip.get(pricePerScrip.size() - 1).set(5, "");
                            }
                        }
                        break;
                    }
                    if (ii == 1 && !pricePerScrip.get(ii).get(3).equals("sell")
                            && buyCount > 0) {
                        if ((buyAccumulated / buyCount) > resultDatas.get(resultDatas.size() - 1).getPrice()) {
                            resultDatas.get(resultDatas.size() - 1).setLastCallOneUpdated("no-sell");
                            resultDatas.get(resultDatas.size() - 1).setTallyOneUpdated("");
                            pricePerScrip.get(pricePerScrip.size() - 1).set(3, "no-sell");
                            pricePerScrip.get(pricePerScrip.size() - 1).set(5, "");
                        }
                    }
//                    }
                }
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////         
            buyAccumulated = 0.0;
            buyCount = 0;
            if (resultDatas.get(resultDatas.size() - 1).getLastCallVersionTwo().equals("sell")) {
                for (int ii = pricePerScrip.size() - 2; ii > 0; ii--) {
//                    if (pricePerScrip.get(ii).get(0).equals(listFileArray.get(i))) {
                    if (pricePerScrip.get(ii).get(4).equals("buy")) {
                        buyCount = buyCount + 1;
                        buyAccumulated = buyAccumulated
                                + Double.parseDouble(pricePerScrip.get(ii).get(10));
                    }
                    if (pricePerScrip.get(ii).get(4).equals("sell")) {
                        if (buyCount > 0) {
//                        selling price should be more than last buy price+0.5% commision+GST
                            if ((buyAccumulated / buyCount) > resultDatas.get(resultDatas.size() - 1).getPrice()) {
                                resultDatas.get(resultDatas.size() - 1).setLastCallTwoUpdated("no-sell");
                                resultDatas.get(resultDatas.size() - 1).setTallyVersionTwo("");
                                pricePerScrip.get(pricePerScrip.size() - 1).set(4, "no-sell");
                                pricePerScrip.get(pricePerScrip.size() - 1).set(6, "");
                            }
                        }
                        break;
                    }
                    if (ii == 1 && !pricePerScrip.get(ii).get(4).equals("sell")
                            && buyCount > 0) {
                        if ((buyAccumulated / buyCount) > resultDatas.get(resultDatas.size() - 1).getPrice()) {
                            resultDatas.get(resultDatas.size() - 1).setLastCallTwoUpdated("no-sell");
                            resultDatas.get(resultDatas.size() - 1).setTallyVersionTwo("");
                            pricePerScrip.get(pricePerScrip.size() - 1).set(4, "no-sell");
                            pricePerScrip.get(pricePerScrip.size() - 1).set(6, "");
                        }
                    }
//                    }
                }
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////           
            System.out.println("Done in scrip loop");
            printUpdatedList.addAll(pricePerScrip);
        }

//        List<String> rowToAdd = new ArrayList<>();
//        for (int ii = 0; ii < resultDatas.size(); ii++) {
//            rowToAdd.add(0, resultDatas.get(ii).getScripID());
//            rowToAdd.add(1, resultDatas.get(ii).getLastUpdateTime());
//            rowToAdd.add(2, resultDatas.get(ii).getPrice().toString());
//            rowToAdd.add(3, resultDatas.get(ii).getLastCallVersionOne());
//            rowToAdd.add(4, resultDatas.get(ii).getLastCallVersionTwo());
//            rowToAdd.add(5, resultDatas.get(ii).getTallyVersionOne());
//            rowToAdd.add(6, resultDatas.get(ii).getTallyVersionTwo());
//            rowToAdd.add(7, resultDatas.get(ii).getRetraceVersionOne().toString());
//            rowToAdd.add(8, resultDatas.get(ii).getRetraceVersionTwo().toString());
//            rowToAdd.add(9, resultDatas.get(ii).getPriceBrokerageGstOne().toString());
//            rowToAdd.add(10, resultDatas.get(ii).getPriceBrokerageGstTwo().toString());
//            recordCallUpdated.add(rowToAdd);
//            rowToAdd = new ArrayList<>();
//        }
//        ////////////////////////////////////////////////////////////////////////
//        int indexL = prevIndex(recordPrev, recordLast);
//        System.out.println("indexL:" +  Integer.toString(indexL));
//        https://www.geeksforgeeks.org/arraylist-sublist-method-in-java-with-examples/
//        String printFile = "/home/sb/Documents/java_testing/calls10thmayjava.csv";
        priceHeading.addAll(printUpdatedList);
        
        String printFile = "/home/sb/Documents/java_testing/calls15thmayjava.csv";
        PrintMatrix printMatrix = new PrintMatrix();
        printMatrix.saveListData(recordCallUpdated, printFile);
        
        String priceFile = "/home/sb/Documents/java_testing/price15thmayjava.csv";
        printMatrix = new PrintMatrix();
        printMatrix.saveListData(priceHeading, priceFile);
        
        System.out.println("Done");
        ////////////////////////////////////////////////////////////////////////

    }

    private List<String> updateCallsFile(ResultData dataToAdd) {
        List<String> rowToAdd = new ArrayList<>();
        rowToAdd.add(0, dataToAdd.getScripID());
        rowToAdd.add(1, dataToAdd.getLastUpdateTime());
        rowToAdd.add(2, dataToAdd.getPrice().toString());
        rowToAdd.add(3, dataToAdd.getLastCallVersionOne());
        rowToAdd.add(4, dataToAdd.getLastCallVersionTwo());
        rowToAdd.add(5, dataToAdd.getTallyVersionOne());
        rowToAdd.add(6, dataToAdd.getTallyVersionTwo());
        rowToAdd.add(7, dataToAdd.getRetraceVersionOne().toString());
        rowToAdd.add(8, dataToAdd.getRetraceVersionTwo().toString());
        rowToAdd.add(9, dataToAdd.getPriceBrokerageGstOne().toString());
        rowToAdd.add(10, dataToAdd.getPriceBrokerageGstTwo().toString());

        return rowToAdd;
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
