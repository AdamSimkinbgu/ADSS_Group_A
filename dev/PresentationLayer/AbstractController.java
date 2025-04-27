package PresentationLayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ServiceLayer.Interfaces_and_Abstracts.IService;

public abstract class AbstractController {
   protected Map<String, Runnable> controllerMenuOptions = new HashMap<>();
   protected Map<String, Consumer<String>> serviceMenuOptions = new HashMap<>();
   protected Map<Class<?>, List<String>> classFieldNames = new HashMap<>();
   protected static final ObjectMapper mapper = new ObjectMapper();
   protected IService service;
   protected final View view;
   protected boolean implemented;

   // last raw JSON response from the service
   protected String lastServiceResponse;

   /**
    * @return the last JSON response returned by the service
    */
   public String getLastServiceResponse() {
      return lastServiceResponse;
   }

   public AbstractController(View view, IService service) {
      this.view = view;
      this.service = service;
      this.implemented = false;
      mapper.registerModule(new JavaTimeModule());
      // serialize/deserialize dates as ISO-strings, not timestamps
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      controllerMenuOptions.put("?", () -> {
         System.out.println("Invalid choice. Please try again.");
      });
   }

   public abstract List<String> showMenu();

   protected void handleModuleMenu() {
      while (true) {
         List<String> menu = showMenu();
         view.showOptions(menu.get(0), menu.subList(0, menu.size()));
         String choice = view.readLine();
         Runnable action = controllerMenuOptions.get(choice);
         if (!implemented) {
            System.out.println("This module is not implemented yet.");
            break;
         }
         if (action != null) {
            action.run();
            break;
         } else {
            controllerMenuOptions.get("?").run();
         }
      }
   }

   public String handleModuleCommand(String function, String JsonDTO) {
      lastServiceResponse = service.execute(function, JsonDTO);
      return lastServiceResponse;
   }

   protected String fuseClassAttributesAndParametersToJson(
         Class<?> cls,
         List<String> parameters) {
      AtomicInteger idx = new AtomicInteger(0);

      // build the tree
      JsonNode tree;
      try {
         tree = buildJsonForClass(cls, parameters, idx);
      } catch (Exception e) {
         // return "{\"error\": \"Invalid input for " +
         // cls.getSimpleName() + ": " + e.getMessage() + "\", \"returnValue\": null}";
         return null;
      }

      // ensure we consumed exactly all tokens (or decide how to handle extras)
      if (idx.get() != parameters.size()) {
         // throw new IllegalArgumentException(
         // "Expected to consume " + parameters.size() +
         // " tokens but used " + idx.get());
         view.showError(
               "Expected to consume " + parameters.size() +
                     " tokens but used " + idx.get());
         return null;
      }

      try {
         return mapper.writeValueAsString(tree);
      } catch (Exception e) {
         // throw new RuntimeException("Failed to serialize JSON for " +
         // cls.getSimpleName(), e);
         view.showError("Failed to serialize JSON for " +
               cls.getSimpleName() + ": " + e.getMessage());
         return null;
      }
   }

   private JsonNode buildJsonForClass(
         Class<?> cls,
         List<String> params,
         AtomicInteger idx) {
      ObjectNode node = mapper.createObjectNode();
      Constructor<?> ctor = pickPrimaryConstructor(cls);

      for (Parameter p : ctor.getParameters()) {
         // 1) check for @JsonProperty
         JsonProperty jp = p.getAnnotation(JsonProperty.class);
         String name = (jp != null && !jp.value().isEmpty())
               ? jp.value()
               : p.getName(); // fallback if no annotation

         Class<?> type = p.getType();
         if (isLeafType(type)) {
            String token = params.get(idx.getAndIncrement());
            node.put(name, token);
         } else {
            JsonNode child = buildJsonForClass(type, params, idx);
            node.set(name, child);
         }
      }

      return node;
   }

   private boolean isLeafType(Class<?> t) {
      return t.isPrimitive()
            || Number.class.isAssignableFrom(t)
            || t == String.class
            || t == Boolean.class
            || t == UUID.class
            || t.isEnum();
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

   protected boolean requestBoolean(String message) {
      String input = view.readLine(message);
      return Boolean.parseBoolean(input);
   }
}