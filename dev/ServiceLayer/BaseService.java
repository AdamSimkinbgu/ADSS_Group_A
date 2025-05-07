package ServiceLayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public abstract class BaseService {
   protected HashMap<String, Function<String, ServiceResponse<?>>> serviceFunctions = new HashMap<>();
   protected ObjectMapper objectMapper;

   public BaseService() {
      this.objectMapper = new ObjectMapper();
      serviceFunctions.put("?", this::commandDoesNotExist);
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
   }

   public ServiceResponse<String> commandDoesNotExist(String data) {
      return ServiceResponse.error("Command does not exist.");
   }

   /**
    * Helper to serialize the ServiceResponse envelope.
    */
   protected <T> String serialize(ServiceResponse<T> resp) {
      try {
         return objectMapper.writeValueAsString(resp);
      } catch (JsonProcessingException e) {
         return "{\"value\":null,\"error\":\"Serialization error\"}";
      }
   }

   /**
    * Try to bind the JSON into `targetType`. If it fails, return an error
    * ServiceResponse<Boolean>. Otherwise return a “true” value so you can go
    * on and call the facade.
    */
   public <T> ServiceResponse<Boolean> validateBinding(String json, Class<T> targetType) {
      try {
         // this will invoke the same @JsonCreator constructor
         objectMapper.readValue(json, targetType);
         return ServiceResponse.ok(true);
      } catch (JsonProcessingException e) {
         // Jackson tells you exactly which field/type failed
         return ServiceResponse.error(
               "Type conversion error: " + e.getOriginalMessage());
      }
   }

   protected <T> ServiceResponse<Void> validateJsonPayload(
         String json,
         Class<T> targetType) {
      List<String> errors = new ArrayList<>();
      JsonNode root;
      try {
         root = objectMapper.readTree(json);
      } catch (JsonProcessingException e) {
         return new ServiceResponse<>(null,
               "Malformed JSON: " + e.getOriginalMessage());
      }

      Constructor<?> ctor = pickPrimaryConstructor(targetType);
      for (Parameter param : ctor.getParameters()) {
         JsonProperty jp = param.getAnnotation(JsonProperty.class);
         String name = jp != null && !jp.value().isEmpty()
               ? jp.value()
               : param.getName();
         boolean required = jp != null && jp.required();

         JsonNode node = root.get(name);
         if (node == null) {
            if (required)
               errors.add("Missing field: " + name);
            continue;
         }
         if (node.isNull() && param.getType().isPrimitive()) {
            errors.add("Field \"" + name + "\" must not be null");
            continue;
         }

         JavaType javaType = objectMapper.getTypeFactory()
               .constructType(param.getParameterizedType());

         try {
            objectMapper.readerFor(javaType)
                  .readValue(node);
         } catch (MismatchedInputException mie) {
            errors.add("Field \"" + name + "\" invalid: " +
                  mie.getOriginalMessage());
         } catch (Exception ex) {
            errors.add("Field \"" + name + "\" invalid: " + ex.getMessage());
         }
      }

      if (!errors.isEmpty()) {
         String combined = String.join(";\n ", errors);
         return ServiceResponse.error(combined);
      }
      // all good
      return ServiceResponse.ok(null);
   }

   private Constructor<?> pickPrimaryConstructor(Class<?> cls) {
      Constructor<?>[] ctors = cls.getConstructors();
      if (ctors.length == 0) {
         try {
            return cls.getDeclaredConstructor();
         } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("No accessible constructor for " + cls, ex);
         }
      }
      Constructor<?> best = ctors[0];
      for (Constructor<?> c : ctors) {
         if (c.getParameterCount() > best.getParameterCount()) {
            best = c;
         }
      }
      return best;
   }
}
