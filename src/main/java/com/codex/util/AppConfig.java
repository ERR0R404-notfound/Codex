package com.codex.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static AppConfig instance;
    private final Properties properties;

    private AppConfig() {
        properties = new Properties();
        loadProperties();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("[AppConfig] config.properties not found in classpath");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("[AppConfig] Failed to load config.properties: " + e.getMessage());
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("[AppConfig] Invalid integer for key '" + key + "': " + value);
            return defaultValue;
        }
    }

    public long getLongProperty(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            System.err.println("[AppConfig] Invalid long for key '" + key + "': " + value);
            return defaultValue;
        }
    }
}
