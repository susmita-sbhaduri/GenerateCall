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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import org.bhaduri.datatransfer.DTO.*;
import org.bhaduri.minutedataaccess.services.MasterDataServices;

public class GenerateCallDetails1 {

    public void getFileList() {
        

        File directory = new File(DataStoreNames.TICKER_DATA_DETAILS);
        List listFileArray = Arrays.asList(directory.list());
        Collections.sort(listFileArray); //directories are sorted as per their name
        int dirCount = listFileArray.size();
        String titleExist;
        List<RecordCallPrice> recordCalls = new ArrayList<>();
        titleExist = "yes";
        recordCalls = readCSVCallList(DataStoreNames.INPUT_CALL_DATA_PATH, titleExist);
        titleExist = "no";

        String scripFolderPath = "";
        String[] delimitedString;
        List<ResultData> resultDatas = new ArrayList<ResultData>(); // call list for the last and 
//        previous days file using elliot curve algo

        for (int i = 0; i < dirCount; i++) {
            scripFolderPath = DataStoreNames.TICKER_DATA_DETAILS.concat(listFileArray.get(i).toString());
            scripFolderPath = scripFolderPath.concat("/");
            File fileListPerScrip = new File(scripFolderPath);
            File[] arrayPerScrip = fileListPerScrip.listFiles();
            int fileCount = arrayPerScrip.length;
            Arrays.sort(arrayPerScrip, LASTMODIFIED_COMPARATOR);

            String scripLast = arrayPerScrip[fileCount - 1].getAbsolutePath();

            System.out.println("scripid" + listFileArray.get(i));

            delimitedString = scripFolderPath.split("/");
            String scripId = delimitedString[6];//to be fixed

            CsvTickData recordDataLast = new CsvTickData();
//            recordDataLast = readCSVData(scripLast);
            
            MasterDataServices masterDataService = new MasterDataServices();
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

            recordCalls.add(callToAdd);

/////////// Update existing call list for today's call
            System.out.println("Done in scrip loop");
        }


        String priceHeading = "EQ,Date,Price,CallOne,CallTwo,RetraceOne,RetraceTwo,"
                + "PriceGSTOne,PriceGSTTwo";

        String printFile = DataStoreNames.OUTPUT_CALL_DATA_PATH;
        String callOrPrice = "call";
        Collections.sort(recordCalls , new SortCallList());
        PrintMatrix printMatrix = new PrintMatrix();
        printMatrix.printResultData(recordCalls, printFile, priceHeading, callOrPrice);
        System.out.println("Done");
        ////////////////////////////////////////////////////////////////////////

    }

//    

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
//                record.setLastUpdateTime(fields[1]);
                DateFormat originalFormat = new SimpleDateFormat("MMM-dd-yyyy", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                try {
                    Date date = originalFormat.parse(fields[1]);
                    String formattedDate = targetFormat.format(date);
                    record.setLastUpdateTime(formattedDate);
                } catch (ParseException ex) {
                    record.setLastUpdateTime(fields[1]);
                }
                record.setPrice(Double.valueOf(fields[2]));
                record.setLastCallVersionOne(fields[3]);
                record.setLastCallVersionTwo(fields[4]);
//                record.setTallyVersionOne(fields[5]);
//                record.setTallyVersionTwo(fields[6]);
                record.setRetraceVersionOne(Double.valueOf(fields[5]));
                record.setRetraceVersionTwo(Double.valueOf(fields[6]));
                record.setPriceBrokerageGstOne(Double.valueOf(fields[7]));
                record.setPriceBrokerageGstTwo(Double.valueOf(fields[8]));

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

    private CsvTickData readCSVData(String csvPath) {
        CsvTickData retCsvTickData = new CsvTickData();
        String line;
        double index = 1;
        List<List<Double>> recordData = new ArrayList<>();
        List<Double> row = new ArrayList<>();
        String[] fields = null;
        try {
            BufferedReader brPrev = new BufferedReader(new FileReader(csvPath));
            line = brPrev.readLine();
            while ((line = brPrev.readLine()) != null) {
                row = new ArrayList<>();  
                fields = line.split(",");
                row.add(index);
                row.add(Double.valueOf(fields[3]));
                
                recordData.add(row);                
                index = index+1;
            }
            retCsvTickData.setTickData(recordData);  
            retCsvTickData.setDateTime(fields[1]);
        } catch (IOException ex) {
            ex.printStackTrace();
//                Logger.getLogger(PerMinuteResposeOfNSE.class.getName()).log(Level.SEVERE, null, ex);
        }       
        return retCsvTickData;
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
