package PresentationLayer.GUI.MainMenuScreen.Controllers;

import PresentationLayer.EmployeeSubModule.controllers.MainViewController;
import PresentationLayer.EmployeeSubModule.utils.ServiceFacade;
import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import PresentationLayer.GUI.LoginScreen.Controllers.LoginViewController;
import PresentationLayer.GUI.LoginScreen.ViewModels.LoginViewModel;
import PresentationLayer.GUI.MainMenuScreen.ViewModels.MainMenuViewModel;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.function.Consumer;

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
   }

   private void onSuppliers() {
      loadModule(ScreensEnum.SUPPLIERS.getFxmlPath(), loader -> {
         // if you need to inject a SuppliersController / VM, do it here:
         // loader.setControllerFactory(...);
      });
   }

   private void onInventory() {
      loadModule(ScreensEnum.INVENTORY.getFxmlPath(), null);
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
      loadModule(ScreensEnum.SHIPMENTS.getFxmlPath(), null);
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
         FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
         if (preloader != null) {
            preloader.accept(loader);
         }
         Node moduleRoot = loader.load();

         // replace whatever is in the center with the new module
         contentArea.getChildren().setAll(moduleRoot);

      } catch (IOException e) {
         e.printStackTrace();
         // TODO: show user-friendly error dialog
         Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load module: " + e.getMessage());
         alert.showAndWait();
      }
   }
}