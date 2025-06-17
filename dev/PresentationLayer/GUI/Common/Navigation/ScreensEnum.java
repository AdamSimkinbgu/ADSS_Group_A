package PresentationLayer.GUI.Common.Navigation;

public enum ScreensEnum {
   LOGIN("/GUI/LoginScreen/Views/LoginView.fxml"),
   MENU("/GUI/MainMenuScreen/Views/MainMenuView.fxml"),
   EMPLOYEE("/GUI/EmployeeScreen/Views/MainView.fxml"),
   SUPPLIERS("/GUI/SupplierScreen/Views/SuppliersView.fxml"),
   INVENTORY("/GUI/InventoryScreen/Views/InventoryView.fxml"),
   SHIPMENTS("/GUI/TransportationScreen/Views/ShipmentsView.fxml");

   private final String fxmlPath;

   ScreensEnum(String p) { // ← constructor name must match
      this.fxmlPath = p;
   }

   public String getFxmlPath() {
      return fxmlPath;
   }
}