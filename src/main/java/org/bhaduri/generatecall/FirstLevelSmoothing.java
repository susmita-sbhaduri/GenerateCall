/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sb
 */
public class FirstLevelSmoothing {

    private List<List<Double>> scripData;
    private int callCount;

    public FirstLevelSmoothing(List<List<Double>> scripData, int callCount) {
        this.scripData = scripData;
        this.callCount = callCount;
    }

    public String[] genCallV01() {
        List<List<Double>> smoothLvl1 = processInput(scripData);
//        String printFile = "/home/sb/b.txt";
//        PrintMatrix printMatrix = new PrintMatrix(smoothLvl1, printFile);
//        printMatrix.saveToFile();
        CallData callInputData = new CallData();
        callInputData.setCallCount(callCount);
        callInputData.setInputSmoothedData(smoothLvl1);
        
        CallsVersion1 versionOneCall = new CallsVersion1(callInputData);
        CallData callOutputData = new CallData();
        
        
        String[] outPut = new String[1];
        outPut[0] = "done";
        return outPut;
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
