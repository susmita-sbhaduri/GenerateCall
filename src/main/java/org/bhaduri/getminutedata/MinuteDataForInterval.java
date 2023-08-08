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
        List<RecordMinute> minuteData = new ArrayList<>();
        List<RecordMinute> minuteDataForInterval = new ArrayList<>();
        RecordMinute record = new RecordMinute();
        String line;
        String[] fields = null;
        long diffSecond = 0;
        long diffInMillies = 0;
        minuteDataForInterval.add(minuteData.get(0)); //store the first record
        Date firstDate = null;
        Date secondDate = null;

        try {
            BufferedReader brMinute = new BufferedReader(new FileReader(intraDayMinDataFile));
            line = brMinute.readLine();

            while (true) {
                line = brMinute.readLine();
                if (line == null) {
                    break;
                } else {
                    fields = line.split(",");
                    DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    try {
                        firstDate = targetFormat.parse(fields[1]);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    line = brMinute.readLine();
                    if (line == null) {
                        break;
                    } else {
                        fields = line.split(",");
                        try {
                            secondDate = targetFormat.parse(fields[1]);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        if (secondDate != null && firstDate != null) {
                            diffInMillies = Math.abs(firstDate.getTime() - secondDate.getTime());
                            diffSecond = diffSecond + TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                            if (diffSecond > inputInterval) {
                                record.setScripID(fields[0]);
                                record.setLastUpdateTime(secondDate);
                                record.setOpenprice(Double.valueOf(fields[2]));
                                record.setDaylastprice(Double.valueOf(fields[3]));
                                record.setDayhighprice(Double.valueOf(fields[4]));
                                record.setDaylowprice(Double.valueOf(fields[5]));
                                record.setPrevcloseprice(Double.valueOf(fields[6]));
                                record.setTotaltradedvolume(Double.valueOf(fields[7]));
                                minuteDataForInterval.add(record);
                                record = new RecordMinute();
                                diffSecond = 0;
                            }
                        }
                        
                    }

                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    public String getCompleteStatus() {
        return statusString;
    }
    
}
