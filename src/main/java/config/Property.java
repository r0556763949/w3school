package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Property {
    private final Properties properties = new Properties();

    public Property() {
        loadFromClasspath("root.config.properties");
    }

    public Property(String resourceName) {
        loadFromClasspath(resourceName);
    }

    private void loadFromClasspath(String resourceName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load property file: " + resourceName, e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public int getIntProperty(String key, int defaultValue) {
        String v = getProperty(key);
        if (v == null) return defaultValue;
        return Integer.parseInt(v);
    }

}
