package PresentationLayer.GUI.EmployeeScreen.utils;

import DomainLayer.SystemFactory;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;

import java.io.IOException;

/**
 * Facade class for accessing service layer classes.
 * This class simplifies the initialization and access to service layer classes.
 */
public class ServiceFacade {
    private static ServiceFacade instance;

    private final EmployeeService employeeService;
    private final ShiftService shiftService;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes service layer classes with their required dependencies.
     */
    private ServiceFacade() throws IOException {
        // Initialize controllers
        SystemFactory factory = new SystemFactory();

        // System Factory creates the Modules components
        SystemFactory.EmployeeModuleComponents employeeComponents = factory.createEmployeeModule(false);

        employeeService = employeeComponents.getEmployeeService();
        shiftService = employeeComponents.getShiftService();
    }

    /**
     * Gets the singleton instance of the ServiceFacade.
     * 
     * @return The ServiceFacade instance
     */
    public static ServiceFacade getInstance() throws IOException {
        if (instance == null) {
            instance = new ServiceFacade();
        }
        return instance;
    }

    /**
     * Gets the EmployeeService instance.
     * 
     * @return The EmployeeService instance
     */
    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    /**
     * Gets the ShiftService instance.
     * 
     * @return The ShiftService instance
     */
    public ShiftService getShiftService() {
        return shiftService;
    }
}