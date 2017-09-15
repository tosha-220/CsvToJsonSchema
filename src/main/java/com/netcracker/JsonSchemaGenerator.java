package com.netcracker;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class JsonSchemaGenerator {

    private Logger logger = LoggerFactory.getLogger(JsonSchemaGenerator.class);
    private StringBuilder fullJ = new StringBuilder()
            .append("{\"$schema\": \"http://json-schema.org/draft-04/schema#\", \"type\": \"object\",");
    private Set<String> parentsNames = new HashSet<>();
    private Map<String, Map<String, Object>> allSchemaEntity;

    void generate(Map<String, Map<String, Object>> allSchemaEntity, String schemaName) {
        Set<String> parentsNames = new HashSet<>();
        for (Map.Entry<String, Map<String, Object>> entry : allSchemaEntity.entrySet()) {
            if (!entry.getValue().containsKey("parent")) {
                parentsNames.add(entry.getKey());
            }
        }
        if (parentsNames.size() == 0) {
            logger.error("CSV for schema " + schemaName + " doesn't have root attributes (without parent)");
            return;
        }
        this.allSchemaEntity = allSchemaEntity;
        fullJ.append("\"description\": \"").append(schemaName).append("\",");
        fullJ.append("\"properties\":{");
        for (String name : parentsNames) {
            fillJson(name);
            findChildAttributes(name);
        }
        bracketSeparator();
        setRequired(new ArrayList<>(parentsNames));
        new OutputSchemaWriter().writeJsonSchema(schemaName, leadToFinalForm());
    }

    private String leadToFinalForm() {
        String fullJ = this.fullJ.toString().replaceAll(",}", "}").replaceAll("},}", "}}").replaceAll(",]", "]");
        if (fullJ.endsWith(",")) {
            fullJ = fullJ.substring(0, fullJ.length() - 1).concat("}");  ////////
        }
        return new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(fullJ));
    }

    private boolean setPropToParentAttributes(String name) {
        int beforeLength = parentsNames.size();
        for (Map.Entry<String, Map<String, Object>> entry : this.allSchemaEntity.entrySet()) {
            for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
                if (inner.getKey().equals("parent") && inner.getValue().equals(name)) {
                    parentsNames.add(name);
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
        int beforeSize = childs.size();
        if (childs.size() != 0) {
            for (String child : childs) {
                fillJson(child);
                findChildAttributes(child);
                beforeSize--;
                if (beforeSize == 0) {
                    bracketSeparator();
                    setRequired(childs);
                    bracketSeparator();
                }
            }
        }
    }

    private void setRequired(List<String> childs) {
        List<String> required = new ArrayList<>();
        for (String name : childs) {
            if (this.allSchemaEntity.get(name).get("required") != null) {
                if (this.allSchemaEntity.get(name).get("required").equals("●") || this.allSchemaEntity.get(name).get("required").equals("★")) {
                    required.add(name);
                }
            }
        }
        if (required.size() != 0) {
            fullJ.append("\"").append("required").append("\"").append(":").append("[");
            for (String name : required) {
                fullJ.append("\"").append(name).append("\"").append(",");
            }
            fullJ.append("],");
        }
    }

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
