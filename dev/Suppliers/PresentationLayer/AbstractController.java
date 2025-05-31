package Suppliers.PresentationLayer;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public abstract class AbstractController {
   protected Map<String, Runnable> controllerMenuOptions = new HashMap<>();
   protected Map<String, Consumer<String>> serviceMenuOptions = new HashMap<>();
   protected Map<Class<?>, List<String>> classFieldNames = new HashMap<>();
   protected final View view;
   protected boolean implemented;

   // last raw JSON response from the service
   protected String lastServiceResponse;

   /**
    * @return the last JSON response returned by the service
    */
   public String getLastServiceResponse() {
      return null;
   }

   public AbstractController(View view) {
      this.view = view;
      this.implemented = false;
   }

   public abstract List<String> showMenu();

   protected void handleModuleMenu() {

   }

   public String handleModuleCommand(String function, String JsonDTO) {
      return null;
   }

   protected <T> String serialize(ServiceResponse<T> resp) {
      return null;
   }

   protected boolean requestBoolean(String message) {
      return false;
   }
}