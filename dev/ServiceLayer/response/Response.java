package ServiceLayer.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A generic response class that wraps data with error status information.
 * This class is used for JSON serialization/deserialization when transferring data between layers.
 * 
 * @param <T> The type of the value contained in the response
 */
public class Response<T> {
    @JsonProperty("isError")
    private boolean isError;
    
    @JsonProperty("value")
    private T value;
    
    @JsonProperty("errorMessage")
    private String errorMessage;
    
    // Default constructor for Jackson
    public Response() {
        this.isError = false;
        this.value = null;
        this.errorMessage = null;
    }
    
    // Constructor for successful responses
    public Response(T value) {
        this.isError = false;
        this.value = value;
        this.errorMessage = null;
    }
    
    // Constructor for error responses
    public Response(String errorMessage) {
        this.isError = true;
        this.value = null;
        this.errorMessage = errorMessage;
    }
    
    // Getters and setters
    public boolean isError() {
        return isError;
    }
    
    public void setError(boolean error) {
        isError = error;
    }
    
    public T getValue() {
        return value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    // Static factory methods for creating responses
    public static <T> Response<T> success(T value) {
        return new Response<>(value);
    }
    
    public static <T> Response<T> error(String errorMessage) {
        return new Response<>(errorMessage);
    }
}