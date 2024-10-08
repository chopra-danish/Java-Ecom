/*package com.ecommerce.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    private static final String PROPERTIES_FILE_PATH = "src/main/resources/db.properties";

    public static String getPropertyString(String key) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load properties file: " + e.getMessage());
        }
        return properties.getProperty(key);
    }
}
*/
package com.ecommerce.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    private static final String PROPERTIES_FILE_PATH = "/db.properties"; // Classpath resource

    public static String getPropertyString(String key) {
        Properties properties = new Properties();
        try (InputStream input = PropertyUtil.class.getResourceAsStream(PROPERTIES_FILE_PATH)) {
            if (input == null) {
                throw new RuntimeException("Unable to find properties file: " + PROPERTIES_FILE_PATH);
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load properties file: " + e.getMessage());
        }
        return properties.getProperty(key);
    }
}
