package DomainLayer.EmployeeSubModule;

import DomainLayer.enums.ShiftType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;


/**
 * The Shift class represents a work shift for a specified date, time, and type.
 * It maintains details about required roles, assigned employees, available employees,
 * shift manager assignment status, and the open/close status of the shift.
 */
public class Shift {
    private long id;
    private ShiftType shiftType;
    private LocalDate shiftDate;
    private Map<String,Integer> rolesRequired;   // e.g., {"Cashier": 2, "Security": 1}
    private Map<String, Set<Long>> assignedEmployees;  // e.g., {"Cashier": [123456789, 987654321], "Security": [112233445]}
    private Set<Long> AvailableEmployees;  // e.g., [123456789, 555444333]
    private boolean isAssignedShitManager;
    private boolean isOpen;
    private LocalTime startHour;
    private LocalTime endHour;
    private LocalDate createDate;
    private LocalDate updateDate;
    private long branchId; // Branch that this shift belongs to

    public Shift(long id,ShiftType shiftType, LocalDate shiftDate, Map<String, Integer> rolesRequired, Map<String, Set<Long>> assignedEmployees, Set<Long> AvailableEmployees, boolean isAssignedShitManager, boolean isOpen ,LocalTime startHour , LocalTime endHour, LocalDate updateDate, long branchId) {
        this.id = id;
        this.shiftType = shiftType;
        this.shiftDate = shiftDate;
        this.rolesRequired = rolesRequired;
        this.assignedEmployees = assignedEmployees;
        this.AvailableEmployees = AvailableEmployees;
        this.isAssignedShitManager = isAssignedShitManager;
        this.isOpen = isOpen;
        this.startHour = startHour;
        this.endHour = endHour;
        this.createDate = LocalDate.now();
        this.updateDate = updateDate;
        this.branchId = branchId;
    }

    public long getId() {
        return id;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public Map<String, Integer> getRolesRequired() {
        return rolesRequired;
    }

    public void setRolesRequired(Map<String, Integer> rolesRequired) {
        this.rolesRequired = rolesRequired;
    }

    public Map<String, Set<Long>> getAssignedEmployees() {
        return assignedEmployees;
    }

    public void setAssignedEmployees(Map<String, Set<Long>> assignedEmployees) {
        this.assignedEmployees = assignedEmployees;
    }

    public Set<Long> getAvailableEmployees() {
        return AvailableEmployees;
    }

    public void setAvailableEmployees(Set<Long> availableEmployees) {
        AvailableEmployees = availableEmployees;
    }

    public boolean isAssignedShiftManager() {
        return isAssignedShitManager;
    }

    public void setAssignedShiftManager(boolean assignedShitManager) {
        isAssignedShitManager = assignedShitManager;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {this.updateDate = updateDate;}

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {this.startHour = startHour;}

    public LocalTime getEndHour() {return endHour;}

    public void setEndHour(LocalTime endHour) {this.endHour = endHour;}

    public String getHours() {
        return startHour + " - " + endHour;
    }

    /**
     * Gets the branch that this shift belongs to.
     * 
     * @return The branch name
     */
    public long getBranchId() {
        return branchId;
    }

    /**
     * Sets the branch that this shift belongs to.
     * 
     * @param branchId The branch name
     */
    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }
}
