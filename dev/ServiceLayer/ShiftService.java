package ServiceLayer;

import DomainLayer.*;
import DomainLayer.enums.ShiftType;
import ServiceLayer.response.Response;
import ServiceLayer.util.JsonUtil;
import Util.Week;

import java.time.LocalDate;
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

    /**
     * Creates a new shift
     * 
     * @param doneBy the employee creating the shift
     * @param shiftType the type of shift
     * @param date the date of the shift
     * @param rolesRequired the roles required for the shift
     * @param assignedEmployees the employees assigned to the shift
     * @param availableEmployees the employees available for the shift
     * @param isAssignedShiftManager whether a shift manager is assigned
     * @param isOpen whether the shift is open
     * @param hours the hours of the shift
     * @param updateDate the date the shift was last updated
     * @return JSON string containing a Response with a success or error message
     */
    public String createShift(long doneBy, ShiftType shiftType, LocalDate date,
                              Map<String, Integer> rolesRequired,
                              Map<String, Set<Long>> assignedEmployees,
                              Set<Long> availableEmployees,
                              boolean isAssignedShiftManager,
                              boolean isOpen, String hours, LocalDate updateDate) {
        try {
            boolean result = shiftController.createShift(doneBy, shiftType, date, rolesRequired, assignedEmployees,
                    availableEmployees, isAssignedShiftManager, isOpen, hours, updateDate);
            return result ? 
                JsonUtil.successResponse("Shift created successfully") : 
                JsonUtil.errorResponse("Failed to create shift");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Creates shifts for an entire week
     * 
     * @param doneBy the employee creating the shifts
     * @param date the starting date of the week
     * @param rolesRequired the roles required for the shifts
     * @return JSON string containing a Response with a success or error message
     */
    public String createWeeklyShifts(long doneBy, LocalDate date,
                                      Map<String, Integer> rolesRequired) {
        try {
            boolean result = shiftController.createWeeklyShifts(doneBy, date, rolesRequired);
            return result ? 
                JsonUtil.successResponse("Weekly shifts created successfully") : 
                JsonUtil.errorResponse("Failed to create weekly shifts");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Removes a shift by its ID
     * 
     * @param doneBy the employee removing the shift
     * @param shiftId the ID of the shift to remove
     * @return JSON string containing a Response with a success or error message
     */
    public String removeShiftByID(long doneBy, long shiftId) {
        try {
            if (shiftId <= 0) {
                return JsonUtil.errorResponse("Shift ID must be a positive number");
            }
            boolean result = shiftController.removeShiftByID(doneBy, shiftId);
            return result ? 
                JsonUtil.successResponse("Shift deleted successfully") : 
                JsonUtil.errorResponse("Failed to delete shift");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Removes a shift by date and type
     * 
     * @param doneBy the employee removing the shift
     * @param date the date of the shift
     * @param shiftType the type of the shift
     * @return JSON string containing a Response with a success or error message
     */
    public String removeShift(long doneBy, LocalDate date, ShiftType shiftType) {
        try {
            boolean result = shiftController.removeShift(doneBy, date, shiftType);
            return result ? 
                JsonUtil.successResponse("Shift deleted successfully") : 
                JsonUtil.errorResponse("Failed to delete shift");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Updates a shift's details
     * 
     * @param doneBy the employee updating the shift
     * @param shiftId the ID of the shift to update
     * @param shiftType the new type of the shift
     * @param date the new date of the shift
     * @param isAssignedShiftManager whether a shift manager is assigned
     * @param isOpen whether the shift is open
     * @param hours the hours of the shift
     * @param updateDate the date the shift was last updated
     * @return JSON string containing a Response with a success or error message
     */
    public String updateShift(long doneBy, long shiftId, ShiftType shiftType, LocalDate date,
                              boolean isAssignedShiftManager, boolean isOpen, String hours, LocalDate updateDate) {
        try {
            boolean result = shiftController.updateShift(doneBy, shiftId, shiftType, date, isAssignedShiftManager, isOpen, hours, updateDate);
            return result ? 
                JsonUtil.successResponse("Shift updated successfully") : 
                JsonUtil.errorResponse("Failed to update shift");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Gets a shift by its ID
     * 
     * @param doneBy the employee retrieving the shift
     * @param shiftId the ID of the shift to retrieve
     * @return JSON string containing a Response with the shift data or an error message
     */
    public String getShiftById(long doneBy, long shiftId) {
        try {
            Shift shift = shiftController.getShiftByID(doneBy, shiftId);
            if (shift != null && shift.getId() >= 0) {
                return JsonUtil.successResponse(shift);
            } else {
                return JsonUtil.errorResponse("Shift with ID " + shiftId + " not found");
            }
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Gets all shifts in the system
     * 
     * @param doneBy the employee retrieving the shifts
     * @return JSON string containing a Response with all shifts
     */
    public String getAllShifts(long doneBy) {
        try {
            List<Shift> shifts = new ArrayList<>(shiftController.getAllShifts(doneBy));
            return JsonUtil.successResponse(shifts);
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error retrieving shifts: " + e.getMessage());
        }
    }

    /**
     * Gets all shifts for a specific date
     * 
     * @param doneBy the employee retrieving the shifts
     * @param date the date to get shifts for
     * @return JSON string containing a Response with all shifts for the date
     */
    public String getAllShiftsByDate(long doneBy, LocalDate date) {
        try {
            List<Shift> shifts = new ArrayList<>(shiftController.getAllShiftsByDate(doneBy, date));
            return JsonUtil.successResponse(shifts);
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error retrieving shifts for date " + date + ": " + e.getMessage());
        }
    }

    /**
     * Gets all shifts for a specific employee
     * 
     * @param doneBy the employee retrieving the shifts
     * @param employeeID the ID of the employee to get shifts for
     * @return JSON string containing a Response with all shifts for the employee
     */
    public String getShiftsByEmployee(long doneBy, long employeeID) {
        try {
            List<Shift> shifts = new ArrayList<>(shiftController.getShiftsByEmployee(doneBy, employeeID));
            return JsonUtil.successResponse(shifts);
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error retrieving shifts for employee " + employeeID + ": " + e.getMessage());
        }
    }

    /**
     * Gets a shift for a specific date and type
     * 
     * @param doneBy the employee retrieving the shift
     * @param date the date of the shift
     * @param shiftType the type of the shift
     * @return JSON string containing a Response with the shift
     */
    public String getShift(long doneBy, LocalDate date, ShiftType shiftType) {
        try {
            Shift shift = shiftController.getshift(doneBy, date, shiftType);
            if (shift != null && shift.getId() >= 0) {
                return JsonUtil.successResponse(shift);
            } else {
                return JsonUtil.errorResponse("The " + shiftType + " Shift on date " + date + " not found");
            }
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error retrieving shift: " + e.getMessage());
        }
    }

    /**
     * Updates the number of employees required for a specific role in a shift
     * 
     * @param doneBy the employee updating the shift
     * @param shiftId the ID of the shift
     * @param role the role to update
     * @param roleRequired the number of employees required for the role
     * @return JSON string containing a Response with a success or error message
     */
    public String updateRolesRequired(long doneBy, long shiftId, String role, Integer roleRequired) {
        try {
            boolean result = shiftController.updateRolesRequired(doneBy, shiftId, role, roleRequired);
            return result ? 
                JsonUtil.successResponse("Successfully updated roles required") : 
                JsonUtil.errorResponse("Failed to update roles required");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Updates whether a shift has an assigned shift manager
     * 
     * @param doneBy the employee updating the shift
     * @param shiftId the ID of the shift
     * @param isAssignedShiftManager whether a shift manager is assigned
     * @return JSON string containing a Response with a success or error message
     */
    public String updateShiftManager(long doneBy, long shiftId, boolean isAssignedShiftManager) {
        try {
            boolean result = shiftController.updateShiftManager(doneBy, shiftId, isAssignedShiftManager);
            return result ? 
                JsonUtil.successResponse("Successfully updated shift manager status") : 
                JsonUtil.errorResponse("Failed to update shift manager status");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Updates whether a shift is open
     * 
     * @param doneBy the employee updating the shift
     * @param shiftId the ID of the shift
     * @param isOpen whether the shift is open
     * @return JSON string containing a Response with a success or error message
     */
    public String updateOpenStatus(long doneBy, long shiftId, boolean isOpen) {
        try {
            boolean result = shiftController.updateOpenStatus(doneBy, shiftId, isOpen);
            return result ? 
                JsonUtil.successResponse("Successfully updated shift open status") : 
                JsonUtil.errorResponse("Failed to update shift open status");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }


    /**
     * Removes a role from the required roles for a shift
     * 
     * @param doneBy the employee updating the shift
     * @param shiftId the ID of the shift
     * @param role the role to remove
     * @return JSON string containing a Response with a success or error message
     */
    public String removeRoleRequired(long doneBy, long shiftId, String role) {
        try {
            boolean result = shiftController.removeRoleRequired(doneBy, shiftId, role);
            return result ? 
                JsonUtil.successResponse("Successfully removed role from required roles") : 
                JsonUtil.errorResponse("Failed to remove role from required roles");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Gets all roles available in the system
     * 
     * @param doneBy the employee retrieving the roles
     * @return JSON string containing a Response with all roles
     */
    public String getRoles(long doneBy) {
        try {
            Set<String> roles = shiftController.getRoles(doneBy);
            return JsonUtil.successResponse(new ArrayList<>(roles));
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error retrieving roles: " + e.getMessage());
        }
    }

    /**
     * Assigns an employee to a role in a shift
     * 
     * @param doneBy the employee making the assignment
     * @param shiftId the ID of the shift
     * @param employeeId the ID of the employee to assign
     * @param role the role to assign the employee to
     * @return JSON string containing a Response with a success or error message
     */
    public String assignEmployeeToRole(long doneBy, long shiftId, long employeeId, String role) {
        try {
            Shift shift = shiftController.getShiftByID(doneBy, shiftId);
            if (shift == null) {
                return JsonUtil.errorResponse("Shift not found");
            }

            boolean result = assignmentController.assignEmployeeToRole(shift, doneBy, role, employeeId);
            return result ? 
                JsonUtil.successResponse("Employee assigned to role successfully") : 
                JsonUtil.errorResponse("Failed to assign employee to role");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Removes an employee's assignment from a shift
     * 
     * @param doneBy the employee making the change
     * @param shiftId the ID of the shift
     * @param role the role from which to remove the employee
     * @param employeeId the ID of the employee to remove
     * @return JSON string containing a Response with a success or error message
     */
    public String removeAssignment(long doneBy, long shiftId, String role, long employeeId) {
        try {
            Shift shift = shiftController.getShiftByID(doneBy, shiftId);
            if (shift == null) {
                return JsonUtil.errorResponse("Shift not found");
            }

            boolean result = assignmentController.removeAssignment(doneBy, shift, role, employeeId);
            return result ? 
                JsonUtil.successResponse("Employee assignment removed successfully") : 
                JsonUtil.errorResponse("Failed to remove employee assignment");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Checks if an employee is assigned to a shift
     * 
     * @param doneBy the employee making the query
     * @param shiftId the ID of the shift
     * @param employeeId the ID of the employee to check
     * @return JSON string containing a Response with the result of the check
     */
    public String isEmployeeAssigned(long doneBy, long shiftId, long employeeId) {
        try {
            Shift shift = shiftController.getShiftByID(doneBy, shiftId);
            if (shift == null) {
                return JsonUtil.errorResponse("Shift not found");
            }

            boolean isAssigned = assignmentController.isAssigned(doneBy, shift, employeeId);
            return JsonUtil.successResponse(isAssigned);
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error checking assignment: " + e.getMessage());
        }
    }

    /**
     * Marks an employee as available for a shift
     * 
     * @param doneBy the employee making the change
     * @param shiftId the ID of the shift
     * @return JSON string containing a Response with a success or error message
     */
    public String markEmployeeAvailable(long doneBy, long shiftId) {
        try {
            Shift shift = shiftController.getShiftByID(doneBy, shiftId);
            if (shift == null) {
                return JsonUtil.errorResponse("Shift not found");
            }

            availabilityController.markAvailable(shift, doneBy);
            return JsonUtil.successResponse("Employee marked as available successfully");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Removes an employee's availability for a shift
     * 
     * @param doneBy the employee making the change
     * @param shiftId the ID of the shift
     * @return JSON string containing a Response with a success or error message
     */
    public String removeEmployeeAvailability(long doneBy, long shiftId) {
        try {
            Shift shift = shiftController.getShiftByID(doneBy, shiftId);
            if (shift == null) {
                return JsonUtil.errorResponse("Shift not found");
            }
            availabilityController.removeAvailability(shift, doneBy);
            return JsonUtil.successResponse("Employee availability removed successfully");
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Checks if an employee is available for a shift
     * 
     * @param doneBy the employee making the query
     * @param shiftId the ID of the shift
     * @param employeeId the ID of the employee to check
     * @return JSON string containing a Response with the result of the check
     */
    public String isEmployeeAvailable(long doneBy, long shiftId, long employeeId) {
        try {
            Shift shift = shiftController.getShiftByID(doneBy, shiftId);
            if (shift == null) {
                return JsonUtil.errorResponse("Shift not found");
            }
            boolean isAvailable = availabilityController.isAvailable(shift, employeeId);
            return JsonUtil.successResponse(isAvailable);
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error checking availability: " + e.getMessage());
        }
    }

    /**
     * Gets an employee's weekly availability
     * 
     * @param doneBy the employee making the query
     * @param employeeId the ID of the employee to get availability for
     * @param week the week of the shifts
     * @return JSON string containing a Response with a map of dates to shift types and availability
     */
    public String getEmployeeWeeklyAvailability(long doneBy, long employeeId, Week week) {
        try {
            List<Shift> weekShifts = shiftController.getShiftsByWeek(doneBy, week);
            if (weekShifts.isEmpty()) {
                return JsonUtil.successResponse(new HashMap<>());
            }

            Map<LocalDate, Map<String, Boolean>> availability = availabilityController.getWeeklyAvailability(weekShifts, employeeId);
            return JsonUtil.successResponse(availability);
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error retrieving weekly availability: " + e.getMessage());
        }
    }

    /**
     * Gets all shifts for a specific week
     * 
     * @param doneBy the employee retrieving the shifts
     * @param week the week to get shifts for
     * @return JSON string containing a Response with all shifts for the week
     */
    public String getShiftsByWeek(long doneBy, Week week) {
        try {
            List<Shift> weekShifts = shiftController.getShiftsByWeek(doneBy, week);
            if (weekShifts == null || weekShifts.isEmpty()) {
                return JsonUtil.successResponse(new ArrayList<>());
            }
            return JsonUtil.successResponse(weekShifts);
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error retrieving shifts for week: " + e.getMessage());
        }
    }

}
