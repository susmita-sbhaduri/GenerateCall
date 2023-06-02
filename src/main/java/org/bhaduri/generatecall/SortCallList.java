/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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
        LocalDate localDateR1 = LocalDate.parse(r1.getLastUpdateTime());
        LocalDate localDateR2 = LocalDate.parse(r2.getLastUpdateTime());
        System.out.println(localDateR1);
        System.out.println(localDateR2);
        LocalDateTime localDateTimeR1 = localDateR1.atStartOfDay();
        LocalDateTime localDateTimeR2 = localDateR2.atStartOfDay();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        
//        SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        sDate = localDateTimeR1.format(dtf).compareTo(localDateTimeR2.format(dtf));
        
        if (sDate != 0) {
            return r1.getScripID().compareTo(r2.getScripID());
        } else {
            return sDate;
        }
    }
}

