package com.netcracker;

public class Main {

    public static void main(String[] args) {
        final String csvFile = "/test.csv";
        CSVParser csvParser = new CSVParser();
        csvParser.parseCSV(csvFile);
    }
}