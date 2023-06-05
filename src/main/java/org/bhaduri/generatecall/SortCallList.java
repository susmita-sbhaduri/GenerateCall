/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sb
 */
public class SortCallList implements Comparator<RecordCallPrice> {
 @Override
    public int compare(RecordCallPrice r1, RecordCallPrice r2) {
//        return r1.getScripID().compareTo(r2.getScripID());
        int sDate = 0; 
        int sComp = 0;
        sComp = r1.getScripID().compareTo(r2.getScripID());
              
        if (sComp == 0) {
         try {
             SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//             SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
             sDate = formatter.parse(r1.getLastUpdateTime()).compareTo(formatter.parse(r2.getLastUpdateTime()));
         } catch (ParseException ex) {
             Logger.getLogger(SortCallList.class.getName()).log(Level.SEVERE, null, ex);
         }
        } else {
         return sComp;
        }
        return sDate;
    }
}
