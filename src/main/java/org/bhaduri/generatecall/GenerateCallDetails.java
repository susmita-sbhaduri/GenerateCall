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
        String[] fileArray = directory.list();
        List listFileArray = Arrays.asList(fileArray);
        Collections.sort(listFileArray);
        int dirCount = fileArray.length;
        System.out.println("listFileArray[1]:" + listFileArray.get(0));

        String scripFolderPath = "";

        for (int i = 0; i < dirCount; i++) {
            scripFolderPath = fullDataPath.concat(listFileArray.get(i));
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
//            System.out.println("First value:" + recordPrev.get(1).get(3));

//            List<List<Double>> recordPrevData = new ArrayList<>();
//            List<Double> row = new ArrayList<>();
//            for (int ii = 1; ii < recordPrev.size(); ii++) {
//                row.add(Double.valueOf(ii));
//                row.add(Double.valueOf(recordPrev.get(ii).get(3)));
//                recordPrevData.add(row); 
//                row = new ArrayList<>();
//            }
//            int callCount = 3;
//            Smoothing smoothing = new Smoothing(recordPrevData, callCount);
//            List<SmoothData> smoothData = new ArrayList<SmoothData>();  
//            smoothData = smoothing.genCall();
// filling up resultant data            
            List<ResultData> resultDatas = new ArrayList<ResultData>();
//            ResultData eachResultData = new ResultData();
//            Double thresHold;
//            thresHold=(0.5/100)*(recordPrevData.get(recordPrevData.size()-1).get(1))+
//                    ((0.5/100)*recordPrevData.get(recordPrevData.size()-1).get(1))*18/100;
            String[] delimitedString = scripFolderPath.split("/");
            String scripId = delimitedString[6];//to be fixed
            delimitedString = scripPrev.split("_");
            String lastUpdateDate = delimitedString[3];//to be fixed
//            eachResultData.setPrice(recordPrevData.get(recordPrevData.size()-1).get(1));
//            eachResultData.setLastCallVersionOne(smoothData.get(2).getCallArrayOne());
//            eachResultData.setLastCallVersionTwo(smoothData.get(2).getCallArrayTwo());
//            eachResultData.setTally("");
//            eachResultData.setRetraceVersionOne(smoothData.get(2).getRetraceOne());
//            eachResultData.setRetraceVersionTwo(smoothData.get(2).getRetraceTwo());
//            
//            if(smoothData.get(2).getCallArrayOne().equals("buy")){
//                eachResultData.setPriceBrokerageGstOne(recordPrevData.get(recordPrevData.size()-1).get(1)+thresHold);
//            }
//            if(smoothData.get(2).getCallArrayOne().equals("sell")){
//                eachResultData.setPriceBrokerageGstOne(recordPrevData.get(recordPrevData.size()-1).get(1)+thresHold);
//            }
//            if(smoothData.get(2).getCallArrayTwo().equals("buy")){
//                eachResultData.setPriceBrokerageGstTwo(recordPrevData.get(recordPrevData.size()-1).get(1)+thresHold);
//            }
//            if(smoothData.get(2).getCallArrayTwo().equals("sell")){
//                eachResultData.setPriceBrokerageGstTwo(recordPrevData.get(recordPrevData.size()-1).get(1)+thresHold);
//            }
            resultDatas.add(fillResult(recordPrev, scripId, lastUpdateDate));
            delimitedString = scripLast.split("_");
            lastUpdateDate = delimitedString[3];//to be fixed
            resultDatas.add(fillResult(recordLast, scripId, lastUpdateDate));
            System.out.println("Done for :" + scripId);
//            eachResultData = new ResultData();
//            int indexL = prevIndex(recordPrev, recordLast);
//            System.out.println("indexL:" +  Integer.toString(indexL));
//            https://www.geeksforgeeks.org/arraylist-sublist-method-in-java-with-examples/

        }
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
    
    private ResultData fillResult(List<List<String>> recordStringData, String scripId, String lastUpdateDate) {
        List<List<Double>> recordData = new ArrayList<>();
        List<Double> row = new ArrayList<>();
        for (int ii = 1; ii < recordStringData.size(); ii++) {
            row.add(Double.valueOf(ii));
            row.add(Double.valueOf(recordStringData.get(ii).get(3)));
            recordData.add(row);
            row = new ArrayList<>();
        }
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
        eachResultData.setTally("");
        eachResultData.setRetraceVersionOne(smoothData.get(2).getRetraceOne());
        eachResultData.setRetraceVersionTwo(smoothData.get(2).getRetraceTwo());

        if (smoothData.get(2).getCallArrayOne().equals("buy")) {
            eachResultData.setPriceBrokerageGstOne(recordData.get(recordData.size() - 1).get(1) + thresHold);
        }
        if (smoothData.get(2).getCallArrayOne().equals("sell")) {
            eachResultData.setPriceBrokerageGstOne(recordData.get(recordData.size() - 1).get(1) - thresHold);
        }
        if (smoothData.get(2).getCallArrayTwo().equals("buy")) {
            eachResultData.setPriceBrokerageGstTwo(recordData.get(recordData.size() - 1).get(1) + thresHold);
        }
        if (smoothData.get(2).getCallArrayTwo().equals("sell")) {
            eachResultData.setPriceBrokerageGstTwo(recordData.get(recordData.size() - 1).get(1) - thresHold);
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
