package ServiceLayer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ServiceLayer.exception.ServiceException;
import ServiceLayer.response.Response;

/**
 * Utility class for JSON serialization and deserialization.
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // Configure ObjectMapper
        objectMapper.registerModule(new JavaTimeModule()); // For handling LocalDate
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Format dates as ISO strings
    }
    
    /**
     * Converts an object to a JSON string.
     *
     * @param object The object to convert
     * @return JSON string representation of the object
     * @throws ServiceException if serialization fails
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Error serializing object to JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param json The JSON string to convert
     * @param valueType The class of the object to create
     * @return An object of the specified type
     * @throws ServiceException if deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Error deserializing JSON to object: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converts a JSON string to an object using a TypeReference.
     * Useful for generic types like collections.
     *
     * @param json The JSON string to convert
     * @param typeReference The TypeReference describing the type
     * @return An object of the specified type
     * @throws ServiceException if deserialization fails
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Error deserializing JSON to object: " + e.getMessage(), e);
        }
    }
    
    /**
     * Creates a success response with the given value and converts it to JSON.
     *
     * @param value The value to include in the response
     * @return JSON string representation of the response
     */
    public static <T> String successResponse(T value) {
        return toJson(Response.success(value));
    }
    
    /**
     * Creates an error response with the given message and converts it to JSON.
     *
     * @param errorMessage The error message
     * @return JSON string representation of the response
     */
    public static <T> String errorResponse(String errorMessage) {
        return toJson(Response.error(errorMessage));
    }
    
    /**
     * Gets the ObjectMapper instance for custom configuration.
     *
     * @return The ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}