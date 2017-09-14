package com.netcracker;

import java.util.*;

class SchemaSeparator {
    private Set<String> schemaName = new HashSet<>();
    private Map<String, Map<String, Object>> result;

    void findSchemaNames(Map<String, Map<String, Object>> result) {
        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
            for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
                if (inner.getKey().equals("call")) {
                    if (inner.getValue() != null)
                        schemaName.add((String) inner.getValue());
                }
            }
        }
        this.result = result;
        separate();
    }

    private void separate() {
        for (String name : schemaName) {
            Map<String, Map<String, Object>> allSchemaEntity = new HashMap<>();
            for (Map.Entry<String, Map<String, Object>> entry : this.result.entrySet()) {
                for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
                    if (inner.getKey().equals("call") && inner.getValue().equals(name)) {
                        allSchemaEntity.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            new JsonSchemaGenerator().generate(allSchemaEntity, name);
        }
    }
}
