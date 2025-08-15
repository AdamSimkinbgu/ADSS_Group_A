package DomainLayer.EmployeeSubModule;

import DTOs.EmployeeDTO;
import DTOs.ShiftDTO;
import DomainLayer.EmployeeSubModule.Repository.interfaces.ShiftReposetory;
import DomainLayer.exception.UnauthorizedPermissionException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class AssignmentController {
    private final EmployeeController employeeController;
    private final ShiftController shiftController;
    private final ShiftReposetory shiftReposetory;

    public AssignmentController(EmployeeController employeeController, ShiftController shiftController, ShiftReposetory shiftReposetory) {
        this.employeeController = employeeController;
        this.shiftController = shiftController;
        this.shiftReposetory = shiftReposetory;
    }
    /**
     * Assigns an employee to a specified role within a designated shift.
     * Validates permissions for the action, checks if the role is available and not already full,
     * and updates the shift's assigned employees and roles accordingly.
     *
     * @param shiftId The ID of the shift where the employee is being assigned.
     * @param doneBy The ID of the user performing the assignment. This user must have the appropriate permissions.
     * @param role The role the employee should be assigned to within the shift.
     * @param employeeId The ID of the employee being assigned to the specified role.
     * @return true if the employee was successfully assigned to the role.
     * @throws UnauthorizedPermissionException if the user performing the action does not have permission to assign employees.
     * @throws IllegalArgumentException if the specified role does not exist in the shift or if the shift ID is invalid.
     * @throws IllegalStateException if the role is already full and cannot accommodate additional employees.
     */
    public boolean assignEmployeeToRole(long shiftId, long doneBy, String role, long employeeId) {
        String PERMISSION = "ASSIGN_EMPLOYEE";
        Shift shift = shiftController.getShiftByIdAsShift(doneBy, shiftId);
        if (!employeeController.isEmployeeAuthorised(doneBy, PERMISSION)) {
            throw new UnauthorizedPermissionException("User does not have permission to assign employees");
        }
        if (!shift.getRolesRequired().containsKey(role)){
            throw new IllegalArgumentException("Role does not exist in shift");
        }
        Map <String, Set<Long>> assignedEmployees = shift.getAssignedEmployees();
        if (!assignedEmployees.containsKey(role)) {
            assignedEmployees.put(role, new HashSet<>());
        }
        assignedEmployees.computeIfAbsent(role, k -> new HashSet<>());

        Set<Long> employeesInRole = assignedEmployees.get(role);
        int requiredCount = shift.getRolesRequired().get(role);

        if (employeesInRole.size() >= requiredCount) {
            throw new IllegalStateException("Role is already full");
        }
        if (Objects.equals(role, "SHIFT_MANAGER")) {
            shift.setAssignedShiftManager(true);
        }
        employeesInRole.add(employeeId);
        boolean assigned = assignedEmployees.put(role, employeesInRole) != null;
        shift.setAssignedEmployees(assignedEmployees);
        if (assigned) {
            return shiftReposetory.update(convertShiftToDTO(shift));
        }
        return false;
    }

    /**
     * Removes an employee from a specified role within a designated shift.
     * Validates permissions for the action and updates the shift's assigned employees accordingly.
     *
     * @param doneBy The ID of the user performing the action. This user must have the appropriate permissions.
     * @param shiftId The ID of the shift from which the employee is being removed.
     * @param role The role from which the employee should be removed within the shift.
     * @param employeeId The ID of the employee being removed from the specified role.
     * @return true if the employee was successfully removed from the role.
     * @throws UnauthorizedPermissionException if the user performing the action does not have the required permission.
     * @throws IllegalArgumentException if the specified shift ID or role is invalid.
     */
    public boolean removeAssignment(long doneBy, long shiftId, String role, long employeeId) {
        String PERMISSION = "ASSIGN_EMPLOYEE";
        if (!employeeController.isEmployeeAuthorised(doneBy, PERMISSION)) {
            throw new UnauthorizedPermissionException("User does not have permission to remove employees");
        }
        Shift shift = shiftController.getShiftByIdAsShift(doneBy, shiftId);

        // Special case for SHIFT_MANAGER role - if removing, set isAssignedShiftManager to false and show error if no other manager is assigned
        if (Objects.equals(role, "Shift Manager")) {
            if (shift.getAssignedEmployees().get(role).size() <= 1)
                throw new IllegalStateException("Cannot remove the only Shift Manager from the shift, at least one must be assigned");
        }
        Map <String, Set<Long>> assignedEmployees = shift.getAssignedEmployees();
        Set<Long> employeesInRole = assignedEmployees.get(role);
        employeesInRole.remove(employeeId);
        boolean removes = assignedEmployees.put(role, employeesInRole) != null;
        shift.setAssignedEmployees(assignedEmployees);
        if (removes) {
            return shiftReposetory.update(convertShiftToDTO(shift));
        }
        return false;
    }

    /**
     * Checks whether a specific employee is assigned to any role within a given shift.
     * Verifies that the user performing the check has the required permission before retrieving and analyzing the shift data.
     *
     * @param doneBy The ID of the user initiating the check. This user must have the appropriate permissions.
     * @param shiftId The ID of the shift being checked for the employee's assignment.
     * @param employeeId The ID of the employee whose assignment status is being verified.
     * @return true if the employee is assigned to any role in the specified shift; false otherwise.
     * @throws UnauthorizedPermissionException if the user performing the check does not have permission to verify assignments.
     * @throws IllegalArgumentException if the specified shift ID is invalid or the shift cannot be retrieved.
     */
    public boolean isAssigned(long doneBy, long shiftId, long employeeId) {
        String PERMISSION = "ASSIGN_EMPLOYEE";
        if (!employeeController.isEmployeeAuthorised(doneBy, PERMISSION)) {
            throw new UnauthorizedPermissionException("User does not have permission to assign employees");
        }
        Shift shift = shiftController.getShiftByIdAsShift(doneBy, shiftId);
        return shift.getAssignedEmployees().values().stream()
                .anyMatch(set -> set.contains(employeeId));
    }

    /**
     * Checks whether a Shift Manager is assigned to the specified shift.
     * Ensures the user performing the check is authorized to verify shift assignments.
     * Throws an exception if no Shift Manager is assigned to the provided shift.
     *
     * @param doneBy The ID of the user performing the check. This user must have the appropriate permissions.
     * @param shiftId The ID of the shift being checked for a Shift Manager assignment.
     * @return true if a Shift Manager is assigned to the specified shift.
     * @throws UnauthorizedPermissionException if the user performing the check does not have permission to verify shift assignments.
     * @throws RuntimeException if no Shift Manager is assigned to the specified shift.
     */
    public boolean isAssignedManager(long doneBy, long shiftId) {
        String PERMISSION = "ASSIGN_EMPLOYEE";
        if (!employeeController.isEmployeeAuthorised(doneBy, PERMISSION)) {
            throw new UnauthorizedPermissionException("User does not have permission to assign employees");
        }
        Shift shift = shiftController.getShiftByIdAsShift(doneBy, shiftId);
        if (!shift.isAssignedShiftManager()){
            throw new RuntimeException("Shift Manager is not assigned to this shift");
        }
        return shift.isAssignedShiftManager();
    }

    public List<Set<Long>> getUnassignedEmployees(long doneBy, long shiftId){
        String PERMISSION = "ASSIGN_EMPLOYEE";
        if(!employeeController.isEmployeeAuthorised(doneBy,PERMISSION)){
            throw new UnauthorizedPermissionException("User does not have permission to assign employees");
        }
        Shift shift = shiftController.getShiftByIdAsShift(doneBy, shiftId);
        List<Set<Long>> unAssignedEmployees = new ArrayList<>();
        Set<Long> employees = employeeController.getAllEmployees().keySet();
        employees.removeIf(employee -> isAssigned(doneBy, shiftId, employee)); // All the employees that are not assigned to the shi
        Set<Long> availableEmployees = shift.getAvailableEmployees();
        Set<Long> availableEmployeesAndUnassigned = new HashSet<>(availableEmployees);
        availableEmployeesAndUnassigned.retainAll(employees);
        employees.removeAll(availableEmployees);


        unAssignedEmployees.add(employees);
        unAssignedEmployees.add(availableEmployeesAndUnassigned);

        return unAssignedEmployees;
    }

    /**
     * Gets all unassigned employees for a shift, filtered by branch
     * 
     * @param doneBy The ID of the user performing the action
     * @param shiftId The ID of the shift
     * @param branchId The ID of the branch to filter by
     * @return A list containing two sets: 
     *         1. Employees not assigned to the shift and not available (filtered by branch)
     *         2. Employees not assigned to the shift and available (filtered by branch)
     */
    public List<Set<Long>> getUnassignedEmployeesByBranch(long doneBy, long shiftId, long branchId){
        String PERMISSION = "ASSIGN_EMPLOYEE";
        if(!employeeController.isEmployeeAuthorised(doneBy,PERMISSION)){
            throw new UnauthorizedPermissionException("User does not have permission to assign employees");
        }

        Shift shift = shiftController.getShiftByIdAsShift(doneBy, shiftId);
        List<Set<Long>> unAssignedEmployees = new ArrayList<>();

        // Get employees for the specified branch
        List<EmployeeDTO> branchEmployees = employeeController.getEmployeesByBranch(branchId);
        Set<Long> employees = new HashSet<>();
        for (EmployeeDTO employee : branchEmployees) {
            employees.add(employee.getIsraeliId());
        }

        // Remove employees already assigned to the shift
        employees.removeIf(employee -> isAssigned(doneBy, shiftId, employee));

        // Get available employees for the shift
        Set<Long> availableEmployees = shift.getAvailableEmployees();

        // Filter available employees by branch
        Set<Long> availableEmployeesAndUnassigned = new HashSet<>(availableEmployees);
        availableEmployeesAndUnassigned.retainAll(employees);

        // Remove available employees from the unavailable set
        employees.removeAll(availableEmployees);

        unAssignedEmployees.add(employees);
        unAssignedEmployees.add(availableEmployeesAndUnassigned);

        return unAssignedEmployees;
    }

    /**
     * Checks if a specific employee is assigned to a shift at a given date, time, and branch location.
     * Verifies that the user performing the check has the required permission
     * before retrieving and analyzing the shift data.
     *
     * @param doneBy The ID of the user performing the check. This user must have the appropriate permissions.
     * @param date The date of the shift being checked.
     * @param hour The start time of the shift being checked.
     * @param employeeId The ID of the employee whose assignment status is being verified.
     * @param branchId The branch location where the shift is scheduled.
     * @return true if the specified employee is assigned to any role in the given shift; false otherwise.
     */
    public boolean isAssignedEmployeeByDateTimeBranch(long doneBy, LocalDate date, LocalTime hour, long employeeId, long branchId) {
        String PERMISSION = "GET_ASSIGN";
        if (!employeeController.isEmployeeAuthorised(doneBy, PERMISSION)) {
            throw new UnauthorizedPermissionException("User does not have permission to assign employees");
        }
        Shift shift = shiftController.getShiftbyDateTimeAndBranch(date, hour, branchId);
        return shift.getAssignedEmployees().values().stream()
                .anyMatch(set -> set.contains(employeeId));
    }


    /**
     * Checks if a specific role is assigned within a shift scheduled at a given date, time,
     * and branch determined by its address and area code.
     *
     * @param date The date of the shift to be checked.
     * @param time The time of the shift to be checked.
     * @param role The role to verify within the shift.
     * @param address The address of the branch where the shift is scheduled.
     * @param areaCode The area code of the branch where the shift is scheduled.
     * @return true if the specified role is assigned in the given shift; false otherwise.
     */
    public boolean isAssignedRoleByDateTimeBranch( LocalDate date, LocalTime time, String role, String address, int areaCode) {
        long branchId = shiftController.getBranchIdByAddress(address, areaCode);
        Shift shift = shiftController.getShiftbyDateTimeAndBranch(date, time, branchId);

        if (shift == null) {  return false;  }   //   null if no such shift exists, so false.

        return shift.getAssignedEmployees().entrySet().stream()
                .anyMatch(entry -> entry.getKey().contains(role));
    }


    private ShiftDTO convertShiftToDTO(Shift shift) {
        return new ShiftDTO(
                shift.getId(),
                shift.getShiftType(),
                shift.getShiftDate(),
                shift.getRolesRequired(),
                shift.getAssignedEmployees(),
                shift.getAvailableEmployees(),
                shift.isAssignedShiftManager(),
                shift.isOpen(),
                shift.getStartHour(),
                shift.getEndHour(),
                shift.getCreateDate(),
                shift.getUpdateDate(),
                shift.getBranchId()
        );
    }


    public boolean isAssignedDriverByDateTimeAddress(long driverId, LocalDate date, LocalTime time, int areaCode, String address) {
        long branchId = shiftController.getBranchIdByAddress(address, areaCode);
        Shift shift = shiftController.getShiftbyDateTimeAndBranch(date, time, branchId);

        if (shift == null) {  return false;  }   //   null if no such shift exists, so false.

        return shift.getAssignedEmployees().entrySet().stream()
                .anyMatch(entry -> entry.getKey().startsWith("Driver")
                        && entry.getValue().contains(driverId));
        }

}
