package PresentationLayer.Controllers;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

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
      File f = new File("data.json");
      if (!f.exists()) {
         view.showError("Can't find data.json in " + f.getAbsolutePath());
      } else {
         DATA_PATH = f.getAbsolutePath();
         view.showMessage("Data path: " + DATA_PATH);
      }
      controllerMenuOptions.put("1", this::loadData);
      controllerMenuOptions.put("2", this::noData);
      controllerMenuOptions.put("3", this::getAllData);
      controllerMenuOptions.put("4", () -> System.out.println("Returning to the main menu..."));
      controllerMenuOptions.put("5", () -> {
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
            "See all data that's loaded",
            "Back to Main Menu",
            "Exit");
   }

   public void loadData() {
      view.showMessage("Loading system data...");
      ObjectNode payload = mapper.createObjectNode();
      payload.put("dataPath", DATA_PATH);
      payload.put("data", "data.json");
      String response = handleModuleCommand("loadData", payload.toString());
      view.dispatchResponse(response, String.class);
   }

   public void noData() {
      view.showMessage("Starting with no data.");
   }

   public void getAllData() {
      view.showMessage("Getting all data...");
      String response = handleModuleCommand("GetAllData", "");
      view.dispatchResponse(response, String.class);
   }
}
