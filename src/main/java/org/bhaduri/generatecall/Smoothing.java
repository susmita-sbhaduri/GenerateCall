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
public class Smoothing {

    private List<List<Double>> scripData;
    private int callCount;

    public Smoothing(List<List<Double>> scripData, int callCount) {
        this.scripData = scripData;
        this.callCount = callCount;
    }

    public List<SmoothData> genCall() {
        List<List<Double>> smoothInput = new ArrayList<>();
        smoothInput = scripData;
        List<SmoothData> smoothResultData = new ArrayList<SmoothData>(); 
        SmoothData eachSmoothData = new SmoothData();
//        String printFile = "/home/sb/b.txt";
//        PrintMatrix printMatrix = new PrintMatrix(smoothLvl1, printFile);
//        printMatrix.saveToFile();
        CallData callInputData;
        CallData callOutputData;
        for (int i = 0; i < callCount; i++) {
            smoothInput = processInput(smoothInput);
            //
            String printFile = "/home/sb/b.txt";
            PrintMatrix printMatrix = new PrintMatrix(smoothInput, printFile);
            printMatrix.saveToFile();
            //
            callInputData = new CallData();
            callInputData.setCallCount(i + 1);
            callInputData.setInputSmoothedData(smoothInput);
            callInputData.setMarginValue(0.5);
            CallCreate callCreate = new CallCreate(callInputData);
            callOutputData = new CallData();
            callOutputData = callCreate.callGen(callInputData);

            eachSmoothData.setCallArrayOne(callOutputData.getLastCallVersionOne());
            eachSmoothData.setCallArrayTwo(callOutputData.getLastCallVersionTwo());
            eachSmoothData.setRetraceOne(callOutputData.getRetraceVersionOne());
            eachSmoothData.setRetraceTwo(callOutputData.getRetraceVersionTwo());
            smoothResultData.add(eachSmoothData);

            eachSmoothData = new SmoothData();
            smoothInput = new ArrayList<>();
            smoothInput = callOutputData.getOutputSmoothedData();

        }
        return smoothResultData;
    }

    private List<List<Double>> processInput(List<List<Double>> numericData) {
//        numericData.forEach((List<Double> oneRow) -> oneRow.forEach((Double ele) -> System.out.println(ele)));

        List<List<Double>> smoothData = new ArrayList<>();
        List<Double> diffData = new ArrayList<>();
        for (int i = 0; i < numericData.size() - 1; i++) {
            diffData.add(numericData.get(i + 1).get(1) - numericData.get(i).get(1));
        }
        int k = 0;
        List<Double> rowSmooth = new ArrayList<>();
        rowSmooth.add(numericData.get(k).get(0));
        rowSmooth.add(numericData.get(k).get(1));
        smoothData.add(rowSmooth);
        rowSmooth = new ArrayList<>();

        int flag = 0;

        while (k < diffData.size()) {
//            System.out.println(k);
            if (diffData.get(k) > 0) {
                if (flag == 2) {
                    rowSmooth.add(numericData.get(k).get(0));
                    rowSmooth.add(numericData.get(k).get(1));
                    smoothData.add(rowSmooth);
                    rowSmooth = new ArrayList<>();
                }
                flag = 1;
            }
            if (diffData.get(k) < 0) {
                if (flag == 1) {
                    rowSmooth.add(numericData.get(k).get(0));
                    rowSmooth.add(numericData.get(k).get(1));
                    smoothData.add(rowSmooth);
                    rowSmooth = new ArrayList<>();
                }
                flag = 2;
            }
            k = k + 1;
        }
        rowSmooth.add(numericData.get(k).get(0));
        rowSmooth.add(numericData.get(k).get(1));
        smoothData.add(rowSmooth);

//        System.out.println("recordTest:" + records.get(records.size() - 1).get(1));
        return smoothData;
    }

}
