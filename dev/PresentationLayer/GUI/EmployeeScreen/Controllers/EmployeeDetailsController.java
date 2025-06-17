package PresentationLayer.GUI.EmployeeScreen.Controllers;

import PresentationLayer.GUI.EmployeeScreen.Models.EmployeeUIModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the employee details view.
 * Displays detailed information about a selected employee.
 */
public class EmployeeDetailsController {

    @FXML
    private Label employeeNameLabel;

    @FXML
    private Label employeeIdLabel;

    @FXML
    private Label employeeStatusLabel;

    @FXML
    private Button toggleStatusButton;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label israeliIdLabel;

    @FXML
    private Label branchLabel;

    @FXML
    private Label bankAccountLabel;

    @FXML
    private Label salaryLabel;

    @FXML
    private Label employmentTermsLabel;

    @FXML
    private Label startDateLabel;

    @FXML
    private ListView<String> rolesListView;

    @FXML
    private DatePicker shiftDatePicker;

    @FXML
    private TableView<ShiftEntry> shiftsTableView;

    @FXML
    private TableColumn<ShiftEntry, String> shiftDateColumn;

    @FXML
    private TableColumn<ShiftEntry, String> shiftTypeColumn;

    @FXML
    private TableColumn<ShiftEntry, String> shiftTimeColumn;

    @FXML
    private TableColumn<ShiftEntry, String> shiftBranchColumn;

    @FXML
    private TableColumn<ShiftEntry, String> shiftRoleColumn;

    @FXML
    private GridPane availabilityGrid;

    private EmployeeUIModel employee;
    private MainViewController mainViewController;

    /**
     * Sets the main view controller.
     * This is needed for navigation back to the employee list.
     * 
     * @param controller The main view controller
     */
    public void setMainViewController(MainViewController controller) {
        this.mainViewController = controller;
    }

    /**
     * Sets the employee to display.
     * 
     * @param employee The employee to display
     */
    public void setEmployee(EmployeeUIModel employee) {
        this.employee = employee;
        loadEmployeeDetails();
    }

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Initialize shift table columns
        shiftDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        shiftTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        shiftTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        shiftBranchColumn.setCellValueFactory(new PropertyValueFactory<>("branch"));
        shiftRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Initialize date picker
        shiftDatePicker.setValue(LocalDate.now());
        shiftDatePicker.setOnAction(event -> loadShifts());

        // Create availability grid
        createAvailabilityGrid();
    }

    /**
     * Loads employee details into the UI.
     */
    private void loadEmployeeDetails() {
        if (employee == null) {
            return;
        }

        // Set header information
        employeeNameLabel.setText(employee.getFirstName() + " " + employee.getLastName());
        employeeIdLabel.setText("ID: " + employee.getIsraeliId());
        employeeStatusLabel.setText("Status: " + (employee.isActive() ? "Active" : "Inactive"));

        // Set toggle button text based on current status
        toggleStatusButton.setText(employee.isActive() ? "Deactivate" : "Activate");

        // Set basic information
        firstNameLabel.setText(employee.getFirstName());
        lastNameLabel.setText(employee.getLastName());
        israeliIdLabel.setText(String.valueOf(employee.getIsraeliId()));
        branchLabel.setText("Branch " + employee.getBranchId());

        // Set mock data for fields not in the EmployeeUIModel
        bankAccountLabel.setText("Bank Leumi - 12345");
        salaryLabel.setText("₪5,000");
        employmentTermsLabel.setText("Full-time");
        startDateLabel.setText("2023-01-15");

        // Load roles
        loadRoles();

        // Load shifts
        loadShifts();

        // Load availability
        loadAvailability();
    }

    /**
     * Loads the employee's roles into the UI.
     */
    private void loadRoles() {
        // In a real implementation, this would get data from the service layer
        // For now, use mock data based on the roles string
        String[] roles = employee.getRoles().split(", ");
        rolesListView.setItems(FXCollections.observableArrayList(roles));
    }

    /**
     * Loads the employee's shifts into the UI.
     */
    private void loadShifts() {
        // In a real implementation, this would get data from the service layer
        // For now, use mock data
        ObservableList<ShiftEntry> shiftData = FXCollections.observableArrayList(
                new ShiftEntry(LocalDate.now().plusDays(1), "Morning", "08:00 - 16:00",
                        "Branch " + employee.getBranchId(), "Cashier"),
                new ShiftEntry(LocalDate.now().plusDays(3), "Evening", "16:00 - 00:00",
                        "Branch " + employee.getBranchId(), "Manager"),
                new ShiftEntry(LocalDate.now().plusDays(5), "Morning", "08:00 - 16:00",
                        "Branch " + employee.getBranchId(), "Stocker"),
                new ShiftEntry(LocalDate.now().plusDays(7), "Evening", "16:00 - 00:00",
                        "Branch " + employee.getBranchId(), "Cashier"));

        shiftsTableView.setItems(shiftData);
    }

    /**
     * Creates the availability grid.
     */
    private void createAvailabilityGrid() {
        // Add column headers (days of week)
        for (int col = 1; col <= 7; col++) {
            Label dayLabel = new Label(DayOfWeek.of(col).toString());
            dayLabel.getStyleClass().add("availability-header");
            availabilityGrid.add(dayLabel, col, 0);
        }

        // Add row headers (shift types)
        Label morningLabel = new Label("Morning");
        morningLabel.getStyleClass().add("availability-header");
        availabilityGrid.add(morningLabel, 0, 1);

        Label eveningLabel = new Label("Evening");
        eveningLabel.getStyleClass().add("availability-header");
        availabilityGrid.add(eveningLabel, 0, 2);

        Label nightLabel = new Label("Night");
        nightLabel.getStyleClass().add("availability-header");
        availabilityGrid.add(nightLabel, 0, 3);
    }

    /**
     * Loads the employee's availability into the UI.
     */
    private void loadAvailability() {
        // In a real implementation, this would get data from the service layer
        // For now, use mock data
        Map<String, Boolean> availability = new HashMap<>();
        availability.put("MONDAY_MORNING", true);
        availability.put("MONDAY_EVENING", false);
        availability.put("MONDAY_NIGHT", false);
        availability.put("TUESDAY_MORNING", true);
        availability.put("TUESDAY_EVENING", true);
        availability.put("TUESDAY_NIGHT", false);
        availability.put("WEDNESDAY_MORNING", false);
        availability.put("WEDNESDAY_EVENING", true);
        availability.put("WEDNESDAY_NIGHT", false);
        availability.put("THURSDAY_MORNING", true);
        availability.put("THURSDAY_EVENING", false);
        availability.put("THURSDAY_NIGHT", false);
        availability.put("FRIDAY_MORNING", true);
        availability.put("FRIDAY_EVENING", false);
        availability.put("FRIDAY_NIGHT", false);
        availability.put("SATURDAY_MORNING", false);
        availability.put("SATURDAY_EVENING", false);
        availability.put("SATURDAY_NIGHT", false);
        availability.put("SUNDAY_MORNING", true);
        availability.put("SUNDAY_EVENING", true);
        availability.put("SUNDAY_NIGHT", false);

        // Populate the grid with availability indicators
        for (int col = 1; col <= 7; col++) {
            DayOfWeek day = DayOfWeek.of(col);

            for (int row = 1; row <= 3; row++) {
                String shift;
                if (row == 1)
                    shift = "MORNING";
                else if (row == 2)
                    shift = "EVENING";
                else
                    shift = "NIGHT";

                String key = day.toString() + "_" + shift;
                boolean isAvailable = availability.getOrDefault(key, false);

                Label availabilityLabel = new Label(isAvailable ? "✓" : "✗");
                availabilityLabel.getStyleClass().add(isAvailable ? "available" : "unavailable");
                availabilityGrid.add(availabilityLabel, col, row);
            }
        }
    }

    /**
     * Navigates back to the employee list.
     */
    @FXML
    public void goBackToList() {
        if (mainViewController != null) {
            mainViewController.showEmployees();
        }
    }

    /**
     * Shows the form to edit the employee.
     */
    @FXML
    public void editEmployee() {
        System.out.println("Edit employee requested: " + employee.getIsraeliId());
        if (mainViewController != null) {
            mainViewController.showEditEmployeeForm(employee);
        } else {
            System.err.println("MainViewController is not set");
        }
    }

    /**
     * Toggles the employee's active status.
     */
    @FXML
    public void toggleEmployeeStatus() {
        // In a real implementation, this would update the employee's status in the
        // service layer
        employee.setActive(!employee.isActive());

        // Update UI
        employeeStatusLabel.setText("Status: " + (employee.isActive() ? "Active" : "Inactive"));
        toggleStatusButton.setText(employee.isActive() ? "Deactivate" : "Activate");

        System.out.println("Employee status toggled: " + employee.getIsraeliId() + " is now " +
                (employee.isActive() ? "active" : "inactive"));
    }

    /**
     * Shows the manage roles dialog.
     */
    @FXML
    public void manageRoles() {
        System.out.println("Manage roles requested for employee: " + employee.getIsraeliId());
        // In the future, this will show a dialog to manage the employee's roles
    }

    /**
     * Shows all shifts for the employee.
     */
    @FXML
    public void viewAllShifts() {
        System.out.println("View all shifts requested for employee: " + employee.getIsraeliId());
        // In the future, this will show a view with all shifts for the employee
    }

    /**
     * Shows the update availability dialog.
     */
    @FXML
    public void updateAvailability() {
        System.out.println("Update availability requested for employee: " + employee.getIsraeliId());
        // In the future, this will show a dialog to update the employee's availability
    }

    /**
     * Inner class to represent a shift entry in the shifts table.
     */
    public static class ShiftEntry {
        private final String date;
        private final String type;
        private final String time;
        private final String branch;
        private final String role;

        public ShiftEntry(LocalDate date, String type, String time, String branch, String role) {
            this.date = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.type = type;
            this.time = time;
            this.branch = branch;
            this.role = role;
        }

        public String getDate() {
            return date;
        }

        public String getType() {
            return type;
        }

        public String getTime() {
            return time;
        }

        public String getBranch() {
            return branch;
        }

        public String getRole() {
            return role;
        }
    }
}
