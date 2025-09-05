package io.github.julianobrl.discordbots.configs;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class YamlConfigManager {
    private final String filePath;
    private final Yaml yaml;

    public YamlConfigManager(String filePath) {
        this.filePath = filePath;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Enforce block style
        options.setPrettyFlow(true); // Ensure readable formatting
        this.yaml = new Yaml(options);
        ensureFileExists();
    }

    /**
     * Creates a new key-value pair in the YAML file using dot notation for nested keys.
     * If the key already exists, it will be updated.
     */
    public void createKey(String key, Object value) throws Exception {
        Map<String, Object> data = loadYaml();
        setNestedKey(data, key, value);
        saveYaml(data);
    }

    /**
     * Edits an existing key's value in the YAML file using dot notation.
     * Throws an exception if the key doesn't exist.
     */
    public void editKey(String key, Object newValue) throws Exception {
        Map<String, Object> data = loadYaml();
        if (!hasNestedKey(data, key)) {
            throw new IllegalArgumentException("Key '" + key + "' not found in YAML file");
        }
        setNestedKey(data, key, newValue);
        saveYaml(data);
    }

    /**
     * Deletes a key from the YAML file using dot notation.
     * Throws an exception if the key doesn't exist.
     */
    public void deleteKey(String key) throws Exception {
        Map<String, Object> data = loadYaml();
        if (!hasNestedKey(data, key)) {
            throw new IllegalArgumentException("Key '" + key + "' not found in YAML file");
        }
        removeNestedKey(data, key);
        saveYaml(data);
    }

    /**
     * Retrieves the value associated with a key from the YAML file using dot notation.
     * Returns null if the key doesn't exist.
     */
    public Object getKey(String key) throws Exception {
        Map<String, Object> data = loadYaml();
        return getNestedKey(data, key);
    }

    /**
     * Ensures the YAML file and its parent directories exist.
     * Creates them if they don't.
     */
    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();

            // Create parent directories if they don't exist
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Create empty YAML file if it doesn't exist
            if (!file.exists()) {
                try (FileWriter writer = new FileWriter(file)) {
                    yaml.dump(new HashMap<>(), writer);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create YAML file or directories: " + filePath, e);
        }
    }

    /**
     * Loads and parses the YAML file into a Map.
     */
    private Map<String, Object> loadYaml() throws Exception {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            Map<String, Object> data = yaml.load(inputStream);
            return data != null ? data : new HashMap<>();
        } catch (Exception e) {
            throw new Exception("Failed to load YAML file: " + filePath, e);
        }
    }

    /**
     * Saves the Map data to the YAML file.
     */
    private void saveYaml(Map<String, Object> data) throws Exception {
        try (FileWriter writer = new FileWriter(filePath)) {
            yaml.dump(data, writer);
        } catch (Exception e) {
            throw new Exception("Failed to save YAML file: " + filePath, e);
        }
    }

    /**
     * Sets a nested key in the map using dot notation.
     */
    private void setNestedKey(Map<String, Object> data, String key, Object value) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = data;

        for (int i = 0; i < parts.length - 1; i++) {
            current = (Map<String, Object>) current.computeIfAbsent(parts[i], k -> new HashMap<String, Object>());
        }

        current.put(parts[parts.length - 1], value);
    }

    /**
     * Checks if a nested key exists in the map using dot notation.
     */
    private boolean hasNestedKey(Map<String, Object> data, String key) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = data;

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                return false;
            }
            current = (Map<String, Object>) next;
        }

        return current.containsKey(parts[parts.length - 1]);
    }

    /**
     * Retrieves a nested key's value from the map using dot notation.
     */
    private Object getNestedKey(Map<String, Object> data, String key) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = data;

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                return null;
            }
            current = (Map<String, Object>) next;
        }

        return current.get(parts[parts.length - 1]);
    }

    /**
     * Removes a nested key from the map using dot notation.
     */
    private void removeNestedKey(Map<String, Object> data, String key) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = data;

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                return;
            }
            current = (Map<String, Object>) next;
        }

        current.remove(parts[parts.length - 1]);
    }
}