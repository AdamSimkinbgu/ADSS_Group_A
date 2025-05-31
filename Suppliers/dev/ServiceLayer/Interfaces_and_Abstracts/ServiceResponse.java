package ServiceLayer.Interfaces_and_Abstracts;

import java.util.List;

public class ServiceResponse<T> {
   private T value;
   private List<String> errors;

   public ServiceResponse(T value, List<String> errors) {
      this.value = value;
      this.errors = errors;
   }

   public T getValue() {
      return value;
   }

   public void setValue(T value) {
      this.value = value;
   }

   public List<String> getErrors() {
      return errors;
   }

   public void setError(List<String> errors) {
      this.errors = errors;
   }

   public boolean isSuccess() {
      return errors == null;
   }

   public static <T> ServiceResponse<T> ok(T value) {
      return new ServiceResponse<>(value, null);
   }

   public static <T> ServiceResponse<T> fail(List<String> errors) {
      return new ServiceResponse<>(null, errors);
   }

   @Override
   public String toString() {
      return "{\n" +
            "   \"value\": " + (value != null ? value.toString() : "null") + ",\n" +
            "   \"errors\": " + (errors != null ? errors.toString() : "null") + "\n" +
            "}";
   }
}