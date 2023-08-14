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
import org.bhaduri.minutedataaccess.entities.Minutedata;
import org.bhaduri.minutedataaccess.services.MasterDataServices;

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
    public void getIntervalData() {
        MasterDataServices masterDataService = new MasterDataServices();
        List<RecordMinute> minuteData = new ArrayList<>();
        List<RecordMinute> minuteDataForInterval = new ArrayList<>();
        RecordMinute record = new RecordMinute();
        long diffSecond = 0;
        long diffInMillies = 0;
        
        Date firstDate = null;
        Date secondDate = null;
        
        List<String> scripIDList = new ArrayList<>();
        scripIDList = masterDataService.readScripData();
        
        for (int i = 0; i < scripIDList.size(); i++) {
            minuteData = masterDataService.getMindataForScripid(scripIDList.get(i));
            minuteDataForInterval.add(minuteData.get(0)); //first record for a particular scripid
            firstDate = minuteData.get(0).getLastUpdateTime();
            for (int k = 1; k < minuteData.size(); k++) {
                secondDate = minuteData.get(k).getLastUpdateTime();
                diffInMillies = Math.abs(firstDate.getTime() - secondDate.getTime());
//                diffSecond = diffSecond + TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                diffSecond = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (diffSecond > inputInterval) {
                    minuteDataForInterval.add(minuteData.get(k));
//                    record = new RecordMinute();
                    diffSecond = 0;
                    firstDate = secondDate;
                    System.out.println("Inside loop firstDate="+firstDate);
                    System.out.println("Inside loop secondDate="+secondDate);
                }
            }
            if (minuteDataForInterval.get(minuteDataForInterval.size() - 1).getLastUpdateTime()
                    != secondDate) {
                if (diffSecond > (inputInterval / 2)) {
                    minuteDataForInterval.add(minuteData.get(minuteData.size() - 1));
                }
            }
            diffSecond = 0;//reset diffSecond to 0
            minuteData = new ArrayList<>();
        }
    }
    

    
    public String getCompleteStatus() {
        return statusString;
    }
//    private RecordMinute createMinuteDataRec(String lineFromFile) {
//        RecordMinute record = new RecordMinute();
//        String[] fields = null;
//        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        fields = lineFromFile.split(",");
//        record.setScripID(fields[0]);
//        try {
//            record.setLastUpdateTime(targetFormat.parse(fields[1]));
//        } catch (ParseException ex) {
//            ex.printStackTrace();
//        }
//        record.setOpenprice(Double.valueOf(fields[2]));
//        record.setDaylastprice(Double.valueOf(fields[3]));
//        record.setDayhighprice(Double.valueOf(fields[4]));
//        record.setDaylowprice(Double.valueOf(fields[5]));
//        record.setPrevcloseprice(Double.valueOf(fields[6]));
//        record.setTotaltradedvolume(Double.valueOf(fields[7]));
//
//        return record;
//    }
    
}
