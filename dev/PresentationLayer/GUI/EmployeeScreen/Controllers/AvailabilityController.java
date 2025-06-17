package PresentationLayer.GUI.EmployeeScreen.Controllers;

import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Availability view.
 * Allows employees to mark their availability for shifts.
 */
public class AvailabilityController {

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Label currentWeekLabel;

    @FXML
    private DatePicker calendarDatePicker;

    @FXML
    private ComboBox<String> branchFilterComboBox;

    @FXML
    private Label selectedShiftLabel;

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
    private ToggleButton availabilityToggle;

    @FXML
    private TableView<RoleRequirement> rolesRequiredTable;

    @FXML
    private TableColumn<RoleRequirement, String> roleNameColumn;

    @FXML
    private TableColumn<RoleRequirement, Integer> roleCountColumn;

    @FXML
    private TableColumn<RoleRequirement, Integer> roleAssignedColumn;

    @FXML
    private Label totalAvailableShiftsLabel;

    @FXML
    private Label assignedShiftsLabel;

    private ShiftService shiftService;
    private EmployeeService employeeService;
    private MainViewController mainViewController;

    private LocalDate currentWeekStart;
    private ShiftData selectedShift;
    private List<ShiftData> allShifts = new ArrayList<>();
    private List<ShiftData> filteredShifts = new ArrayList<>();
    private String selectedBranch = "All Branches";
    private long currentEmployeeId = 123456789; // Default employee ID, should be updated with actual logged-in user

    // Map to track pending availability changes: shift ID -> new availability state
    private Map<String, Boolean> pendingAvailabilityChanges = new HashMap<>();

    /**
     * Sets the ShiftService for this controller.
     * 
     * @param shiftService The ShiftService to use
     */
    public void setShiftService(ShiftService shiftService) {
        this.shiftService = shiftService;
        if (calendarGrid != null) {
            refreshCalendar();
        }
    }

    /**
     * Sets the EmployeeService for this controller.
     * 
     * @param employeeService The EmployeeService to use
     */
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
        if (calendarGrid != null) {
            loadBranches();
        }
    }

    /**
     * Sets the MainViewController for this controller.
     * 
     * @param controller The MainViewController to use
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
        // Initialize the calendar to the current week
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        updateWeekLabel();

        // Set up the calendar grid
        createCalendarGrid();

        // Set up the roles required table
        roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        roleCountColumn.setCellValueFactory(new PropertyValueFactory<>("required"));
        roleAssignedColumn.setCellValueFactory(new PropertyValueFactory<>("assigned"));

        // Set up the date picker
        calendarDatePicker.setValue(LocalDate.now());
        calendarDatePicker.setOnAction(e -> jumpToDate());

        // Set up the branch filter
        branchFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedBranch = newVal;
                filterShiftsByBranch(selectedBranch);
                updateCalendarGrid();
            }
        });

        // Set up the availability toggle
        availabilityToggle.setOnAction(e -> toggleAvailability());

        // Clear shift details initially
        clearShiftDetails();
    }

    /**
     * Loads the branches from the service.
     */
    private void loadBranches() {
        try {
            // Add "All Branches" option
            ObservableList<String> branches = FXCollections.observableArrayList("All Branches");

            // Add branches from service (this is a placeholder - actual implementation
            // would get branches from service)
            branches.addAll("Branch 1", "Branch 2", "Branch 3");

            branchFilterComboBox.setItems(branches);
            branchFilterComboBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load branches: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Creates the calendar grid with days of the week as columns and shift types as
     * rows.
     */
    private void createCalendarGrid() {
        calendarGrid.getChildren().clear();
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        // Add column headers (days of week)
        for (int col = 0; col <= 7; col++) {
            if (col == 0) {
                // Empty cell in top-left corner
                calendarGrid.add(createEmptyCell(), 0, 0);
            } else {
                // Day of week header
                LocalDate date = currentWeekStart.plusDays(col - 1);
                String dayOfWeek = date.getDayOfWeek().toString().substring(0, 3);
                String dayOfMonth = String.valueOf(date.getDayOfMonth());

                Text text = new Text(dayOfWeek + "\n" + dayOfMonth);
                text.setTextAlignment(TextAlignment.CENTER);

                StackPane cell = new StackPane(text);
                cell.setPadding(new Insets(5));
                cell.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc;");

                calendarGrid.add(cell, col, 0);
            }
        }

        // Add row headers (shift types) and empty cells - removed Night option
        String[] shiftTypes = { "Morning", "Evening" };
        for (int row = 1; row <= shiftTypes.length; row++) {
            // Shift type header
            Text text = new Text(shiftTypes[row - 1]);
            StackPane cell = new StackPane(text);
            cell.setPadding(new Insets(5));
            cell.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc;");

            calendarGrid.add(cell, 0, row);

            // Empty cells for each day
            for (int col = 1; col <= 7; col++) {
                calendarGrid.add(createEmptyCell(), col, row);
            }
        }
    }

    /**
     * Creates an empty cell for the calendar grid.
     * 
     * @return An empty StackPane
     */
    private StackPane createEmptyCell() {
        Rectangle rect = new Rectangle(100, 60);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.LIGHTGRAY);

        StackPane cell = new StackPane(rect);
        cell.setPadding(new Insets(2));

        return cell;
    }

    /**
     * Updates the week label with the current week's date range.
     */
    private void updateWeekLabel() {
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        currentWeekLabel.setText("Week of " + currentWeekStart.format(formatter) + " to " + weekEnd.format(formatter));
    }

    /**
     * Refreshes the calendar with data from the service.
     */
    private void refreshCalendar() {
        try {
            if (shiftService == null) {
                System.out.println("ShiftService not set in AvailabilityController");
                return;
            }

            // Clear existing shifts
            allShifts.clear();
            filteredShifts.clear();

            // Get shifts for the current week
            LocalDate weekEnd = currentWeekStart.plusDays(6);

            // This is a placeholder - actual implementation would get shifts from service
            // For example: Set<ShiftDTO> shifts =
            // shiftService.getShiftsByWeek(currentEmployeeId, new Week(currentWeekStart));

            // Create sample shifts for demonstration
            createSampleShifts();

            // Filter shifts by branch
            filterShiftsByBranch(selectedBranch);

            // Update the calendar grid
            updateCalendarGrid();

            // Update availability summary
            updateAvailabilitySummary();
        } catch (Exception e) {
            e.printStackTrace();
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to refresh calendar: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Creates sample shifts for demonstration purposes.
     */
    private void createSampleShifts() {
        // Sample shifts for the current week
        for (int day = 0; day < 7; day++) {
            LocalDate date = currentWeekStart.plusDays(day);

            // Morning shift
            Map<String, Integer> morningRoles = new HashMap<>();
            morningRoles.put("Cashier", 2);
            morningRoles.put("Security", 1);

            Map<String, Integer> morningAssignments = new HashMap<>();
            morningAssignments.put("Cashier", 1);
            morningAssignments.put("Security", 0);

            Map<String, Set<Long>> morningEmployees = new HashMap<>();
            morningEmployees.put("Cashier", new HashSet<>(Arrays.asList(123456789L)));
            morningEmployees.put("Security", new HashSet<>());

            ShiftData morningShift = new ShiftData(
                    "1" + day,
                    date,
                    "Morning",
                    "08:00 - 16:00",
                    "Branch 1",
                    "Open",
                    1,
                    morningRoles,
                    morningAssignments,
                    morningEmployees);

            // Evening shift
            Map<String, Integer> eveningRoles = new HashMap<>();
            eveningRoles.put("Cashier", 2);
            eveningRoles.put("Security", 1);

            Map<String, Integer> eveningAssignments = new HashMap<>();
            eveningAssignments.put("Cashier", 0);
            eveningAssignments.put("Security", 1);

            Map<String, Set<Long>> eveningEmployees = new HashMap<>();
            eveningEmployees.put("Cashier", new HashSet<>());
            eveningEmployees.put("Security", new HashSet<>(Arrays.asList(987654321L)));

            ShiftData eveningShift = new ShiftData(
                    "2" + day,
                    date,
                    "Evening",
                    "16:00 - 00:00",
                    "Branch 2",
                    "Open",
                    1,
                    eveningRoles,
                    eveningAssignments,
                    eveningEmployees);

            allShifts.add(morningShift);
            allShifts.add(eveningShift);
        }
    }

    /**
     * Filters shifts by branch.
     * 
     * @param branch The branch to filter by, or "All Branches" for no filtering
     */
    private void filterShiftsByBranch(String branch) {
        if (branch == null || branch.equals("All Branches")) {
            filteredShifts = new ArrayList<>(allShifts);
        } else {
            filteredShifts = allShifts.stream()
                    .filter(shift -> shift.getBranch().equals(branch))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Updates the calendar grid with the filtered shifts.
     */
    private void updateCalendarGrid() {
        // Clear existing shift cells - only 2 rows now (Morning and Evening)
        for (int row = 1; row <= 2; row++) {
            for (int col = 1; col <= 7; col++) {
                final int finalRow = row;
                final int finalCol = col;
                calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) == finalRow &&
                        GridPane.getColumnIndex(node) == finalCol &&
                        node instanceof StackPane);

                calendarGrid.add(createEmptyCell(), col, row);
            }
        }

        // Add shift cells
        for (ShiftData shift : filteredShifts) {
            int dayOfWeek = shift.getDate().getDayOfWeek().getValue();
            int col = dayOfWeek == 7 ? 1 : dayOfWeek + 1; // Sunday is 7 in DayOfWeek but 1 in our grid

            int row;
            switch (shift.getType()) {
                case "Morning":
                    row = 1;
                    break;
                case "Evening":
                    row = 2;
                    break;
                default:
                    continue; // Skip unknown shift types (including Night)
            }

            // Remove the empty cell
            final int finalRow = row;
            final int finalCol = col;
            calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) == finalRow &&
                    GridPane.getColumnIndex(node) == finalCol &&
                    node instanceof StackPane);

            // Add the shift cell
            calendarGrid.add(createShiftCell(shift), col, row);
        }
    }

    /**
     * Creates a cell for a shift in the calendar grid.
     * 
     * @param shift The shift data
     * @return A StackPane representing the shift
     */
    private StackPane createShiftCell(ShiftData shift) {
        // Create the background rectangle
        Rectangle rect = new Rectangle(100, 60);

        // Set the color based on availability
        boolean isAvailable = isEmployeeAvailable(shift);
        boolean isAssigned = isEmployeeAssigned(shift);

        if (isAssigned) {
            rect.setFill(Color.LIGHTGREEN);
        } else if (isAvailable) {
            rect.setFill(Color.LIGHTYELLOW);
        } else {
            rect.setFill(Color.WHITE);
        }

        rect.setStroke(Color.LIGHTGRAY);

        // Create the text
        Text text = new Text(shift.getTimeRange());
        text.setTextAlignment(TextAlignment.CENTER);

        // Create the cell
        StackPane cell = new StackPane(rect, text);
        cell.setPadding(new Insets(2));

        // Store the current availability state in the cell's user data
        cell.setUserData(isAvailable);

        // Add left-click handler - toggle availability on click
        cell.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                // Left click - toggle availability (once for available, twice for not
                // available)
                boolean currentAvailability = (boolean) cell.getUserData();
                boolean newAvailability = !currentAvailability;

                // Update the cell's appearance
                if (isAssigned) {
                    rect.setFill(Color.LIGHTGREEN); // Assigned cells stay green
                } else if (newAvailability) {
                    rect.setFill(Color.LIGHTYELLOW); // Available
                } else {
                    rect.setFill(Color.WHITE); // Not available
                }

                // Store the new availability state
                cell.setUserData(newAvailability);

                // Update the shift's availability in our local data
                updateShiftAvailability(shift, newAvailability);
            }
        });

        // Add right-click context menu for marking availability
        ContextMenu contextMenu = new ContextMenu();
        MenuItem markAvailable = new MenuItem("Mark as Available");
        MenuItem markUnavailable = new MenuItem("Mark as Unavailable");

        markAvailable.setOnAction(e -> {
            if (!isAssigned) {
                rect.setFill(Color.LIGHTYELLOW);
                cell.setUserData(true);
                updateShiftAvailability(shift, true);
            }
        });

        markUnavailable.setOnAction(e -> {
            if (!isAssigned) {
                rect.setFill(Color.WHITE);
                cell.setUserData(false);
                updateShiftAvailability(shift, false);
            }
        });

        contextMenu.getItems().addAll(markAvailable, markUnavailable);

        cell.setOnContextMenuRequested(e -> contextMenu.show(cell, e.getScreenX(), e.getScreenY()));

        return cell;
    }

    /**
     * Updates a shift's availability in our local data.
     * This doesn't save to the backend until the save button is clicked.
     * 
     * @param shift       The shift to update
     * @param isAvailable Whether the employee is available for the shift
     */
    private void updateShiftAvailability(ShiftData shift, boolean isAvailable) {
        // Store the pending change in our map
        pendingAvailabilityChanges.put(shift.getId(), isAvailable);
    }

    /**
     * Saves all pending availability changes to the backend.
     * This is called when the save button is clicked.
     */
    @FXML
    public void saveAvailability() {
        if (shiftService == null) {
            showError("Error", "ShiftService not available");
            return;
        }

        if (pendingAvailabilityChanges.isEmpty()) {
            showInfo("No Changes", "No availability changes to save");
            return;
        }

        int successCount = 0;
        int failCount = 0;

        try {
            // Process each pending change
            for (Map.Entry<String, Boolean> entry : pendingAvailabilityChanges.entrySet()) {
                String shiftId = entry.getKey();
                boolean isAvailable = entry.getValue();

                try {
                    // Call the appropriate ShiftService method
                    if (isAvailable) {
                        shiftService.markEmployeeAvailable(currentEmployeeId, Long.parseLong(shiftId));
                    } else {
                        shiftService.removeEmployeeAvailability(currentEmployeeId, Long.parseLong(shiftId));
                    }
                    successCount++;
                } catch (Exception e) {
                    System.err.println("Error updating availability for shift " + shiftId + ": " + e.getMessage());
                    failCount++;
                }
            }

            // Clear the pending changes
            pendingAvailabilityChanges.clear();

            // Show a success message
            if (failCount == 0) {
                showInfo("Success", "Successfully saved availability for " + successCount + " shifts");
            } else {
                showWarning("Partial Success",
                        "Saved availability for " + successCount + " shifts, but failed for " + failCount + " shifts");
            }

            // Refresh the calendar to reflect the saved changes
            refreshCalendar();

        } catch (Exception e) {
            showError("Error", "Failed to save availability changes: " + e.getMessage());
        }
    }

    /**
     * Shows an information alert.
     * 
     * @param title   The alert title
     * @param message The alert message
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a warning alert.
     * 
     * @param title   The alert title
     * @param message The alert message
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error alert.
     * 
     * @param title   The alert title
     * @param message The alert message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Checks if the current employee is available for a shift.
     * 
     * @param shift The shift to check
     * @return true if the employee is available, false otherwise
     */
    private boolean isEmployeeAvailable(ShiftData shift) {
        // This is a placeholder - actual implementation would check if the employee is
        // available
        // For example: return shiftService.isEmployeeAvailable(currentEmployeeId,
        // Long.parseLong(shift.getId()), currentEmployeeId);

        // For demonstration, we'll say the employee is available for some shifts
        return shift.getId().endsWith("0") || shift.getId().endsWith("2") || shift.getId().endsWith("4");
    }

    /**
     * Checks if the current employee is assigned to a shift.
     * 
     * @param shift The shift to check
     * @return true if the employee is assigned, false otherwise
     */
    private boolean isEmployeeAssigned(ShiftData shift) {
        // Check if the employee is assigned to any role in the shift
        for (Set<Long> employees : shift.getAssignedEmployeesMap().values()) {
            if (employees.contains(currentEmployeeId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Selects a shift and displays its details.
     * 
     * @param shift The shift to select
     */
    private void selectShift(ShiftData shift) {
        selectedShift = shift;

        // Update shift details
        selectedShiftLabel.setText(shift.getType() + " Shift on "
                + shift.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        shiftDateLabel.setText(shift.getDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        shiftTypeLabel.setText(shift.getType());
        shiftTimeLabel.setText(shift.getTimeRange());
        shiftBranchLabel.setText(shift.getBranch());
        shiftStatusLabel.setText(shift.getStatus());

        // Update availability toggle
        boolean isAvailable = isEmployeeAvailable(shift);
        availabilityToggle.setSelected(isAvailable);
        availabilityToggle.setText(isAvailable ? "Mark as Unavailable" : "Mark as Available");
        availabilityToggle.setDisable(false);

        // Load roles required
        loadRolesRequired();
    }

    /**
     * Loads the roles required for the selected shift.
     */
    private void loadRolesRequired() {
        if (selectedShift == null) {
            rolesRequiredTable.getItems().clear();
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
     * Clears the shift details.
     */
    private void clearShiftDetails() {
        selectedShift = null;
        selectedShiftLabel.setText("No shift selected");
        shiftDateLabel.setText("-");
        shiftTypeLabel.setText("-");
        shiftTimeLabel.setText("-");
        shiftBranchLabel.setText("-");
        shiftStatusLabel.setText("-");

        availabilityToggle.setSelected(false);
        availabilityToggle.setText("Mark as Available");
        availabilityToggle.setDisable(true);

        rolesRequiredTable.getItems().clear();
    }

    /**
     * Updates the availability summary.
     */
    private void updateAvailabilitySummary() {
        int availableCount = 0;
        int assignedCount = 0;

        for (ShiftData shift : allShifts) {
            if (isEmployeeAvailable(shift)) {
                availableCount++;
            }

            if (isEmployeeAssigned(shift)) {
                assignedCount++;
            }
        }

        totalAvailableShiftsLabel.setText(String.valueOf(availableCount));
        assignedShiftsLabel.setText(String.valueOf(assignedCount));
    }

    /**
     * Shows the previous week.
     */
    @FXML
    public void showPreviousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateWeekLabel();
        refreshCalendar();
    }

    /**
     * Shows the next week.
     */
    @FXML
    public void showNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateWeekLabel();
        refreshCalendar();
    }

    /**
     * Shows the current week.
     */
    @FXML
    public void showCurrentWeek() {
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        updateWeekLabel();
        refreshCalendar();
    }

    /**
     * Jumps to the date selected in the date picker.
     */
    @FXML
    public void jumpToDate() {
        LocalDate selectedDate = calendarDatePicker.getValue();
        if (selectedDate != null) {
            currentWeekStart = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            updateWeekLabel();
            refreshCalendar();
        }
    }

    /**
     * Toggles the availability for the selected shift.
     */
    @FXML
    public void toggleAvailability() {
        if (selectedShift == null || shiftService == null) {
            return;
        }

        try {
            boolean isAvailable = availabilityToggle.isSelected();

            // Update availability in the service
            if (isAvailable) {
                // Mark as available
                shiftService.markEmployeeAvailable(currentEmployeeId, Long.parseLong(selectedShift.getId()));
            } else {
                // Remove availability
                shiftService.removeEmployeeAvailability(currentEmployeeId, Long.parseLong(selectedShift.getId()));
            }

            // Update UI
            availabilityToggle.setText(isAvailable ? "Mark as Unavailable" : "Mark as Available");

            // Refresh calendar to show updated availability
            refreshCalendar();
        } catch (Exception e) {
            e.printStackTrace();
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update availability: " + e.getMessage());
            alert.showAndWait();

            // Reset toggle button to previous state
            availabilityToggle.setSelected(!availabilityToggle.isSelected());
        }
    }

    /**
     * Class representing a shift in the calendar.
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
            this.rolesRequired = rolesRequired;
            this.roleAssignments = roleAssignments;
            this.assignedEmployeesMap = assignedEmployeesMap;
        }

        public String getId() {
            return id;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getType() {
            return type;
        }

        public String getTimeRange() {
            return timeRange;
        }

        public String getBranch() {
            return branch;
        }

        public String getStatus() {
            return status;
        }

        public int getEmployeeCount() {
            return employeeCount;
        }

        public Map<String, Integer> getRolesRequired() {
            return rolesRequired;
        }

        public Map<String, Integer> getRoleAssignments() {
            return roleAssignments;
        }

        public Map<String, Set<Long>> getAssignedEmployeesMap() {
            return assignedEmployeesMap;
        }
    }

    /**
     * Class representing a role requirement in the roles required table.
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

        public String getRoleName() {
            return roleName;
        }

        public int getRequired() {
            return required;
        }

        public int getAssigned() {
            return assigned;
        }
    }
}
