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
import ServiceLayer.exception.ServiceException;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Labeled;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.function.Consumer;

import DTOs.EmployeeDTO;
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

   private EmployeeDTO employeeDTO;

   public MainMenuController(MainMenuViewModel vm, EmployeeDTO employeeDTO) {
      this.vm = vm;
      this.employeeDTO = employeeDTO;
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
                  employeeDTO = null; // reset employeeDTO on logout
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
      if (!userHasAccessToSuppliers()) {
         // userHasAccess already showed an alert
         return;
      }

      loadModule(ScreensEnum.SUPPLIERS.getFxmlPath(), loader -> {
         SupplierModuleComponents components = new SystemFactory().getSupplierModule();
         loader.setControllerFactory(type -> {
            if (type == SuppliersDashboardController.class) {
               var ctrl = new SuppliersDashboardController();
               ctrl.setSupplierService(components.getSupplierService());
               ctrl.setOrderService(components.getOrderService());
               return ctrl;
            }
            try {
               return type.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         });
      });
   }

   private boolean userHasAccessToSuppliers() {
      if (!checkIfAnyUserIsConnected()) {
         return false;
      }

      try {
         EmployeeService empSvc = ServiceFacade.getInstance().getEmployeeService();
         empSvc.canAccessSuppliersModule(employeeDTO.getIsraeliId());
         return true;
      } catch (ServiceException se) {
         // Explicitly catch the authorization failure
         Alert a = new Alert(Alert.AlertType.ERROR,
               "You do not have permission to access the Suppliers module.",
               ButtonType.OK);
         a.setHeaderText("Access Denied");
         a.showAndWait();
         return false;
      } catch (IOException ioe) {
         // Something went wrong talking to the service
         Alert a = new Alert(Alert.AlertType.ERROR,
               ioe.getMessage(),
               ButtonType.OK);
         a.setHeaderText("Error Checking Permissions");
         a.showAndWait();
         return false;
      }
   }

   private void onInventory() {
      // currently not implemented, show an alert
      if (!userHasAccessToInventory()) {
         // userHasAccess already showed an alert
         return;
      }
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Not Implemented");
      alert.setHeaderText("Inventory Module");
      alert.setContentText("The Inventory module is not yet implemented.");
      alert.showAndWait();
      // loadModule(ScreensEnum.INVENTORY.getFxmlPath(), null);
   }

   private boolean userHasAccessToInventory() {
      if (!checkIfAnyUserIsConnected()) {
         return false;
      }

      try {
         EmployeeService empSvc = ServiceFacade.getInstance().getEmployeeService();
         empSvc.canAccessInventoryModule(employeeDTO.getIsraeliId());
         return true;
      } catch (ServiceException se) {
         // Explicitly catch the authorization failure
         Alert a = new Alert(Alert.AlertType.ERROR,
               "You do not have permission to access the Inventory module.",
               ButtonType.OK);
         a.setHeaderText("Access Denied");
         a.showAndWait();
         return false;
      } catch (IOException ioe) {
         // Something went wrong talking to the service
         Alert a = new Alert(Alert.AlertType.ERROR,
               ioe.getMessage(),
               ButtonType.OK);
         a.setHeaderText("Error Checking Permissions");
         a.showAndWait();
         return false;
      }
   }

   private void onHR() {
      if (!userHasAccessToHR()) {
         // userHasAccess already showed an alert
         return;
      }

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

   private boolean userHasAccessToHR() {
      if (!checkIfAnyUserIsConnected()) {
         return false;
      }

      try {
         EmployeeService empSvc = ServiceFacade.getInstance().getEmployeeService();
         empSvc.canAccessHRModule(employeeDTO.getIsraeliId());
         return true;
      } catch (ServiceException se) {
         // Explicitly catch the authorization failure
         Alert a = new Alert(Alert.AlertType.ERROR,
               "You do not have permission to access the HR module.",
               ButtonType.OK);
         a.setHeaderText("Access Denied");
         a.showAndWait();
         return false;
      } catch (IOException ioe) {
         // Something went wrong talking to the service
         Alert a = new Alert(Alert.AlertType.ERROR,
               ioe.getMessage(),
               ButtonType.OK);
         a.setHeaderText("Error Checking Permissions");
         a.showAndWait();
         return false;
      }
   }

   private boolean checkIfAnyUserIsConnected() {
      // This method can be used to check if any user is connected
      // For now, we assume that if the employeeDTO is not null, a user is connected
      if (employeeDTO == null) {
         Alert a = new Alert(Alert.AlertType.WARNING,
               "You must log in before accessing the Inventory module.",
               ButtonType.OK);
         a.setHeaderText("Access Denied");
         a.showAndWait();
         return false;
      }
      return true;
   }

   private void onShipments() {
      // currently not implemented, show an alert
      if (!userHasAccessToInventory()) {
         // userHasAccess already showed an alert
         return;
      }
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