package com.netcracker;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class CSVParser {
    private Logger logger = LoggerFactory.getLogger(CSVParser.class);
    private final String[] HEADERS = {"ID", "parent", "name", "type", "appearance", "value", "description", "required", "call"};

    private Map<String, Map<String, Object>> result = new LinkedHashMap<>();

    void parseCSV(String csvFile) {
        logger.info("Starting parsing csv");
        try (Reader reader = new FileReader(csvFile)) {
            List<CSVRecord> records = CSVFormat.RFC4180.withHeader(HEADERS).
                    parse(reader).getRecords();
            if (records.size() == 0) {
                throw new IllegalStateException();
            }
            for (CSVRecord record : records) {
                Map<String, Object> params = new LinkedHashMap<>();
                for (int i = 0; i < HEADERS.length; i++) {
                    if (!record.get(i).equals(""))
                        params.put(HEADERS[i], record.get(i));
                }
                result.put(record.get("name"), params);
            }
            new SchemaSeparator().findSchemaNames(result);
        } catch (IllegalStateException e) {
            logger.error("File " + csvFile + " is empty", e);
        } catch (IOException | IllegalArgumentException e) {
            logger.error("Error while parsing csv", e);
        } catch (NullPointerException e) {
            logger.error("Error opening file " + csvFile, e);
        }
    }
}
