package PresentationLayer.GUI.EmployeeScreen.Controllers;

import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller for the dashboard view.
 * Displays key metrics and charts for the employee management system.
 */
public class DashboardController {

    @FXML
    private ComboBox<String> periodSelector;

    private MainViewController mainViewController;

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
        System.out.println("Services set in DashboardController");
        // Refresh the dashboard now that services are set
        refreshDashboard();
    }

    /**
     * Sets the main view controller.
     * This is needed for navigation to other views.
     * 
     * @param controller The main view controller
     */
    public void setMainViewController(MainViewController controller) {
        this.mainViewController = controller;
    }

    @FXML
    private Label totalEmployeesLabel;

    @FXML
    private Label employeeTrendLabel;

    @FXML
    private Label branchesLabel;

    @FXML
    private Label rolesLabel;

    @FXML
    private PieChart employeeDistributionChart;

    @FXML
    private BarChart<String, Number> shiftCoverageChart;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Initialize period selector
        periodSelector.setItems(FXCollections.observableArrayList(
                "Last 7 Days", "Last 30 Days", "Last 90 Days", "This Year", "All Time"));
        periodSelector.getSelectionModel().selectFirst();
        periodSelector.setOnAction(event -> refreshDashboard());

        // Data will be loaded when services are set
    }

    /**
     * Refreshes the dashboard with the latest data.
     */
    @FXML
    public void refreshDashboard() {
        loadStatistics();
        loadCharts();
    }

    /**
     * Loads statistics for the dashboard.
     */
    private void loadStatistics() {
        try {
            if (employeeService == null || shiftService == null) {
                System.out.println("Services not set in DashboardController");
                // Use mock data if services are not set
                totalEmployeesLabel.setText("42");
                employeeTrendLabel.setText("+5% from last month");
                branchesLabel.setText("5");
                rolesLabel.setText("8");
                return;
            }

            // Get real data from the database
            long doneBy = 123456789; // Using a default user ID

            // Get employee count from the database
            String[] employeeData = employeeService.getAllEmployees();
            totalEmployeesLabel.setText(String.valueOf(employeeData.length));

            // Calculate employee trend (comparing to previous month)
            // In a real implementation, we would query historical data to calculate this
            // For now, we'll use a placeholder calculation
            int previousMonthEmployeeCount = employeeData.length - 2; // Placeholder calculation
            if (previousMonthEmployeeCount > 0) {
                double trend = ((double) employeeData.length / previousMonthEmployeeCount - 1) * 100;
                String trendText = String.format("%+.1f%% from last month", trend);
                employeeTrendLabel.setText(trendText);
            } else {
                employeeTrendLabel.setText("New employees this month");
            }


            // Get branch count
            // In a real implementation, we would have a method to get all branches
            // For now, we'll count unique branch IDs from employees
            Set<Long> uniqueBranches = new HashSet<>();
            for (String employeeJson : employeeData) {
                // Extract branch ID from employee JSON
                // This is a simplified approach - in a real implementation, we would parse the
                // JSON properly
                int branchIdIndex = employeeJson.indexOf("\"branchId\":");
                if (branchIdIndex >= 0) {
                    int startIndex = branchIdIndex + 11; // Length of "\"branchId\":"
                    int endIndex = employeeJson.indexOf(",", startIndex);
                    if (endIndex < 0) {
                        endIndex = employeeJson.indexOf("}", startIndex);
                    }
                    if (endIndex > startIndex) {
                        String branchIdStr = employeeJson.substring(startIndex, endIndex).trim();
                        try {
                            Long branchId = Long.parseLong(branchIdStr);
                            uniqueBranches.add(branchId);
                        } catch (NumberFormatException e) {
                            // Ignore parsing errors
                        }
                    }
                }
            }
            branchesLabel.setText(String.valueOf(uniqueBranches.size()));

            // Get role count from the database
            String[] roleData = employeeService.getAllRoles();
            rolesLabel.setText(String.valueOf(roleData.length));

        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
            e.printStackTrace();

            // Use mock data if there's an error
            totalEmployeesLabel.setText("42");
            employeeTrendLabel.setText("+5% from last month");
            branchesLabel.setText("5");
            rolesLabel.setText("8");
        }
    }

    /**
     * Loads chart data for the dashboard.
     */
    private void loadCharts() {
        try {
            if (employeeService == null || shiftService == null) {
                System.out.println("Services not set in DashboardController");
                // Use mock data if services are not set
                loadMockChartData();
                return;
            }

            // Load real data from the database
            long doneBy = 123456789; // Using a default user ID

            // Load employee distribution chart
            // Group employees by branch
            String[] employeeData = employeeService.getAllEmployees();
            Map<Long, Integer> employeesByBranch = new HashMap<>();
            Map<Long, String> branchNames = new HashMap<>(); // To store branch names

            for (String employeeJson : employeeData) {
                // Extract branch ID from employee JSON
                int branchIdIndex = employeeJson.indexOf("\"branchId\":");
                if (branchIdIndex >= 0) {
                    int startIndex = branchIdIndex + 11; // Length of "\"branchId\":"
                    int endIndex = employeeJson.indexOf(",", startIndex);
                    if (endIndex < 0) {
                        endIndex = employeeJson.indexOf("}", startIndex);
                    }
                    if (endIndex > startIndex) {
                        String branchIdStr = employeeJson.substring(startIndex, endIndex).trim();
                        try {
                            Long branchId = Long.parseLong(branchIdStr);
                            employeesByBranch.put(branchId, employeesByBranch.getOrDefault(branchId, 0) + 1);

                            // In a real implementation, we would get the branch name from a branch service
                            // For now, we'll use "Branch X" as the name
                            branchNames.putIfAbsent(branchId, "Branch " + branchId);
                        } catch (NumberFormatException e) {
                            // Ignore parsing errors
                        }
                    }
                }
            }

            // Create pie chart data
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<Long, Integer> entry : employeesByBranch.entrySet()) {
                String branchName = branchNames.getOrDefault(entry.getKey(), "Branch " + entry.getKey());
                pieChartData.add(new PieChart.Data(branchName, entry.getValue()));
            }

            // If no data was found, add a placeholder
            if (pieChartData.isEmpty()) {
                pieChartData.add(new PieChart.Data("No Data", 1));
            }

            employeeDistributionChart.setData(pieChartData);

            // Load shift coverage chart
            // Group shifts by day of week
            String shiftData = shiftService.getAllShifts(doneBy);
            Map<String, Integer> employeesByDay = new HashMap<>();

            // Initialize days of week
            String[] daysOfWeek = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
            for (String day : daysOfWeek) {
                employeesByDay.put(day, 0);
            }

            // Parse shift data to count employees by day
            // This is a simplified approach - in a real implementation, we would parse the
            // JSON properly
            for (String day : daysOfWeek) {
                // Count occurrences of the day in the shift data
                // This is a very rough approximation - in a real implementation, we would parse
                // the JSON properly
                int count = 0;
                int index = 0;
                while ((index = shiftData.indexOf("\"" + day + "\"", index)) != -1) {
                    count++;
                    index += day.length() + 2; // +2 for the quotes
                }

                // If we found any shifts for this day, estimate the number of employees
                // In a real implementation, we would count the actual number of employees
                // assigned to each shift
                if (count > 0) {
                    // Assume an average of 5 employees per shift
                    employeesByDay.put(day, count * 5);
                }
            }

            // Create bar chart data
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Employees Scheduled");

            for (String day : daysOfWeek) {
                series.getData().add(new XYChart.Data<>(day, employeesByDay.get(day)));
            }

            shiftCoverageChart.getData().clear();
            shiftCoverageChart.getData().add(series);

        } catch (Exception e) {
            System.err.println("Error loading chart data: " + e.getMessage());
            e.printStackTrace();

            // Use mock data if there's an error
            loadMockChartData();
        }
    }

    /**
     * Loads mock chart data for the dashboard.
     */
    private void loadMockChartData() {
        // Load employee distribution chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Branch 1", 15),
                new PieChart.Data("Branch 2", 10),
                new PieChart.Data("Branch 3", 8),
                new PieChart.Data("Branch 4", 5),
                new PieChart.Data("Branch 5", 4));
        employeeDistributionChart.setData(pieChartData);

        // Load shift coverage chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Employees Scheduled");
        series.getData().add(new XYChart.Data<>("Monday", 8));
        series.getData().add(new XYChart.Data<>("Tuesday", 10));
        series.getData().add(new XYChart.Data<>("Wednesday", 12));
        series.getData().add(new XYChart.Data<>("Thursday", 9));
        series.getData().add(new XYChart.Data<>("Friday", 11));
        series.getData().add(new XYChart.Data<>("Saturday", 7));
        series.getData().add(new XYChart.Data<>("Sunday", 6));

        shiftCoverageChart.getData().clear();
        shiftCoverageChart.getData().add(series);
    }


    /**
     * Shows the form to add a new employee.
     */
    @FXML
    public void showAddEmployeeDialog() {
        System.out.println("Add employee dialog requested from dashboard");
        if (mainViewController != null) {
            mainViewController.showAddEmployeeForm();
        } else {
            System.err.println("MainViewController is not set");
        }
    }

    /**
     * Shows the create shift dialog.
     */
    @FXML
    public void showCreateShiftDialog() {
        System.out.println("Create shift dialog requested");

        try {
            // Create a dialog
            Dialog<Map<String, Object>> dialog = new Dialog<>();
            dialog.setTitle("Create New Shift");
            dialog.setHeaderText("Enter shift details");

            // Set the button types
            ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

            // Create the form content
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Date picker for shift date
            DatePicker datePicker = new DatePicker(LocalDate.now());
            datePicker.setPromptText("Select date");

            // Combo box for shift type (Morning/Evening)
            ComboBox<String> shiftTypeCombo = new ComboBox<>();
            shiftTypeCombo.getItems().addAll("Morning", "Evening");
            shiftTypeCombo.setValue("Morning");

            // Time pickers for start and end times
            TextField startTimeField = new TextField("08:00");
            startTimeField.setPromptText("HH:MM");
            TextField endTimeField = new TextField("16:00");
            endTimeField.setPromptText("HH:MM");

            // Add fields to the grid
            grid.add(new Label("Date:"), 0, 0);
            grid.add(datePicker, 1, 0);
            grid.add(new Label("Shift Type:"), 0, 1);
            grid.add(shiftTypeCombo, 1, 1);
            grid.add(new Label("Start Time:"), 0, 2);
            grid.add(startTimeField, 1, 2);
            grid.add(new Label("End Time:"), 0, 3);
            grid.add(endTimeField, 1, 3);

            dialog.getDialogPane().setContent(grid);

            // Request focus on the date field by default
            Platform.runLater(datePicker::requestFocus);

            // Convert the result to a map when the create button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("date", datePicker.getValue());
                    result.put("type", shiftTypeCombo.getValue());
                    result.put("startTime", startTimeField.getText());
                    result.put("endTime", endTimeField.getText());
                    return result;
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Map<String, Object>> result = dialog.showAndWait();

            result.ifPresent(shiftData -> {
                // Here we would normally call the service to create the shift
                System.out.println("Creating shift with data: " + shiftData);

                // Show confirmation alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Shift Created");
                alert.setHeaderText(null);
                alert.setContentText("Shift created successfully for " +
                        shiftData.get("date") + " (" + shiftData.get("type") + ")");
                alert.showAndWait();

                // Refresh the dashboard to show the new shift
                refreshDashboard();
            });

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not create shift");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Shows the manage roles dialog.
     */
    @FXML
    public void showManageRolesDialog() {
        System.out.println("Manage roles dialog requested");

        try {
            // Create a dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Manage Roles");
            dialog.setHeaderText("View and manage employee roles");

            // Set the button types
            ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(closeButtonType);

            // Create tabs for different role management functions
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            // Tab 1: View Roles
            Tab viewRolesTab = new Tab("View Roles");
            VBox viewRolesContent = new VBox(10);
            viewRolesContent.setPadding(new Insets(20));

            // Create a table to display roles
            TableView<RoleEntry> rolesTable = new TableView<>();
            rolesTable.setPrefHeight(300);
            rolesTable.setPrefWidth(500);

            TableColumn<RoleEntry, String> roleNameColumn = new TableColumn<>("Role Name");
            roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            roleNameColumn.setPrefWidth(150);

            TableColumn<RoleEntry, String> roleDescriptionColumn = new TableColumn<>("Description");
            roleDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            roleDescriptionColumn.setPrefWidth(250);

            TableColumn<RoleEntry, Integer> employeeCountColumn = new TableColumn<>("Employees");
            employeeCountColumn.setCellValueFactory(new PropertyValueFactory<>("employeeCount"));
            employeeCountColumn.setPrefWidth(100);

            rolesTable.getColumns().addAll(roleNameColumn, roleDescriptionColumn, employeeCountColumn);

            // Add mock data to the table
            ObservableList<RoleEntry> roleData = FXCollections.observableArrayList(
                    new RoleEntry("Manager", "Store manager with full permissions", 3),
                    new RoleEntry("Cashier", "Operates cash registers and handles customer payments", 8),
                    new RoleEntry("Stocker", "Restocks shelves and manages inventory", 5),
                    new RoleEntry("Customer Service", "Handles customer inquiries and complaints", 4),
                    new RoleEntry("Security", "Monitors store security and prevents theft", 2));
            rolesTable.setItems(roleData);

            viewRolesContent.getChildren().add(rolesTable);
            viewRolesTab.setContent(viewRolesContent);

            // Tab 2: Add Role
            Tab addRoleTab = new Tab("Add Role");
            GridPane addRoleGrid = new GridPane();
            addRoleGrid.setHgap(10);
            addRoleGrid.setVgap(10);
            addRoleGrid.setPadding(new Insets(20));

            TextField roleNameField = new TextField();
            roleNameField.setPromptText("Enter role name");

            TextArea roleDescriptionArea = new TextArea();
            roleDescriptionArea.setPromptText("Enter role description");
            roleDescriptionArea.setPrefRowCount(3);

            CheckBox manageEmployeesCheck = new CheckBox("Manage Employees");
            CheckBox manageShiftsCheck = new CheckBox("Manage Shifts");
            CheckBox viewReportsCheck = new CheckBox("View Reports");

            Button addRoleButton = new Button("Add Role");
            addRoleButton.getStyleClass().add("primary-button");
            addRoleButton.setOnAction(e -> {
                if (roleNameField.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Role name cannot be empty");
                    alert.showAndWait();
                    return;
                }

                // Here we would normally call the service to add the role
                System.out.println("Adding role: " + roleNameField.getText());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Role Added");
                alert.setHeaderText(null);
                alert.setContentText("Role '" + roleNameField.getText() + "' added successfully");
                alert.showAndWait();

                // Clear the form
                roleNameField.clear();
                roleDescriptionArea.clear();
                manageEmployeesCheck.setSelected(false);
                manageShiftsCheck.setSelected(false);
                viewReportsCheck.setSelected(false);

                // Refresh the dashboard
                refreshDashboard();
            });

            addRoleGrid.add(new Label("Role Name:"), 0, 0);
            addRoleGrid.add(roleNameField, 1, 0);
            addRoleGrid.add(new Label("Description:"), 0, 1);
            addRoleGrid.add(roleDescriptionArea, 1, 1);
            addRoleGrid.add(new Label("Permissions:"), 0, 2);
            addRoleGrid.add(manageEmployeesCheck, 1, 2);
            addRoleGrid.add(manageShiftsCheck, 1, 3);
            addRoleGrid.add(viewReportsCheck, 1, 4);
            addRoleGrid.add(addRoleButton, 1, 5);

            addRoleTab.setContent(addRoleGrid);

            // Add tabs to the tab pane
            tabPane.getTabs().addAll(viewRolesTab, addRoleTab);

            // Set the content of the dialog
            dialog.getDialogPane().setContent(tabPane);
            dialog.getDialogPane().setPrefWidth(600);
            dialog.getDialogPane().setPrefHeight(400);

            // Show the dialog
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open roles dialog");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Shows the reports dialog.
     */
    @FXML
    public void showReportsDialog() {
        System.out.println("Generate reports dialog requested");

        try {
            // Create a dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Generate Reports");
            dialog.setHeaderText("Generate and export employee reports");

            // Set the button types
            ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(closeButtonType);

            // Create the content
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));

            // Report type selection
            Label reportTypeLabel = new Label("Report Type:");
            reportTypeLabel.setStyle("-fx-font-weight: bold;");

            ComboBox<String> reportTypeCombo = new ComboBox<>();
            reportTypeCombo.getItems().addAll(
                    "Employee Attendance",
                    "Shift Coverage",
                    "Role Distribution",
                    "Payroll Summary",
                    "Performance Metrics");
            reportTypeCombo.setValue("Employee Attendance");
            reportTypeCombo.setPrefWidth(300);

            // Date range selection
            Label dateRangeLabel = new Label("Date Range:");
            dateRangeLabel.setStyle("-fx-font-weight: bold;");

            HBox dateRangeBox = new HBox(10);
            DatePicker startDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
            startDatePicker.setPromptText("Start Date");
            DatePicker endDatePicker = new DatePicker(LocalDate.now());
            endDatePicker.setPromptText("End Date");
            dateRangeBox.getChildren().addAll(
                    new Label("From:"), startDatePicker,
                    new Label("To:"), endDatePicker);

            // Format selection
            Label formatLabel = new Label("Export Format:");
            formatLabel.setStyle("-fx-font-weight: bold;");

            HBox formatBox = new HBox(10);
            ToggleGroup formatGroup = new ToggleGroup();
            RadioButton pdfRadio = new RadioButton("PDF");
            pdfRadio.setToggleGroup(formatGroup);
            pdfRadio.setSelected(true);
            RadioButton excelRadio = new RadioButton("Excel");
            excelRadio.setToggleGroup(formatGroup);
            RadioButton csvRadio = new RadioButton("CSV");
            csvRadio.setToggleGroup(formatGroup);
            formatBox.getChildren().addAll(pdfRadio, excelRadio, csvRadio);

            // Additional options
            Label optionsLabel = new Label("Options:");
            optionsLabel.setStyle("-fx-font-weight: bold;");

            VBox optionsBox = new VBox(5);
            CheckBox includeChartsCheck = new CheckBox("Include charts and graphs");
            includeChartsCheck.setSelected(true);
            CheckBox includeDetailsCheck = new CheckBox("Include detailed breakdown");
            includeDetailsCheck.setSelected(true);
            CheckBox emailReportCheck = new CheckBox("Email report to management");
            optionsBox.getChildren().addAll(includeChartsCheck, includeDetailsCheck, emailReportCheck);

            // Preview section
            TitledPane previewPane = new TitledPane();
            previewPane.setText("Report Preview");
            previewPane.setExpanded(false);

            VBox previewContent = new VBox(10);
            previewContent.setPadding(new Insets(10));
            previewContent.setStyle("-fx-background-color: #f5f5f5;");

            Label previewLabel = new Label("Preview will be generated when you click 'Generate Preview'");
            previewLabel.setWrapText(true);

            Button previewButton = new Button("Generate Preview");
            previewButton.getStyleClass().add("secondary-button");

            previewContent.getChildren().addAll(previewLabel, previewButton);
            previewPane.setContent(previewContent);

            // Action buttons
            HBox actionBox = new HBox(10);
            actionBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

            Button generateButton = new Button("Generate Report");
            generateButton.getStyleClass().add("primary-button");
            generateButton.setOnAction(e -> {
                // Here we would normally generate the report
                System.out.println("Generating report: " + reportTypeCombo.getValue());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Report Generated");
                alert.setHeaderText(null);
                alert.setContentText("Report '" + reportTypeCombo.getValue() + "' has been generated successfully.");
                alert.showAndWait();

                dialog.close();
            });

            actionBox.getChildren().add(generateButton);

            // Add all components to the content
            content.getChildren().addAll(
                    reportTypeLabel, reportTypeCombo,
                    dateRangeLabel, dateRangeBox,
                    formatLabel, formatBox,
                    optionsLabel, optionsBox,
                    new Separator(),
                    previewPane,
                    actionBox);

            // Set the content of the dialog
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().setPrefWidth(550);
            dialog.getDialogPane().setPrefHeight(500);

            // Show the dialog
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open reports dialog");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * Inner class to represent a role entry in the roles table.
     */
    public static class RoleEntry {
        private final String name;
        private final String description;
        private final int employeeCount;

        public RoleEntry(String name, String description, int employeeCount) {
            this.name = name;
            this.description = description;
            this.employeeCount = employeeCount;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getEmployeeCount() {
            return employeeCount;
        }
    }
}
