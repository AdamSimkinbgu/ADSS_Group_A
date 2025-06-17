package PresentationLayer.GUI;

import PresentationLayer.GUI.Common.Navigation.ScreenNavigator;
import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import PresentationLayer.GUI.LoginScreen.Controllers.LoginViewController;
import PresentationLayer.GUI.LoginScreen.ViewModels.LoginViewModel;
import Util.Database;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppLauncher extends Application {

   @Override
   public void start(Stage primaryStage) throws Exception {
      // 1) init DB, CSS, navigator
      Database.init(false);
      String css = getClass().getResource("/GUI/Common/Styles/main.css").toExternalForm();
      System.out.println("CSS file loaded successfully: " + css);

      ScreenNavigator navigator = ScreenNavigator.getInstance();
      primaryStage.setTitle("Super Lee - Fully Integrated Management System");
      navigator.init(primaryStage, css);

      // 2) show the LOGIN screen only
      navigator.navigateTo(ScreensEnum.LOGIN, loader -> {
         LoginViewModel vm = new LoginViewModel();
         // when FXMLLoader needs a LoginViewController, we give it one with our VM
         loader.setControllerFactory(type -> {
            if (type == LoginViewController.class) {
               return new LoginViewController(vm);
            }
            // fallback for anything else (if you have other controllers in that FXML)
            try {
               return type.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         });
      });

      primaryStage.setTitle("My Application");
      primaryStage.show();
   }

   public void run(String[] args) {
      launch(args);
   }
}