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

public class GenerateCallDetails {

    public void getFileList() {
        String fullDataPath = "/home/sb/Documents/java_testing/EQ_test/";
        String nifty50Path = "/home/sb/Documents/java_testing/EQ_test_data/";
        File directory = new File(nifty50Path);
//        String[] fileArray = directory.list();
        List listFileArray = Arrays.asList(directory.list());
        Collections.sort(listFileArray);
        int dirCount = listFileArray.size();
//        System.out.println("listFileArray[1]:" + listFileArray.get(0).toString());

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

//            System.out.println("Previous file:" + scripPrev);
//            System.out.println("Previous file:" + scripLast);
            List<List<String>> recordPrev = new ArrayList<>();
            List<List<String>> recordLast = new ArrayList<>();
                        
            recordPrev = readCSV(scripPrev);
            recordLast = readCSV(scripLast);

            
            String[] delimitedString = scripFolderPath.split("/");
            String scripId = delimitedString[6];//to be fixed
            delimitedString = scripPrev.split("_");
            String lastUpdateDate = delimitedString[3];//to be fixed            
            List<List<Double>> recordDataPrev = new ArrayList<>();
            recordDataPrev = readCSVData(recordPrev);
            resultDatas.add(fillResult(recordDataPrev, scripId, lastUpdateDate));
            
            System.out.println(lastUpdateDate);
            
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
            double lastDataFromPrev = recordDataPrev.get(recordDataPrev.size()-1).get(1);
            String tally="";
            
            tally = fillTally(resultDatas.get(resultDatas.size()-2).getLastCallVersionOne(),recordDataLastNext,lastDataFromPrev);
            resultDatas.get(resultDatas.size()-2).setTallyVersionOne(tally);
            tally="";
            tally = fillTally(resultDatas.get(resultDatas.size()-2).getLastCallVersionTwo(),recordDataLastNext,lastDataFromPrev);
            resultDatas.get(resultDatas.size()-2).setTallyVersionTwo(tally);
            
            System.out.println("Done");
        }
//        System.out.println("Done");
//        ////////////////////////////////////////////////////////////////////////
//        int indexL = prevIndex(recordPrev, recordLast);
//        System.out.println("indexL:" +  Integer.toString(indexL));
//        https://www.geeksforgeeks.org/arraylist-sublist-method-in-java-with-examples/
//        String printFile = "/home/sb/b.txt";
//        PrintMatrix printMatrix = new PrintMatrix();
//        printMatrix.saveResultData(resultDatas, printFile);
        ////////////////////////////////////////////////////////////////////////
    }
    
    private String fillTally(String resultTallyData, List<List<Double>> dataNext, Double lastData) {
        double threshold = (0.5 / 100) * lastData;
        String tally ="";
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
        System.out.println("recordTest:" + records.get(records.size() - 1).get(1));
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
        System.out.println("indexLast:" + Integer.toString(indexLast));
        return indexLast;
    }

}
