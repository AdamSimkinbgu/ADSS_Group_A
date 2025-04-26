// File: service/Response.java
package ServiceLayer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * A generic wrapper for service responses.
 *
 * @param <T> the type of the returned data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private T returnValue;
    private String errorMessage;

    // Jackson needs a no-arg constructor
    public Response() {
    }

    /** Success with a return value */
    public Response(T returnValue) {
        this.returnValue = returnValue;
    }

    /** Failure with an error message */
    public Response(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /** Both returnValue and errorMessage (rarely used) */
    public Response(T returnValue, String errorMessage) {
        this.returnValue = returnValue;
        this.errorMessage = errorMessage;
    }

    public T getReturnValue() {
        return returnValue;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Serialize this Response to JSON.
     */
    public String toJson() {
        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // fallback if JSON fails
            return "{\"errorMessage\":\"JSON serialization error\"}";
        }
    }

    // static factories for convenience
    public static <T> Response<T> ok(T value) {
        return new Response<>(value);
    }

    public static <T> Response<T> error(String msg) {
        return new Response<>(msg);
    }
}