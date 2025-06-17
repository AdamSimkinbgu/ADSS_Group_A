package PresentationLayer.GUI.EmployeeScreen.Controllers;

import PresentationLayer.GUI.EmployeeScreen.Models.EmployeeUIModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for the employee form view.
 * Handles creating new employees and editing existing ones.
 */
public class EmployeeFormController {

    @FXML
    private Label formTitleLabel;

    @FXML
    private TextField israeliIdField;

    @FXML
    private Label israeliIdErrorLabel;

    @FXML
    private TextField firstNameField;

    @FXML
    private Label firstNameErrorLabel;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label lastNameErrorLabel;

    @FXML
    private ComboBox<String> branchComboBox;

    @FXML
    private Label branchErrorLabel;

    @FXML
    private CheckBox activeCheckBox;

    @FXML
    private ComboBox<String> bankComboBox;

    @FXML
    private TextField accountNumberField;

    @FXML
    private TextField salaryField;

    @FXML
    private ComboBox<String> employmentTermsComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private VBox rolesContainer;

    private MainViewController mainViewController;
    private EmployeeUIModel employeeToEdit;
    private boolean isEditMode = false;
    private List<CheckBox> roleCheckboxes = new ArrayList<>();

    /**
     * Sets the main view controller.
     * This is needed for navigation back to the previous view.
     * 
     * @param controller The main view controller
     */
    public void setMainViewController(MainViewController controller) {
        this.mainViewController = controller;
    }

    /**
     * Sets the employee to edit.
     * If null, the form will be in "create" mode.
     * 
     * @param employee The employee to edit, or null for create mode
     */
    public void setEmployee(EmployeeUIModel employee) {
        this.employeeToEdit = employee;
        this.isEditMode = (employee != null);

        if (isEditMode) {
            formTitleLabel.setText("Edit Employee");
            loadEmployeeData();
        } else {
            formTitleLabel.setText("Add New Employee");
            clearForm();
        }
    }

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Initialize branch combo box
        branchComboBox.setItems(FXCollections.observableArrayList(
                "Branch 1", "Branch 2", "Branch 3", "Branch 4", "Branch 5"));

        // Initialize bank combo box
        bankComboBox.setItems(FXCollections.observableArrayList(
                "Bank Leumi", "Bank Hapoalim", "Bank Discount", "Bank Mizrahi-Tefahot", "Bank Otsar Ha-Hayal"));

        // Initialize employment terms combo box
        employmentTermsComboBox.setItems(FXCollections.observableArrayList(
                "Full-time", "Part-time", "Temporary", "Seasonal", "Intern"));

        // Initialize start date picker
        startDatePicker.setValue(LocalDate.now());

        // Initialize roles
        initializeRoles();
    }

    /**
     * Initializes the roles section with checkboxes for each role.
     */
    private void initializeRoles() {
        // In a real implementation, this would get roles from the service layer
        // For now, use mock data
        String[] availableRoles = {
                "Manager", "Cashier", "Stocker", "Security", "Cleaner", "Shift Manager", "HR", "Accountant"
        };

        rolesContainer.getChildren().clear();
        roleCheckboxes.clear();

        for (String role : availableRoles) {
            CheckBox roleCheckbox = new CheckBox(role);
            roleCheckboxes.add(roleCheckbox);
            rolesContainer.getChildren().add(roleCheckbox);
        }
    }

    /**
     * Loads employee data into the form fields.
     */
    private void loadEmployeeData() {
        if (employeeToEdit == null) {
            return;
        }

        // Set basic information
        israeliIdField.setText(String.valueOf(employeeToEdit.getIsraeliId()));
        israeliIdField.setDisable(true); // Can't change ID in edit mode
        firstNameField.setText(employeeToEdit.getFirstName());
        lastNameField.setText(employeeToEdit.getLastName());
        branchComboBox.getSelectionModel().select("Branch " + employeeToEdit.getBranchId());
        activeCheckBox.setSelected(employeeToEdit.isActive());

        // Set mock data for fields not in the EmployeeUIModel
        bankComboBox.getSelectionModel().select("Bank Leumi");
        accountNumberField.setText("12345");
        salaryField.setText("5000");
        employmentTermsComboBox.getSelectionModel().select("Full-time");
        startDatePicker.setValue(LocalDate.of(2023, 1, 15));

        // Set roles
        String[] roles = employeeToEdit.getRoles().split(", ");
        for (CheckBox checkbox : roleCheckboxes) {
            checkbox.setSelected(Arrays.asList(roles).contains(checkbox.getText()));
        }
    }

    /**
     * Clears all form fields.
     */
    private void clearForm() {
        israeliIdField.clear();
        israeliIdField.setDisable(false);
        firstNameField.clear();
        lastNameField.clear();
        branchComboBox.getSelectionModel().clearSelection();
        activeCheckBox.setSelected(true);

        bankComboBox.getSelectionModel().clearSelection();
        accountNumberField.clear();
        salaryField.clear();
        employmentTermsComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(LocalDate.now());

        // Clear roles
        for (CheckBox checkbox : roleCheckboxes) {
            checkbox.setSelected(false);
        }

        // Clear error messages
        israeliIdErrorLabel.setVisible(false);
        firstNameErrorLabel.setVisible(false);
        lastNameErrorLabel.setVisible(false);
        branchErrorLabel.setVisible(false);
    }

    /**
     * Adds a new role checkbox to the roles container.
     */
    @FXML
    public void addRole() {
        // In a real implementation, this would show a dialog to add a custom role
        // For now, just show a message
        System.out.println("Add role requested");
    }

    /**
     * Validates the form input.
     * 
     * @return True if the input is valid, false otherwise
     */
    private boolean validateForm() {
        boolean isValid = true;

        // Validate Israeli ID
        if (israeliIdField.getText().isEmpty()) {
            israeliIdErrorLabel.setText("Israeli ID is required");
            israeliIdErrorLabel.setVisible(true);
            isValid = false;
        } else if (!israeliIdField.getText().matches("\\d{9}")) {
            israeliIdErrorLabel.setText("Israeli ID must be 9 digits");
            israeliIdErrorLabel.setVisible(true);
            isValid = false;
        } else {
            israeliIdErrorLabel.setVisible(false);
        }

        // Validate first name
        if (firstNameField.getText().isEmpty()) {
            firstNameErrorLabel.setText("First name is required");
            firstNameErrorLabel.setVisible(true);
            isValid = false;
        } else {
            firstNameErrorLabel.setVisible(false);
        }

        // Validate last name
        if (lastNameField.getText().isEmpty()) {
            lastNameErrorLabel.setText("Last name is required");
            lastNameErrorLabel.setVisible(true);
            isValid = false;
        } else {
            lastNameErrorLabel.setVisible(false);
        }

        // Validate branch
        if (branchComboBox.getSelectionModel().isEmpty()) {
            branchErrorLabel.setText("Branch is required");
            branchErrorLabel.setVisible(true);
            isValid = false;
        } else {
            branchErrorLabel.setVisible(false);
        }

        return isValid;
    }

    /**
     * Saves the employee data.
     */
    @FXML
    public void save() {
        if (!validateForm()) {
            return;
        }

        try {
            // Get branch ID from selected branch
            String selectedBranch = branchComboBox.getSelectionModel().getSelectedItem();
            long branchId = Long.parseLong(selectedBranch.replace("Branch ", ""));

            // Get selected roles
            List<String> selectedRoles = new ArrayList<>();
            for (CheckBox checkbox : roleCheckboxes) {
                if (checkbox.isSelected()) {
                    selectedRoles.add(checkbox.getText());
                }
            }
            String roles = String.join(", ", selectedRoles);

            // Create or update employee
            if (isEditMode) {
                // Update existing employee
                employeeToEdit.setFirstName(firstNameField.getText());
                employeeToEdit.setLastName(lastNameField.getText());
                employeeToEdit.setBranchId(branchId);
                employeeToEdit.setRoles(roles);
                employeeToEdit.setActive(activeCheckBox.isSelected());

                System.out.println("Employee updated: " + employeeToEdit.getIsraeliId());
            } else {
                // Create new employee
                long israeliId = Long.parseLong(israeliIdField.getText());
                EmployeeUIModel newEmployee = new EmployeeUIModel(
                        israeliId,
                        firstNameField.getText(),
                        lastNameField.getText(),
                        roles,
                        branchId,
                        activeCheckBox.isSelected());

                System.out.println("New employee created: " + newEmployee.getIsraeliId());
            }

            // Go back to previous view
            goBack();

        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter valid numeric values.");
        } catch (Exception e) {
            showError("Error", "An error occurred while saving the employee: " + e.getMessage());
        }
    }

    /**
     * Cancels the form and goes back to the previous view.
     */
    @FXML
    public void cancel() {
        goBack();
    }

    /**
     * Goes back to the previous view.
     */
    @FXML
    public void goBack() {
        if (mainViewController != null) {
            if (isEditMode) {
                // Go back to employee details
                mainViewController.showEmployeeDetails(employeeToEdit);
            } else {
                // Go back to employee list
                mainViewController.showEmployees();
            }
        }
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