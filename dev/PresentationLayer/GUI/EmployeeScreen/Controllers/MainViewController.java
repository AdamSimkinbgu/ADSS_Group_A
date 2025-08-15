package PresentationLayer.GUI.EmployeeScreen.Controllers;

import PresentationLayer.GUI.EmployeeScreen.Models.EmployeeUIModel;
import PresentationLayer.GUI.Common.Navigation.ScreenNavigator;
import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import PresentationLayer.GUI.LoginScreen.Controllers.LoginViewController;
import PresentationLayer.GUI.LoginScreen.ViewModels.LoginViewModel;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the main view of the Employee Management System.
 * Handles navigation between different views and other UI interactions.
 */
public class MainViewController {

    private static final Logger logger = Logger.getLogger(MainViewController.class.getName());

    @FXML
    private StackPane contentArea;

    private EmployeeService employeeService;
    private ShiftService shiftService;

    /**
     * Sets the services for this controller.
     * This method should be called after the controller is initialized.
     * 
     * @param employeeService The EmployeeService to use
     * @param shiftService    The ShiftService to use
     */
    public void setServices(EmployeeService employeeService, ShiftService shiftService) {
        this.employeeService = employeeService;
        this.shiftService = shiftService;
        logger.info("Services set in MainViewController");
    }

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Set default user ID (can be updated when login is implemented)
        // long currentUserId = 123456789; // Example user ID
        // userLabel.setText("Logged in as: " + currentUserId);
    }

    /**
     * Shows the dashboard view.
     */
    @FXML
    public void showDashboard() {
        logger.info("Dashboard view requested");
        DashboardController controller = loadView("/PresentationLayer/GUI/EmployeeScreen/Views/DashboardView.fxml");
        if (controller != null) {
            controller.setMainViewController(this);
            if (employeeService != null && shiftService != null) {
                controller.setServices(employeeService, shiftService);
            }
        }
    }

    /**
     * Shows the employees view.
     */
    @FXML
    public void showEmployees() {
        logger.info("Employees view requested");
        EmployeeListController controller = loadView(
                "/PresentationLayer/GUI/EmployeeScreen/Views/EmployeeListView.fxml");
        if (controller != null) {
            controller.setMainViewController(this);
            if (employeeService != null) {
                controller.setEmployeeService(employeeService);
            }
        }
    }

    /**
     * Shows the shifts view.
     */
    @FXML
    public void showShifts() {
        logger.info("Shifts view requested");
        ShiftCalendarController controller = loadView(
                "/PresentationLayer/GUI/EmployeeScreen/Views/ShiftCalendarView.fxml");
        if (controller != null) {
            controller.setMainViewController(this);
            if (shiftService != null) {
                controller.setShiftService(shiftService);
            }
            if (employeeService != null) {
                controller.setEmployeeService(employeeService);
            }
        }
    }

    /**
     * Shows the roles view.
     */
    @FXML
    public void showRoles() {
        logger.info("Roles view requested");
        RoleManagementController controller = loadView(
                "/PresentationLayer/GUI/EmployeeScreen/Views/RoleManagementView.fxml");
        if (controller != null) {
            controller.setMainViewController(this);
            if (employeeService != null) {
                controller.setEmployeeService(employeeService);
            }
        }
    }

    /**
     * Shows the reports view.
     */
    @FXML
    public void showReports() {
        logger.info("Reports view requested");
        // Load the dashboard view and show the reports dialog
        DashboardController controller = loadView("/PresentationLayer/GUI/EmployeeScreen/Views/DashboardView.fxml");
        if (controller != null) {
            controller.setMainViewController(this);
            controller.setServices(employeeService, shiftService);
            controller.showReportsDialog();
        }
    }

    /**
     * Loads a view into the content area.
     * 
     * @param fxmlPath The path to the FXML file to load
     * @return The controller of the loaded view, or null if loading failed
     */
    private <T> T loadView(String fxmlPath) {
        try {
            // Convert path from /PresentationLayer/GUI/... to /GUI/...
            String correctedPath = fxmlPath.replace("/PresentationLayer/GUI", "/GUI");
            logger.info("Loading view from: " + correctedPath);

            // Use proper resource loading with corrected path
            FXMLLoader loader = new FXMLLoader(getClass().getResource(correctedPath));
            Parent view = loader.load();

            // Clear existing content and add new view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            return loader.getController();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading view", e);
            // Show error message to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Loading View");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load view: " + e.getMessage());
            alert.showAndWait();
            return null;
        }
    }

    /**
     * Shows the employee details view for the selected employee.
     * 
     * @param employee The employee to show details for
     */
    public void showEmployeeDetails(EmployeeUIModel employee) {
        logger.info("Employee details view requested for: " + employee.getIsraeliId());
        EmployeeDetailsController controller = loadView(
                "/PresentationLayer/GUI/EmployeeScreen/Views/EmployeeDetailsView.fxml");
        if (controller != null) {
            controller.setMainViewController(this);
            controller.setEmployee(employee);
        }
    }

    /**
     * Shows the employee form view for editing an existing employee.
     * 
     * @param employee The employee to edit
     */
    public void showEditEmployeeForm(EmployeeUIModel employee) {
        logger.info("Edit employee form requested for: " + employee.getIsraeliId());
        EmployeeFormController controller = loadView(
                "/PresentationLayer/GUI/EmployeeScreen/Views/EmployeeFormView.fxml");
        if (controller != null) {
            controller.setMainViewController(this);
            controller.setEmployee(employee);
        }
    }

    /**
     * Shows the employee form view for creating a new employee.
     */
    public void showAddEmployeeForm() {
        logger.info("Add employee form requested");
        EmployeeFormController controller = loadView(
                "/PresentationLayer/GUI/EmployeeScreen/Views/EmployeeFormView.fxml");
        if (controller != null) {
            controller.setMainViewController(this);
            controller.setEmployee(null); // null for create mode
        }
    }

    /**
     * Shows the availability view.
     */
    @FXML
    public void showAvailability() {
        logger.info("Availability view requested");
        AvailabilityController controller = loadView(
                "/PresentationLayer/GUI/EmployeeScreen/Views/AvailabilityView.fxml");
        if (controller != null) {
            controller.setMainViewController(this);
            if (employeeService != null) {
                controller.setEmployeeService(employeeService);
            }
            if (shiftService != null) {
                controller.setShiftService(shiftService);
            }
        }
    }
}
