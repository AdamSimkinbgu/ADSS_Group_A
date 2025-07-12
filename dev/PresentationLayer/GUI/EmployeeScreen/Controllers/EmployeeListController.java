package PresentationLayer.GUI.EmployeeScreen.Controllers;

import PresentationLayer.GUI.EmployeeScreen.Models.EmployeeUIModel;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import DTOs.EmployeeDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * Controller for the employee list view.
 * Handles loading employee data, search and filter operations, and employee
 * actions.
 */
public class EmployeeListController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TableView<EmployeeUIModel> employeeTable;

    @FXML
    private TableColumn<EmployeeUIModel, Long> idColumn;

    @FXML
    private TableColumn<EmployeeUIModel, String> firstNameColumn;

    @FXML
    private TableColumn<EmployeeUIModel, String> lastNameColumn;

    @FXML
    private TableColumn<EmployeeUIModel, String> rolesColumn;

    @FXML
    private TableColumn<EmployeeUIModel, Long> branchIdColumn;

    @FXML
    private TableColumn<EmployeeUIModel, String> statusColumn;

    @FXML
    private TableColumn<EmployeeUIModel, Void> actionsColumn;

    // Service for accessing employee data
    private EmployeeService employeeService;
    private ObservableList<EmployeeUIModel> employeeList;

    private MainViewController mainViewController;

    /**
     * Sets the main view controller.
     * This is needed for navigation to the employee details view.
     * 
     * @param controller The main view controller
     */
    public void setMainViewController(MainViewController controller) {
        this.mainViewController = controller;
    }

    /**
     * Sets the employee service.
     * This method should be called after the controller is initialized.
     * 
     * @param employeeService The EmployeeService to use
     */
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
        System.out.println("EmployeeService set in EmployeeListController");

        // Refresh the employee list with real data
        if (employeeTable != null) {
            refreshEmployeeList();
        }
    }

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // In a real implementation, this would initialize the service layer
        // employeeService = ServiceFacade.getInstance().getEmployeeService();

        // Initialize filter options
        filterComboBox.setItems(FXCollections.observableArrayList(
                "All Employees", "Active Only", "Inactive Only", "By Branch", "By Role"));
        filterComboBox.getSelectionModel().selectFirst();

        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("israeliId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));
        branchIdColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Configure actions column with buttons
        setupActionsColumn();

        // Data will be loaded when the service is set

        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEmployees();
        });

        // Add filter functionality
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterEmployees();
        });
    }

    /**
     * Sets up the actions column with edit and details buttons.
     */
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> {
            return new TableCell<>() {
                private final Button editButton = new Button("Edit");
                private final Button detailsButton = new Button("Details");

                {
                    editButton.getStyleClass().add("action-button");
                    detailsButton.getStyleClass().add("action-button");

                    editButton.setOnAction(event -> {
                        EmployeeUIModel employee = getTableView().getItems().get(getIndex());
                        editEmployee(employee);
                    });

                    detailsButton.setOnAction(event -> {
                        EmployeeUIModel employee = getTableView().getItems().get(getIndex());
                        showEmployeeDetails(employee);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox buttons = new HBox(5, editButton, detailsButton);
                        setGraphic(buttons);
                    }
                }
            };
        });
    }

    /**
     * Refreshes the employee list with data from the database.
     */
    @FXML
    public void refreshEmployeeList() {
        try {
            if (employeeService == null) {
                System.out.println("EmployeeService not set in EmployeeListController");
                return;
            }

            // Get employee data from the database
            employeeList = FXCollections.observableArrayList();

            // Get all employees from the service
            String[] employeeData = employeeService.getAllEmployees();

            // Convert each employee to an EmployeeUIModel
            for (String employeeJson : employeeData) {
                try {
                    EmployeeDTO employeeDTO = EmployeeDTO.deserialize(employeeJson);

                    // Use the fromDTO method to create an EmployeeUIModel
                    employeeList.add(EmployeeUIModel.fromDTO(employeeDTO));

                } catch (Exception e) {
                    System.err.println("Error parsing employee data: " + e.getMessage());
                    // Continue with next employee
                }
            }

            // Update table
            employeeTable.setItems(employeeList);
        } catch (Exception e) {
            System.err.println("Error loading employees: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to load employees", e.getMessage());

        }
    }

    /**
     * Filters the employee list based on the search text and selected filter.
     */
    private void filterEmployees() {
        String searchText = searchField.getText();
        String filterOption = filterComboBox.getValue();

        if (employeeList == null) {
            return;
        }

        // Start with all employees
        ObservableList<EmployeeUIModel> filteredList = FXCollections.observableArrayList(employeeList);

        // Apply filter based on selected option
        if (filterOption != null) {
            switch (filterOption) {
                case "Active Only":
                    filteredList.removeIf(employee -> !employee.isActive());
                    break;
                case "Inactive Only":
                    filteredList.removeIf(employee -> employee.isActive());
                    break;
                case "By Branch":
                    // This would ideally open a dialog to select a branch
                    // For now, we'll just keep all employees
                    break;
                case "By Role":
                    // This would ideally open a dialog to select a role
                    // For now, we'll just keep all employees
                    break;
                case "All Employees":
                default:
                    // Keep all employees
                    break;
            }
        }

        // Apply search text filter if not empty
        if (searchText != null && !searchText.isEmpty()) {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredList.removeIf(employee -> 
                !(employee.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                  employee.getLastName().toLowerCase().contains(lowerCaseFilter) ||
                  String.valueOf(employee.getIsraeliId()).contains(lowerCaseFilter) ||
                  employee.getRoles().toLowerCase().contains(lowerCaseFilter))
            );
        }

        employeeTable.setItems(filteredList);
    }

    /**
     * Shows the form to add a new employee.
     */
    @FXML
    public void showAddEmployeeDialog() {
        System.out.println("Add employee dialog requested");
        if (mainViewController != null) {
            mainViewController.showAddEmployeeForm();
        } else {
            System.err.println("MainViewController is not set");
        }
    }

    /**
     * Shows the form to edit an employee.
     * 
     * @param employee The employee to edit
     */
    private void editEmployee(EmployeeUIModel employee) {
        System.out.println("Edit employee requested: " + employee.getIsraeliId());
        if (mainViewController != null) {
            mainViewController.showEditEmployeeForm(employee);
        } else {
            System.err.println("MainViewController is not set");
        }
    }

    /**
     * Shows the employee details view for the selected employee.
     * 
     * @param employee The employee to show details for
     */
    private void showEmployeeDetails(EmployeeUIModel employee) {
        System.out.println("Show employee details requested: " + employee.getIsraeliId());

        if (mainViewController != null) {
            mainViewController.showEmployeeDetails(employee);
        } else {
            System.err.println("MainViewController is not set");
        }
    }

    /**
     * Exports employee data to a file.
     */
    @FXML
    public void exportEmployeeData() {
        // For now, just show a message
        System.out.println("Export employee data requested");
        // In the future, this will export employee data to a file
    }

    /**
     * Shows an error dialog.
     * 
     * @param title   The title of the dialog
     * @param message The message to display
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
