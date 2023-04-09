/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

/**
 *
 * @author sb
 */
import java.io.IOException;
import static org.apache.commons.io.comparator.LastModifiedFileComparator.*;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GenerateCallDetails {

    public void getFileList() {
        List<List<String>> recordTest = new ArrayList<>();

        String fullDataPath = "/home/sb/Documents/testing/EQ_test/";
        String nifty50Path = "/home/sb/Documents/testing/EQ_test_data/";
        File directory = new File(nifty50Path);
        String[] fileArray = directory.list();
        int dirCount = fileArray.length;
        System.out.println("fTotal number of directories:" + dirCount);

        String scripFolderPath = "";

        for (int i = 0; i < dirCount; i++) {
            scripFolderPath = fullDataPath.concat(fileArray[i]);
            scripFolderPath = scripFolderPath.concat("/");
            File filePerScripList = new File(scripFolderPath);
            File[] arrayPerScrip = filePerScripList.listFiles();
            int fileCount = arrayPerScrip.length;
            Arrays.sort(arrayPerScrip, LASTMODIFIED_COMPARATOR);

            String scripPrev = arrayPerScrip[fileCount - 2].getAbsolutePath();
            String scripLast = arrayPerScrip[fileCount - 1].getAbsolutePath();
            System.out.println("Previous file:" + scripPrev);
            System.out.println("Previous file:" + scripLast);
            recordTest = lastLine(scripPrev, scripLast);
            System.out.println("recordTest:" + recordTest.get(0));

        }
    }

    private List<List<String>> lastLine(String scripPrev, String scripLast) {
        String line;
        List<List<String>> records = new ArrayList<>();
        try {
            BufferedReader brPrev = new BufferedReader(new FileReader(scripPrev));

            while ((line = brPrev.readLine()) != null) {
                // use comma as separator  
                String[] fields = line.split(",");
                //fields will now contain all values    
                records.add(Arrays.asList(fields));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
//                Logger.getLogger(PerMinuteResposeOfNSE.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("recordTest:" + records.get(records.size()-1).get(1));
        return records;
    }

}
