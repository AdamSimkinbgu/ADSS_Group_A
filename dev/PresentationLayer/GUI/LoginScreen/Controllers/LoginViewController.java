package PresentationLayer.GUI.LoginScreen.Controllers;

import PresentationLayer.GUI.Common.Navigation.ScreenNavigator;
import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import PresentationLayer.GUI.LoginScreen.ViewModels.LoginViewModel;
import PresentationLayer.GUI.MainMenuScreen.Controllers.MainMenuController;
import PresentationLayer.GUI.MainMenuScreen.ViewModels.MainMenuViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
   private void onLoginClicked() {
      if (viewModel.login()) {
         // single place for menu navigation
         ScreenNavigator.getInstance().navigateTo(ScreensEnum.MENU, loader -> {
            // build and inject your main‐menu VM/controller here...
            MainMenuViewModel mvm = new MainMenuViewModel(viewModel.getLoggedInUser());
            loader.setControllerFactory(type -> {
               if (type == MainMenuController.class) {
                  return new MainMenuController(mvm);
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
