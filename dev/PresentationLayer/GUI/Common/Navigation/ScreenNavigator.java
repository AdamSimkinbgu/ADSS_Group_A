package PresentationLayer.GUI.Common.Navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import PresentationLayer.GUI.LoginScreen.Controllers.LoginViewController;
import PresentationLayer.GUI.LoginScreen.ViewModels.LoginViewModel;
import PresentationLayer.GUI.MainMenuScreen.Controllers.MainMenuController;
import PresentationLayer.GUI.MainMenuScreen.ViewModels.MainMenuViewModel;

public class ScreenNavigator {
   private static final ScreenNavigator instance = new ScreenNavigator();

   private Stage primaryStage;
   private String globalCss;

   private ScreenNavigator() {
   }

   public static ScreenNavigator getInstance() {
      return instance;
   }

   /** Must be called once, right after you have your Stage */
   public void init(Stage stage, String cssUrl) {
      this.primaryStage = stage;
      this.globalCss = cssUrl;
   }

   /** Simple navigation (no controller-injection) */
   public void navigateTo(ScreensEnum screen) {
      navigateTo(screen, null);
   }

   /**
    * Navigate to a screen, optionally configuring the FXMLLoader
    * (e.g. setControllerFactory, pass ViewModel, etc.)
    */
   public void navigateTo(ScreensEnum screen, Consumer<FXMLLoader> preloader) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.getFxmlPath()));
         if (preloader != null) {
            preloader.accept(loader);
         }
         URL loc = getClass().getResource(screen.getFxmlPath());
         System.out.println(">> Switching to " + screen.name()
               + " @ " + screen.getFxmlPath()
               + " → resource URL = " + loc);
         Parent root = loader.load();
         Scene scene = primaryStage.getScene();

         if (scene == null) {
            scene = new Scene(root);
            primaryStage.setScene(scene);
         } else {
            scene.setRoot(root);
         }

         // resize the window to fit the new content’s prefWidth/prefHeight:
         primaryStage.sizeToScene();
         primaryStage.centerOnScreen();

         // re-apply your global stylesheet
         if (globalCss != null && !scene.getStylesheets().contains(globalCss)) {
            scene.getStylesheets().add(globalCss);
         }

         primaryStage.show();
      } catch (IOException e) {
         e.printStackTrace();
         // TODO: your error-dialog or log
      }
   }
}