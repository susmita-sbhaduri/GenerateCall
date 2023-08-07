/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.getminutedata;

import static org.bhaduri.generatecall.DataStoreNames.*;
import org.bhaduri.validatecall.ValidateCallDetails;

/**
 *
 * @author sb
 */
public class GetMinuteData {
    public static void main(String[] args) {

        MinuteDataForInterval minuteDataForInterval= new MinuteDataForInterval(DATA_DURATION_SEC);
        System.out.println(minuteDataForInterval.getCompleteStatus());

    }
    
}
