package PresentationLayer.GUI.LoginScreen.ViewModels;

import java.io.IOException;

import DTOs.EmployeeDTO;
import DomainLayer.SystemFactory;
import PresentationLayer.GUI.EmployeeScreen.utils.ServiceFacade;
import ServiceLayer.EmployeeIntegrationService;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {
   private final StringProperty userId = new SimpleStringProperty("");
   private final StringProperty errorMessage = new SimpleStringProperty(
         "Enter your user ID.\nThen, press 'login'/Enter key.");
   private final BooleanProperty loginSuccess = new SimpleBooleanProperty(false);
   private EmployeeDTO loggedInUser;
   // get the factory
   private final SystemFactory systemFactory = new SystemFactory();
   private final EmployeeService employeeService;
   private final ShiftService shiftService;
   private final EmployeeIntegrationService authService;

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

   public EmployeeDTO getLoggedInUserDTO() {
      if (!loginSuccess.get()) {
         throw new IllegalStateException("No user is currently logged in.");
      }
      return loggedInUser;
   }

   public String getLoggedInUser() {
      if (!loginSuccess.get()) {
         throw new IllegalStateException("No user is currently logged in.");
      }
      int id;
      try {
         id = Integer.parseInt(userId.get());
      } catch (NumberFormatException e) {
         throw new IllegalStateException("Cannot retrieve logged-in user ID, invalid format: " + userId.get(), e);
      }
      EmployeeDTO userDTO = employeeService.getEmployeeByIdAsDTO(id);
      // supply the two arguments right here:
      return String.format("%s (ID: %s)",
            userDTO.getFullName(),
            userDTO.getIsraeliId());
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
         loggedInUser = employeeService.getEmployeeByIdAsDTO(id);
         return true;
      } else {
         errorMessage.set("Invalid user ID");
         return false;
      }
   }
}