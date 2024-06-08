package com.zwy.appformysql.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigurationReader {
    private String filePath;
    private Map<String, Map<String, String>> sections;

    public ConfigurationReader(String filePath) {
        this.filePath = filePath;
        this.sections = new HashMap<>();
        parseConfigurationFile();
    }

    private void parseConfigurationFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentSection = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    sections.put(currentSection, new HashMap<>());
                } else if (currentSection != null && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    sections.get(currentSection).put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getSectionNames() {
        return sections.keySet();
    }

    public Map<String, String> getSection(String sectionName) {
        return sections.get(sectionName);
    }

    public String getValue(String sectionName, String key) {
        Map<String, String> section = sections.get(sectionName);
        if (section != null) {
            return section.get(key);
        }
        return null;
    }
}

