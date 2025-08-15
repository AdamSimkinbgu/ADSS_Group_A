package DataAccessLayer.EmployeeDAL;

import DTOs.BankAccountDTO;
import DTOs.EmployeeDTO;
import Util.CliUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data Access Object for Employee entities.
 * This class provides methods to interact with the Employee table in the database.
 * Uses EmployeeDTO for data transfer between layers.
 */
public class EmployeeDAO {
    private Connection connection;
    private BankAccountDAO bankAccountDAO;

    public EmployeeDAO(Connection connection) {
        this.connection = connection;
        this.bankAccountDAO = new BankAccountDAO(connection);
    }

    /**
     * Inserts a new employee into the database.
     *
     * @param employeeDTO The employee DTO to insert
     * @return true if the insertion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean insert(EmployeeDTO employeeDTO) throws SQLException {
        String sql = "INSERT INTO Employees (israeliId, firstName, lastName, salary, startOfEmployment, isActive, creationDate, updateDate, branchId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, employeeDTO.getIsraeliId());
            pstmt.setString(2, employeeDTO.getFirstName());
            pstmt.setString(3, employeeDTO.getLastName());
            pstmt.setLong(4, employeeDTO.getSalary());
            pstmt.setString(5, employeeDTO.getStartOfEmployment().format(CliUtil.dateFormatter));
            pstmt.setBoolean(6, employeeDTO.isActive());
            pstmt.setString(7, employeeDTO.getCreationDate().format(CliUtil.dateFormatter));
            pstmt.setString(8, employeeDTO.getUpdateDate().format(CliUtil.dateFormatter));

            Long branchId = employeeDTO.getBranchId() == 0 ? null : employeeDTO.getBranchId();
            if (branchId != null) {
                pstmt.setLong(9, branchId);
            } else {
                pstmt.setNull(9, java.sql.Types.BIGINT);
            }

            int affectedRows = pstmt.executeUpdate();

            // Insert employee roles
            if (affectedRows > 0) {
                insertEmployeeRoles(employeeDTO.getIsraeliId(), employeeDTO.getRoles());

                // Insert terms of employment as JSON
                insertTermsOfEmployment(employeeDTO.getIsraeliId(), employeeDTO.getTermsOfEmployment());

                // Insert bank account if available
                if (employeeDTO.getBankAccount() != null) {
                    BankAccountDTO bankAccountDTO = employeeDTO.getBankAccount();
                    bankAccountDTO.setEmployeeId(employeeDTO.getIsraeliId());
                    bankAccountDAO.insert(bankAccountDTO);
                }

                return true;
            }
            return false;
        }
    }

    /**
     * Updates an existing employee in the database.
     *
     * @param employeeDTO The employee DTO to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean update(EmployeeDTO employeeDTO) throws SQLException {
        String sql = "UPDATE Employees SET firstName = ?, lastName = ?, salary = ?, " +
                "startOfEmployment = ?, isActive = ?, updateDate = ?, branchId = ? " +
                "WHERE israeliId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employeeDTO.getFirstName());
            pstmt.setString(2, employeeDTO.getLastName());
            pstmt.setLong(3, employeeDTO.getSalary());
            pstmt.setString(4, employeeDTO.getStartOfEmployment().format(CliUtil.dateFormatter));
            pstmt.setBoolean(5, employeeDTO.isActive());
            pstmt.setString(6, employeeDTO.getUpdateDate().format(CliUtil.dateFormatter));

            Long branchId = employeeDTO.getBranchId() == 0 ? null : employeeDTO.getBranchId();
            if (branchId != null) {
                pstmt.setLong(7, branchId);
            } else {
                pstmt.setNull(7, java.sql.Types.BIGINT);
            }

            pstmt.setLong(8, employeeDTO.getIsraeliId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Update roles - delete existing and insert new ones
                deleteEmployeeRoles(employeeDTO.getIsraeliId());
                insertEmployeeRoles(employeeDTO.getIsraeliId(), employeeDTO.getRoles());

                // Update terms of employment
                updateTermsOfEmployment(employeeDTO.getIsraeliId(), employeeDTO.getTermsOfEmployment());

                // Update bank account
                BankAccountDTO existingBankAccount = bankAccountDAO.getByEmployeeId(employeeDTO.getIsraeliId());
                if (employeeDTO.getBankAccount() != null) {
                    BankAccountDTO bankAccountDTO = employeeDTO.getBankAccount();
                    bankAccountDTO.setEmployeeId(employeeDTO.getIsraeliId());

                    if (existingBankAccount != null) {
                        // Update existing bank account
                        bankAccountDAO.update(bankAccountDTO);
                    } else {
                        // Insert new bank account
                        bankAccountDAO.insert(bankAccountDTO);
                    }
                } else if (existingBankAccount != null) {
                    // Delete existing bank account if the new one is null
                    bankAccountDAO.delete(employeeDTO.getIsraeliId());
                }

                return true;
            }
            return false;
        }
    }

    /**
     * Deletes an employee from the database.
     *
     * @param israeliId The ID of the employee to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean delete(long israeliId) throws SQLException {
        // First delete related data
        deleteEmployeeRoles(israeliId);
        deleteTermsOfEmployment(israeliId);

        // Delete bank account if exists
        bankAccountDAO.delete(israeliId);

        // Then delete the employee
        String sql = "DELETE FROM Employees WHERE israeliId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, israeliId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves an employee from the database by ID.
     *
     * @param israeliId The ID of the employee to retrieve
     * @return The employee DTO if found, null otherwise
     * @throws SQLException if a database access error occurs
     */
    public EmployeeDTO getById(long israeliId) throws SQLException {
        String sql = "SELECT * FROM Employees WHERE israeliId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, israeliId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployeeDTO(rs);
                }
                return null;
            }
        }
    }

    /**
     * Retrieves all employees from the database.
     *
     * @return A list of all employee DTOs
     * @throws SQLException if a database access error occurs
     */
    public List<EmployeeDTO> getAll() throws SQLException {
        String sql = "SELECT * FROM Employees";
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                employeeDTOs.add(mapResultSetToEmployeeDTO(rs));
            }

            return employeeDTOs;
        }
    }

    /**
     * Retrieves all employees assigned to a specific branch.
     *
     * @param branchId The ID of the branch
     * @return A list of employee DTOs assigned to the branch
     * @throws SQLException if a database access error occurs
     */
    public List<EmployeeDTO> getByBranch(long branchId) throws SQLException {
        String sql = "SELECT * FROM Employees WHERE branchId = ?";
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, branchId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employeeDTOs.add(mapResultSetToEmployeeDTO(rs));
                }

                return employeeDTOs;
            }
        }
    }

    /**
     * Maps a ResultSet to an EmployeeDTO object.
     *
     * @param rs The ResultSet containing employee data
     * @return The mapped EmployeeDTO object
     * @throws SQLException if a database access error occurs
     */
    private EmployeeDTO mapResultSetToEmployeeDTO(ResultSet rs) throws SQLException {
        long israeliId = rs.getLong("israeliId");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        long salary = rs.getLong("salary");

        // Parse date strings manually to handle different formats
        LocalDate startOfEmployment = parseLocalDate(rs.getString("startOfEmployment"));
        boolean isActive = rs.getBoolean("isActive");
        LocalDate creationDate = parseLocalDate(rs.getString("creationDate"));
        LocalDate updateDate = parseLocalDate(rs.getString("updateDate"));

        Long branchId = rs.getLong("branchId");
        if (rs.wasNull()) {
            branchId = null;
        }

        // Get roles for this employee
        Set<String> roles = getEmployeeRoles(israeliId);

        // Get terms of employment for this employee
        Map<String, Object> termsOfEmployment = getTermsOfEmployment(israeliId);

        // Get bank account for this employee
        BankAccountDTO bankAccountDTO = bankAccountDAO.getByEmployeeId(israeliId);

        return new EmployeeDTO(
            israeliId, 
            firstName, 
            lastName, 
            salary, 
            termsOfEmployment, 
            roles, 
            startOfEmployment, 
            isActive, 
            creationDate, 
            updateDate, 
            branchId,
            bankAccountDTO
        );
    }

    /**
     * Inserts roles for an employee.
     *
     * @param israeliId The ID of the employee
     * @param roles The roles to insert
     * @throws SQLException if a database access error occurs
     */
    private void insertEmployeeRoles(long israeliId, Set<String> roles) throws SQLException {
        String sql = "INSERT INTO EmployeeRoles (israeliId, role) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String role : roles) {
                pstmt.setLong(1, israeliId);
                pstmt.setString(2, role);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    /**
     * Deletes all roles for an employee.
     *
     * @param israeliId The ID of the employee
     * @throws SQLException if a database access error occurs
     */
    private void deleteEmployeeRoles(long israeliId) throws SQLException {
        String sql = "DELETE FROM EmployeeRoles WHERE israeliId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, israeliId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves all roles for an employee.
     *
     * @param israeliId The ID of the employee
     * @return A set of roles for the employee
     * @throws SQLException if a database access error occurs
     */
    private Set<String> getEmployeeRoles(long israeliId) throws SQLException {
        String sql = "SELECT role FROM EmployeeRoles WHERE israeliId = ?";
        Set<String> roles = new HashSet<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, israeliId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("role"));
                }

                return roles;
            }
        }
    }

    /**
     * Inserts terms of employment for an employee.
     *
     * @param israeliId The ID of the employee
     * @param termsOfEmployment The terms of employment to insert
     * @throws SQLException if a database access error occurs
     */
    private void insertTermsOfEmployment(long israeliId, Map<String, Object> termsOfEmployment) throws SQLException {
        String sql = "INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Map.Entry<String, Object> entry : termsOfEmployment.entrySet()) {
                pstmt.setLong(1, israeliId);
                pstmt.setString(2, entry.getKey());
                pstmt.setString(3, entry.getValue().toString());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    /**
     * Updates terms of employment for an employee.
     *
     * @param israeliId The ID of the employee
     * @param termsOfEmployment The terms of employment to update
     * @throws SQLException if a database access error occurs
     */
    private void updateTermsOfEmployment(long israeliId, Map<String, Object> termsOfEmployment) throws SQLException {
        // Delete existing terms
        deleteTermsOfEmployment(israeliId);

        // Insert new terms
        insertTermsOfEmployment(israeliId, termsOfEmployment);
    }

    /**
     * Deletes all terms of employment for an employee.
     *
     * @param israeliId The ID of the employee
     * @throws SQLException if a database access error occurs
     */
    private void deleteTermsOfEmployment(long israeliId) throws SQLException {
        String sql = "DELETE FROM EmployeeTerms WHERE israeliId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, israeliId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves all terms of employment for an employee.
     *
     * @param israeliId The ID of the employee
     * @return A map of terms of employment for the employee
     * @throws SQLException if a database access error occurs
     */
    private Map<String, Object> getTermsOfEmployment(long israeliId) throws SQLException {
        String sql = "SELECT termKey, termValue FROM EmployeeTerms WHERE israeliId = ?";
        Map<String, Object> termsOfEmployment = new HashMap<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, israeliId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    termsOfEmployment.put(rs.getString("termKey"), rs.getString("termValue"));
                }

                return termsOfEmployment;
            }
        }
    }

    /**
     * Parses a date string into a LocalDate object.
     * Only accepts "dd-MM-yyyy" format (e.g., "01-01-2020").
     *
     * @param dateStr The date string to parse
     * @return The parsed LocalDate
     * @throws SQLException if the date string cannot be parsed
     */
    private LocalDate parseLocalDate(String dateStr) throws SQLException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new SQLException("Date string is null or empty");
        }

        try {
            // Only accept "dd-MM-yyyy" format (e.g., "01-01-2020")
            if (dateStr.matches("\\d{2}-\\d{2}-\\d{4}")) {
                return LocalDate.parse(dateStr, CliUtil.dateFormatter);
            } else {
                throw new SQLException("Date format not supported. Please use dd-MM-yyyy format: " + dateStr);
            }
        } catch (Exception e) {
            throw new SQLException("Error parsing date: " + dateStr + ". Please use dd-MM-yyyy format.", e);
        }
    }
}
