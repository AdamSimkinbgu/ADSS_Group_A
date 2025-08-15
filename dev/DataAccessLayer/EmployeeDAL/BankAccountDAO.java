package DataAccessLayer.EmployeeDAL;

import DTOs.BankAccountDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for BankAccount entities.
 * This class provides methods to interact with the BankAccounts table in the database.
 * Uses BankAccountDTO for data transfer between layers.
 */
public class BankAccountDAO {
    private Connection connection;

    public BankAccountDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new bank account into the database.
     *
     * @param bankAccountDTO The bank account DTO to insert
     * @return true if the insertion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean insert(BankAccountDTO bankAccountDTO) throws SQLException {
        String sql = "INSERT INTO BankAccounts (employeeId, bankNumber, bankBranchNumber, bankAccountNumber) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, bankAccountDTO.getEmployeeId());
            pstmt.setLong(2, bankAccountDTO.getBankNumber());
            pstmt.setLong(3, bankAccountDTO.getBankBranchNumber());
            pstmt.setLong(4, bankAccountDTO.getBankAccountNumber());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Updates an existing bank account in the database.
     *
     * @param bankAccountDTO The bank account DTO to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean update(BankAccountDTO bankAccountDTO) throws SQLException {
        String sql = "UPDATE BankAccounts SET bankNumber = ?, bankBranchNumber = ?, bankAccountNumber = ? " +
                "WHERE employeeId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, bankAccountDTO.getBankNumber());
            pstmt.setLong(2, bankAccountDTO.getBankBranchNumber());
            pstmt.setLong(3, bankAccountDTO.getBankAccountNumber());
            pstmt.setLong(4, bankAccountDTO.getEmployeeId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Deletes a bank account from the database.
     *
     * @param employeeId The ID of the employee whose bank account to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean delete(long employeeId) throws SQLException {
        String sql = "DELETE FROM BankAccounts WHERE employeeId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, employeeId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves a bank account from the database by employee ID.
     *
     * @param employeeId The ID of the employee whose bank account to retrieve
     * @return The bank account DTO if found, null otherwise
     * @throws SQLException if a database access error occurs
     */
    public BankAccountDTO getByEmployeeId(long employeeId) throws SQLException {
        String sql = "SELECT * FROM BankAccounts WHERE employeeId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBankAccountDTO(rs);
                }
                return null;
            }
        }
    }

    /**
     * Maps a ResultSet to a BankAccountDTO object.
     *
     * @param rs The ResultSet containing bank account data
     * @return The mapped BankAccountDTO object
     * @throws SQLException if a database access error occurs
     */
    private BankAccountDTO mapResultSetToBankAccountDTO(ResultSet rs) throws SQLException {
        long employeeId = rs.getLong("employeeId");
        long bankNumber = rs.getLong("bankNumber");
        long bankBranchNumber = rs.getLong("bankBranchNumber");
        long bankAccountNumber = rs.getLong("bankAccountNumber");

        return new BankAccountDTO(employeeId, bankNumber, bankBranchNumber, bankAccountNumber);
    }
}