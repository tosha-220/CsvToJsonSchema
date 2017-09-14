//package com.netcracker;
//
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVRecord;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.util.*;
//
//public class CSVToJsonSchemaGeneration {
//
//    private final String[] HEADERS = {"ID", "parent", "name", "type", "appearance", "value", "description", "required"};
//
//    private final String csvFile = "/test.csv";
//
//    private StringBuilder fullJ = new StringBuilder();
//
//    private String properties;
//    private Map<String, Map<String, Object>> result = new LinkedHashMap<>();
//    Set<String> parentsNames = new TreeSet<>();
//    String schemaw;
//
//    public void parseCSV() {
//
//        Reader reader = new InputStreamReader(CSVToJsonSchemaGeneration.class.getResourceAsStream(csvFile));
//        fullJ.append("{\"$schema\": \"http://json-schema.org/draft-04/schema#\", \"type\": \"object\", \"description\": \"Modify CE Service Reqest\",");
//        try {
//            List<CSVRecord> records = CSVFormat.RFC4180.withHeader(HEADERS).
//                    parse(reader).getRecords();
//
//
//            for (CSVRecord record : records) {
//                Map<String, Object> params = new LinkedHashMap<>();
//                for (int i = 0; i < HEADERS.length; i++) {
//                    if (!record.get(i).equals(""))
//                        params.put(HEADERS[i], record.get(i));
//                }
//                result.put(record.get("name"), params);
//            }
//            generate();
//            String s = fullJ.toString().replaceAll(",}", "}").replaceAll("},}", "}}");
//            if (s.endsWith(",")) {
//                s = s.substring(0, s.length() - 1).concat("}");  ///
//            }
//            System.out.println(result);
//            System.out.println(s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void generate() {
//        Set<String> parentsNames = new HashSet<>();
//        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
//            if (!entry.getValue().containsKey("parent")) {
//                parentsNames.add(entry.getKey());
//            }
//        }
//        fullJ.append("\"properties\":{");
//        for (String name : parentsNames) {
////            if (name.equals("ce_parameters")) {
//
//            fillJson(name);
//            findChildAttributes(name);
//
////            }
//        }
//        fullJ.append("},");
//    }
//
//    public boolean setPropToParentAtrributes(String name) {
//        int beforeLength = parentsNames.size();
//        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
//            for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
//                if (inner.getKey().equals("parent") && inner.getValue().equals(name)) {
//
//                    parentsNames.add(name);
//                }
//            }
//        }
//        if (parentsNames.size() > beforeLength) {
//            if (isArray(name)) {
//                fullJ.append("\"items\":{");
//            } else {
//                fullJ.append("\"properties\":{");
//            }
//            return true;
//        }
//        return false;
//
//    }
//
//    private boolean isArray(String name) {
//        return result.get(name).get("type").equals("array");
//    }
//
//    private void findChildAttributes(String name) {
//        List<String> childs = new ArrayList<>();
//        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
//            for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
//
//                if (inner.getKey().equals("parent") && inner.getValue().equals(name)) {
//                    childs.add(entry.getKey());
//                }
//            }
//        }
////        result.get(name).get("parent").equals()
//
//        int beforeSize = childs.size();
//        if (childs.size() != 0) {
//
//            for (String x : childs) {
//                fillJson(x);
//
//                findChildAttributes(x);
//
////                System.out.println(childs.size() + " " + childs.hashCode());
//                beforeSize--;
//
//                if (beforeSize == 0) {
////                    System.out.println("Name=" + x);
//                    fullJ.append("},");
//                    fullJ.append("},");
//                }
//            }
//        }
//
//    }
//
//    private void fillJson(String name) {
//        Map<String, Object> findingEntity = new LinkedHashMap<>();
//        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
//            for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
//                if (inner.getKey().equals("name") && inner.getValue().equals(name)) {
//                    findingEntity = entry.getValue();
////                    System.out.println(findingEntity);
////                    findChildAtrr((String) findingEntity.get("name"));
//                }
//            }
//        }
////        System.out.println(findingEntity.get("value"));
//        fullJ.append("\"").append(findingEntity.get("name")).append("\":{\"");
//        fullJ.append("type\":\"").append(findingEntity.get("type")).append("\"");
//        separator();
//        if (findingEntity.get("type").equals("integer")) {
//            fullJ.append(findingEntity.get("value"));
//        }
//
//        fullJ.append("\"").append("description").append("\"").append(":").append("\"").
//                append(findingEntity.get("description"));
//
//        if (findingEntity.get("ID") != null) {
//            fullJ.append(" ").append(findingEntity.get("ID"));
//        }
//        fullJ.append("\"");
//        separator();
//
//        String appearance = (String) findingEntity.get("appearance");
//        if (appearance != null) {
//            if (appearance.equals("enum")) {
//                fullJ.append("\"").append("enum\":").append("[");
//                fullJ.append(findingEntity.get("value")).append("]");
//                separator();
//            } else {
//                fullJ.append("\"").append(appearance).append("\"").append(":");
//                fullJ.append("\"").append(findingEntity.get("value")).append("\"");
//                separator();
//            }
//        }
//
//        if (!setPropToParentAtrributes(name))
//            fullJ.append("},");
//    }
//
//    private void separator() {
//        fullJ.append(",");
//    }
//}
//
//
////    public void getSchamaHead(Reader reader) {
////        String line = "";
////
////        try {
////            BufferedReader br = new BufferedReader(reader);
////            while ((line = br.readLine()) != null) {
////                if (line.contains("$schema")) {
////                    String[] split = line.split("\"");
////                    for (int i = 0; i < split.length; i++) {
////                        if (split[i].contains("htt")) {
////                            System.out.println(split[i]);
////                        }
////                    }
////                }
////                if (line.contains("\"type\"") && !line.contains("ID")) {
////                    System.out.println(line);
//////                    String[] split = line.split("\"");
//////                    for (int i = 0; i < split.length; i++) {
//////                        if (!split[i].contains("type") && !split[i].equals("")) {
//////                            System.out.println(split[i]);
//////                        }
//////                    }
////                    Pattern pattern = Pattern.compile("a-zA-Z");
////                    Matcher matcher = pattern.matcher(line);
////                    if (matcher.find())
////                    {
////                        System.out.println(matcher.group(1));
////                    }
////
////                }
////            }
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }