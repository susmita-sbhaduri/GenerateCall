/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sb
 */
public class PrintMatrix {
//    private List<List<Double>> dataToPrint;
//    private String printFile;
//    private List<ResultData> resultDatas;
//
//    public PrintMatrix(List<List<Double>> dataToPrint, String printFile) {
//        this.dataToPrint = dataToPrint;
//        this.printFile = printFile;
//    }


    
    public void saveToFile(List<List<Double>> dataToPrint, String printFile) {
        try {
            File output = new File(printFile);
            output.createNewFile();
            PrintStream write = new PrintStream(output);
            for (List<Double> list : dataToPrint) {
                for (Double iPrint : list) {
                    write.print(iPrint);
                    write.print(",");
                }
                write.println();
            }
            write.close();
        } catch (IOException ex) {
            Logger.getLogger(PrintMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void saveResultData(List<ResultData> dataToPrint, String printFile) {
        try {
            File output = new File(printFile);
            output.createNewFile();
            PrintStream write = new PrintStream(output);
            for (int i = 0; i < dataToPrint.size(); i++) {                
                write.print(dataToPrint.get(i).getScripID()+","+dataToPrint.get(i).getLastUpdateTime()
                +","+dataToPrint.get(i).getPrice().toString()
                +","+dataToPrint.get(i).getLastCallVersionOne()
                +","+dataToPrint.get(i).getLastCallVersionTwo()
                +","+dataToPrint.get(i).getTallyVersionOne()
                +","+dataToPrint.get(i).getTallyVersionTwo()
                +","+dataToPrint.get(i).getRetraceVersionOne().toString()
                +","+dataToPrint.get(i).getRetraceVersionTwo().toString()
                +","+dataToPrint.get(i).getPriceBrokerageGstOne().toString()
                +","+dataToPrint.get(i).getPriceBrokerageGstTwo().toString());
                write.println();
            }
            write.close();
        } catch (IOException ex) {
            Logger.getLogger(PrintMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void saveListData(List<List<String>> dataToPrint, String printFile) {
        try {
            File output = new File(printFile);
            output.createNewFile();
            PrintStream write = new PrintStream(output);
            for (int i = 0; i < dataToPrint.size(); i++) {                
                write.print(dataToPrint.get(i).get(0)
                +","+dataToPrint.get(i).get(1)
                +","+dataToPrint.get(i).get(2)
                +","+dataToPrint.get(i).get(3)
                +","+dataToPrint.get(i).get(4)
                +","+dataToPrint.get(i).get(5)
                +","+dataToPrint.get(i).get(6)
                +","+dataToPrint.get(i).get(7)
                +","+dataToPrint.get(i).get(8)
                +","+dataToPrint.get(i).get(9)
                +","+dataToPrint.get(i).get(10));
                write.println();
            }
            write.close();
        } catch (IOException ex) {
            Logger.getLogger(PrintMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
