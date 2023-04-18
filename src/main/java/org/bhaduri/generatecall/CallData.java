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
    private String lastCall;
    private Double retraceValue;

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

    public String getLastCall() {
        return lastCall;
    }

    public void setLastCall(String lastCall) {
        this.lastCall = lastCall;
    }

    public Double getRetraceValue() {
        return retraceValue;
    }

    public void setRetraceValue(Double retraceValue) {
        this.retraceValue = retraceValue;
    }
}
