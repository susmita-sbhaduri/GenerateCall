/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.getminutedata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bhaduri.datatransfer.DTO.RecordMinute;

/**
 *
 * @author sb
 */
public class MinuteDataForInterval {
    Double inputInterval;
    String intraDayMinDataFile;
    String statusString;
    
    public MinuteDataForInterval(Double inputInterval, String intraDayMinDataFile) {        
        this.inputInterval = inputInterval;
        this.intraDayMinDataFile = intraDayMinDataFile;
        getIntervalData();
    }
//    public void getIntervalData() {
//        MasterDataServices masterDataService = new MasterDataServices();
//        List<RecordMinute> minuteData = new ArrayList<>();
//        List<RecordMinute> minuteDataForInterval = new ArrayList<>();
//        RecordMinute record = new RecordMinute();
//        minuteDataForInterval = masterDataService.getAllMindata();
//        for (int k = 0; k < minuteDataForInterval.size(); k++) {
//            record.setScripID(minuteData.get(k).getScripID());
//            record.setLastUpdateTime(minuteData.get(k).getLastUpdateTime());
//            record.setOpenprice(minuteData.get(k).getOpenprice());
//            record.setDaylastprice(minuteData.get(k).getDaylastprice());
//            record.setDayhighprice(minuteData.get(k).getDayhighprice());
//            record.setDaylowprice(minuteData.get(k).getDaylowprice());
//            record.setPrevcloseprice(minuteData.get(k).getPrevcloseprice());
//            record.setTotaltradedvolume(minuteData.get(k).getTotaltradedvolume());
//            minuteData.add(record);
//            record = new RecordMinute();
//        }
//        long diffSecond =0;
//        long diffInMillies =0;
//        minuteDataForInterval.add(minuteData.get(0)); //store the first record
//        Date firstDate;
//        Date secondDate;
//        
//        
//        for (int k = 0; k < minuteDataForInterval.size()-1; k++) {
//            firstDate = minuteData.get(k+1).getLastUpdateTime();
//            secondDate = minuteData.get(k).getLastUpdateTime();
//            diffInMillies = Math.abs(firstDate.getTime()-secondDate.getTime());
//            diffSecond = diffSecond+TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
//            if(diffSecond>inputInterval){
//                minuteDataForInterval.add(minuteData.get(k+1));
//                diffSecond = 0;
//            }
//        }
//        if(minuteDataForInterval.get(minuteDataForInterval.size()-1).getLastUpdateTime()!=
//                minuteData.get(minuteData.size()-1).getLastUpdateTime()){
//            if(diffSecond > (inputInterval/2)){
//                minuteDataForInterval.add(minuteData.get(minuteData.size()-1));                
//            }           
//            
//        }
//        
//    }
    
    public void getIntervalData() {
        List<RecordMinute> minuteDataForInterval = new ArrayList<>();
        RecordMinute record = new RecordMinute();
        String firstLine;
        String secondLine = null;
        String[] fields = null;
        int k =0;
        long diffSecond = 0;
        long diffInMillies = 0;
        
        Date firstDate = null;
        Date secondDate = null;

        try {
            BufferedReader brMinute = new BufferedReader(new FileReader(intraDayMinDataFile));
            firstLine = brMinute.readLine();
            firstLine = brMinute.readLine();
            k = k + 1;
            if (k == 1) {
                record = createMinuteDataRec(firstLine);//first record of minutedata
                minuteDataForInterval.add(record);
                record = new RecordMinute();
            }
            fields = firstLine.split(",");
            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                firstDate = targetFormat.parse(fields[1]);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            while (true) {               

                secondLine = brMinute.readLine();
                if (secondLine == null) {
                    break;
                } else {
                    k = k + 1;
                    fields = secondLine.split(",");
                    try {
                        secondDate = targetFormat.parse(fields[1]);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    if (secondDate != null && firstDate != null) {
                        diffInMillies = Math.abs(firstDate.getTime() - secondDate.getTime());
                        diffSecond = diffSecond + TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        if (diffSecond > inputInterval) {
                            record = createMinuteDataRec(secondLine);
                            minuteDataForInterval.add(record);
                            record = new RecordMinute();
                            diffSecond = 0;
                            firstDate = secondDate;
                        }
                    }
                }
            }//end while
            if (secondLine == null
                    && secondDate != minuteDataForInterval.get(minuteDataForInterval.size() - 1).getLastUpdateTime()) {
                if (diffSecond > (inputInterval / 2)) {
                    record = new RecordMinute();
                    record = createMinuteDataRec(secondLine);
                    minuteDataForInterval.add(record);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getCompleteStatus() {
        return statusString;
    }
    private RecordMinute createMinuteDataRec(String lineFromFile) {
        RecordMinute record = new RecordMinute();
        String[] fields = null;
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        fields = lineFromFile.split(",");
        record.setScripID(fields[0]);
        try {
            record.setLastUpdateTime(targetFormat.parse(fields[1]));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        record.setOpenprice(Double.valueOf(fields[2]));
        record.setDaylastprice(Double.valueOf(fields[3]));
        record.setDayhighprice(Double.valueOf(fields[4]));
        record.setDaylowprice(Double.valueOf(fields[5]));
        record.setPrevcloseprice(Double.valueOf(fields[6]));
        record.setTotaltradedvolume(Double.valueOf(fields[7]));

        return record;
    }
    
}
