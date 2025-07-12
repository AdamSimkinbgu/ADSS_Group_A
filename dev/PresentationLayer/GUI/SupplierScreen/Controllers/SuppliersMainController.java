package PresentationLayer.GUI.SupplierScreen.Controllers;

import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

import DomainLayer.SystemFactory;
import DomainLayer.SystemFactory.SupplierModuleComponents;

public class SuppliersMainController {

   @FXML
   private StackPane contentArea;
   @FXML
   private Button dashboardBtn;
   @FXML
   private Button suppliersBtn;
   @FXML
   private Button ordersBtn;
   @FXML
   private Button productsBtn;
   @FXML
   private Button backBtn;

   @FXML
   public void initialize() {
      // show current user
      dashboardBtn.setOnAction(
            _ -> loadSection("Dashboard",
                  ScreensEnum.SUPPLIERS_DASHBOARD.getFxmlPath()));
      suppliersBtn.setOnAction(_ -> loadSection(
            "Suppliers List",
            ScreensEnum.SUPPLIERS_LIST.getFxmlPath()));
      ordersBtn.setOnAction(_ -> {
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle("Not Implemented");
         alert.setHeaderText("Orders Tab");
         alert.setContentText("The Orders module is not implemented yet.");
         alert.showAndWait();
      });

      // next buttons are not implemented yet, show an alert
      productsBtn.setOnAction(_ -> loadSection(
            "Products List",
            ScreensEnum.SUPPLIERS_PRODUCTS.getFxmlPath()));

   }

   private void loadSection(String title, String fxmlPath) {
      try {
         // 1) Grab the URL of the FXML on the classpath
         var url = getClass().getResource(fxmlPath);
         if (url == null) {
            throw new IllegalArgumentException("Cannot find FXML: " + fxmlPath);
         }
         SupplierModuleComponents components = new SystemFactory().getSupplierModule();
         // 2) Create the loader with that URL (this both sets location & source)
         FXMLLoader loader = new FXMLLoader(url);

         // 3) Tell FXMLLoader how to build your SuppliersDashboardController
         loader.setControllerFactory(type -> {
            if (type == SuppliersDashboardController.class) {
               // Instantiate with your real services
               SuppliersDashboardController controller = new SuppliersDashboardController();
               controller.setSupplierService(components.getSupplierService());
               controller.setOrderService(components.getOrderService());
               controller.setAgreementService(components.getAgreementService());
               return controller;
            }
            if (type == SuppliersListController.class) {
               SuppliersListController ctrl = new SuppliersListController();
               ctrl.setSupplierService(components.getSupplierService());
               ctrl.setAgreementService(components.getAgreementService());
               return ctrl;
            }

            if (type == OrdersController.class) {
               OrdersController ctrl = new OrdersController();
               ctrl.setOrderService(components.getOrderService());
               return ctrl;
            }

            if (type == SupplierProductsController.class) {
               SupplierProductsController ctrl = new SupplierProductsController();
               ctrl.init(components.getSupplierService());
               return ctrl;
            }
            try {
               return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         });

         // 4) Now load: controller is constructed, @FXML fields injected, then
         // initialize() runs
         Parent view = loader.load();

         // 5) Swap into your content area
         contentArea.getChildren().setAll(view);

      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}