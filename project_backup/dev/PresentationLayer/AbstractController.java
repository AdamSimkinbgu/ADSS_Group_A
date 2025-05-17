package PresentationLayer;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

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
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
      lastServiceResponse = serialize(service.execute(function, JsonDTO));
      return lastServiceResponse;
   }

   protected <T> String serialize(ServiceResponse<T> resp) {
      try {
         return mapper.writeValueAsString(resp);
      } catch (JsonProcessingException e) {
         return "{\"value\":null,\"error\":\"Serialization error\"}";
      }
   }

   protected boolean requestBoolean(String message) {
      String input = view.readLine(message);
      if (input.toLowerCase().equals("y") || input.toLowerCase().equals("yes"))
         return true;
      else if (input.toLowerCase().equals("n") || input.toLowerCase().equals("no"))
         return false;
      return Boolean.parseBoolean(input);
   }
}