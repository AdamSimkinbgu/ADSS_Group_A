package PresentationLayer.GUI.MainMenuScreen.Controllers;

import PresentationLayer.EmployeeSubModule.controllers.MainViewController;
import PresentationLayer.EmployeeSubModule.utils.ServiceFacade;
import PresentationLayer.GUI.Common.Navigation.ScreenNavigator;
import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import PresentationLayer.GUI.LoginScreen.Controllers.LoginViewController;
import PresentationLayer.GUI.LoginScreen.ViewModels.LoginViewModel;
import PresentationLayer.GUI.MainMenuScreen.ViewModels.MainMenuViewModel;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;

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

   private final MainMenuViewModel vm;

   public MainMenuController(MainMenuViewModel vm) {
      this.vm = vm;
   }

   @SuppressWarnings("unused")
   @FXML
   public void initialize() {
      suppliersBtn.setOnAction(e -> ScreenNavigator.getInstance().navigateTo(ScreensEnum.SUPPLIERS));
      inventoryBtn.setOnAction(e -> ScreenNavigator.getInstance().navigateTo(ScreensEnum.INVENTORY));
      hrBtn.setOnAction(e -> {
         // grab your services
         try {
            EmployeeService empSvc = ServiceFacade.getInstance().getEmployeeService();
            ShiftService shiftSvc = ServiceFacade.getInstance().getShiftService();

            ScreenNavigator.getInstance().navigateTo(ScreensEnum.EMPLOYEE, loader -> {
               // tell FXMLLoader how to build your MainViewController
               loader.setControllerFactory(type -> {
                  if (type == MainViewController.class) {
                     MainViewController c = new MainViewController();
                     c.setServices(empSvc, shiftSvc);
                     return c;
                  }
                  // default fallback for other controllers
                  try {
                     return type.getDeclaredConstructor().newInstance();
                  } catch (ReflectiveOperationException ex) {
                     throw new RuntimeException(ex);
                  }
               });
            });
         } catch (Exception ex) {
            // handle exceptions, e.g., show an error dialog
            System.err.println("Error initializing HR services: " + ex.getMessage());
         }
      });

      shipmentsBtn.setOnAction(e -> ScreenNavigator.getInstance().navigateTo(ScreensEnum.SHIPMENTS));

      logoutBtn.setOnAction(e -> {
         ScreenNavigator.getInstance().navigateTo(ScreensEnum.LOGIN, loader -> {
            // 1) New VM
            LoginViewModel loginVm = new LoginViewModel();
            // 2) Give FXMLLoader the factory that returns your controller with that VM
            loader.setControllerFactory(type -> {
               if (type == LoginViewController.class) {
                  return new LoginViewController(loginVm);
               }
               // fallback -- let JavaFX instantiate any other controllers normally
               try {
                  return type.getDeclaredConstructor().newInstance();
               } catch (Exception ex) {
                  throw new RuntimeException(ex);
               }
            });
         });
      });
      welcomeLabel.textProperty().bind(
            Bindings.concat("Welcome, user #", vm.userIdProperty()));
   }
}