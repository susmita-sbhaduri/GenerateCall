/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sb
 */
public class CallCreate {

    private CallData callData;

    public CallCreate(CallData callData) {
        this.callData = callData;
    }

    public CallData callGen(CallData inputData) {
//        callData.setCallCount(inputData.getCallCount());
//        callData.setInputSmoothedData(inputData.getInputSmoothedData());
        CallData callOutputData = new CallData();
        int loopCounter = 0;
        int loopCount = callData.getCallCount();
        double margin = callData.getMarginValue();
        List<List<Double>> inputSmoothedData = callData.getInputSmoothedData();
        List<List<Double>> outputSmoothedData = new ArrayList<>();
        List<Double> row = new ArrayList<>();
        row.add(inputSmoothedData.get(0).get(0));
        row.add(inputSmoothedData.get(0).get(1));
//      #####################################
        outputSmoothedData.add(row);
//      #####################################
        row = new ArrayList<>();
        int startLevelOne = 0;
        int trendFlag = 0;
        String call = "";

        if (inputSmoothedData.get(1).get(1) > inputSmoothedData.get(2).get(1)) {
            startLevelOne = 3; //starting with second point rising above the first, so 4th element onwards comparison done
            if (inputSmoothedData.get(1).get(1) < inputSmoothedData.get(3).get(1)) {
                // we compare the first higher highs
                trendFlag = 1; //uptrend
            }
            if (inputSmoothedData.get(1).get(1) > inputSmoothedData.get(3).get(1)) {
                // we compare the first higher high than the fourth one
                row.add(inputSmoothedData.get(1).get(0));
                row.add(inputSmoothedData.get(1).get(1));
                //#####################################
                outputSmoothedData.add(row);
                //#####################################
                row = new ArrayList<>();
                call = "sell";
                trendFlag = 2; //downtrend as second element is a sell call
            }
        }

        if (inputSmoothedData.get(1).get(1) < inputSmoothedData.get(2).get(1)) {
            startLevelOne = 2; //starting with second point falling from the first, so 3rd element onwards comparison done
            if (inputSmoothedData.get(0).get(1) < inputSmoothedData.get(2).get(1)) {
                // we compare the first higher highs
                row.add(inputSmoothedData.get(1).get(0));
                row.add(inputSmoothedData.get(1).get(1));
                //#####################################
                outputSmoothedData.add(row);
                //#####################################
                row = new ArrayList<>();
                call = "buy";
                trendFlag = 1; //uptrend as the current call is buy
            }
            if (inputSmoothedData.get(0).get(1) > inputSmoothedData.get(2).get(1)) {
                trendFlag = 2; //downtrend remains
            }
        }

        for (int i = startLevelOne; i < inputSmoothedData.size() - 2; i = i + 2) {
            loopCounter=i;
            if (inputSmoothedData.get(i).get(1) < inputSmoothedData.get(i + 2).get(1)) {
                if (trendFlag == 2) {
                    if (inputSmoothedData.get(i - 1).get(1) < inputSmoothedData.get(i + 1).get(1)) {
                        row.add(inputSmoothedData.get(i - 1).get(0));
                        row.add(inputSmoothedData.get(i - 1).get(1));
                    } else {
                        row.add(inputSmoothedData.get(i + 1).get(0));
                        row.add(inputSmoothedData.get(i + 1).get(1));
                    }
                    call = "buy";
                    outputSmoothedData.add(row);
                    row = new ArrayList<>();
                }
                trendFlag = 1; //uptrend
            }
            if (inputSmoothedData.get(i).get(1) > inputSmoothedData.get(i + 2).get(1)) {
                if (trendFlag == 1) {
                    row.add(inputSmoothedData.get(i).get(0));
                    row.add(inputSmoothedData.get(i).get(1));
                    call = "sell";
                    outputSmoothedData.add(row);
                    row = new ArrayList<>();
                }
                trendFlag = 2; //downtrend
            }
        }
//      #####################################lastcallversion1#########################################
        String lastCallVersionOne = "";
        Double retraceVersionOne = 0.0;
        Double retraceDown = 0.0;
        Double retraceUp = 0.0;
        
        if(call.equals("buy")){
            if((outputSmoothedData.size()-(loopCounter+3))==1){
                retraceDown = inputSmoothedData.get(inputSmoothedData.size()-2).get(1) -
                        inputSmoothedData.get(inputSmoothedData.size()-1).get(1);
                retraceUp = inputSmoothedData.get(inputSmoothedData.size()-2).get(1) -
                        outputSmoothedData.get(outputSmoothedData.size()-1).get(1);
                if(((retraceDown/retraceUp)*100) > 62){
                    lastCallVersionOne="buy";
                } else {
                    if(((retraceDown/retraceUp)*100) < 38.2){
                        lastCallVersionOne="sell";
                    } else{
                        lastCallVersionOne="no";
                        if (loopCount==3){
                            retraceVersionOne= (retraceDown/retraceUp)*100;
                        }
                    }                    
                }
                
            }else{
                lastCallVersionOne="sell";
            }
        }
        if(call.equals("sell")){
            lastCallVersionOne="buy";
        }
        
        
//      ###################################lastcallversiontwo###########################################
        String lastCallVersionTwo = "";
        Double retraceVersionTwo = 0.0;
        Double riseLength = 0.0;
        Double fallLength = 0.0;
        if(call.equals("buy")){
            if((outputSmoothedData.size()-(loopCounter+3))==1){
                retraceDown = inputSmoothedData.get(inputSmoothedData.size()-2).get(1) -
                        inputSmoothedData.get(inputSmoothedData.size()-1).get(1);
                retraceUp = inputSmoothedData.get(inputSmoothedData.size()-2).get(1) -
                        outputSmoothedData.get(outputSmoothedData.size()-1).get(1);
                riseLength = inputSmoothedData.get(inputSmoothedData.size()-1).get(1) -
                        outputSmoothedData.get(outputSmoothedData.size()-1).get(1);
                
                if (riseLength < ((margin / 100) * outputSmoothedData.get(outputSmoothedData.size() - 1).get(1))) {
                    lastCallVersionTwo = "buy";
                } else {
                    if (((retraceDown / retraceUp) * 100) > 62) {
                        lastCallVersionTwo = "buy";
                    } else {
                        if (((retraceDown / retraceUp) * 100) < 38.2) {
                            lastCallVersionTwo = "sell";
                        } else {
                            lastCallVersionTwo = "no";
                            if (loopCount == 3) {
                                retraceVersionTwo = (retraceDown / retraceUp) * 100;
                            }
                        }
                    }
                }
            }else{
                riseLength = inputSmoothedData.get(inputSmoothedData.size()-1).get(1) -
                        outputSmoothedData.get(outputSmoothedData.size()-1).get(1);
                if (riseLength < ((margin / 100) * outputSmoothedData.get(outputSmoothedData.size() - 1).get(1))) {
                    lastCallVersionTwo = "buy";
                }else{
                    lastCallVersionTwo = "sell";
                }
            }
        }
        
        if (call.equals("sell")) {
            fallLength = outputSmoothedData.get(outputSmoothedData.size() - 1).get(1)
                    - inputSmoothedData.get(inputSmoothedData.size() - 1).get(1);
            if (fallLength < ((margin / 100) * outputSmoothedData.get(outputSmoothedData.size() - 1).get(1))) {
                lastCallVersionTwo = "sell";
            } else {
                lastCallVersionTwo = "buy";
            }

        }
  //      ##############################################################################      
        row.add(inputSmoothedData.get(inputSmoothedData.size() - 1).get(0));
        row.add(inputSmoothedData.get(inputSmoothedData.size() - 1).get(1));
        outputSmoothedData.add(row);
        
        callOutputData.setLastCallVersionOne(lastCallVersionOne);
        callOutputData.setRetraceVersionOne(retraceVersionOne);
        callOutputData.setLastCallVersionTwo(lastCallVersionTwo);
        callOutputData.setRetraceVersionTwo(retraceVersionTwo);
        callOutputData.setOutputSmoothedData(outputSmoothedData);        

        return callOutputData;
    }

}
