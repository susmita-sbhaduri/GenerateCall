/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author sb
 */
public class genCallV01 {

    public String[] genCallV01(List<List<Double>> scripData, int callCount) {
        List<List<Double>> smoothLvl1 = processInput(scripData);
        Path output = Paths.get("output.txt");
//        try {
//            Files.write(output, smoothLvl1.get().get());
//        } 
//        catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("exception:" + e);
//        }
        String[] outPut = new String[1];
        outPut[0] = "done";
        return outPut;
    }

    private List<List<Double>> processInput(List<List<Double>> numericData) {
        List<List<Double>> smoothData = new ArrayList<>();
        List<Double> diffData = new ArrayList<>();
        for (int i = 0; i < numericData.size() - 1; i++) {
            diffData.add(numericData.get(i + 1).get(1) - numericData.get(i).get(1));
        }
        int k = 0;
        int k1 = 0;
        List<Double> rowSmooth = new ArrayList<>();
        rowSmooth.add(numericData.get(k).get(0));
        rowSmooth.add(numericData.get(k).get(1));
        smoothData.add(rowSmooth);
        rowSmooth = new ArrayList<>();

        k1 = k1 + 1;
        int flag = 0;

        while (k <= diffData.size()) {
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
