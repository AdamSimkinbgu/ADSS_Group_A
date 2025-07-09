package PresentationLayer.GUI;

import java.awt.Taskbar;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import PresentationLayer.GUI.Common.Navigation.ScreenNavigator;
import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import PresentationLayer.GUI.LoginScreen.Controllers.LoginViewController;
import PresentationLayer.GUI.LoginScreen.ViewModels.LoginViewModel;
import PresentationLayer.GUI.MainMenuScreen.Controllers.MainMenuController;
import PresentationLayer.GUI.MainMenuScreen.ViewModels.MainMenuViewModel;
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
      navigator.init(primaryStage, css);

      // 2) show the MAIN MENU screen only
      navigator.navigateTo(ScreensEnum.MENU, loader -> {
         // create & inject your MainMenuViewModel
         MainMenuViewModel vm = new MainMenuViewModel();

         // tell FXMLLoader how to build MainMenuController
         loader.setControllerFactory(type -> {
            if (type == MainMenuController.class) {
               return new MainMenuController(vm,
                     null);
            }
            try {
               return type.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         });
      });

      // 3) application icon & show stage
      Image fxIcon = new Image(getClass().getResourceAsStream("/icon/SuperLee128Icon.png"));
      if (Taskbar.isTaskbarSupported()) {
         Taskbar.getTaskbar().setIconImage(
               SwingFXUtils.fromFXImage(fxIcon, null));
      }

      primaryStage.setTitle("Super Lee - Fully Integrated Management System");
      primaryStage.show();
   }

   public void run(String[] args) {
      launch(args);
   }
}