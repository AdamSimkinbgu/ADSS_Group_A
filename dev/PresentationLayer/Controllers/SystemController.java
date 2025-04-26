package PresentationLayer.Controllers;

import java.util.List;

import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.SystemService;

/**
 * Controller for System-level commands.
 */
public class SystemController extends AbstractController {
   private String DATA_PATH;

   public SystemController(String data, SystemService systemService, View view) {
      super(view, systemService);
      this.implemented = true;
      if (data != null) {
         this.DATA_PATH = data;
      } else {
         this.DATA_PATH = "data.json";
      }
      controllerMenuOptions.put("1", this::loadData);
      controllerMenuOptions.put("2", this::noData);
      controllerMenuOptions.put("3", () -> System.out.println("Returning to the main menu..."));
      controllerMenuOptions.put("4", () -> {
         System.out.println("Exiting...");
         System.exit(0);
      });
      controllerMenuOptions.put("?", () -> System.out.println("Invalid choice. Please try again."));
   }

   @Override
   public List<String> showMenu() {
      return List.of(
            "Please choose how to initialize the system:",
            "Load data",
            "No data",
            "Back to Main Menu",
            "Exit");
   }

   public void loadData() {
      view.showMessage("Loading system data...");
      String response = handleModuleCommand("fakeLoadData", "");
      view.dispatchResponse(response, String.class);
   }

   public void noData() {
      view.showMessage("Starting with no data.");
   }
}
