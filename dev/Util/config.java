package Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class config {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "dev/Util/config.properties";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Default hour values
    public static final LocalTime START_HOUR_MORNING = initTimeValue("START_HOUR_MORNING", "08:00");
    public static final LocalTime END_HOUR_MORNING = initTimeValue("END_HOUR_MORNING", "16:00");
    public static final LocalTime START_HOUR_EVENING = initTimeValue("START_HOUR_EVENING", "16:00");
    public static final LocalTime END_HOUR_EVENING = initTimeValue("END_HOUR_EVENING", "20:00");

    // Default block availability values
    public static final LocalTime BLOCK_AVAILABILITY_START_HOUR = initTimeValue("BLOCK_AVAILABILITY_START_HOUR",
            "16:00");
    public static final DayOfWeek BLOCK_AVAILABILITY_START_DAY = DayOfWeek
            .valueOf(initStringValue("BLOCK_AVAILABILITY_START_DAY", "THURSDAY").toUpperCase());

    // Default roles
    public static final String ROLE_ADMIN = initStringValue("ROLE_ADMIN", "Admin");
    public static final String ROLE_HR_MANAGER = initStringValue("ROLE_HR_MANAGER", "HR Manager");
    public static final String ROLE_SHIFT_MANAGER = initStringValue("ROLE_SHIFT_MANAGER", "Shift Manager");
    public static final String ROLE_TRANSPORT_MANAGER = initStringValue("ROLE_TRANSPORT_MANAGER", "Transport Manager");
    public static final String ROLE_WAREHOUSEMAN = initStringValue("ROLE_WAREHOUSEMAN", "WarehouseMan");
    public static final String ROLE_CASHIER = initStringValue("ROLE_CASHIER", "Cashier");
    public static final String ROLE_CLEANER = initStringValue("ROLE_CLEANER", "Cleaner");
    public static final String ROLE_STOCKER = initStringValue("ROLE_STOCKER", "Stocker");
    public static final String ROLE_DRIVER_A = initStringValue("ROLE_DRIVER_A", "DriverA");
    public static final String ROLE_DRIVER_B = initStringValue("ROLE_DRIVER_B", "DriverB");
    public static final String ROLE_DRIVER_C = initStringValue("ROLE_DRIVER_C", "DriverC");
    public static final String ROLE_DRIVER_D = initStringValue("ROLE_DRIVER_D", "DriverD");
    public static final String ROLE_DRIVER_E = initStringValue("ROLE_DRIVER_E", "DriverE");
    public static final String ROLE_SUPPLIERS_MANAGER = initStringValue("ROLE_SUPPLIERS_MANAGER", "Suppliers Manager");
    public static final String ROLE_INVENTORY_MANAGER = initStringValue("ROLE_INVENTORY_MANAGER", "Inventory Manager");

    // Database configuration
    // public static final String DB_URL = initStringValue("DB_URL",
    // "jdbc:sqlite:superLee.db");
    // public static final boolean LOAD_DATA_FROM_DB =
    // Boolean.parseBoolean(initStringValue("LOAD_DATA_FROM_DB", "true"));

    static {
        // Load properties from file
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            // System.out.println("Configuration loaded successfully from " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("Warning: Could not load configuration file. Using default values.");
        }
    }

    /**
     * Get a property value from the configuration file
     * 
     * @param key          The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value or the default value if not found
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get a property value from the configuration file
     * 
     * @param key The property key
     * @return The property value or null if not found
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Initialize a LocalTime value from the properties file
     * 
     * @param key          The property key
     * @param defaultValue The default value to use if the property is not found
     * @return The initialized LocalTime value
     */
    private static LocalTime initTimeValue(String key, String defaultValue) {
        try {
            return LocalTime.parse(getProperty(key, defaultValue), TIME_FORMATTER);
        } catch (Exception e) {
            System.err.println("Error parsing time value for key " + key + ": " + e.getMessage());
            String[] parts = defaultValue.split(":");
            return LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    /**
     * Initialize a String value from the properties file
     * 
     * @param key          The property key
     * @param defaultValue The default value to use if the property is not found
     * @return The initialized String value
     */
    private static String initStringValue(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }
}
