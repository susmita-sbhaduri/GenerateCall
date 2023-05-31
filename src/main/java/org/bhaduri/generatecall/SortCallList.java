/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sb
 */
public class SortCallList implements Comparator<RecordCallPrice>{
    @Override
    public int compare(RecordCallPrice r1, RecordCallPrice r2) {
        int sComp = r1.getScripID().compareTo(r2.getScripID());
        String oldPattern = "MMM-dd-yyyy";
        String newPattern = "yyyy-MM-dd HH:mm:ss.SSS";
        int dateIntr1 = isValidDate(r1.getLastUpdateTime(),oldPattern);
        int dateIntr2;
        if(dateIntr1==0){
            dateIntr1 = isValidDate(r1.getLastUpdateTime(),newPattern);
        }
        if(dateIntr1==1){
          dateIntr2 = isValidDate(r2.getLastUpdateTime(),oldPattern); 
        }
        if(dateIntr2==0){
          dateIntr2 = isValidDate(r2.getLastUpdateTime(),newPattern); 
        }
        if((dateIntr1==1)&&(dateIntr2==1)){
            
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(newPattern);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(r1.getLastUpdateTime());
        } catch (ParseException ex) {
            return false
        }
        
        if (sComp != 0) {
               return sComp;
        }
        else{
            return 0;
        }

    }
    public int isValidDate(String inDate, String pattern) {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return 0;
        }
        return 1;
    }
}
