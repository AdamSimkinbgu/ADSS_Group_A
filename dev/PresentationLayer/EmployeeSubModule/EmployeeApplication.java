package PresentationLayer.EmployeeSubModule;

import PresentationLayer.EmployeeSubModule.controllers.MainViewController;
import PresentationLayer.EmployeeSubModule.utils.ServiceFacade;
import Util.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

/**
 * Main application class for the Employee Management System GUI.
 * This class serves as the entry point for the JavaFX application.
 */
public class EmployeeApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Initialize the database
            System.out.println("Initializing database...");
            Database.init(false);

            // Initialize the ServiceFacade to get access to services
            System.out.println("Initializing services...");
            ServiceFacade serviceFacade = ServiceFacade.getInstance();

            // Try loading directly from file system
            String filePath = "dev/PresentationLayer/EmployeeSubModule/views/MainView.fxml";
            File file = new File(filePath);
            System.out.println("Trying to load FXML from file: " + file.getAbsolutePath());

            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(new FileInputStream(file));

            // Get the controller and set the services
            MainViewController controller = loader.getController();
            controller.setServices(serviceFacade.getEmployeeService(), serviceFacade.getShiftService());

            // Load CSS file
            String cssPath = "dev/PresentationLayer/EmployeeSubModule/styles/main.css";
            File cssFile = new File(cssPath);
            if (cssFile.exists()) {
                String cssUrl = cssFile.toURI().toURL().toExternalForm();
                root.getStylesheets().add(cssUrl);
                System.out.println("CSS loaded from: " + cssUrl);
            } else {
                System.err.println("CSS file not found at: " + cssFile.getAbsolutePath());
            }

            // Set up the primary stage
            primaryStage.setTitle("Employee Management System");
            primaryStage.setScene(new Scene(root, 1024, 768));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Main method to launch the JavaFX application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
