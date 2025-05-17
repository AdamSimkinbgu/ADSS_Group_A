package ServiceLayer.Interfaces_and_Abstracts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A generic envelope for all service responses.
 * 
 * @param <T> the type of the successful payload
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse<T> {
   @JsonProperty("value")
   private T value;

   @JsonProperty("error")
   private String error;

   public ServiceResponse() {
   }

   public ServiceResponse(T value, String error) {
      this.value = value;
      this.error = error;
   }

   public T getValue() {
      return value;
   }

   public void setValue(T value) {
      this.value = value;
   }

   public String getError() {
      return error;
   }

   public void setError(String error) {
      this.error = error;
   }

   @JsonIgnore
   public boolean isSuccess() {
      return error == null;
   }

   @JsonIgnore
   public boolean isFailure() {
      return error != null;
   }

   public static <T> ServiceResponse<T> ok(T value) {
      return new ServiceResponse<>(value, null);
   }

   public static <T> ServiceResponse<T> error(String error) {
      return new ServiceResponse<>(null, error);
   }
}