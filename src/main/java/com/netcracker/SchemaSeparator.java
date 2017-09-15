package com.netcracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class SchemaSeparator {
    private Set<String> schemaName = new HashSet<>();
    private Map<String, Map<String, Object>> result;
    private Logger logger = LoggerFactory.getLogger(SchemaSeparator.class);

    void findSchemaNames(Map<String, Map<String, Object>> result) {
        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
            for (Map.Entry<String, Object> inner : entry.getValue().entrySet()) {
                if (inner.getKey().equals("call")) {
                    if (inner.getValue() != null)
                        schemaName.add((String) inner.getValue());
                }
            }
        }
        logger.info("Find " + schemaName.size() + " schemes");
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
            logger.info("Starting writing " + name + " schema");
            new JsonSchemaGenerator().generate(allSchemaEntity, name);
        }
    }
}
