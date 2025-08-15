// dev/PresentationLayer/Common/Navigation/MainLayoutController.java
package PresentationLayer.GUI.Common.Navigation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainLayoutController {
   @FXML
   private ToggleButton toggleBtn;
   @FXML
   private VBox sidebar;
   @FXML
   private StackPane contentArea;

   @FXML
   public void initialize() {
      // collapse / expand sidebar
      toggleBtn.setOnAction(e -> sidebar.setVisible(!sidebar.isVisible()));
      // default panel
      onSuppliers();
   }

   @FXML
   private void onSuppliers() {
      loadModule("/suppliers/SuppliersView.fxml");
   }

   @FXML
   private void onInventory() {
      loadModule("/inventory/InventoryView.fxml");
   }

   @FXML
   private void onHR() {
      loadModule("/hr/EmployeeMainView.fxml");
   }

   @FXML
   private void onShipments() {
      loadModule("/shipments/ShipmentsView.fxml");
   }

   @FXML
   private void onLogout() {
      /* your logout logic */ }

   private void loadModule(String fxmlPath) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
         Parent view = loader.load();
         // **If you need to inject services**, you can do:
         // loader.setControllerFactory(...)
         contentArea.getChildren().setAll(view);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}