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
public class CallData {
    private int callCount;
    private List<List<Double>> inputSmoothedData;
    private List<List<Double>> outputSmoothedData; 
    private String lastCallVersionOne;
    private String lastCallVersionTwo;
    private Double retraceVersionOne;
    private Double retraceVersionTwo;   
    private Double marginValue;

    public Double getMarginValue() {
        return marginValue;
    }

    public void setMarginValue(Double marginValue) {
        this.marginValue = marginValue;
    }

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    public List<List<Double>> getInputSmoothedData() {
        return inputSmoothedData;
    }

    public void setInputSmoothedData(List<List<Double>> inputSmoothedData) {
        this.inputSmoothedData = inputSmoothedData;
    }

    public List<List<Double>> getOutputSmoothedData() {
        return outputSmoothedData;
    }

    public void setOutputSmoothedData(List<List<Double>> outputSmoothedData) {
        this.outputSmoothedData = outputSmoothedData;
    }
    public String getLastCallVersionOne() {
        return lastCallVersionOne;
    }

    public void setLastCallVersionOne(String lastCallVersionOne) {
        this.lastCallVersionOne = lastCallVersionOne;
    }

    public String getLastCallVersionTwo() {
        return lastCallVersionTwo;
    }

    public void setLastCallVersionTwo(String lastCallVersionTwo) {
        this.lastCallVersionTwo = lastCallVersionTwo;
    }

    public Double getRetraceVersionOne() {
        return retraceVersionOne;
    }

    public void setRetraceVersionOne(Double retraceVersionOne) {
        this.retraceVersionOne = retraceVersionOne;
    }

    public Double getRetraceVersionTwo() {
        return retraceVersionTwo;
    }

    public void setRetraceVersionTwo(Double retraceVersionTwo) {
        this.retraceVersionTwo = retraceVersionTwo;
    }
    
}
