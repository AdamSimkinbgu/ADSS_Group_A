package PresentationLayer.EmployeeSubModule.controllers;

import DTOs.ShiftDTO;
import PresentationLayer.EmployeeSubModule.models.EmployeeUIModel;
import PresentationLayer.EmployeeSubModule.utils.ServiceFacade;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the shift calendar view.
 * Displays a weekly calendar of shifts and allows managing shifts.
 */
public class ShiftCalendarController {

    @FXML
    private DatePicker calendarDatePicker;

    @FXML
    private ComboBox<String> branchFilterComboBox;

    @FXML
    private Label currentWeekLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Label selectedShiftLabel;

    @FXML
    private Button editShiftButton;

    @FXML
    private Button deleteShiftButton;

    @FXML
    private Label shiftDateLabel;

    @FXML
    private Label shiftTypeLabel;

    @FXML
    private Label shiftTimeLabel;

    @FXML
    private Label shiftBranchLabel;

    @FXML
    private Label shiftStatusLabel;

    @FXML
    private Label shiftRolesStatusLabel;

    @FXML
    private TableView<RoleRequirement> rolesRequiredTable;

    @FXML
    private TableColumn<RoleRequirement, String> roleNameColumn;

    @FXML
    private TableColumn<RoleRequirement, Integer> roleCountColumn;

    @FXML
    private TableColumn<RoleRequirement, Integer> roleAssignedColumn;

    @FXML
    private TableView<EmployeeShiftAssignment> assignedEmployeesTable;

    @FXML
    private TableColumn<EmployeeShiftAssignment, Long> employeeIdColumn;

    @FXML
    private TableColumn<EmployeeShiftAssignment, String> employeeNameColumn;

    @FXML
    private TableColumn<EmployeeShiftAssignment, String> employeeRoleColumn;

    @FXML
    private TableColumn<EmployeeShiftAssignment, Void> employeeActionsColumn;

    private MainViewController mainViewController;
    private LocalDate currentWeekStart;
    private ShiftData selectedShift;
    private Map<String, ShiftData> shiftsMap = new HashMap<>();
    private List<String> availableBranches = new ArrayList<>();

    // Services
    private ServiceLayer.EmployeeSubModule.ShiftService shiftService;
    private ServiceLayer.EmployeeSubModule.EmployeeService employeeService;

    /**
     * Sets the shift service.
     * This method should be called after the controller is initialized.
     * 
     * @param shiftService The ShiftService to use
     */
    public void setShiftService(ServiceLayer.EmployeeSubModule.ShiftService shiftService) {
        this.shiftService = shiftService;
        System.out.println("ShiftService set in ShiftCalendarController");

        // Refresh the calendar with real data
        if (calendarGrid != null) {
            loadBranches();
            refreshCalendar();
        }
    }

    /**
     * Sets the employee service.
     * This method should be called after the controller is initialized.
     * 
     * @param employeeService The EmployeeService to use
     */
    public void setEmployeeService(ServiceLayer.EmployeeSubModule.EmployeeService employeeService) {
        this.employeeService = employeeService;
        System.out.println("EmployeeService set in ShiftCalendarController");
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
        // Initialize branch filter combo box
        branchFilterComboBox.setPromptText("Select Branch");
        branchFilterComboBox.setOnAction(event -> refreshCalendar());

        // Initialize date picker
        calendarDatePicker.setValue(LocalDate.now());
        calendarDatePicker.setOnAction(event -> jumpToDate());

        // Initialize table columns for roles required
        roleNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoleName()));
        roleCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRequired()).asObject());
        roleAssignedColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAssigned()).asObject());

        // Initialize table columns for assigned employees
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        employeeRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        setupEmployeeActionsColumn();

        // Set current week to this week
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateWeekLabel();

        // Create calendar grid
        createCalendarGrid();

        // Data will be loaded when the service is set
    }

    /**
     * Loads available branches from the service.
     */
    private void loadBranches() {
        try {
            // In a real implementation, this would get branches from a service
            // For now, use some default branches
            availableBranches = new ArrayList<>();
            availableBranches.add("Branch 1");
            availableBranches.add("Branch 2");
            availableBranches.add("Branch 3");
            availableBranches.add("Branch 4");
            availableBranches.add("Branch 5");

            branchFilterComboBox.setItems(FXCollections.observableArrayList(availableBranches));

            // Select the first branch by default
            if (!availableBranches.isEmpty()) {
                branchFilterComboBox.getSelectionModel().select(0);
            }
        } catch (Exception e) {
            System.err.println("Error loading branches: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates the calendar grid with headers and empty cells.
     */
    private void createCalendarGrid() {
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear();
        calendarGrid.getColumnConstraints().clear();

        // Add column headers (days of week)
        for (int col = 1; col <= 7; col++) {
            DayOfWeek day = DayOfWeek.of(col);
            Label dayLabel = new Label(day.toString());
            dayLabel.getStyleClass().add("calendar-header");
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            calendarGrid.add(dayLabel, col, 0);
        }

        // Add row headers (shift types)
        String[] shiftTypes = {"Morning", "Evening"};
        for (int row = 1; row <= shiftTypes.length; row++) {
            Label typeLabel = new Label(shiftTypes[row - 1]);
            typeLabel.getStyleClass().add("calendar-header");
            calendarGrid.add(typeLabel, 0, row);
        }

        // Add empty cells
        for (int col = 1; col <= 7; col++) {
            for (int row = 1; row <= shiftTypes.length; row++) {
                StackPane cell = createEmptyCell();
                calendarGrid.add(cell, col, row);
            }
        }
    }

    /**
     * Creates an empty calendar cell.
     * 
     * @return The empty cell
     */
    private StackPane createEmptyCell() {
        StackPane cell = new StackPane();
        cell.getStyleClass().add("calendar-cell");
        cell.setMinHeight(150);  // Increased height
        cell.setMinWidth(150);   // Increased width

        Rectangle background = new Rectangle();
        background.setWidth(150);
        background.setHeight(150);
        background.setFill(Color.TRANSPARENT);
        background.setStroke(Color.LIGHTGRAY);

        cell.getChildren().add(background);
        return cell;
    }

    /**
     * Updates the week label with the current week's date range.
     */
    private void updateWeekLabel() {
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        currentWeekLabel.setText("Week of " + currentWeekStart.format(formatter) + " - " + weekEnd.format(formatter));
    }

    /**
     * Refreshes the calendar with shift data.
     */
    private void refreshCalendar() {
        // Clear existing shifts
        shiftsMap.clear();

        try {
            if (shiftService == null) {
                System.out.println("ShiftService not set in ShiftCalendarController");
                // Don't use mock data, just show empty calendar
            } else {
                // Get real data from the database
                long doneBy = 123456789; // Using a default user ID

                // Get shifts for the current week using the Week utility class
                Util.Week currentWeek = Util.Week.from(currentWeekStart);
                Set<ShiftDTO> shifts = shiftService.getShiftsByWeek(doneBy, currentWeek);

                // Process each shift
                for (ShiftDTO shiftDTO : shifts) {
                    LocalDate shiftDate = shiftDTO.getShiftDate();
                    if (shiftDate != null) {
                        // Map shift type to display type
                        String displayType;
                        String timeRange;
                        DomainLayer.enums.ShiftType shiftType = shiftDTO.getShiftType();

                        if (Objects.equals(shiftType.toString(), "MORNING")) {
                            displayType = "Morning";
                            timeRange = shiftDTO.getStartHour() != null && shiftDTO.getEndHour() != null ? 
                                shiftDTO.getStartHour() + " - " + shiftDTO.getEndHour() : "08:00 - 16:00";
                        } else {
                            displayType = "Evening";
                            timeRange = shiftDTO.getStartHour() != null && shiftDTO.getEndHour() != null ? 
                                shiftDTO.getStartHour() + " - " + shiftDTO.getEndHour() : "16:00 - 00:00";
                        }

                        // Map branch ID to branch name
                        long branchId = shiftDTO.getBranchId();
                        String branchName = "Branch " + branchId;

                        // Map open status to display status
                        String status = shiftDTO.isOpen() ? "Open" : "Closed";

                        // Count assigned employees
                        int employeeCount = 0;
                        Map<String, Integer> roleAssignments = new HashMap<>();

                        if (shiftDTO.getAssignedEmployees() != null) {
                            for (Map.Entry<String, Set<Long>> entry : shiftDTO.getAssignedEmployees().entrySet()) {
                                String role = entry.getKey();
                                Set<Long> employees = entry.getValue();
                                employeeCount += employees.size();
                                roleAssignments.put(role, employees.size());
                            }
                        }

                        // Create shift data object
                        ShiftData shift = new ShiftData(
                            String.valueOf(shiftDTO.getId()),
                            shiftDate,
                            displayType,
                            timeRange,
                            branchName,
                            status,
                            employeeCount,
                            shiftDTO.getRolesRequired(),
                            roleAssignments,
                            shiftDTO.getAssignedEmployees()
                        );

                        // Add to shifts map
                        shiftsMap.put(shift.getId(), shift);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading shifts: " + e.getMessage());
            e.printStackTrace();
        }

        // Apply branch filter if needed
        String selectedBranch = branchFilterComboBox.getSelectionModel().getSelectedItem();
        if (selectedBranch != null && !selectedBranch.isEmpty()) {
            filterShiftsByBranch(selectedBranch);
        }

        // Update calendar grid with shifts
        updateCalendarGrid();

        // Clear shift details
        clearShiftDetails();
    }

    /**
     * Filters shifts by branch.
     * 
     * @param branch The branch to filter by
     */
    private void filterShiftsByBranch(String branch) {
        Iterator<Map.Entry<String, ShiftData>> iterator = shiftsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ShiftData> entry = iterator.next();
            if (!entry.getValue().getBranch().equals(branch)) {
                iterator.remove();
            }
        }
    }

    /**
     * Updates the calendar grid with shift data.
     */
    private void updateCalendarGrid() {
        // Clear all existing cells
        calendarGrid.getChildren().clear();

        // Recreate the headers
        // Add column headers (days of week)
        for (int col = 1; col <= 7; col++) {
            DayOfWeek day = DayOfWeek.of(col);
            Label dayLabel = new Label(day.toString());
            dayLabel.getStyleClass().add("calendar-header");
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            calendarGrid.add(dayLabel, col, 0);
        }

        // Add row headers (shift types)
        String[] shiftTypes = {"Morning", "Evening"};
        for (int row = 1; row <= shiftTypes.length; row++) {
            Label typeLabel = new Label(shiftTypes[row - 1]);
            typeLabel.getStyleClass().add("calendar-header");
            calendarGrid.add(typeLabel, 0, row);
        }

        // Add empty cells
        for (int col = 1; col <= 7; col++) {
            for (int row = 1; row <= 2; row++) {
                calendarGrid.add(createEmptyCell(), col, row);
            }
        }

        // Add shifts to the grid
        for (ShiftData shift : shiftsMap.values()) {
            // Calculate column (day of week)
            int dayOfWeek = shift.getDate().getDayOfWeek().getValue();

            // Calculate row (shift type)
            int row;
            switch (shift.getType()) {
                case "Morning":
                    row = 1;
                    break;
                case "Evening":
                    row = 2;
                    break;
                default:
                    row = 1;
            }

            // Create shift cell
            StackPane cell = createShiftCell(shift);
            calendarGrid.add(cell, dayOfWeek, row);
        }
    }

    /**
     * Creates a cell with shift information.
     * 
     * @param shift The shift data
     * @return The shift cell
     */
    private StackPane createShiftCell(ShiftData shift) {
        StackPane cell = new StackPane();
        cell.getStyleClass().add("calendar-cell");
        cell.setMinHeight(150);  // Increased height
        cell.setMinWidth(150);   // Increased width

        Rectangle background = new Rectangle();
        background.setWidth(150);
        background.setHeight(150);
        background.setFill(getShiftColor(shift.getStatus()));
        background.setStroke(Color.GRAY);

        VBox content = new VBox(5);
        content.getStyleClass().add("shift-cell-content");
        content.setPadding(new Insets(5));
        content.setAlignment(Pos.TOP_LEFT);

        Label typeLabel = new Label(shift.getType());
        typeLabel.getStyleClass().add("shift-type-label");
        typeLabel.setStyle("-fx-font-weight: bold;");

        Label timeLabel = new Label(shift.getTimeRange());
        timeLabel.getStyleClass().add("shift-time-label");

        Label branchLabel = new Label(shift.getBranch());
        branchLabel.getStyleClass().add("shift-branch-label");

        Label statusLabel = new Label("Status: " + shift.getStatus());
        statusLabel.getStyleClass().add("shift-status-label");

        Label employeesLabel = new Label(shift.getEmployeeCount() + " employees assigned");
        employeesLabel.getStyleClass().add("shift-employees-label");

        // Add role information
        VBox rolesBox = new VBox(2);
        rolesBox.getStyleClass().add("shift-roles-box");

        // Show up to 3 roles in the cell
        int roleCount = 0;
        for (Map.Entry<String, Integer> entry : shift.getRolesRequired().entrySet()) {
            if (roleCount >= 3) {
                Label moreLabel = new Label("... and more roles");
                moreLabel.getStyleClass().add("shift-more-roles-label");
                rolesBox.getChildren().add(moreLabel);
                break;
            }

            String role = entry.getKey();
            int required = entry.getValue();
            int assigned = shift.getRoleAssignments().getOrDefault(role, 0);

            Label roleLabel = new Label(role + ": " + assigned + "/" + required);
            roleLabel.getStyleClass().add("shift-role-label");

            if (assigned < required) {
                roleLabel.setTextFill(Color.RED);
            } else {
                roleLabel.setTextFill(Color.GREEN);
            }

            rolesBox.getChildren().add(roleLabel);
            roleCount++;
        }

        content.getChildren().addAll(typeLabel, timeLabel, branchLabel, statusLabel, employeesLabel, rolesBox);

        cell.getChildren().addAll(background, content);

        // Add click handler
        cell.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                selectShift(shift);
            }
        });

        // Add context menu for right-click
        ContextMenu contextMenu = createShiftContextMenu(shift);
        cell.setOnContextMenuRequested(event -> {
            contextMenu.show(cell, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        return cell;
    }

    /**
     * Creates a context menu for a shift.
     * 
     * @param shift The shift data
     * @return The context menu
     */
    private ContextMenu createShiftContextMenu(ShiftData shift) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit Shift");
        editItem.setOnAction(event -> {
            selectShift(shift);
            editSelectedShift();
        });

        MenuItem assignItem = new MenuItem("Assign Employees");
        assignItem.setOnAction(event -> {
            selectShift(shift);
            showAssignEmployeesDialog();
        });

        MenuItem deleteItem = new MenuItem("Delete Shift");
        deleteItem.setOnAction(event -> {
            selectShift(shift);
            deleteSelectedShift();
        });

        contextMenu.getItems().addAll(editItem, assignItem, new SeparatorMenuItem(), deleteItem);

        return contextMenu;
    }

    /**
     * Gets the color for a shift based on its status.
     * 
     * @param status The shift status
     * @return The color for the shift
     */
    private Color getShiftColor(String status) {
        switch (status) {
            case "Open":
                return Color.rgb(200, 230, 200, 0.7); // Light green
            case "Closed":
                return Color.rgb(230, 200, 200, 0.7); // Light red
            case "In Progress":
                return Color.rgb(200, 200, 230, 0.7); // Light blue
            default:
                return Color.rgb(240, 240, 240, 0.7); // Light gray
        }
    }

    /**
     * Selects a shift and displays its details.
     * 
     * @param shift The shift to select
     */
    private void selectShift(ShiftData shift) {
        selectedShift = shift;

        // Update shift details
        selectedShiftLabel.setText(shift.getType() + " Shift on " + shift.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        shiftDateLabel.setText(shift.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        shiftTypeLabel.setText(shift.getType());
        shiftTimeLabel.setText(shift.getTimeRange());
        shiftBranchLabel.setText(shift.getBranch());
        shiftStatusLabel.setText(shift.getStatus());

        // Check if all roles are filled
        boolean allRolesFilled = true;
        for (Map.Entry<String, Integer> entry : shift.getRolesRequired().entrySet()) {
            String role = entry.getKey();
            int required = entry.getValue();
            int assigned = shift.getRoleAssignments().getOrDefault(role, 0);

            if (assigned < required) {
                allRolesFilled = false;
                break;
            }
        }

        if (shift.getRolesRequired().isEmpty()) {
            shiftRolesStatusLabel.setText("No roles defined");
        } else if (allRolesFilled) {
            shiftRolesStatusLabel.setText("All positions filled");
            shiftRolesStatusLabel.setTextFill(Color.GREEN);
        } else {
            shiftRolesStatusLabel.setText("Some positions unfilled");
            shiftRolesStatusLabel.setTextFill(Color.RED);
        }

        // Enable buttons
        editShiftButton.setDisable(false);
        deleteShiftButton.setDisable(false);

        // Load roles required
        loadRolesRequired();

        // Load assigned employees
        loadAssignedEmployees();
    }

    /**
     * Loads the roles required for the selected shift.
     */
    private void loadRolesRequired() {
        if (selectedShift == null) {
            return;
        }

        ObservableList<RoleRequirement> roles = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : selectedShift.getRolesRequired().entrySet()) {
            String role = entry.getKey();
            int required = entry.getValue();
            int assigned = selectedShift.getRoleAssignments().getOrDefault(role, 0);

            roles.add(new RoleRequirement(role, required, assigned));
        }

        rolesRequiredTable.setItems(roles);
    }

    /**
     * Clears the shift details section.
     */
    private void clearShiftDetails() {
        selectedShift = null;
        selectedShiftLabel.setText("No shift selected");
        shiftDateLabel.setText("-");
        shiftTypeLabel.setText("-");
        shiftTimeLabel.setText("-");
        shiftBranchLabel.setText("-");
        shiftStatusLabel.setText("-");
        shiftRolesStatusLabel.setText("-");

        // Disable buttons
        editShiftButton.setDisable(true);
        deleteShiftButton.setDisable(true);

        // Clear tables
        rolesRequiredTable.getItems().clear();
        assignedEmployeesTable.getItems().clear();
    }

    /**
     * Loads the assigned employees for the selected shift.
     */
    private void loadAssignedEmployees() {
        if (selectedShift == null) {
            return;
        }

        ObservableList<EmployeeShiftAssignment> employees = FXCollections.observableArrayList();

        // Get assigned employees from the shift data
        Map<String, Set<Long>> assignedEmployees = selectedShift.getAssignedEmployeesMap();

        if (assignedEmployees != null) {
            for (Map.Entry<String, Set<Long>> entry : assignedEmployees.entrySet()) {
                String role = entry.getKey();
                Set<Long> employeeIds = entry.getValue();

                for (Long employeeId : employeeIds) {
                    // In a real implementation, get employee details from the service
                    String employeeName = "Employee " + employeeId;

                    employees.add(new EmployeeShiftAssignment(employeeId, employeeName, role));
                }
            }
        }

        assignedEmployeesTable.setItems(employees);
    }

    /**
     * Sets up the employee actions column in the assigned employees table.
     */
    private void setupEmployeeActionsColumn() {
        employeeActionsColumn.setCellFactory(column -> {
            return new TableCell<>() {
                private final Button removeButton = new Button("X");

                {
                    removeButton.getStyleClass().add("action-button");
                    removeButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-font-weight: bold;");
                    removeButton.setOnAction(event -> {
                        EmployeeShiftAssignment employee = getTableView().getItems().get(getIndex());
                        removeEmployeeFromShift(employee);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(removeButton);
                    }
                }
            };
        });
    }

    /**
     * Removes an employee from the selected shift.
     * 
     * @param employee The employee to remove
     */
    private void removeEmployeeFromShift(EmployeeShiftAssignment employee) {
        if (selectedShift == null || shiftService == null) {
            return;
        }

        try {
            // Call the service to remove the assignment
            long doneBy = 123456789; // Using a default user ID
            long shiftId = Long.parseLong(selectedShift.getId());
            long employeeId = employee.getEmployeeId();
            String role = employee.getRole();

            String result = shiftService.removeAssignment(doneBy, shiftId, role, employeeId);

            if (result.startsWith("Employee assignment removed")) {
                // Update UI
                assignedEmployeesTable.getItems().remove(employee);

                // Update shift data
                selectedShift.decrementEmployeeCount();
                selectedShift.decrementRoleAssignment(role);

                // Refresh calendar to update the employee count
                updateCalendarGrid();

                // Refresh roles required table
                loadRolesRequired();

                // Update roles status
                boolean allRolesFilled = true;
                for (Map.Entry<String, Integer> entry : selectedShift.getRolesRequired().entrySet()) {
                    String r = entry.getKey();
                    int required = entry.getValue();
                    int assigned = selectedShift.getRoleAssignments().getOrDefault(r, 0);

                    if (assigned < required) {
                        allRolesFilled = false;
                        break;
                    }
                }

                if (selectedShift.getRolesRequired().isEmpty()) {
                    shiftRolesStatusLabel.setText("No roles defined");
                } else if (allRolesFilled) {
                    shiftRolesStatusLabel.setText("All positions filled");
                    shiftRolesStatusLabel.setTextFill(Color.GREEN);
                } else {
                    shiftRolesStatusLabel.setText("Some positions unfilled");
                    shiftRolesStatusLabel.setTextFill(Color.RED);
                }
            } else {
                // Show error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to remove employee: " + result);
                alert.showAndWait();
            }
        } catch (Exception e) {
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error removing employee: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Shows the previous week in the calendar.
     */
    @FXML
    public void showPreviousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateWeekLabel();
        refreshCalendar();
    }

    /**
     * Shows the next week in the calendar.
     */
    @FXML
    public void showNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateWeekLabel();
        refreshCalendar();
    }

    /**
     * Shows the current week in the calendar.
     */
    @FXML
    public void showCurrentWeek() {
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateWeekLabel();
        refreshCalendar();
    }

    /**
     * Jumps to the week containing the selected date.
     */
    private void jumpToDate() {
        LocalDate selectedDate = calendarDatePicker.getValue();
        if (selectedDate != null) {
            currentWeekStart = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            updateWeekLabel();
            refreshCalendar();
        }
    }

    /**
     * Shows the dialog to create a new shift.
     */
    @FXML
    public void showCreateShiftDialog() {
        System.out.println("Create shift dialog requested");

        // Create a dialog
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Create New Shift");
        dialog.setHeaderText("Enter shift details");

        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Date picker for shift date
        DatePicker datePicker = new DatePicker(LocalDate.now());

        // Shift type combo box
        ComboBox<String> shiftTypeCombo = new ComboBox<>();
        shiftTypeCombo.getItems().addAll("Morning", "Evening");
        shiftTypeCombo.getSelectionModel().selectFirst();

        // Branch combo box
        ComboBox<String> branchCombo = new ComboBox<>();
        branchCombo.getItems().addAll(availableBranches);
        branchCombo.getSelectionModel().selectFirst();

        // Add fields to the grid
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Shift Type:"), 0, 1);
        grid.add(shiftTypeCombo, 1, 1);
        grid.add(new Label("Branch:"), 0, 2);
        grid.add(branchCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a map when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Map<String, Object> result = new HashMap<>();
                result.put("date", datePicker.getValue());
                result.put("type", shiftTypeCombo.getValue());
                result.put("branch", branchCombo.getValue());
                return result;
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Map<String, Object>> result = dialog.showAndWait();

        result.ifPresent(shiftData -> {
            System.out.println("Creating shift: " + shiftData);

            // In a real implementation, this would call the service layer to create the shift
            if (shiftService != null) {
                try {
                    // Extract branch ID from branch name
                    String branchName = (String) shiftData.get("branch");
                    long branchId = Long.parseLong(branchName.replace("Branch ", ""));

                    // Map shift type to enum
                    String shiftTypeStr = (String) shiftData.get("type");
                    DomainLayer.enums.ShiftType shiftType = "Morning".equals(shiftTypeStr) ? 
                        DomainLayer.enums.ShiftType.MORNING : DomainLayer.enums.ShiftType.EVENING;

                    // Create a new shift DTO
                    ShiftDTO newShift = new ShiftDTO();
                    newShift.setShiftDate((LocalDate) shiftData.get("date"));
                    newShift.setShiftType(shiftType);
                    newShift.setBranchId(branchId);
                    newShift.setOpen(true);

                    // Set default times based on shift type
                    if (shiftType == DomainLayer.enums.ShiftType.MORNING) {
                        newShift.setStartHour(LocalTime.of(8, 0));
                        newShift.setEndHour(LocalTime.of(16, 0));
                    } else {
                        newShift.setStartHour(LocalTime.of(16, 0));
                        newShift.setEndHour(LocalTime.of(0, 0));
                    }

                    // Call the service to create the shift
                    long doneBy = 123456789; // Using a default user ID

                    // Create empty maps for roles and employees
                    Map<String, Integer> rolesRequired = new HashMap<>();
                    Map<String, Set<Long>> assignedEmployees = new HashMap<>();
                    Set<Long> availableEmployees = new HashSet<>();

                    // Call the service with all required parameters
                    String createResult = shiftService.createShift(
                        doneBy, 
                        shiftType, 
                        (LocalDate) shiftData.get("date"), 
                        rolesRequired, 
                        assignedEmployees, 
                        availableEmployees, 
                        false, // isAssignedShiftManager
                        true,  // isOpen
                        newShift.getStartHour(), 
                        newShift.getEndHour(), 
                        LocalDate.now() // updateDate
                    );
                    System.out.println("Shift created: " + createResult);

                    // Refresh the calendar to show the new shift
                    refreshCalendar();

                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Shift created successfully!");
                    alert.showAndWait();
                } catch (Exception e) {
                    System.err.println("Error creating shift: " + e.getMessage());
                    e.printStackTrace();

                    // Show error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error creating shift: " + e.getMessage());
                    alert.showAndWait();
                }
            } else {
                // If service is not available, just show a message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Demo Mode");
                alert.setHeaderText(null);
                alert.setContentText("Shift would be created with these details: " + shiftData);
                alert.showAndWait();
            }
        });
    }

    /**
     * Shows the dialog to edit the selected shift.
     */
    @FXML
    public void editSelectedShift() {
        if (selectedShift != null) {
            System.out.println("Edit shift requested: " + selectedShift.getId());

            // Create a dialog
            Dialog<Map<String, Object>> dialog = new Dialog<>();
            dialog.setTitle("Edit Shift");
            dialog.setHeaderText("Edit shift details");

            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create the form grid
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Date picker for shift date
            DatePicker datePicker = new DatePicker(selectedShift.getDate());

            // Shift type combo box
            ComboBox<String> shiftTypeCombo = new ComboBox<>();
            shiftTypeCombo.getItems().addAll("Morning", "Evening");
            shiftTypeCombo.setValue(selectedShift.getType());

            // Branch combo box
            ComboBox<String> branchCombo = new ComboBox<>();
            branchCombo.getItems().addAll(availableBranches);
            branchCombo.setValue(selectedShift.getBranch());

            // Status combo box
            ComboBox<String> statusCombo = new ComboBox<>();
            statusCombo.getItems().addAll("Open", "Closed");
            statusCombo.setValue(selectedShift.getStatus());

            // Add fields to the grid
            grid.add(new Label("Date:"), 0, 0);
            grid.add(datePicker, 1, 0);
            grid.add(new Label("Shift Type:"), 0, 1);
            grid.add(shiftTypeCombo, 1, 1);
            grid.add(new Label("Branch:"), 0, 2);
            grid.add(branchCombo, 1, 2);
            grid.add(new Label("Status:"), 0, 3);
            grid.add(statusCombo, 1, 3);

            dialog.getDialogPane().setContent(grid);

            // Convert the result to a map when the save button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", selectedShift.getId());
                    result.put("date", datePicker.getValue());
                    result.put("type", shiftTypeCombo.getValue());
                    result.put("branch", branchCombo.getValue());
                    result.put("status", statusCombo.getValue());
                    return result;
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Map<String, Object>> result = dialog.showAndWait();

            result.ifPresent(shiftData -> {
                System.out.println("Updating shift: " + shiftData);

                // In a real implementation, this would call the service layer to update the shift
                if (shiftService != null) {
                    try {
                        // Extract branch ID from branch name
                        String branchName = (String) shiftData.get("branch");
                        long branchId = Long.parseLong(branchName.replace("Branch ", ""));

                        // Map shift type to enum
                        String shiftTypeStr = (String) shiftData.get("type");
                        DomainLayer.enums.ShiftType shiftType = "Morning".equals(shiftTypeStr) ? 
                            DomainLayer.enums.ShiftType.MORNING : DomainLayer.enums.ShiftType.EVENING;

                        // Get the existing shift from the service
                        long shiftId = Long.parseLong(selectedShift.getId());
                        long doneBy = 123456789; // Using a default user ID

                        // Set default times based on shift type
                        LocalTime startHour = shiftType == DomainLayer.enums.ShiftType.MORNING ? 
                            LocalTime.of(8, 0) : LocalTime.of(16, 0);
                        LocalTime endHour = shiftType == DomainLayer.enums.ShiftType.MORNING ? 
                            LocalTime.of(16, 0) : LocalTime.of(0, 0);

                        // Call the service to update the shift with all required parameters
                        String updateResult = shiftService.updateShift(
                            doneBy,
                            shiftId,
                            shiftType,
                            (LocalDate) shiftData.get("date"),
                            false, // isAssignedShiftManager
                            "Open".equals(shiftData.get("status")), // isOpen
                            startHour,
                            endHour,
                            LocalDate.now() // updateDate
                        );
                        System.out.println("Shift updated: " + updateResult);

                        // Refresh the calendar to show the updated shift
                        refreshCalendar();

                        // Show success message
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Shift updated successfully!");
                        alert.showAndWait();
                    } catch (Exception e) {
                        System.err.println("Error updating shift: " + e.getMessage());
                        e.printStackTrace();

                        // Show error message
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Error updating shift: " + e.getMessage());
                        alert.showAndWait();
                    }
                } else {
                    // If service is not available, just show a message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Demo Mode");
                    alert.setHeaderText(null);
                    alert.setContentText("Shift would be updated with these details: " + shiftData);
                    alert.showAndWait();
                }
            });
        }
    }

    /**
     * Deletes the selected shift.
     */
    @FXML
    public void deleteSelectedShift() {
        if (selectedShift != null) {
            System.out.println("Delete shift requested: " + selectedShift.getId());

            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Shift");
            confirmAlert.setContentText("Are you sure you want to delete this shift?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // In a real implementation, this would call the service layer to delete the shift
                if (shiftService != null) {
                    try {
                        long doneBy = 123456789; // Using a default user ID
                        long shiftId = Long.parseLong(selectedShift.getId());

                        String deleteResult = shiftService.removeShiftByID(doneBy, shiftId);
                        System.out.println("Shift deleted: " + deleteResult);

                        // Remove from local map
                        shiftsMap.remove(selectedShift.getId());

                        // Update UI
                        updateCalendarGrid();
                        clearShiftDetails();

                        // Show success message
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Shift deleted successfully!");
                        alert.showAndWait();
                    } catch (Exception e) {
                        System.err.println("Error deleting shift: " + e.getMessage());
                        e.printStackTrace();

                        // Show error message
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Error deleting shift: " + e.getMessage());
                        alert.showAndWait();
                    }
                } else {
                    // If service is not available, just update the UI
                    shiftsMap.remove(selectedShift.getId());
                    updateCalendarGrid();
                    clearShiftDetails();
                }
            }
        }
    }

    /**
     * Shows the dialog to assign employees to the selected shift.
     */
    @FXML
    public void showAssignEmployeesDialog() {
        if (selectedShift != null) {
            System.out.println("Assign employees dialog requested for shift: " + selectedShift.getId());

            // Create a dialog
            Dialog<Map<String, Object>> dialog = new Dialog<>();
            dialog.setTitle("Assign Employees to Shift");
            dialog.setHeaderText("Assign employees to roles for this shift");

            // Set the button types
            ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButtonType);

            // Create the content
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));

            // Role selection
            ComboBox<String> roleComboBox = new ComboBox<>();
            for (String role : selectedShift.getRolesRequired().keySet()) {
                roleComboBox.getItems().add(role);
            }

            if (!roleComboBox.getItems().isEmpty()) {
                roleComboBox.getSelectionModel().selectFirst();
            } else {
                // Add a button to add roles
                Button addRoleButton = new Button("Add Role");
                addRoleButton.setOnAction(event -> {
                    showAddRoleDialog();

                    // Refresh the role combo box
                    roleComboBox.getItems().clear();
                    for (String role : selectedShift.getRolesRequired().keySet()) {
                        roleComboBox.getItems().add(role);
                    }

                    if (!roleComboBox.getItems().isEmpty()) {
                        roleComboBox.getSelectionModel().selectFirst();
                    }
                });

                content.getChildren().add(new Label("No roles defined for this shift."));
                content.getChildren().add(addRoleButton);

                dialog.getDialogPane().setContent(content);
                dialog.showAndWait();
                return;
            }

            // Employee selection
            ListView<EmployeeListItem> employeeListView = new ListView<>();
            employeeListView.setPrefHeight(300);

            // Update employee list when role changes
            roleComboBox.setOnAction(event -> {
                updateEmployeeList(employeeListView, roleComboBox.getValue());
            });

            // Initial update
            updateEmployeeList(employeeListView, roleComboBox.getValue());

            // Add components to the content
            content.getChildren().addAll(
                new Label("Select Role:"),
                roleComboBox,
                new Label("Select Employees:"),
                employeeListView
            );

            // Add a button to add a new role
            Button addRoleButton = new Button("Add New Role");
            addRoleButton.setOnAction(event -> {
                showAddRoleDialog();

                // Refresh the role combo box
                roleComboBox.getItems().clear();
                for (String role : selectedShift.getRolesRequired().keySet()) {
                    roleComboBox.getItems().add(role);
                }

                if (!roleComboBox.getItems().isEmpty()) {
                    roleComboBox.getSelectionModel().selectFirst();
                }
            });

            content.getChildren().add(addRoleButton);

            dialog.getDialogPane().setContent(content);
            dialog.showAndWait();
        }
    }

    /**
     * Shows a dialog to add a new role to the shift.
     */
    private void showAddRoleDialog() {
        if (selectedShift == null || shiftService == null) {
            return;
        }

        // Create a dialog
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Add Role");
        dialog.setHeaderText("Add a new role to the shift");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Role name field
        TextField roleNameField = new TextField();
        roleNameField.setPromptText("Role name");

        // Required count spinner
        Spinner<Integer> requiredCountSpinner = new Spinner<>(1, 10, 1);
        requiredCountSpinner.setEditable(true);

        // Add fields to the grid
        grid.add(new Label("Role Name:"), 0, 0);
        grid.add(roleNameField, 1, 0);
        grid.add(new Label("Required Count:"), 0, 1);
        grid.add(requiredCountSpinner, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a map when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Map<String, Object> result = new HashMap<>();
                result.put("roleName", roleNameField.getText());
                result.put("requiredCount", requiredCountSpinner.getValue());
                return result;
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Map<String, Object>> result = dialog.showAndWait();

        result.ifPresent(roleData -> {
            String roleName = (String) roleData.get("roleName");
            Integer requiredCount = (Integer) roleData.get("requiredCount");

            if (roleName != null && !roleName.trim().isEmpty() && requiredCount != null) {
                try {
                    // Call the service to update roles required
                    long doneBy = 123456789; // Using a default user ID
                    long shiftId = Long.parseLong(selectedShift.getId());

                    String updateResult = shiftService.updateRolesRequired(doneBy, shiftId, roleName, requiredCount);
                    System.out.println("Role added: " + updateResult);

                    // Update local data
                    selectedShift.addRoleRequired(roleName, requiredCount);

                    // Refresh UI
                    loadRolesRequired();
                    updateCalendarGrid();

                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Role added successfully!");
                    alert.showAndWait();
                } catch (Exception e) {
                    // Show error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error adding role: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    /**
     * Updates the employee list for a specific role.
     * 
     * @param employeeListView The list view to update
     * @param role The role to get employees for
     */
    private void updateEmployeeList(ListView<EmployeeListItem> employeeListView, String role) {
        if (selectedShift == null || role == null) {
            return;
        }

        ObservableList<EmployeeListItem> employees = FXCollections.observableArrayList();

        // Get assigned employees for this role
        Set<Long> assignedEmployeeIds = new HashSet<>();
        Map<String, Set<Long>> assignedEmployees = selectedShift.getAssignedEmployeesMap();
        if (assignedEmployees != null && assignedEmployees.containsKey(role)) {
            assignedEmployeeIds.addAll(assignedEmployees.get(role));
        }

        // In a real implementation, get available employees from the service
        // For now, use some mock data
        List<EmployeeListItem> mockEmployees = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            long id = 100000000 + i;
            String name = "Employee " + i;
            boolean assigned = assignedEmployeeIds.contains(id);

            mockEmployees.add(new EmployeeListItem(id, name, assigned));
        }

        employees.addAll(mockEmployees);
        employeeListView.setItems(employees);

        // Set cell factory to show checkboxes
        employeeListView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(EmployeeListItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    checkBox.setSelected(item.isAssigned());
                    checkBox.setText(item.getName() + " (ID: " + item.getId() + ")");

                    checkBox.setOnAction(event -> {
                        boolean newValue = checkBox.isSelected();
                        item.setAssigned(newValue);

                        // Update the assignment in the database
                        updateEmployeeAssignment(role, item.getId(), newValue);
                    });

                    setGraphic(checkBox);
                    setText(null);
                }
            }
        });
    }

    /**
     * Updates an employee's assignment to a role.
     * 
     * @param role The role to assign to
     * @param employeeId The employee ID
     * @param assign Whether to assign or unassign
     */
    private void updateEmployeeAssignment(String role, long employeeId, boolean assign) {
        if (selectedShift == null || shiftService == null) {
            return;
        }

        try {
            long doneBy = 123456789; // Using a default user ID
            long shiftId = Long.parseLong(selectedShift.getId());

            String result;
            if (assign) {
                // Assign employee to role
                result = shiftService.assignEmployeeToRole(doneBy, shiftId, employeeId, role);

                if (result.startsWith("Employee assigned")) {
                    // Update local data
                    selectedShift.addEmployeeToRole(role, employeeId);
                    selectedShift.incrementEmployeeCount();

                    // Refresh UI
                    loadAssignedEmployees();
                    loadRolesRequired();
                    updateCalendarGrid();

                    // Update roles status
                    updateRolesStatus();
                }
            } else {
                // Remove assignment
                result = shiftService.removeAssignment(doneBy, shiftId, role, employeeId);

                if (result.startsWith("Employee assignment removed")) {
                    // Update local data
                    selectedShift.removeEmployeeFromRole(role, employeeId);
                    selectedShift.decrementEmployeeCount();

                    // Refresh UI
                    loadAssignedEmployees();
                    loadRolesRequired();
                    updateCalendarGrid();

                    // Update roles status
                    updateRolesStatus();
                }
            }

            if (!result.startsWith("Employee")) {
                // Show error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error updating assignment: " + result);
                alert.showAndWait();
            }
        } catch (Exception e) {
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error updating assignment: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Updates the roles status label.
     */
    private void updateRolesStatus() {
        if (selectedShift == null) {
            return;
        }

        boolean allRolesFilled = true;
        for (Map.Entry<String, Integer> entry : selectedShift.getRolesRequired().entrySet()) {
            String role = entry.getKey();
            int required = entry.getValue();
            int assigned = selectedShift.getRoleAssignments().getOrDefault(role, 0);

            if (assigned < required) {
                allRolesFilled = false;
                break;
            }
        }

        if (selectedShift.getRolesRequired().isEmpty()) {
            shiftRolesStatusLabel.setText("No roles defined");
            shiftRolesStatusLabel.setTextFill(Color.BLACK);
        } else if (allRolesFilled) {
            shiftRolesStatusLabel.setText("All positions filled");
            shiftRolesStatusLabel.setTextFill(Color.GREEN);
        } else {
            shiftRolesStatusLabel.setText("Some positions unfilled");
            shiftRolesStatusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Inner class to represent shift data.
     */
    private static class ShiftData {
        private final String id;
        private final LocalDate date;
        private final String type;
        private final String timeRange;
        private final String branch;
        private final String status;
        private int employeeCount;
        private final Map<String, Integer> rolesRequired;
        private final Map<String, Integer> roleAssignments;
        private final Map<String, Set<Long>> assignedEmployeesMap;

        public ShiftData(String id, LocalDate date, String type, String timeRange, String branch, String status, 
                         int employeeCount, Map<String, Integer> rolesRequired, Map<String, Integer> roleAssignments,
                         Map<String, Set<Long>> assignedEmployeesMap) {
            this.id = id;
            this.date = date;
            this.type = type;
            this.timeRange = timeRange;
            this.branch = branch;
            this.status = status;
            this.employeeCount = employeeCount;
            this.rolesRequired = rolesRequired != null ? new HashMap<>(rolesRequired) : new HashMap<>();
            this.roleAssignments = roleAssignments != null ? new HashMap<>(roleAssignments) : new HashMap<>();
            this.assignedEmployeesMap = assignedEmployeesMap != null ? new HashMap<>(assignedEmployeesMap) : new HashMap<>();
        }

        public String getId() { return id; }
        public LocalDate getDate() { return date; }
        public String getType() { return type; }
        public String getTimeRange() { return timeRange; }
        public String getBranch() { return branch; }
        public String getStatus() { return status; }
        public int getEmployeeCount() { return employeeCount; }
        public Map<String, Integer> getRolesRequired() { return rolesRequired; }
        public Map<String, Integer> getRoleAssignments() { return roleAssignments; }
        public Map<String, Set<Long>> getAssignedEmployeesMap() { return assignedEmployeesMap; }

        public void incrementEmployeeCount() { this.employeeCount++; }
        public void decrementEmployeeCount() { this.employeeCount = Math.max(0, this.employeeCount - 1); }

        public void addRoleRequired(String role, int count) {
            rolesRequired.put(role, count);
            if (!roleAssignments.containsKey(role)) {
                roleAssignments.put(role, 0);
            }
        }

        public void incrementRoleAssignment(String role) {
            roleAssignments.put(role, roleAssignments.getOrDefault(role, 0) + 1);
        }

        public void decrementRoleAssignment(String role) {
            roleAssignments.put(role, Math.max(0, roleAssignments.getOrDefault(role, 0) - 1));
        }

        public void addEmployeeToRole(String role, long employeeId) {
            if (!assignedEmployeesMap.containsKey(role)) {
                assignedEmployeesMap.put(role, new HashSet<>());
            }
            assignedEmployeesMap.get(role).add(employeeId);
            incrementRoleAssignment(role);
        }

        public void removeEmployeeFromRole(String role, long employeeId) {
            if (assignedEmployeesMap.containsKey(role)) {
                assignedEmployeesMap.get(role).remove(employeeId);
                decrementRoleAssignment(role);
            }
        }
    }

    /**
     * Inner class to represent an employee assigned to a shift.
     */
    public static class EmployeeShiftAssignment {
        private final long employeeId;
        private final String employeeName;
        private final String role;

        public EmployeeShiftAssignment(long employeeId, String employeeName, String role) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.role = role;
        }

        public long getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getRole() { return role; }
    }

    /**
     * Inner class to represent a role requirement.
     */
    public static class RoleRequirement {
        private final String roleName;
        private final int required;
        private final int assigned;

        public RoleRequirement(String roleName, int required, int assigned) {
            this.roleName = roleName;
            this.required = required;
            this.assigned = assigned;
        }

        public String getRoleName() { return roleName; }
        public int getRequired() { return required; }
        public int getAssigned() { return assigned; }
    }

    /**
     * Inner class to represent an employee in a list.
     */
    public static class EmployeeListItem {
        private final long id;
        private final String name;
        private boolean assigned;

        public EmployeeListItem(long id, String name, boolean assigned) {
            this.id = id;
            this.name = name;
            this.assigned = assigned;
        }

        public long getId() { return id; }
        public String getName() { return name; }
        public boolean isAssigned() { return assigned; }
        public void setAssigned(boolean assigned) { this.assigned = assigned; }
    }
}
