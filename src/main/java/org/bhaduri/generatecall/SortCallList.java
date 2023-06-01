/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        int sComp = r1.getScripID().compareTo(r2.getScripID());
        if (sComp != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
            try {
                return formatter.parse(r1.getLastUpdateTime()).compareTo(formatter.parse(r2.getLastUpdateTime()));
//            String oldPattern = "MMM-dd-yyyy";
//            String newPattern = "yyyy-MM-dd HH:mm:ss.SSS";
//            int dateIntr1 = isValidDate(r1.getLastUpdateTime(), oldPattern);
//            int dateIntr2 = 0;
//            if (dateIntr1 == 0) {
//                dateIntr1 = isValidDate(r1.getLastUpdateTime(), newPattern);
//            }
//            if (dateIntr1 == 1) {
//                dateIntr2 = isValidDate(r2.getLastUpdateTime(), oldPattern);
//            }
//            if (dateIntr2 == 0) {
//                dateIntr2 = isValidDate(r2.getLastUpdateTime(), newPattern);
//            }
//            if ((dateIntr1 == 1) && (dateIntr2 == 1)) {
//                try {
//                    Date dater1 = new SimpleDateFormat(newPattern).parse(r1.getLastUpdateTime());
//                    Date dater2 = new SimpleDateFormat(newPattern).parse(r2.getLastUpdateTime());
//                    return dater1.compareTo(dater2);
//                } catch (ParseException ex) {
//                    Logger.getLogger(SortCallList.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
            } catch (ParseException ex) {
                Logger.getLogger(SortCallList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sComp;
    }

    public int isValidDate(String inDate, String pattern) {
        System.out.println("beforevalidation"+inDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return 0;
        }
        System.out.println("aftervalidation"+inDate);
        return 1;
        
    }
}
