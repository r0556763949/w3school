package config;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class ConfigLoader {
    private static ConfigLoader instance;
    private final Properties properties = new Properties();

    public ConfigLoader() {
        try (InputStream input = new FileInputStream("src/test/resources/root.config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties file", e);
        }
    }

    // אני לא בטוחה שצריך את זה:
    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property not found: " + key);
        }
        return value;
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
