package PresentationLayer.EmployeeSubModule.controllers;

import DTOs.RoleDTO;
import PresentationLayer.EmployeeSubModule.models.EmployeeUIModel;
import PresentationLayer.EmployeeSubModule.utils.ServiceFacade;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Controller for the role management view.
 * Handles displaying and managing roles and their permissions.
 */
public class RoleManagementController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> rolesListView;

    @FXML
    private Label roleNameLabel;

    @FXML
    private Button editRoleButton;

    @FXML
    private Button deleteRoleButton;

    @FXML
    private TextArea roleDescriptionArea;

    @FXML
    private TableView<PermissionEntry> employeePermissionsTable;

    @FXML
    private TableColumn<PermissionEntry, String> empPermissionNameColumn;

    @FXML
    private TableColumn<PermissionEntry, String> empPermissionDescriptionColumn;

    @FXML
    private TableColumn<PermissionEntry, String> empPermissionStatusColumn;

    @FXML
    private TableView<EmployeeWithRole> employeesWithRoleTable;

    @FXML
    private TableColumn<EmployeeWithRole, Long> employeeIdColumn;

    @FXML
    private TableColumn<EmployeeWithRole, String> employeeNameColumn;

    @FXML
    private TableColumn<EmployeeWithRole, String> employeeBranchColumn;

    @FXML
    private TableColumn<EmployeeWithRole, String> employeeStatusColumn;

    private MainViewController mainViewController;
    private Map<String, RoleData> rolesMap = new HashMap<>();
    private String selectedRoleName;

    // Services
    private ServiceLayer.EmployeeSubModule.EmployeeService employeeService;

    /**
     * Sets the employee service.
     * This method should be called after the controller is initialized.
     * 
     * @param employeeService The EmployeeService to use
     */
    public void setEmployeeService(ServiceLayer.EmployeeSubModule.EmployeeService employeeService) {
        this.employeeService = employeeService;
        System.out.println("EmployeeService set in RoleManagementController");

        // Refresh the roles with real data
        if (rolesListView != null) {
            loadRoles();
        }
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

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Initialize search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRoles(newValue);
        });

        // Initialize roles list view
        rolesListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    selectRole(newValue);
                }
            }
        );

        // Initialize permission tables
        initializePermissionTables();

        // Initialize employees table
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        employeeBranchColumn.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        employeeStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Data will be loaded when the service is set
    }

    /**
     * Initializes the permission tables with columns.
     */
    private void initializePermissionTables() {
        // All permissions table
        empPermissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        empPermissionDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        empPermissionStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    /**
     * Loads roles data.
     */
    private void loadRoles() {
        // Clear existing roles
        rolesMap.clear();

        try {
            if (employeeService == null) {
                System.out.println("EmployeeService not set in RoleManagementController");
                // Use mock data if service is not set
                createMockRoles();
            } else {
                // Get real data from the database
                String[] roleData = employeeService.getAllRoles();

                // Process each role
                for (String role : roleData) {
                    try {
                        // Get employees with this role
                        List<EmployeeWithRole> employeesWithRole = new ArrayList<>();

                        // Create permission lists
                        List<PermissionEntry> employeePermissions = new ArrayList<>();
                        List<PermissionEntry> shiftPermissions = new ArrayList<>();
                        List<PermissionEntry> systemPermissions = new ArrayList<>();

                        // Deserialize the role string to a RoleDTO object
                        RoleDTO roleDTO = null;
                        String roleName = "";
                        Set<String> permissions = new HashSet<>();

                        try {
                            roleDTO = RoleDTO.deserialize(role);
                            roleName = roleDTO.getName();
                            permissions = roleDTO.getPermissions();

                            for (String permission : permissions) {
                                addPermission(permissions, permission, employeePermissions, permission);
                            }

                        } catch (Exception e) {
                            // Role not found or error getting permissions, create default permissions
                            System.out.println("Error processing role permissions for " + role + ": " + e.getMessage());
                        }

                        try {
                            // Get all employees
                            String[] employeeData = employeeService.getAllEmployees();

                            // Filter employees with this role
                            for (String employeeJson : employeeData) {
                                // Check if the employee has this role
                                if (employeeJson.contains("\"" + roleName + "\"")) {
                                    try {
                                        // Extract employee ID
                                        long employeeId = 0;
                                        int idIndex = employeeJson.indexOf("\"israeliId\":");
                                        if (idIndex >= 0) {
                                            int startIndex = idIndex + 13; // Length of "\"israeliId\":"
                                            int endIndex = employeeJson.indexOf(",", startIndex);
                                            if (endIndex > startIndex) {
                                                employeeId = Long.parseLong(employeeJson.substring(startIndex, endIndex).trim());
                                            }
                                        }

                                        // Extract employee name
                                        String employeeName = "Unknown";
                                        int firstNameIndex = employeeJson.indexOf("\"firstName\":");
                                        if (firstNameIndex >= 0) {
                                            int startIndex = firstNameIndex + 13; // Length of "\"firstName\":"
                                            int endIndex = employeeJson.indexOf("\"", startIndex + 1);
                                            if (endIndex > startIndex) {
                                                String firstName = employeeJson.substring(startIndex + 1, endIndex);

                                                // Try to get last name too
                                                int lastNameIndex = employeeJson.indexOf("\"lastName\":");
                                                if (lastNameIndex >= 0) {
                                                    int lastNameStartIndex = lastNameIndex + 12; // Length of "\"lastName\":"
                                                    int lastNameEndIndex = employeeJson.indexOf("\"", lastNameStartIndex + 1);
                                                    if (lastNameEndIndex > lastNameStartIndex) {
                                                        String lastName = employeeJson.substring(lastNameStartIndex + 1, lastNameEndIndex);
                                                        employeeName = firstName + " " + lastName;
                                                    } else {
                                                        employeeName = firstName;
                                                    }
                                                } else {
                                                    employeeName = firstName;
                                                }
                                            }
                                        }

                                        // Extract branch ID
                                        long branchId = 0;
                                        String branchName = "Unknown Branch";
                                        int branchIdIndex = employeeJson.indexOf("\"branchId\":");
                                        if (branchIdIndex >= 0) {
                                            int startIndex = branchIdIndex + 11; // Length of "\"branchId\":"
                                            int endIndex = employeeJson.indexOf(",", startIndex);
                                            if (endIndex < 0) {
                                                endIndex = employeeJson.indexOf("}", startIndex);
                                            }
                                            if (endIndex > startIndex) {
                                                branchId = Long.parseLong(employeeJson.substring(startIndex, endIndex).trim());
                                                branchName = "Branch " + branchId;
                                            }
                                        }

                                        // Extract active status
                                        boolean active = true;
                                        int activeIndex = employeeJson.indexOf("\"active\":");
                                        if (activeIndex >= 0) {
                                            int startIndex = activeIndex + 9; // Length of "\"active\":"
                                            int endIndex = employeeJson.indexOf(",", startIndex);
                                            if (endIndex < 0) {
                                                endIndex = employeeJson.indexOf("}", startIndex);
                                            }
                                            if (endIndex > startIndex) {
                                                active = Boolean.parseBoolean(employeeJson.substring(startIndex, endIndex).trim());
                                            }
                                        }

                                        // Add employee to the list
                                        employeesWithRole.add(new EmployeeWithRole(
                                            employeeId,
                                            employeeName,
                                            branchName,
                                            active ? "Active" : "Inactive"
                                        ));
                                    } catch (Exception e) {
                                        System.err.println("Error parsing employee data: " + e.getMessage());
                                        // Continue with next employee
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error getting employees: " + e.getMessage());
                            // Continue with empty employee list
                        }

                        // Create role data object
                        RoleData roleObj = new RoleData(
                            roleName,
                            "Role: " + roleName, // We don't have a description in the service, so use the role name
                            employeePermissions,
                            shiftPermissions,
                            systemPermissions,
                            employeesWithRole
                        );

                        // Add to roles map
                        rolesMap.put(roleName, roleObj);
                    } catch (Exception e) {
                        System.err.println("Error processing role " + "roleName" + ": " + e.getMessage());
                        e.printStackTrace();
                        // Continue with next role
                    }
                }

                // If no roles were found, use mock data
                if (rolesMap.isEmpty()) {
                    System.out.println("No roles found, using mock data");
                    createMockRoles();
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading roles: " + e.getMessage());
            e.printStackTrace();

            // Use mock data if there's an error
            createMockRoles();
        }

        // Update roles list
        updateRolesList();
    }


    /**
     * Adds a permission to the list based on whether it exists in the permissions set.
     * 
     * @param permissions The set of permissions to check against
     * @param permissionName The name of the permission to check
     * @param permissionsList The list to add the permission to
     * @param description The description of the permission
     */
    private void addPermission(Set<String> permissions, String permissionName, List<PermissionEntry> permissionsList, String description) {
        boolean hasPermission = permissions.contains(permissionName);
        permissionsList.add(new PermissionEntry(
            permissionName,
            description,
            hasPermission ? "Granted" : "Denied"
        ));
    }

    /**
     * Adds a default permission to a list of permissions.
     * 
     * @param permissionsList The list to add the permission to
     * @param permissionName The name of the permission
     * @param description The description of the permission
     * @param granted Whether the permission is granted or denied
     */
    private void addDefaultPermission(List<PermissionEntry> permissionsList, String permissionName, String description, boolean granted) {
        permissionsList.add(new PermissionEntry(
            permissionName,
            description,
            granted ? "Granted" : "Denied"
        ));
    }

    /**
     * Creates mock roles data for testing.
     */
    private void createMockRoles() {
        // Manager role
        RoleData managerRole = new RoleData(
            "Manager",
            "Manages employees and shifts",
            createMockPermissions("Employee", 5, true),
            createMockPermissions("Shift", 4, true),
            createMockPermissions("System", 3, true),
            createMockEmployees(3)
        );
        rolesMap.put("Manager", managerRole);

        // Cashier role
        RoleData cashierRole = new RoleData(
            "Cashier",
            "Handles customer transactions",
            createMockPermissions("Employee", 2, false),
            createMockPermissions("Shift", 1, false),
            createMockPermissions("System", 1, false),
            createMockEmployees(5)
        );
        rolesMap.put("Cashier", cashierRole);

        // Stocker role
        RoleData stockerRole = new RoleData(
            "Stocker",
            "Manages inventory and stocking",
            createMockPermissions("Employee", 1, false),
            createMockPermissions("Shift", 1, false),
            createMockPermissions("System", 1, false),
            createMockEmployees(4)
        );
        rolesMap.put("Stocker", stockerRole);

        // Security role
        RoleData securityRole = new RoleData(
            "Security",
            "Ensures store security",
            createMockPermissions("Employee", 0, false),
            createMockPermissions("Shift", 1, false),
            createMockPermissions("System", 2, false),
            createMockEmployees(2)
        );
        rolesMap.put("Security", securityRole);

        // HR role
        RoleData hrRole = new RoleData(
            "HR",
            "Manages human resources",
            createMockPermissions("Employee", 5, true),
            createMockPermissions("Shift", 2, false),
            createMockPermissions("System", 3, true),
            createMockEmployees(1)
        );
        rolesMap.put("HR", hrRole);
    }

    /**
     * Creates mock permissions for testing.
     * 
     * @param category The permission category
     * @param grantedCount The number of granted permissions
     * @param hasAdmin Whether to include admin permissions
     * @return The list of permissions
     */
    private List<PermissionEntry> createMockPermissions(String category, int grantedCount, boolean hasAdmin) {
        List<PermissionEntry> permissions = new ArrayList<>();

        String[] permissionNames;
        String[] permissionDescriptions;

        if (category.equals("Employee")) {
            permissionNames = new String[] {
                "VIEW_EMPLOYEE", "CREATE_EMPLOYEE", "EDIT_EMPLOYEE", 
                "DELETE_EMPLOYEE", "ADMIN_EMPLOYEE"
            };
            permissionDescriptions = new String[] {
                "View employee details", "Create new employees", "Edit employee information",
                "Delete employees", "Full administrative access to employees"
            };
        } else if (category.equals("Shift")) {
            permissionNames = new String[] {
                "VIEW_SHIFT", "CREATE_SHIFT", "EDIT_SHIFT", 
                "DELETE_SHIFT", "ADMIN_SHIFT"
            };
            permissionDescriptions = new String[] {
                "View shift details", "Create new shifts", "Edit shift information",
                "Delete shifts", "Full administrative access to shifts"
            };
        } else { // System
            permissionNames = new String[] {
                "VIEW_SYSTEM", "EDIT_SETTINGS", "MANAGE_ROLES", 
                "VIEW_LOGS", "ADMIN_SYSTEM"
            };
            permissionDescriptions = new String[] {
                "View system information", "Edit system settings", "Manage roles and permissions",
                "View system logs", "Full administrative access to system"
            };
        }

        // Create permissions
        for (int i = 0; i < permissionNames.length; i++) {
            boolean isGranted = i < grantedCount || (hasAdmin && i == permissionNames.length - 1);
            permissions.add(new PermissionEntry(
                permissionNames[i],
                permissionDescriptions[i],
                isGranted ? "Granted" : "Denied"
            ));
        }

        return permissions;
    }

    /**
     * Creates mock employees for testing.
     * 
     * @param count The number of employees to create
     * @return The list of employees
     */
    private List<EmployeeWithRole> createMockEmployees(int count) {
        List<EmployeeWithRole> employees = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            long id = 100000000 + new Random().nextInt(900000000);
            String firstName = "Employee";
            String lastName = "Name" + (i + 1);
            String branchName = "Branch " + (i % 3 + 1);
            boolean isActive = new Random().nextBoolean();

            employees.add(new EmployeeWithRole(
                id,
                firstName + " " + lastName,
                branchName,
                isActive ? "Active" : "Inactive"
            ));
        }

        return employees;
    }

    /**
     * Updates the roles list view with the current roles.
     */
    private void updateRolesList() {
        ObservableList<String> roleNames = FXCollections.observableArrayList(rolesMap.keySet());
        FXCollections.sort(roleNames);
        rolesListView.setItems(roleNames);

        // Select the first role if available
        if (!roleNames.isEmpty()) {
            rolesListView.getSelectionModel().selectFirst();
        } else {
            clearRoleDetails();
        }
    }

    /**
     * Filters roles based on the search text.
     * 
     * @param searchText The text to search for
     */
    private void filterRoles(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            updateRolesList();
        } else {
            ObservableList<String> filteredRoles = FXCollections.observableArrayList();
            String lowerCaseFilter = searchText.toLowerCase();

            for (String roleName : rolesMap.keySet()) {
                if (roleName.toLowerCase().contains(lowerCaseFilter)) {
                    filteredRoles.add(roleName);
                }
            }

            FXCollections.sort(filteredRoles);
            rolesListView.setItems(filteredRoles);
        }
    }

    /**
     * Selects a role and displays its details.
     * 
     * @param roleName The name of the role to select
     */
    private void selectRole(String roleName) {
        selectedRoleName = roleName;
        RoleData role = rolesMap.get(roleName);

        if (role != null) {
            // Update role details
            roleNameLabel.setText(role.getName());
            roleDescriptionArea.setText(role.getDescription());

            // Enable buttons
            editRoleButton.setDisable(false);
            deleteRoleButton.setDisable(false);

            // Update permission table with all permissions
            employeePermissionsTable.setItems(FXCollections.observableArrayList(role.getAllPermissions()));

            // Update employees table
            employeesWithRoleTable.setItems(FXCollections.observableArrayList(role.getEmployees()));
        } else {
            clearRoleDetails();
        }
    }

    /**
     * Clears the role details section.
     */
    private void clearRoleDetails() {
        selectedRoleName = null;
        roleNameLabel.setText("Select a role");
        roleDescriptionArea.setText("");

        // Disable buttons
        editRoleButton.setDisable(true);
        deleteRoleButton.setDisable(true);

        // Clear tables
        employeePermissionsTable.getItems().clear();
        employeesWithRoleTable.getItems().clear();
    }

    /**
     * Shows the dialog to create a new role.
     */
    @FXML
    public void showCreateRoleDialog() {
        System.out.println("Create role dialog requested");

        // Create a dialog with modern styling
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Create New Role");
        dialog.setHeaderText("Enter role details");

        // Apply CSS to dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStyleClass().add("modern-dialog");

        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 10));
        grid.getStyleClass().add("modern-grid");

        // Role name field
        TextField roleNameField = new TextField();
        roleNameField.setPromptText("Role name");
        roleNameField.getStyleClass().add("modern-field");
        roleNameField.setPrefWidth(300);

        // Role description field
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Role description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.getStyleClass().add("modern-textarea");
        descriptionArea.setPrefWidth(300);

        // Permission checkboxes with modern styling
        VBox permissionsBox = new VBox(8);
        permissionsBox.setPadding(new javafx.geometry.Insets(10));

        Label permissionsLabel = new Label("All Permissions:");
        permissionsLabel.getStyleClass().add("modern-label");
        permissionsBox.getChildren().add(permissionsLabel);

        // Employee permissions
        Label empPermLabel = new Label("Employee Permissions:");
        empPermLabel.getStyleClass().add("modern-label");
        permissionsBox.getChildren().add(empPermLabel);

        CheckBox viewEmployeeCheck = new CheckBox("View employee details");
        CheckBox createEmployeeCheck = new CheckBox("Create new employees");
        CheckBox editEmployeeCheck = new CheckBox("Edit employee information");
        CheckBox deleteEmployeeCheck = new CheckBox("Delete employees");
        CheckBox adminEmployeeCheck = new CheckBox("Full administrative access to employees");

        // Apply modern style to checkboxes
        viewEmployeeCheck.getStyleClass().add("modern-checkbox");
        createEmployeeCheck.getStyleClass().add("modern-checkbox");
        editEmployeeCheck.getStyleClass().add("modern-checkbox");
        deleteEmployeeCheck.getStyleClass().add("modern-checkbox");
        adminEmployeeCheck.getStyleClass().add("modern-checkbox");

        permissionsBox.getChildren().addAll(
            viewEmployeeCheck, createEmployeeCheck, editEmployeeCheck, 
            deleteEmployeeCheck, adminEmployeeCheck
        );

        // Shift permissions
        Label shiftPermLabel = new Label("Shift Permissions:");
        shiftPermLabel.getStyleClass().add("modern-label");
        permissionsBox.getChildren().add(shiftPermLabel);

        CheckBox viewShiftCheck = new CheckBox("View shift details");
        CheckBox createShiftCheck = new CheckBox("Create new shifts");
        CheckBox editShiftCheck = new CheckBox("Edit shift information");
        CheckBox deleteShiftCheck = new CheckBox("Delete shifts");
        CheckBox adminShiftCheck = new CheckBox("Full administrative access to shifts");

        // Apply modern style to checkboxes
        viewShiftCheck.getStyleClass().add("modern-checkbox");
        createShiftCheck.getStyleClass().add("modern-checkbox");
        editShiftCheck.getStyleClass().add("modern-checkbox");
        deleteShiftCheck.getStyleClass().add("modern-checkbox");
        adminShiftCheck.getStyleClass().add("modern-checkbox");

        permissionsBox.getChildren().addAll(
            viewShiftCheck, createShiftCheck, editShiftCheck, 
            deleteShiftCheck, adminShiftCheck
        );

        // System permissions
        Label sysPermLabel = new Label("System Permissions:");
        sysPermLabel.getStyleClass().add("modern-label");
        permissionsBox.getChildren().add(sysPermLabel);

        CheckBox viewSystemCheck = new CheckBox("View system information");
        CheckBox editSettingsCheck = new CheckBox("Edit system settings");
        CheckBox manageRolesCheck = new CheckBox("Manage roles and permissions");
        CheckBox viewLogsCheck = new CheckBox("View system logs");
        CheckBox adminSystemCheck = new CheckBox("Full administrative access to system");

        // Apply modern style to checkboxes
        viewSystemCheck.getStyleClass().add("modern-checkbox");
        editSettingsCheck.getStyleClass().add("modern-checkbox");
        manageRolesCheck.getStyleClass().add("modern-checkbox");
        viewLogsCheck.getStyleClass().add("modern-checkbox");
        adminSystemCheck.getStyleClass().add("modern-checkbox");

        permissionsBox.getChildren().addAll(
            viewSystemCheck, editSettingsCheck, manageRolesCheck, 
            viewLogsCheck, adminSystemCheck
        );

        // Add fields to the grid with modern labels
        Label nameLabel = new Label("Role Name:");
        nameLabel.getStyleClass().add("modern-label");

        Label descLabel = new Label("Description:");
        descLabel.getStyleClass().add("modern-label");

        grid.add(nameLabel, 0, 0);
        grid.add(roleNameField, 1, 0);
        grid.add(descLabel, 0, 1);
        grid.add(descriptionArea, 1, 1);

        // Add permissions box directly to the grid
        grid.add(new Label("Permissions:"), 0, 2);
        grid.add(permissionsBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the role name field by default
        Platform.runLater(() -> roleNameField.requestFocus());

        // Convert the result to a map when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Map<String, Object> result = new HashMap<>();
                result.put("name", roleNameField.getText());
                result.put("description", descriptionArea.getText());

                // Collect permissions
                Set<String> permissions = new HashSet<>();

                // Employee permissions
                if (viewEmployeeCheck.isSelected()) permissions.add("VIEW_EMPLOYEE");
                if (createEmployeeCheck.isSelected()) permissions.add("CREATE_EMPLOYEE");
                if (editEmployeeCheck.isSelected()) permissions.add("EDIT_EMPLOYEE");
                if (deleteEmployeeCheck.isSelected()) permissions.add("DELETE_EMPLOYEE");
                if (adminEmployeeCheck.isSelected()) permissions.add("ADMIN_EMPLOYEE");

                // Shift permissions
                if (viewShiftCheck.isSelected()) permissions.add("VIEW_SHIFT");
                if (createShiftCheck.isSelected()) permissions.add("CREATE_SHIFT");
                if (editShiftCheck.isSelected()) permissions.add("EDIT_SHIFT");
                if (deleteShiftCheck.isSelected()) permissions.add("DELETE_SHIFT");
                if (adminShiftCheck.isSelected()) permissions.add("ADMIN_SHIFT");

                // System permissions
                if (viewSystemCheck.isSelected()) permissions.add("VIEW_SYSTEM");
                if (editSettingsCheck.isSelected()) permissions.add("EDIT_SETTINGS");
                if (manageRolesCheck.isSelected()) permissions.add("MANAGE_ROLES");
                if (viewLogsCheck.isSelected()) permissions.add("VIEW_LOGS");
                if (adminSystemCheck.isSelected()) permissions.add("ADMIN_SYSTEM");

                result.put("permissions", permissions);

                return result;
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Map<String, Object>> result = dialog.showAndWait();

        result.ifPresent(roleData -> {
            System.out.println("Creating role: " + roleData);

            // In a real implementation, this would call the service layer to create the role
            if (employeeService != null) {
                try {
                    String roleName = (String) roleData.get("name");
                    @SuppressWarnings("unchecked")
                    Set<String> permissions = (Set<String>) roleData.get("permissions");

                    // Call the service to create the role with permissions
                    long doneBy = 123456789; // Using a default user ID
                    String createResult = employeeService.createRoleWithPermissions(doneBy, roleName, permissions);
                    System.out.println("Role created: " + createResult);

                    // Refresh the roles list
                    loadRoles();

                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Role created successfully!");
                    alert.showAndWait();
                } catch (Exception e) {
                    System.err.println("Error creating role: " + e.getMessage());
                    e.printStackTrace();

                    // Show error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error creating role: " + e.getMessage());
                    alert.showAndWait();
                }
            } else {
                // If service is not available, just show a message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Demo Mode");
                alert.setHeaderText(null);
                alert.setContentText("Role would be created with these details: " + roleData);
                alert.showAndWait();

                // Add a mock role to the list
                String roleName = (String) roleData.get("name");
                String description = (String) roleData.get("description");
                @SuppressWarnings("unchecked")
                Set<String> permissions = (Set<String>) roleData.get("permissions");

                // Create permission entries
                List<PermissionEntry> employeePermissions = new ArrayList<>();
                List<PermissionEntry> shiftPermissions = new ArrayList<>();
                List<PermissionEntry> systemPermissions = new ArrayList<>();

                // Process employee permissions
                addPermission(permissions, "VIEW_EMPLOYEE", employeePermissions, "View employee details");
                addPermission(permissions, "CREATE_EMPLOYEE", employeePermissions, "Create new employees");
                addPermission(permissions, "EDIT_EMPLOYEE", employeePermissions, "Edit employee information");
                addPermission(permissions, "DELETE_EMPLOYEE", employeePermissions, "Delete employees");
                addPermission(permissions, "ADMIN_EMPLOYEE", employeePermissions, "Full administrative access to employees");

                // Process shift permissions
                addPermission(permissions, "VIEW_SHIFT", shiftPermissions, "View shift details");
                addPermission(permissions, "CREATE_SHIFT", shiftPermissions, "Create new shifts");
                addPermission(permissions, "EDIT_SHIFT", shiftPermissions, "Edit shift information");
                addPermission(permissions, "DELETE_SHIFT", shiftPermissions, "Delete shifts");
                addPermission(permissions, "ADMIN_SHIFT", shiftPermissions, "Full administrative access to shifts");

                // Process system permissions
                addPermission(permissions, "VIEW_SYSTEM", systemPermissions, "View system information");
                addPermission(permissions, "EDIT_SETTINGS", systemPermissions, "Edit system settings");
                addPermission(permissions, "MANAGE_ROLES", systemPermissions, "Manage roles and permissions");
                addPermission(permissions, "VIEW_LOGS", systemPermissions, "View system logs");
                addPermission(permissions, "ADMIN_SYSTEM", systemPermissions, "Full administrative access to system");

                // Create role data
                RoleData newRoleData = new RoleData(
                    roleName,
                    description,
                    employeePermissions,
                    shiftPermissions,
                    systemPermissions,
                    new ArrayList<>() // No employees assigned yet
                );

                // Add to roles map
                rolesMap.put(roleName, newRoleData);

                // Update UI
                updateRolesList();
            }
        });
    }

    /**
     * Shows the dialog to edit the selected role.
     */
    @FXML
    public void editSelectedRole() {
        if (selectedRoleName != null) {
            System.out.println("Edit role requested: " + selectedRoleName);

            RoleData roleData = rolesMap.get(selectedRoleName);
            if (roleData == null) {
                return;
            }

            // Create a dialog with modern styling
            Dialog<Map<String, Object>> dialog = new Dialog<>();
            dialog.setTitle("Edit Role");
            dialog.setHeaderText("Edit role details");

            // Apply CSS to dialog
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStyleClass().add("modern-dialog");

            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create the form grid
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 10));
            grid.getStyleClass().add("modern-grid");

            // Role name field (disabled for editing)
            TextField roleNameField = new TextField(roleData.getName());
            roleNameField.setDisable(true); // Can't change role name
            roleNameField.getStyleClass().add("modern-field");
            roleNameField.setPrefWidth(300);

            // Role description field
            TextArea descriptionArea = new TextArea(roleData.getDescription());
            descriptionArea.setPrefRowCount(3);
            descriptionArea.getStyleClass().add("modern-textarea");
            descriptionArea.setPrefWidth(300);

            // Permission checkboxes with modern styling
            VBox employeePermissionsBox = new VBox(8);
            employeePermissionsBox.setPadding(new javafx.geometry.Insets(10));

            Label empPermLabel = new Label("Employee Permissions:");
            empPermLabel.getStyleClass().add("modern-label");
            employeePermissionsBox.getChildren().add(empPermLabel);

            // Create checkboxes for all permissions with modern styling
            Map<String, CheckBox> permissionCheckboxes = new HashMap<>();
            for (PermissionEntry permission : roleData.getAllPermissions()) {
                CheckBox checkbox = new CheckBox(permission.getDescription());
                checkbox.setSelected("Granted".equals(permission.getStatus()));
                checkbox.getStyleClass().add("modern-checkbox");
                permissionCheckboxes.put(permission.getName(), checkbox);
                employeePermissionsBox.getChildren().add(checkbox);
            }

            // Add fields to the grid with modern labels
            Label nameLabel = new Label("Role Name:");
            nameLabel.getStyleClass().add("modern-label");

            Label descLabel = new Label("Description:");
            descLabel.getStyleClass().add("modern-label");

            grid.add(nameLabel, 0, 0);
            grid.add(roleNameField, 1, 0);
            grid.add(descLabel, 0, 1);
            grid.add(descriptionArea, 1, 1);

            // Add permissions box directly to the grid
            grid.add(new Label("Permissions:"), 0, 2);
            grid.add(employeePermissionsBox, 1, 2);

            dialog.getDialogPane().setContent(grid);

            // Convert the result to a map when the save button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("name", roleNameField.getText());
                    result.put("description", descriptionArea.getText());

                    // Collect permissions from all checkboxes
                    Set<String> permissions = new HashSet<>();
                    for (Map.Entry<String, CheckBox> entry : permissionCheckboxes.entrySet()) {
                        if (entry.getValue().isSelected()) {
                            permissions.add(entry.getKey());
                        }
                    }

                    result.put("permissions", permissions);

                    return result;
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Map<String, Object>> dialogResult = dialog.showAndWait();

            dialogResult.ifPresent(updatedRoleData -> {
                System.out.println("Updating role: " + updatedRoleData);

                // In a real implementation, this would call the service layer to update the role
                if (employeeService != null) {
                    try {
                        String roleName = (String) updatedRoleData.get("name");
                        String description = (String) updatedRoleData.get("description");
                        @SuppressWarnings("unchecked")
                        Set<String> newPermissions = (Set<String>) updatedRoleData.get("permissions");

                        // Get current permissions from all permissions
                        Set<String> currentPermissions = new HashSet<>();
                        for (PermissionEntry permission : roleData.getAllPermissions()) {
                            if ("Granted".equals(permission.getStatus())) {
                                currentPermissions.add(permission.getName());
                            }
                        }

                        // Find permissions to add and remove
                        Set<String> permissionsToAdd = new HashSet<>(newPermissions);
                        permissionsToAdd.removeAll(currentPermissions);

                        Set<String> permissionsToRemove = new HashSet<>(currentPermissions);
                        permissionsToRemove.removeAll(newPermissions);

                        // Call the service to update the role
                        long doneBy = 123456789; // Using a default user ID
                        boolean hasChanges = false;

                        // Add new permissions
                        for (String permission : permissionsToAdd) {
                            String addResult = employeeService.addPermissionToRole(doneBy, roleName, permission);
                            System.out.println("Add permission result: " + addResult);
                            if (addResult.contains("added successfully")) {
                                hasChanges = true;
                            }
                        }

                        // Remove permissions
                        for (String permission : permissionsToRemove) {
                            String removeResult = employeeService.removePermissionFromRole(doneBy, roleName, permission);
                            System.out.println("Remove permission result: " + removeResult);
                            if (removeResult.contains("removed successfully")) {
                                hasChanges = true;
                            }
                        }

                        if (hasChanges) {
                            System.out.println("Role updated");

                            // Get the updated role from the service
                            RoleDTO updatedRole = employeeService.getRoleDetailsAsDTO(roleName);

                            // Create updated permission entries for backward compatibility
                            List<PermissionEntry> employeePermissions = new ArrayList<>();
                            List<PermissionEntry> shiftPermissions = new ArrayList<>();
                            List<PermissionEntry> systemPermissions = new ArrayList<>();

                            // Get permissions from the updated role
                            Set<String> updatedPermissions = updatedRole.getPermissions();

                            // Process employee permissions
                            addPermission(updatedPermissions, "VIEW_EMPLOYEE", employeePermissions, "View employee details");
                            addPermission(updatedPermissions, "CREATE_EMPLOYEE", employeePermissions, "Create new employees");
                            addPermission(updatedPermissions, "EDIT_EMPLOYEE", employeePermissions, "Edit employee information");
                            addPermission(updatedPermissions, "DELETE_EMPLOYEE", employeePermissions, "Delete employees");
                            addPermission(updatedPermissions, "ADMIN_EMPLOYEE", employeePermissions, "Full administrative access to employees");

                            // Process shift permissions
                            addPermission(updatedPermissions, "VIEW_SHIFT", shiftPermissions, "View shift details");
                            addPermission(updatedPermissions, "CREATE_SHIFT", shiftPermissions, "Create new shifts");
                            addPermission(updatedPermissions, "EDIT_SHIFT", shiftPermissions, "Edit shift information");
                            addPermission(updatedPermissions, "DELETE_SHIFT", shiftPermissions, "Delete shifts");
                            addPermission(updatedPermissions, "ADMIN_SHIFT", shiftPermissions, "Full administrative access to shifts");

                            // Process system permissions
                            addPermission(updatedPermissions, "VIEW_SYSTEM", systemPermissions, "View system information");
                            addPermission(updatedPermissions, "EDIT_SETTINGS", systemPermissions, "Edit system settings");
                            addPermission(updatedPermissions, "MANAGE_ROLES", systemPermissions, "Manage roles and permissions");
                            addPermission(updatedPermissions, "VIEW_LOGS", systemPermissions, "View system logs");
                            addPermission(updatedPermissions, "ADMIN_SYSTEM", systemPermissions, "Full administrative access to system");

                            // Create updated role data
                            RoleData newRoleData = new RoleData(
                                roleName,
                                description,
                                employeePermissions,
                                shiftPermissions,
                                systemPermissions,
                                roleData.getEmployees() // Keep the same employees
                            );

                            // Update in roles map
                            rolesMap.put(roleName, newRoleData);

                            // Update UI
                            selectRole(roleName);

                            // Show success message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText(null);
                            alert.setContentText("Role updated successfully!");
                            alert.showAndWait();
                        } else {
                            // Show info message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Information");
                            alert.setHeaderText(null);
                            alert.setContentText("No changes were made to the role.");
                            alert.showAndWait();
                        }
                    } catch (Exception e) {
                        System.err.println("Error updating role: " + e.getMessage());
                        e.printStackTrace();

                        // Show error message
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Error updating role: " + e.getMessage());
                        alert.showAndWait();
                    }
                } else {
                    // If service is not available, just show a message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Demo Mode");
                    alert.setHeaderText(null);
                    alert.setContentText("Role would be updated with these details: " + updatedRoleData);
                    alert.showAndWait();

                    // Update the role in the map
                    String roleName = (String) updatedRoleData.get("name");
                    String description = (String) updatedRoleData.get("description");
                    @SuppressWarnings("unchecked")
                    Set<String> permissions = (Set<String>) updatedRoleData.get("permissions");

                    // Create permission entries for backward compatibility
                    List<PermissionEntry> employeePermissions = new ArrayList<>();
                    List<PermissionEntry> shiftPermissions = new ArrayList<>();
                    List<PermissionEntry> systemPermissions = new ArrayList<>();

                    // Process employee permissions
                    addPermission(permissions, "VIEW_EMPLOYEE", employeePermissions, "View employee details");
                    addPermission(permissions, "CREATE_EMPLOYEE", employeePermissions, "Create new employees");
                    addPermission(permissions, "EDIT_EMPLOYEE", employeePermissions, "Edit employee information");
                    addPermission(permissions, "DELETE_EMPLOYEE", employeePermissions, "Delete employees");
                    addPermission(permissions, "ADMIN_EMPLOYEE", employeePermissions, "Full administrative access to employees");

                    // Process shift permissions
                    addPermission(permissions, "VIEW_SHIFT", shiftPermissions, "View shift details");
                    addPermission(permissions, "CREATE_SHIFT", shiftPermissions, "Create new shifts");
                    addPermission(permissions, "EDIT_SHIFT", shiftPermissions, "Edit shift information");
                    addPermission(permissions, "DELETE_SHIFT", shiftPermissions, "Delete shifts");
                    addPermission(permissions, "ADMIN_SHIFT", shiftPermissions, "Full administrative access to shifts");

                    // Process system permissions
                    addPermission(permissions, "VIEW_SYSTEM", systemPermissions, "View system information");
                    addPermission(permissions, "EDIT_SETTINGS", systemPermissions, "Edit system settings");
                    addPermission(permissions, "MANAGE_ROLES", systemPermissions, "Manage roles and permissions");
                    addPermission(permissions, "VIEW_LOGS", systemPermissions, "View system logs");
                    addPermission(permissions, "ADMIN_SYSTEM", systemPermissions, "Full administrative access to system");

                    // Create role data
                    RoleData updatedRole = new RoleData(
                        roleName,
                        description,
                        employeePermissions,
                        shiftPermissions,
                        systemPermissions,
                        roleData.getEmployees() // Keep the same employees
                    );

                    // Update in roles map
                    rolesMap.put(roleName, updatedRole);

                    // Update UI
                    selectRole(roleName);
                }
            });
        }
    }

    /**
     * Deletes the selected role.
     */
    @FXML
    public void deleteSelectedRole() {
        if (selectedRoleName != null) {
            System.out.println("Delete role requested: " + selectedRoleName);

            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Delete Role");
            confirmAlert.setContentText("Are you sure you want to delete the role '" + selectedRoleName + "'?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (employeeService != null) {
                    try {
                        // Call the service to delete the role
                        long doneBy = 123456789; // Using a default user ID
                        String deleteResult = employeeService.deleteRole(doneBy, selectedRoleName);
                        System.out.println("Role deletion result: " + deleteResult);

                        // Check if deletion was successful
                        if (deleteResult.contains("deleted successfully")) {
                            // Remove from local map
                            rolesMap.remove(selectedRoleName);

                            // Update UI
                            updateRolesList();

                            // Show success message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText(null);
                            alert.setContentText("Role deleted successfully!");
                            alert.showAndWait();
                        } else {
                            // Show error message
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText(deleteResult);
                            alert.showAndWait();
                        }
                    } catch (Exception e) {
                        System.err.println("Error deleting role: " + e.getMessage());
                        e.printStackTrace();

                        // Show error message
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Error deleting role: " + e.getMessage());
                        alert.showAndWait();
                    }
                } else {
                    // If service is not available, just remove from local map
                    rolesMap.remove(selectedRoleName);

                    // Update UI
                    updateRolesList();

                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Demo Mode");
                    alert.setHeaderText(null);
                    alert.setContentText("Role would be deleted in a real implementation.");
                    alert.showAndWait();
                }
            }
        }
    }

    /**
     * Inner class to represent role data.
     */
    private static class RoleData {
        private final String name;
        private final String description;
        private final List<PermissionEntry> allPermissions;
        private final List<EmployeeWithRole> employees;

        // Keep track of which permissions are employee, shift, or system permissions
        private final List<PermissionEntry> employeePermissions;
        private final List<PermissionEntry> shiftPermissions;
        private final List<PermissionEntry> systemPermissions;

        public RoleData(String name, String description, 
                       List<PermissionEntry> employeePermissions, 
                       List<PermissionEntry> shiftPermissions, 
                       List<PermissionEntry> systemPermissions,
                       List<EmployeeWithRole> employees) {
            this.name = name;
            this.description = description;

            // Store the separate permission lists for backward compatibility
            this.employeePermissions = employeePermissions != null ? new ArrayList<>(employeePermissions) : new ArrayList<>();
            this.shiftPermissions = shiftPermissions != null ? new ArrayList<>(shiftPermissions) : new ArrayList<>();
            this.systemPermissions = systemPermissions != null ? new ArrayList<>(systemPermissions) : new ArrayList<>();

            // Combine all permissions into a single list
            this.allPermissions = new ArrayList<>();
            if (employeePermissions != null) this.allPermissions.addAll(employeePermissions);
            if (shiftPermissions != null) this.allPermissions.addAll(shiftPermissions);
            if (systemPermissions != null) this.allPermissions.addAll(systemPermissions);

            this.employees = employees;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public List<PermissionEntry> getAllPermissions() { return allPermissions; }
        public List<PermissionEntry> getEmployeePermissions() { return employeePermissions; }
        public List<PermissionEntry> getShiftPermissions() { return shiftPermissions; }
        public List<PermissionEntry> getSystemPermissions() { return systemPermissions; }
        public List<EmployeeWithRole> getEmployees() { return employees; }
    }

    /**
     * Inner class to represent a permission entry.
     */
    public static class PermissionEntry {
        private final String name;
        private final String description;
        private final String status;

        public PermissionEntry(String name, String description, String status) {
            this.name = name;
            this.description = description;
            this.status = status;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
    }

    /**
     * Inner class to represent an employee with a role.
     */
    public static class EmployeeWithRole {
        private final long employeeId;
        private final String employeeName;
        private final String branchName;
        private final String status;

        public EmployeeWithRole(long employeeId, String employeeName, String branchName, String status) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.branchName = branchName;
            this.status = status;
        }

        public long getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getBranchName() { return branchName; }
        public String getStatus() { return status; }
    }
}
