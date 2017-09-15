package com.netcracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

class OutputSchemaWriter {
    private Logger logger = LoggerFactory.getLogger(OutputSchemaWriter.class);
    private String schema = "schemes/";

    void writeJsonSchema(String schemaName, String fullJ) {

        File schemaFolder = new File(schema);
        if (!schemaFolder.exists()) {
            schemaFolder.mkdir();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(schemaFolder + "/" + schemaName + ".txt"))) {
            logger.info("Starting writing file " + schemaName + ".txt");
            bufferedWriter.write(fullJ);
            bufferedWriter.flush();

        } catch (FileNotFoundException e) {
            logger.error("Path not found", e);
        } catch (IOException e) {
            logger.error("Error while writing file", e);
        }
        logger.info("Completed");
    }
}
