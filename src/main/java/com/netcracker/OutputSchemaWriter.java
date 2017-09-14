package com.netcracker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class OutputSchemaWriter {
    void writeJsonSchema(String schemaName, String fullJ) {

        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(schemaName + ".txt");
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(fullJ);
            bufferedWriter.flush();
            fileWriter.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
