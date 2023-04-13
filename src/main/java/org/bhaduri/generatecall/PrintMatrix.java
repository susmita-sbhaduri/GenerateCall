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
    private List<List<Double>> dataToPrint;
    private String printFile;

    public PrintMatrix(List<List<Double>> dataToPrint, String printFile) {
        this.dataToPrint = dataToPrint;
        this.printFile = printFile;
    }
   
    public void saveToFile() {
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
}
