/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.util.List;

/**
 *
 * @author sb
 */
public class CsvTickData {
    private List<List<Double>> tickData;
    private String dateTime;

    public List<List<Double>> getTickData() {
        return tickData;
    }

    public void setTickData(List<List<Double>> tickData) {
        this.tickData = tickData;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    
    
}
