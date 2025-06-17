package PresentationLayer.GUI.LoginScreen.ViewModels;

import java.io.IOException;

import DomainLayer.SystemFactory;
import PresentationLayer.EmployeeSubModule.utils.ServiceFacade;
import ServiceLayer.EmployeeIntegrationService;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {
   private final StringProperty userId = new SimpleStringProperty("");
   private final StringProperty errorMessage = new SimpleStringProperty("");
   private final BooleanProperty loginSuccess = new SimpleBooleanProperty(false);

   // get the factory
   private final SystemFactory systemFactory = new SystemFactory();
   private final EmployeeService employeeService;
   private final ShiftService shiftService;
   private final EmployeeIntegrationService authService;
   public int loggedInUserId;

   public LoginViewModel() {
      try {

         this.employeeService = systemFactory.createEmployeeModule(false).getEmployeeService();
         this.shiftService = ServiceFacade.getInstance().getShiftService();
      } catch (IOException e) {
         throw new RuntimeException("Failed to initialize services: " + e.getMessage(), e);
      }
      this.authService = new EmployeeIntegrationService(employeeService, shiftService);
   }

   public StringProperty userIdProperty() {
      return userId;
   }

   public StringProperty errorMessageProperty() {
      return errorMessage;
   }

   public BooleanProperty loginSuccessProperty() {
      return loginSuccess;
   }

   public int getLoggedInUser() {
      if (loginSuccess.get()) {
         try {
            loggedInUserId = Integer.parseInt(userId.get());
         } catch (NumberFormatException e) {
            throw new IllegalStateException("Cannot retrieve logged-in user ID, invalid format: " + userId.get());
         }
         return loggedInUserId;
      } else {
         throw new IllegalStateException("No user is currently logged in.");
      }
   }

   public void reset() {
      userId.set("");
      errorMessage.set("");
      loginSuccess.set(false);
   }

   /**
    * Attempts to log in. Returns true if login succeeded, false otherwise.
    * Also flips the loginSuccessProperty to true on success.
    */
   public boolean login() {
      // reset
      loginSuccess.set(false);
      errorMessage.set("");

      // validate numeric
      int id;
      try {
         id = Integer.parseInt(userId.get());
      } catch (NumberFormatException e) {
         errorMessage.set("User ID must be numeric");
         return false;
      }

      // check via your integration service
      if (authService.isActive(id)) {
         loginSuccess.set(true);
         return true;
      } else {
         errorMessage.set("Invalid user ID");
         return false;
      }
   }
}