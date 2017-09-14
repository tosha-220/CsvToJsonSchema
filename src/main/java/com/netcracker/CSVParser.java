package com.netcracker;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class CSVParser {

    private final String[] HEADERS = {"ID", "parent", "name", "type", "appearance", "value", "description", "required", "call"};

    private Map<String, Map<String, Object>> result = new LinkedHashMap<>();

    void parseCSV(String csvFile) {

        Reader reader = new InputStreamReader(CSVParser.class.getResourceAsStream(csvFile));
        try {
            List<CSVRecord> records = CSVFormat.RFC4180.withHeader(HEADERS).
                    parse(reader).getRecords();

            for (CSVRecord record : records) {
                Map<String, Object> params = new LinkedHashMap<>();
                for (int i = 0; i < HEADERS.length; i++) {
                    if (!record.get(i).equals(""))
                        params.put(HEADERS[i], record.get(i));
                }
                result.put(record.get("name"), params);
            }
            new SchemaSeparator().findSchemaNames(result);
//            new JsonSchemaGenerator().generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
