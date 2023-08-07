/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.getminutedata;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bhaduri.datatransfer.DTO.RecordMinute;
import org.bhaduri.minutedataaccess.services.MasterDataServices;

/**
 *
 * @author sb
 */
public class MinuteDataForInterval {
    Double inputInterval;
    String statusString;
    
    public MinuteDataForInterval(Double inputInterval) {        
        this.inputInterval = inputInterval;
        getIntervalData();
    }
    public void getIntervalData() {
        MasterDataServices masterDataService = new MasterDataServices();
        List<RecordMinute> minuteData = new ArrayList<>();
        List<RecordMinute> minuteDataForInterval = new ArrayList<>();
        RecordMinute record = new RecordMinute();
        minuteDataForInterval = masterDataService.getAllMindata();
        for (int k = 0; k < minuteDataForInterval.size(); k++) {
            record.setScripID(minuteData.get(k).getScripID());
            record.setLastUpdateTime(minuteData.get(k).getLastUpdateTime());
            record.setOpenprice(minuteData.get(k).getOpenprice());
            record.setDaylastprice(minuteData.get(k).getDaylastprice());
            record.setDayhighprice(minuteData.get(k).getDayhighprice());
            record.setDaylowprice(minuteData.get(k).getDaylowprice());
            record.setPrevcloseprice(minuteData.get(k).getPrevcloseprice());
            record.setTotaltradedvolume(minuteData.get(k).getTotaltradedvolume());
            minuteData.add(record);
            record = new RecordMinute();
        }
        long diffSecond =0;
        long diffInMillies =0;
        minuteDataForInterval.add(minuteData.get(0)); //store the first record
        Date firstDate;
        Date secondDate;
        
        
        for (int k = 0; k < minuteDataForInterval.size()-1; k++) {
            firstDate = minuteData.get(k+1).getLastUpdateTime();
            secondDate = minuteData.get(k).getLastUpdateTime();
            diffInMillies = Math.abs(firstDate.getTime()-secondDate.getTime());
            diffSecond = diffSecond+TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if(diffSecond>60){
                minuteDataForInterval.add(minuteData.get(k+1));
                diffSecond = 0;
                continue;
            }
        }
        if(minuteDataForInterval.get(minuteDataForInterval.size()-1).getLastUpdateTime()!=
                minuteData.get(minuteData.size()-1).getLastUpdateTime()){
            if(diffSecond>30){
                minuteDataForInterval.add(minuteData.get(minuteData.size()-1));                
            }           
            
        }
        
    }
    public String getCompleteStatus() {
        return statusString;
    }
    
}
