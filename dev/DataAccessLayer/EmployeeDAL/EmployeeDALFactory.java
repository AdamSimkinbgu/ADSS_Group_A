package DataAccessLayer.EmployeeDAL;

import Util.Database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory class for creating DAO objects for the Employee subsystem.
 * This class provides a single point of access to all DAO objects.
 */
public class EmployeeDALFactory {
    private static EmployeeDALFactory instance;
    private final EmployeeDAO employeeDAO;
    private final BranchDAO branchDAO;
    private final AuthorisationDAO authorisationDAO;
    private final ShiftDAO shiftDAO;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes all DAO objects with a database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    private EmployeeDALFactory() throws SQLException {
        Connection connection = Database.getConnection();
        this.employeeDAO = new EmployeeDAO(connection);
        this.branchDAO = new BranchDAO(connection);
        this.authorisationDAO = new AuthorisationDAO(connection);
        this.shiftDAO = new ShiftDAO(connection);
    }

    /**
     * Gets the singleton instance of the factory.
     *
     * @return The singleton instance
     * @throws SQLException if a database access error occurs
     */
    public static synchronized EmployeeDALFactory getInstance() throws SQLException {
        if (instance == null) {
            instance = new EmployeeDALFactory();
        }
        return instance;
    }

    /**
     * Gets the EmployeeDAO instance.
     *
     * @return The EmployeeDAO instance
     */
    public EmployeeDAO getEmployeeDAO() {
        return employeeDAO;
    }

    /**
     * Gets the BranchDAO instance.
     *
     * @return The BranchDAO instance
     */
    public BranchDAO getBranchDAO() {
        return branchDAO;
    }

    /**
     * Gets the AuthorisationDAO instance.
     *
     * @return The AuthorisationDAO instance
     */
    public AuthorisationDAO getAuthorisationDAO() {
        return authorisationDAO;
    }

    /**
     * Gets the ShiftDAO instance.
     *
     * @return The ShiftDAO instance
     */
    public ShiftDAO getShiftDAO() { return shiftDAO; }
}
