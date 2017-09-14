package com.netcracker;

import java.util.*;

class JsonSchemaGenerator {
    private StringBuilder fullJ = new StringBuilder()
            .append("{\"$schema\": \"http://json-schema.org/draft-04/schema#\", \"type\": \"object\",");
    private Set<String> parentsNames = new HashSet<>();
    private Map<String, Map<String, Object>> allSchemaEntity;
    private Map<String, String> required = new HashMap<>();

    void generate(Map<String, Map<String, Object>> allSchemaEntity, String schemaName) {
        Set<String> parentsNames = new HashSet<>();
        for (Map.Entry<String, Map<String, Object>> entry : allSchemaEntity.entrySet()) {
            if (!entry.getValue().containsKey("parent")) {
                parentsNames.add(entry.getKey());
            }
        }
        if (parentsNames.size() == 0) {
            return;
        }
        this.allSchemaEntity = allSchemaEntity;
        fullJ.append("\"description\": \"").append(schemaName).append("\",");
        fullJ.append("\"properties\":{");
        for (String name : parentsNames) {
            fillJson(name);
//            System.out.println(name);
            findChildAttributes(name);
        }
        bracketSeparator();
        leadToFinalForm();
        System.out.println(required);
    }

    private void leadToFinalForm() {
        String s = fullJ.toString().replaceAll(",}", "}").replaceAll("},}", "}}");
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1).concat("}");  ////////
        }
        System.out.println(s);
    }

    private boolean setPropToParentAttributes(String name) {
        int beforeLength = parentsNames.size();
        for (Map.Entry<String, Map<String, Object>> entry : this.allSchemaEntity.entrySet()) {
            for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
                if (inner.getKey().equals("parent") && inner.getValue().equals(name)) {
                    parentsNames.add(name);
                }
                if (inner.getKey().equals("required") && (inner.getValue().equals("●") || inner.getValue().equals("★"))) {
                    required.put(entry.getKey(), (String) inner.getValue());
                }
            }
        }
        if (parentsNames.size() > beforeLength) {
            if (isArray(name)) {
                fullJ.append("\"items\":{");
            } else {
                fullJ.append("\"properties\":{");
            }
            return true;
        }
        return false;

    }

    private boolean isArray(String name) {
        return this.allSchemaEntity.get(name).get("type").equals("array");
    }

    private void findChildAttributes(String name) {

        List<String> childs = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : this.allSchemaEntity.entrySet()) {
            for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
                if (inner.getKey().equals("parent") && inner.getValue().equals(name)) {
                    childs.add(entry.getKey());
                }
            }
        }

        System.out.println(childs);
        int beforeSize = childs.size();
        if (childs.size() != 0) {
            for (String child : childs) {
                fillJson(child);
                findChildAttributes(child);
                beforeSize--;
                if (beforeSize == 0) {
                    bracketSeparator();
                    bracketSeparator();
                }
            }
        }
    }


    ///////
    private boolean isRequired(String child) {
        for (Map.Entry<String, String> entry : this.required.entrySet()) {
            return entry.getKey().equals(child);
        }
        return false;
    }

    ///////////
    private void fillJson(String name) {
        Map<String, Object> findingEntity = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : this.allSchemaEntity.entrySet()) {
            for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
                if (inner.getKey().equals("name") && inner.getValue().equals(name)) {
                    findingEntity = entry.getValue();
                }
            }
        }

        fullJ.append("\"").append(findingEntity.get("name")).append("\":{\"");
        fullJ.append("type\":\"").append(findingEntity.get("type")).append("\"");
        commaSeparator();
        if (findingEntity.get("type").equals("integer")) {
            fullJ.append(findingEntity.get("value"));
        }

        fullJ.append("\"").append("description").append("\"").append(":").append("\"").
                append(findingEntity.get("description"));

        if (findingEntity.get("ID") != null) {
            fullJ.append(" ").append(findingEntity.get("ID"));
        }
        fullJ.append("\"");
        commaSeparator();

        String appearance = (String) findingEntity.get("appearance");
        if (appearance != null) {
            if (appearance.equals("enum")) {
                fullJ.append("\"").append("enum\":").append("[");
                fullJ.append(findingEntity.get("value")).append("]");
                commaSeparator();
            } else {
                fullJ.append("\"").append(appearance).append("\"").append(":");
                fullJ.append("\"").append(findingEntity.get("value")).append("\"");
                commaSeparator();
            }
        }

        if (!setPropToParentAttributes(name)) {
            bracketSeparator();
        }
    }

    private void commaSeparator() {
        fullJ.append(",");
    }

    private void bracketSeparator() {
        fullJ.append("},");
    }
}
