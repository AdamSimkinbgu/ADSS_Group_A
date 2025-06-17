package ServiceLayer.EmployeeSubModule;

import DTOs.ShiftDTO;
import DomainLayer.EmployeeSubModule.*;
import DomainLayer.enums.ShiftType;
import DomainLayer.exception.ShiftNotFoundException;
import ServiceLayer.exception.AssignmentServiceException;
import ServiceLayer.exception.AuthorizationException;
import ServiceLayer.exception.AvailabilityServiceException;
import ServiceLayer.exception.ShiftServiceException;
import ServiceLayer.exception.ValidationException;
import Util.Week;
import DomainLayer.exception.UnauthorizedPermissionException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ShiftService {
    private final ShiftController shiftController;
    private final AssignmentController assignmentController;
    private final AvailabilityController availabilityController;

    public ShiftService(ShiftController shiftController, AssignmentController assignmentController, AvailabilityController availabilityController) {
        this.shiftController = shiftController;
        this.assignmentController = assignmentController;
        this.availabilityController = availabilityController;
    }
    // ========================
    // DTO related methods
    // ========================
    private String serializeShiftDTO(ShiftDTO dto) {
        try {
            return dto.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Error serializing ShiftDTO: " + e.getMessage());
        }
    }
    private ShiftDTO deserializeShiftDTO(String json) {
        try {
            return ShiftDTO.deserialize(json);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing ShiftDTO: " + e.getMessage());
        }
    }
    public ShiftDTO getShiftDTO(long doneBy, long shiftId) {
        try {
            String str = shiftController.getShiftByID(doneBy, shiftId);
            ShiftDTO shift = deserializeShiftDTO(str);

            if (shift.getId() >= 0) {
                return shift;
            } else {
                throw new ShiftNotFoundException("Shift with ID " + shiftId + " not found");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    // ========================
    // Shift related methods
    // ========================

    /**
     * Creates a new shift with the specified parameters
     * 
     * @param doneBy the employee creating the shift
     * @param shiftType the type of shift (morning, evening, etc.)
     * @param date the date of the shift
     * @param rolesRequired the roles required for the shift
     * @param assignedEmployees the employees assigned to the shift
     * @param availableEmployees the employees available for the shift
     * @param isAssignedShiftManager whether a shift manager is assigned
     * @param isOpen whether the shift is open for assignments
     * @param startHour the start hour of the shift
     * @param endHour the end hour of the shift
     * @param updateDate the date the shift was last updated
     * @return a message indicating success
     * @throws ShiftServiceException if the shift creation fails
     * @throws ValidationException if the input parameters are invalid
     * @throws AuthorizationException if the employee doesn't have permission to create shifts
     */
    public String createShift(long doneBy, ShiftType shiftType, LocalDate date,
                              Map<String, Integer> rolesRequired,
                              Map<String, Set<Long>> assignedEmployees,
                              Set<Long> availableEmployees,
                              boolean isAssignedShiftManager,
                              boolean isOpen,LocalTime startHour , LocalTime endHour, LocalDate updateDate) {
        try {
            // Validate inputs
            if (date == null) {
                throw new ValidationException("date", "Date cannot be null");
            }
            if (shiftType == null) {
                throw new ValidationException("shiftType", "Shift type cannot be null");
            }
            if (rolesRequired == null) {
                throw new ValidationException("rolesRequired", "Roles required cannot be null");
            }
            if (assignedEmployees == null) {
                throw new ValidationException("assignedEmployees", "Assigned employees cannot be null");
            }
            if (availableEmployees == null) {
                throw new ValidationException("availableEmployees", "Available employees cannot be null");
            }
            if (startHour == null) {
                throw new ValidationException("startHour", "Start hour cannot be null");
            }
            if (endHour == null) {
                throw new ValidationException("endHour", "End hour cannot be null");
            }
            if (updateDate == null) {
                throw new ValidationException("updateDate", "Update date cannot be null");
            }

            boolean result = shiftController.createShift(doneBy, shiftType, date, rolesRequired, assignedEmployees,
                    availableEmployees, isAssignedShiftManager, isOpen, startHour, endHour, updateDate);

            if (!result) {
                throw ShiftServiceException.forShift(date, shiftType, "Failed to create shift");
            }

            return "Shift created successfully";
        } catch (DomainLayer.exception.UnauthorizedPermissionException e) {
            throw new AuthorizationException("User does not have permission to create shifts", e);
        } catch (ValidationException | ShiftServiceException e) {
            throw e; // Re-throw these exceptions as they are already properly typed
        } catch (RuntimeException e) {
            throw ShiftServiceException.forShift(date, shiftType, e.getMessage());
        }
    }

    /**
     * Creates shifts for an entire week starting from the specified date
     * 
     * @param doneBy the employee creating the shifts
     * @param date the start date for the week
     * @param rolesRequired the roles required for each shift
     * @return a message indicating success
     * @throws ShiftServiceException if the shift creation fails
     * @throws ValidationException if the input parameters are invalid
     * @throws AuthorizationException if the employee doesn't have permission to create shifts
     */
    public String createWeeklyShifts(long doneBy, LocalDate date,
                                      Map<String, Integer> rolesRequired) {
        try {
            // Validate inputs
            if (date == null) {
                throw new ValidationException("date", "Date cannot be null");
            }
            if (rolesRequired == null) {
                throw new ValidationException("rolesRequired", "Roles required cannot be null");
            }

            boolean result = shiftController.createWeeklyShifts(doneBy, date, rolesRequired);

            if (!result) {
                throw ShiftServiceException.forShift(date, "week", "Failed to create weekly shifts");
            }

            return "Weekly shifts created successfully";
        } catch (DomainLayer.exception.UnauthorizedPermissionException e) {
            throw new AuthorizationException("User does not have permission to create weekly shifts", e);
        } catch (ValidationException | ShiftServiceException e) {
            throw e; // Re-throw these exceptions as they are already properly typed
        } catch (RuntimeException e) {
            throw ShiftServiceException.forShift(date, "week", e.getMessage());
        }
    }

    /**
     * Removes a shift by its ID
     * 
     * @param doneBy the employee removing the shift
     * @param shiftId the ID of the shift to remove
     * @return a message indicating success
     * @throws ShiftServiceException if the shift deletion fails
     * @throws ValidationException if the input parameters are invalid
     * @throws AuthorizationException if the employee doesn't have permission to remove shifts
     */
    public String removeShiftByID(long doneBy, long shiftId) {
        try {
            if (shiftId <= 0) {
                throw new ValidationException("shiftId", "Shift ID must be a positive number");
            }

            boolean result = shiftController.removeShiftByID(doneBy, shiftId);

            if (!result) {
                throw ShiftServiceException.forShift(shiftId, "Failed to delete shift");
            }

            return "Shift deleted successfully";
        } catch (DomainLayer.exception.UnauthorizedPermissionException e) {
            throw new AuthorizationException("User does not have permission to delete shifts", e);
        } catch (ValidationException | ShiftServiceException e) {
            throw e; // Re-throw these exceptions as they are already properly typed
        } catch (RuntimeException e) {
            throw ShiftServiceException.forShift(shiftId, e.getMessage());
        }
    }

    /**
     * Removes a shift by its date and type
     * 
     * @param doneBy the employee removing the shift
     * @param date the date of the shift to remove
     * @param shiftType the type of the shift to remove
     * @return a message indicating success
     * @throws ShiftServiceException if the shift deletion fails
     * @throws ValidationException if the input parameters are invalid
     * @throws AuthorizationException if the employee doesn't have permission to remove shifts
     */
    public String removeShift(long doneBy, LocalDate date, ShiftType shiftType) {
        try {
            // Validate inputs
            if (date == null) {
                throw new ValidationException("date", "Date cannot be null");
            }
            if (shiftType == null) {
                throw new ValidationException("shiftType", "Shift type cannot be null");
            }

            boolean result = shiftController.removeShift(doneBy, date, shiftType);

            if (!result) {
                throw ShiftServiceException.forShift(date, shiftType, "Failed to delete shift");
            }

            return "Shift deleted successfully";
        } catch (DomainLayer.exception.UnauthorizedPermissionException e) {
            throw new AuthorizationException("User does not have permission to delete shifts", e);
        } catch (ValidationException | ShiftServiceException e) {
            throw e; // Re-throw these exceptions as they are already properly typed
        } catch (RuntimeException e) {
            throw ShiftServiceException.forShift(date, shiftType, e.getMessage());
        }
    }

    /**
     * Updates a shift with the specified parameters
     * 
     * @param doneBy the employee updating the shift
     * @param shiftId the ID of the shift to update
     * @param shiftType the new type of the shift
     * @param date the new date of the shift
     * @param isAssignedShiftManager whether a shift manager is assigned
     * @param isOpen whether the shift is open for assignments
     * @param startHour the new start hour of the shift
     * @param endHour the new end hour of the shift
     * @param updateDate the date the shift was last updated
     * @return a message indicating success
     * @throws ShiftServiceException if the shift update fails
     * @throws ValidationException if the input parameters are invalid
     * @throws AuthorizationException if the employee doesn't have permission to update shifts
     */
    public String updateShift(long doneBy, long shiftId, ShiftType shiftType, LocalDate date,
                              boolean isAssignedShiftManager, boolean isOpen, LocalTime startHour, LocalTime endHour, LocalDate updateDate) {
        try {
            // Validate inputs
            if (shiftId <= 0) {
                throw new ValidationException("shiftId", "Shift ID must be a positive number");
            }
            if (date == null) {
                throw new ValidationException("date", "Date cannot be null");
            }
            if (shiftType == null) {
                throw new ValidationException("shiftType", "Shift type cannot be null");
            }
            if (startHour == null) {
                throw new ValidationException("startHour", "Start hour cannot be null");
            }
            if (endHour == null) {
                throw new ValidationException("endHour", "End hour cannot be null");
            }
            if (updateDate == null) {
                throw new ValidationException("updateDate", "Update date cannot be null");
            }

            boolean result = shiftController.updateShift(doneBy, shiftId, shiftType, date, isAssignedShiftManager, isOpen, startHour, endHour, updateDate);

            if (!result) {
                throw ShiftServiceException.forShift(shiftId, "Failed to update shift");
            }

            return "Shift updated successfully";
        } catch (DomainLayer.exception.UnauthorizedPermissionException e) {
            throw new AuthorizationException("User does not have permission to update shifts", e);
        } catch (ValidationException | ShiftServiceException e) {
            throw e; // Re-throw these exceptions as they are already properly typed
        } catch (RuntimeException e) {
            throw ShiftServiceException.forShift(shiftId, e.getMessage());
        }
    }

    /**
     * Gets a shift by its ID
     * 
     * @param doneBy the employee retrieving the shift
     * @param shiftId the ID of the shift to retrieve
     * @return a serialized ShiftDTO representing the shift
     * @throws ShiftServiceException if the shift retrieval fails
     * @throws ValidationException if the input parameters are invalid
     * @throws AuthorizationException if the employee doesn't have permission to view shifts
     */
    public String getShiftById(long doneBy, long shiftId) {
        try {
            // Validate inputs
            if (shiftId <= 0) {
                throw new ValidationException("shiftId", "Shift ID must be a positive number");
            }

            String shift = shiftController.getShiftByID(doneBy, shiftId);

            if (shift == null || shift.isEmpty()) {
                throw ShiftServiceException.forShift(shiftId, "Shift not found");
            }

            return shift;
        } catch (DomainLayer.exception.UnauthorizedPermissionException e) {
            throw new AuthorizationException("User does not have permission to view shifts", e);
        } catch (ShiftNotFoundException e) {
            throw ShiftServiceException.forShift(shiftId, "Shift not found: " + e.getMessage());
        } catch (ValidationException | ShiftServiceException e) {
            throw e; // Re-throw these exceptions as they are already properly typed
        } catch (RuntimeException e) {
            throw ShiftServiceException.forShift(shiftId, e.getMessage());
        }
    }

    /**
     * Converts a Shift domain object to a ShiftDTO
     * @param shift the Shift domain object
     * @return a ShiftDTO representing the Shift
     */
    private ShiftDTO convertToDTO(Shift shift) {
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

    /**
     * Retrieves all shifts associated with the given user identifier.
     *
     * @param doneBy the ID representing the user requesting the shifts
     * @return a string containing the details of the retrieved shifts
     * @throws RuntimeException if an error occurs during the retrieval process
     */
    public String getAllShifts(long doneBy) {
        try {
            String domainShifts = shiftController.getAllShifts(doneBy);
            return domainShifts;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAllShiftsByDateBranch(long doneBy, LocalDate date, long branch) {
        try {
            String shifts = shiftController.getAllShiftsByDateAndBranch(doneBy, date, branch);
            return shifts;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all shifts for a specific branch
     * @param doneBy employee who is requesting the shifts
     * @param branchId branch of the shifts
     * @return all shifts for the branch
     */
    public String getAllShiftsByBranch(long doneBy, long branchId) {
        try {
            String shifts = shiftController.getAllShiftsByBranch(doneBy, branchId);
            return shifts;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getShiftsByEmployee(long doneBy, long employeeID) {
        try {
            return shiftController.getShiftsByEmployee(doneBy, employeeID);
        } catch (ShiftNotFoundException e) {
            throw new ShiftNotFoundException(e.getMessage());
        }
    }

    public String getShift(long doneBy, LocalDate date, ShiftType shiftType) {
        try {
            String shift = shiftController.getshift(doneBy, date, shiftType);
            return shift;
        } catch (ShiftNotFoundException e) {
            throw new ShiftNotFoundException(e.getMessage());
        }
    }

    /**
     * Gets a shift by date, type, and branch
     * 
     * @param doneBy the employee making the query
     * @param date the date of the shift
     * @param shiftType the type of the shift
     * @param branchId the ID of the branch
     * @return the shift as a serialized string
     * @throws ShiftNotFoundException if no shift is found
     */
    public String getShiftByBranch(long doneBy, LocalDate date, ShiftType shiftType, long branchId) {
        try {
            String shift = shiftController.getshiftByBranch(doneBy, date, shiftType, branchId);
            return shift;
        } catch (ShiftNotFoundException e) {
            throw new ShiftNotFoundException(e.getMessage());
        }
    }

    public String updateRolesRequired(long doneBy, long shiftId, String role, Integer roleRequired) {
        try {
            boolean result = shiftController.updateRolesRequired(doneBy, shiftId, role, roleRequired);
            return result ? "success to update roles required" : "Failed to update roles required";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String updateShiftManager(long doneBy, long shiftId, boolean isAssignedShiftManager) {
        try {
            boolean result = shiftController.updateShiftManager(doneBy, shiftId, isAssignedShiftManager);
            return result ? "success to update shift manager" : "Failed to update shift manager";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String updateOpenStatus(long doneBy, long shiftId, boolean isOpen) {
        try {
            boolean result = shiftController.updateOpenStatus(doneBy, shiftId, isOpen);
            return result ? "success to update open status" : "Failed to update open status";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }


    public String removeRoleRequired(long doneBy, long shiftId, String role) {
        try {
            boolean result = shiftController.removeRoleRequired(doneBy, shiftId, role);
            return result ? "success to remove role to required roles" : "Failed to remove role to required roles";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getRoles(long doneBy) {
        try{
            String roles = shiftController.getRoles(doneBy);
            return roles;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Assigns an employee to a role in a shift
     * @param doneBy the employee making the assignment
     * @param shiftId the ID of the shift
     * @param employeeId the ID of the employee to assign
     * @param role the role to assign the employee to
     * @return a message indicating success or failure
     */
    public String assignEmployeeToRole(long doneBy, long shiftId, long employeeId, String role) {
        try {
            boolean result = assignmentController.assignEmployeeToRole( shiftId, doneBy, role,employeeId);
            return result ? "Employee assigned to role successfully" : "Failed to assign employee to role";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Removes an employee's assignment from a shift
     * @param doneBy the employee making the change
     * @param shiftId the ID of the shift
     * @param employeeId the ID of the employee to remove
     * @return a message indicating success or failure
     */
    public String removeAssignment(long doneBy, long shiftId, String role, long employeeId) {
        try {
            boolean result = assignmentController.removeAssignment(doneBy, shiftId, role, employeeId);
            return result ? "Employee assignment removed successfully" : "Failed to remove employee assignment";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Checks if an employee is assigned to a shift
     * @param doneBy the employee making the query
     * @param shiftId the ID of the shift
     * @param employeeId the ID of the employee to check
     * @return true if the employee is assigned, false otherwise
     */
    public boolean isEmployeeAssigned(long doneBy, long shiftId, long employeeId) {
        try {
            return assignmentController.isAssigned(doneBy, shiftId, employeeId);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Marks an employee as available for a shift
     * @param doneBy the employee making the change
     * @param shiftId the ID of the shift
     * @return a message indicating success or failure
     */
    public String markEmployeeAvailable(long doneBy, long shiftId) {
        try {
            availabilityController.markAvailable(shiftId, doneBy);
            return "Employee marked as available successfully";
        } catch (RuntimeException e) {
            return  e.getMessage();
        }
    }

    /**
     * Removes an employee's availability for a shift
     * @param doneBy the employee making the change
     * @param shiftId the ID of the shift
     * @return a message indicating success or failure
     */
    public String removeEmployeeAvailability(long doneBy, long shiftId) {
        try {
            availabilityController.removeAvailability(shiftId, doneBy);
            return "Employee availability removed successfully";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Checks if an employee is available for a shift
     * @param doneBy the employee making the query
     * @param shiftId the ID of the shift
     * @param employeeId the ID of the employee to check
     * @return true if the employee is available, false otherwise
     */
    public boolean isEmployeeAvailable(long doneBy, long shiftId, long employeeId) {
        try {
            return availabilityController.isAvailable(shiftId, employeeId);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Gets an employee's weekly availability
     * @param doneBy the employee making the query
     * @param employeeId the ID of the employee to get availability for
     * @param week the week of the shifts
     * @return a map of dates to shift types and availability
     */
    public Map<LocalDate, Map<String, Boolean>> getEmployeeWeeklyAvailability(long doneBy, long employeeId, Week week) {
        try {
            return availabilityController.getWeeklyAvailability(week, employeeId);
        } catch (RuntimeException e) {
            return new HashMap<>();
        }
    }

    public Set<ShiftDTO> getShiftsByWeek(long doneBy, Week week) {
        try {
            Set<ShiftDTO> shiftDTOs = new HashSet<>();
            String str = shiftController.getShiftsByWeek(doneBy, week);
            if (str == null || str.isEmpty()) {
                return shiftDTOs;
            }
            for(String shiftStr : str.split("\n")) {
                ShiftDTO shiftDTO = deserializeShiftDTO(shiftStr);
                shiftDTOs.add(shiftDTO);
            }
            return shiftDTOs;
        }
        catch (ShiftNotFoundException e) {
            throw new ShiftNotFoundException(e.getMessage());
        }
    }

    public String isAssignedManager(long doneBy, long shiftId){
        try{
            assignmentController.isAssignedManager(doneBy, shiftId);
                return "success to check if assigned manager";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    public List<Set<Long>> getUnassignedManager(long doneBy, long shiftId){
        try {
            return assignmentController.getUnassignedEmployees(doneBy, shiftId);
        }
        catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
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
    public List<Set<Long>> getUnassignedManagerByBranch(long doneBy, long shiftId, long branchId){
        try {
            return assignmentController.getUnassignedEmployeesByBranch(doneBy, shiftId, branchId);
        }
        catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a specific employee is assigned to a shift at a given date, time, and branch location.
     *
     * @param doneBy The ID of the user performing the check.
     * @param date The date of the shift being checked.
     * @param hour The start time of the shift being checked.
     * @param employeeId The ID of the employee whose assignment status is being verified.
     * @param branchId The branch location where the shift is scheduled.
     * @return true if the specified employee is assigned to the shift at the given date, time, and branch location; false otherwise.
     */
    public boolean isAssignedByDateTimeBranch(long doneBy, LocalDate date, LocalTime hour, long employeeId, long branchId) {
        try {
            return assignmentController.isAssignedEmployeeByDateTimeBranch(doneBy, date, hour, employeeId, branchId);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    public String getShiftByEmployee(long doneBy, long employeeId) {
        try {
            return shiftController.getShiftByEmployee(doneBy, employeeId);
        } catch (ShiftNotFoundException e) {
            throw new ShiftNotFoundException(e.getMessage());
        }
    }

    /**
     * Checks if a specific role is assigned at a given date, time, and branch location.
     *
     * @param date The date of the shift being checked.
     * @param hour The time of the shift being checked.
     * @param role The role for which the assignment status is being verified.
     * @param address The address of the branch where the shift is scheduled.
     * @param areaCode The area code of the branch location.
     * @return true if the specified role is assigned at the given date, time, and branch location; false otherwise.
     */
    public boolean isAssignedRoleByDateTimeBranch( LocalDate date, LocalTime hour, String role, String address, int areaCode) {
        try {
            return assignmentController.isAssignedRoleByDateTimeBranch( date, hour, role, address, areaCode);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAllShiftsByDate(long doneBy, LocalDate date) {
        try {
            String shifts = shiftController.getAllShiftsByDate(doneBy, date);
            return shifts;
        }
        catch (ShiftNotFoundException e) {
            throw new ShiftNotFoundException(e.getMessage());
        }
        catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    public Boolean isWeekendBlocked() {
        return availabilityController.isWeekendBlocked();
    }


    /**
     * Checks if a driver is assigned to a shift at a specific date, time, and branch location
     *
     * @param driverId The ID of the driver to check
     * @param date The date of the shift
     * @param time The time of the shift
     * @param address The address of the branch
     * @param areaCode The area code of the branch
     * @return true if the driver is assigned to a shift at the specified date, time, and branch location; false otherwise
     */
    public boolean isDriverOnShiftAt(long driverId, LocalDate date, LocalTime time, String address, int areaCode) {
        try {
            // Check if the driver is assigned to a shift at the specified date, time, and branch location
            return assignmentController.isAssignedDriverByDateTimeAddress(driverId, date, time, areaCode, address);
        } catch (RuntimeException e) {
            // Log the error or handle it appropriately
            throw new ShiftServiceException("Error checking if driver is on shift: " + e.getMessage(), e);
        }
    }

    public ShiftDTO getCurrentShift(long doneBy) {
        try {
            String shiftStr = shiftController.getCurrentShift(doneBy);
            if (shiftStr == null || shiftStr.isEmpty()) {
                throw new ShiftNotFoundException("No current shift found for employee ID: " + doneBy);
            }
            return deserializeShiftDTO(shiftStr);
        } catch (ShiftNotFoundException e) {
            throw new ShiftNotFoundException(e.getMessage());
        } catch (RuntimeException e) {
            throw new RuntimeException("Error retrieving current shift: " + e.getMessage(), e);
        }
    }
}
