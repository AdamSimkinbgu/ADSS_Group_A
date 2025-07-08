package PresentationLayer.GUI.MainMenuScreen.Controllers;

import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import PresentationLayer.GUI.EmployeeScreen.Controllers.MainViewController;
import PresentationLayer.GUI.EmployeeScreen.utils.ServiceFacade;
import PresentationLayer.GUI.LoginScreen.Controllers.LoginViewController;
import PresentationLayer.GUI.LoginScreen.ViewModels.LoginViewModel;
import PresentationLayer.GUI.MainMenuScreen.ViewModels.MainMenuViewModel;
import PresentationLayer.GUI.SupplierScreen.Controllers.SuppliersDashboardController;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.function.Consumer;

import DomainLayer.SystemFactory;
import DomainLayer.SystemFactory.SupplierModuleComponents;

public class MainMenuController {
   @FXML
   private Button suppliersBtn;
   @FXML
   private Button inventoryBtn;
   @FXML
   private Button hrBtn;
   @FXML
   private Button shipmentsBtn;
   @FXML
   private Button logoutBtn;
   @FXML
   private Labeled welcomeLabel;
   @FXML
   private StackPane contentArea;

   private final MainMenuViewModel vm;

   private static boolean isInitialized = false;

   public MainMenuController(MainMenuViewModel vm) {
      this.vm = vm;
   }

   @FXML
   public void initialize() {
      // welcome label
      welcomeLabel.textProperty().bind(
            Bindings.concat("Welcome, ", vm.userDetailsProperty(), "!"));

      // wire up each button to load its module into the center pane
      suppliersBtn.setOnAction(_ -> onSuppliers());
      inventoryBtn.setOnAction(_ -> onInventory());
      hrBtn.setOnAction(_ -> onHR());
      shipmentsBtn.setOnAction(_ -> onShipments());

      // log out goes back to login screen
      logoutBtn.setOnAction(_ -> {
         loadModule(ScreensEnum.LOGIN.getFxmlPath(), loader -> {
            // inject your LoginViewController with VM
            LoginViewModel loginVm = new LoginViewModel();
            loader.setControllerFactory(type -> {
               if (type == LoginViewController.class) {
                  welcomeLabel.textProperty().bind(
                        Bindings.concat("Welcome, Please log in!"));
                  return new LoginViewController(loginVm);
               }
               try {
                  return type.getDeclaredConstructor().newInstance();
               } catch (Exception ex) {
                  throw new RuntimeException(ex);
               }
            });
         });
      });
      if (!isInitialized) {
         // only set the content area once
         isInitialized = true;
         // load the default module login
         loadModule(ScreensEnum.LOGIN.getFxmlPath(), loader -> {
            // inject your LoginViewController with VM
            LoginViewModel loginVm = new LoginViewModel();
            loader.setControllerFactory(type -> {
               if (type == LoginViewController.class) {
                  welcomeLabel.textProperty().bind(
                        Bindings.concat("Welcome, Please log in!"));
                  return new LoginViewController(loginVm);
               }
               try {
                  return type.getDeclaredConstructor().newInstance();
               } catch (Exception ex) {
                  throw new RuntimeException(ex);
               }
            });
         });
      }
   }

   private void onSuppliers() {
      loadModule(ScreensEnum.SUPPLIERS.getFxmlPath(), loader -> {
         SupplierModuleComponents components = new SystemFactory().getSupplierModule();
         // if you have a SuppliersDashboardController that needs wiring:
         loader.setControllerFactory(type -> {
            if (type == SuppliersDashboardController.class) {
               SuppliersDashboardController controller = new SuppliersDashboardController();
               controller.setSupplierService(components.getSupplierService());
               controller.setOrderService(components.getOrderService());
               return controller;
            }
            try {
               return type.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         });
      });
   }

   private void onInventory() {
      // currently not implemented, show an alert
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Not Implemented");
      alert.setHeaderText("Inventory Module");
      alert.setContentText("The Inventory module is not yet implemented.");
      alert.showAndWait();
      // loadModule(ScreensEnum.INVENTORY.getFxmlPath(), null);
   }

   private void onHR() {
      loadModule(ScreensEnum.EMPLOYEE.getFxmlPath(), loader -> {
         // ServiceFacade provides employee & shift services
         try {
            EmployeeService empSvc = ServiceFacade.getInstance().getEmployeeService();
            ShiftService shiftSvc = ServiceFacade.getInstance().getShiftService();
            loader.setControllerFactory(type -> {
               if (type == MainViewController.class) {
                  MainViewController ctrl = new MainViewController();
                  ctrl.setServices(empSvc, shiftSvc);
                  return ctrl;
               }
               try {
                  return type.getDeclaredConstructor().newInstance();
               } catch (Exception ex) {
                  throw new RuntimeException(ex);
               }
            });
         } catch (IOException ex) {
            throw new RuntimeException("Failed to init HR services", ex);
         }
      });
   }

   private void onShipments() {
      // currently not implemented, show an alert
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Not Implemented");
      alert.setHeaderText("Shipments Module");
      alert.setContentText("The Shipments module is not yet implemented.");
      alert.showAndWait();
      // loadModule(ScreensEnum.SHIPMENTS.getFxmlPath(), null);
   }

   /**
    * Load an FXML file from the given classpath path into the center contentArea.
    * 
    * @param fxmlPath  the path (e.g.
    *                  "/GUI/InventoryScreen/Views/InventoryView.fxml")
    * @param preloader optional callback to configure FXMLLoader (e.g. inject
    *                  controller)
    */
   private void loadModule(String fxmlPath, Consumer<FXMLLoader> preloader) {
      try {
         var url = getClass().getResource(fxmlPath);
         if (url == null) {
            throw new IllegalArgumentException("Cannot find FXML: " + fxmlPath);
         }

         FXMLLoader loader = new FXMLLoader(url);

         // ← NEW: allow the caller to configure the FXMLLoader (e.g. set
         // controllerFactory)
         if (preloader != null) {
            preloader.accept(loader);
         }

         // (Optionally) you could still add a fallback factory here if you like,
         // but be mindful of overriding the caller’s factory!
         // loader.setControllerFactory(...);

         Parent view = loader.load();
         contentArea.getChildren().setAll(view);

      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}