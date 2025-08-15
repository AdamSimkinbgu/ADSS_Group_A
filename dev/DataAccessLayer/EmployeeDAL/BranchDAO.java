package DataAccessLayer.EmployeeDAL;

import DTOs.BranchDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Branch entities.
 * This class provides methods to interact with the Branch table in the database.
 * Uses BranchDTO for data transfer between layers.
 */
public class BranchDAO {
    private Connection connection;

    public BranchDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new branch into the database.
     *
     * @param branchDTO The branch DTO to insert
     * @return true if the insertion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean insert(BranchDTO branchDTO) throws SQLException {
        String sql = "INSERT INTO Branches (branchId, branchName, areaCode, branchAddress, managerID) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, branchDTO.getBranchId());
            pstmt.setString(2, branchDTO.getBranchName());
            pstmt.setInt(3, branchDTO.getAreaCode());
            pstmt.setString(4, branchDTO.getBranchAddress());
            pstmt.setString(5, branchDTO.getManagerID());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Updates an existing branch in the database.
     *
     * @param branchDTO The branch DTO to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean update(BranchDTO branchDTO) throws SQLException {
        String sql = "UPDATE Branches SET branchName = ?, areaCode = ?, branchAddress = ?, managerID = ? " +
                "WHERE branchId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, branchDTO.getBranchName());
            pstmt.setInt(2, branchDTO.getAreaCode());
            pstmt.setString(3, branchDTO.getBranchAddress());
            pstmt.setString(4, branchDTO.getManagerID());
            pstmt.setLong(5, branchDTO.getBranchId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Deletes a branch from the database.
     *
     * @param branchId The ID of the branch to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean delete(long branchId) throws SQLException {
        String sql = "DELETE FROM Branches WHERE branchId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, branchId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves a branch from the database by ID.
     *
     * @param branchId The ID of the branch to retrieve
     * @return The branch DTO if found, null otherwise
     * @throws SQLException if a database access error occurs
     */
    public BranchDTO getById(long branchId) throws SQLException {
        String sql = "SELECT * FROM Branches WHERE branchId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, branchId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBranchDTO(rs);
                }
                return null;
            }
        }
    }

    /**
     * Retrieves all branches from the database.
     *
     * @return A list of all branch DTOs
     * @throws SQLException if a database access error occurs
     */
    public List<BranchDTO> getAll() throws SQLException {
        String sql = "SELECT * FROM Branches";
        List<BranchDTO> branchDTOs = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                branchDTOs.add(mapResultSetToBranchDTO(rs));
            }

            return branchDTOs;
        }
    }

    /**
     * Retrieves all branches in a specific area.
     *
     * @param areaCode The area code to filter by
     * @return A list of branch DTOs in the specified area
     * @throws SQLException if a database access error occurs
     */
    public List<BranchDTO> getByAreaCode(int areaCode) throws SQLException {
        String sql = "SELECT * FROM Branches WHERE areaCode = ?";
        List<BranchDTO> branchDTOs = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, areaCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    branchDTOs.add(mapResultSetToBranchDTO(rs));
                }

                return branchDTOs;
            }
        }
    }

    /**
     * Retrieves a branch by manager ID.
     *
     * @param managerID The ID of the manager
     * @return The branch DTO managed by the specified manager, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public BranchDTO getByManager(String managerID) throws SQLException {
        String sql = "SELECT * FROM Branches WHERE managerID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, managerID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBranchDTO(rs);
                }
                return null;
            }
        }
    }

    /**
     * Maps a ResultSet to a BranchDTO object.
     *
     * @param rs The ResultSet containing branch data
     * @return The mapped BranchDTO object
     * @throws SQLException if a database access error occurs
     */
    private BranchDTO mapResultSetToBranchDTO(ResultSet rs) throws SQLException {
        long branchId = rs.getLong("branchId");
        String branchName = rs.getString("branchName");
        int areaCode = rs.getInt("areaCode");
        String branchAddress = rs.getString("branchAddress");
        String managerID = rs.getString("managerID");

        return new BranchDTO(branchId, branchName, areaCode, branchAddress, managerID);
    }
}