package ServiceLayer;

import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import ServiceLayer.exception.AuthorizationException;
import ServiceLayer.exception.ServiceException;
import ServiceLayer.exception.ShiftServiceException;
import Util.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * The EmployeeIntegrationService class provides an abstraction layer for integrating and managing
 * operations related to employees and their shifts. This class collaborates with EmployeeService
 * and ShiftService to execute functionality such as checking employee status, roles, and shift assignments.
 */
public class EmployeeIntegrationService {
    final private EmployeeService employeeService;
    final private ShiftService shiftService;

    /**
     * Constructs an instance of the EmployeeIntegrationService.
     *
     * @param employeeService An instance of EmployeeService used to handle operations related to employees.
     * @param shiftService An instance of ShiftService used to manage shift-related operations.
     */
    public EmployeeIntegrationService(EmployeeService employeeService, ShiftService shiftService) {
        this.employeeService = employeeService;
        this.shiftService = shiftService;
    }

    /**
     * Determines whether an employee with the given ID is currently active.
     *
     * @param employeeId The unique identifier of the employee whose status is to be checked.
     * @return true if the employee is active; false otherwise.
     */
    public boolean isActive(long employeeId) {
        try {
            return employeeService.isEmployeeActive(employeeId);
        } catch (Exception e) {
            throw new ServiceException("Error checking employee status: " + e.getMessage());
        }
    }

    /**
     * Checks if the employee with the given ID has the specified role.
     *
     * @param employeeId The unique identifier of the employee.
     * @param role The name of the role to check for the employee.
     * @return true if the employee has the specified role; false otherwise.
     */
    public boolean hasRole(long employeeId, String role) {
        try {
            return employeeService.isEmployeeHaveRole(employeeId, role);
        } catch (Exception e) {
            throw new AuthorizationException("Error checking employee role: " + e.getMessage());
        }
    }



    public boolean isDriverOnShiftAt(long driverId, LocalDateTime dateTime, String address, int areaCode) {
        try {
            LocalDate date = dateTime.toLocalDate();
            LocalTime time = dateTime.toLocalTime();
            return shiftService.isDriverOnShiftAt(driverId, date, time, address, areaCode);
        } catch (Exception e) {
            throw new ShiftServiceException("Error checking if driver is on shift at the specified time: " + e.getMessage());
        }
    }


    public boolean isWarehousemanOnShiftAt(LocalDateTime dateTime, String address, int areaCode) {
        try {
            LocalDate date = dateTime.toLocalDate();
            LocalTime time = dateTime.toLocalTime();
            return shiftService.isAssignedRoleByDateTimeBranch(date, time, config.ROLE_WAREHOUSEMAN, address, areaCode);
        } catch (Exception e) {
            throw new ShiftServiceException("Error checking if warehouseman is on shift at the specified time: " + e.getMessage());
        }
    }

    /**
     * Retrieves a string representation of all drivers in the system.
     * This method returns a string array containing all drover DTO serialized.
     * To extract the object from the string array, you can use the deserialization method.
     * --> Deserialization method -> EmployeeDTO::deserialize
     *
     * @return A string containing details of all drivers.
     */
    public String[] getAllDrivers() { // NOT TESTED and NOT READY FOR USE!
        try {
            return employeeService.getAllDrivers();
        } catch (Exception e) {
            throw new ServiceException("Error retrieving all drivers: " + e.getMessage());
        }
    }

    /**
     * Checks if an employee with the given ID is authorized to perform an action with the specified permission.
     *
     * @param doneBy The unique identifier of the employee whose authorization is being checked.
     * @param permission The specific permission to check for the employee.
     * @return true if the employee is authorized to perform the action, false otherwise.
     */
    public boolean isEmployeeAuthorised(long doneBy, String permission) {
        try {
            return employeeService.isEmployeeAuthorised(doneBy, permission);
        } catch (Exception e) {
            throw new AuthorizationException("Error checking employee authorization: " + e.getMessage());
        }
    }

    /**
     * Checks whether a given address and area code represent a branch.
     *
     * @param address The address of the site to be checked.
     * @param areaCode The area code of the site to be checked.
     * @return true if the given address and area code correspond to a branch; false otherwise.
     */
    public boolean isBranch(String address, int areaCode) {
        try {
            return employeeService.isBranch(address, areaCode);
        } catch (Exception e) {
            throw new ServiceException("Error checking if address is a branch: " + e.getMessage());
        }
    }

}
