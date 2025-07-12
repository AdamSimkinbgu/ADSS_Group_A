package PresentationLayer.GUI.LoginScreen.Controllers;

import PresentationLayer.GUI.Common.Navigation.ScreenNavigator;
import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import PresentationLayer.GUI.LoginScreen.ViewModels.LoginViewModel;
import PresentationLayer.GUI.MainMenuScreen.Controllers.MainMenuController;
import PresentationLayer.GUI.MainMenuScreen.ViewModels.MainMenuViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginViewController {
   private final LoginViewModel viewModel;

   public LoginViewController(LoginViewModel vm) {
      this.viewModel = vm;
   }

   @FXML
   private TextField userIdField;
   @FXML
   private Label errorLabel;

   @FXML
   public void initialize() {
      userIdField.textProperty().bindBidirectional(viewModel.userIdProperty());
      errorLabel.textProperty().bind(viewModel.errorMessageProperty());
   }

   @FXML
   private void onShowTesterInfo() {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Test Accounts");
      alert.setHeaderText("Click 'Select' to login as the corresponding user");

      GridPane grid = new GridPane();
      grid.setHgap(10);
      grid.setVgap(10);

      // Example test accounts – add as many as you like
      String[][] testers = {
            { "Admin", "123456789" },
            { "Store Manager", "111111111" },
            { "HR Manager", "111111111" },
            { "Cashier", "333333333" },
            { "Stocker", "222222222" },
            { "Transport Manager", "444444444" },
            { "DriverE", "555555555" },
            { "Cleaner", "666666666" },
            { "Supplier Manager", "666666666" },
            { "Inventory Manager", "777777777" }
      };

      for (int i = 0; i < testers.length; i++) {
         String name = testers[i][0];
         String id = testers[i][1];

         Label lbl = new Label(name + ":");
         TextField tf = new TextField(id);
         tf.setEditable(false);

         Button copy = new Button("Select");
         int row = i;
         copy.setOnAction(_ -> {
            // set the userIdField to the selected test ID and simulate login then close the
            // alert
            userIdField.setText(id);
            onLoginClicked();
            alert.close();
         });

         grid.add(lbl, 0, row);
         grid.add(tf, 1, row);
         grid.add(copy, 2, row);
      }

      alert.getDialogPane().setContent(grid);
      alert.showAndWait();
   }

   @FXML
   private void onLoginClicked() {
      if (viewModel.login()) {
         // single place for menu navigation
         ScreenNavigator.getInstance().navigateTo(ScreensEnum.MENU, loader -> {
            // build and inject your main‐menu VM/controller here...
            MainMenuViewModel mvm = new MainMenuViewModel(viewModel.getLoggedInUser());
            loader.setControllerFactory(type -> {
               if (type == MainMenuController.class) {
                  return new MainMenuController(mvm, viewModel.getLoggedInUserDTO());
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
}
