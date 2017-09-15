package com.netcracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));) {
            System.out.println("Enter path to csv file (e.g. C:\\Users\\user\\Desktop\\test.csv)");
            final String pathToCSV = bufferedReader.readLine();
            File file = new File(pathToCSV);
            if (file.exists()) {
                CSVParser csvParser = new CSVParser();
                csvParser.parseCSV(pathToCSV);
            } else {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            logger.error("File doesn't exist or path to file is incorrect");
        } catch (IOException e) {
            logger.error("BufferReader error");
        }
    }
}