package PresentationLayer.GUI.Common.Navigation;

public enum ScreensEnum {
   LOGIN("/GUI/LoginScreen/Views/LoginView.fxml"),
   MENU("/GUI/MainMenuScreen/Views/MainMenuView.fxml"),
   EMPLOYEE("/GUI/EmployeeScreen/Views/MainView.fxml"),
   SUPPLIERS("/GUI/SupplierScreen/Views/SuppliersMainView.fxml"),
   INVENTORY("/GUI/InventoryScreen/Views/InventoryView.fxml"),
   SHIPMENTS("/GUI/TransportationScreen/Views/ShipmentsView.fxml"),
   SUPPLIERS_DASHBOARD("/GUI/SupplierScreen/Views/SuppliersDashboardView.fxml"),
   SUPPLIERS_LIST("/GUI/SupplierScreen/Views/SuppliersListView.fxml"),
   SUPPLIER_DETAILS("/GUI/SupplierScreen/Views/SupplierDetailsView.fxml"),
   SUPPLIER_EDIT("/GUI/SupplierScreen/Views/SupplierEditView.fxml"),
   SUPPLIER_ADD("/GUI/SupplierScreen/Views/SupplierAddView.fxml"),
   SUPPLIER_REMOVE("/GUI/SupplierScreen/Views/SupplierRemoveView.fxml");

   private final String fxmlPath;

   ScreensEnum(String p) { // ← constructor name must match
      this.fxmlPath = p;
   }

   public String getFxmlPath() {
      return fxmlPath;
   }
}