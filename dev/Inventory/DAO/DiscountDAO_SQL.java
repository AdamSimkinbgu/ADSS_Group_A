package Inventory.DAO;

import Inventory.DTO.DiscountDTO;
import Inventory.util.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class DiscountDAO_SQL implements DiscountDAO {



    public DiscountDAO_SQL() {
        // Initialize the connection if needed, or leave it to be managed by the methods.
    }

    @Override
    public DiscountDTO add(DiscountDTO d) {
        String sql = """
        INSERT INTO discounts (
            percent,
            discount_start,
            discount_end,
            product_id,
            category_name
        ) VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setFloat(1, d.getPercent());
            ps.setString(2, d.getDiscountStart().toString());  // Store as TEXT in YYYY-MM-DD format
            ps.setString(3, d.getDiscountEnd().toString());    // Store as TEXT in YYYY-MM-DD format
            ps.setInt(4, d.getpId());
            ps.setString(5, d.getCatName());

            ps.executeUpdate();

            // Get the generated discount_id
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    d.setId(generatedKeys.getInt(1));
                }
            }

            return d;

        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public List<DiscountDTO> getAll() {
        String sql = """
            SELECT discount_id, 
            product_id, 
            percent, 
            discount_start, 
            discount_end, 
            category_name 
            FROM discounts""";

        // Use a List to store the discounts
        List<DiscountDTO> discounts = new ArrayList<>();
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Iterate through the ResultSet and populate the list
            while (rs.next()) {
                DiscountDTO discount = new DiscountDTO();
                discount.setId(rs.getInt("discount_id"));
                discount.setpId(rs.getInt("product_id"));
                discount.setPercent(rs.getFloat("percent"));
                // Parse dates from string format
                discount.setDiscountStart(LocalDate.parse(rs.getString("discount_start")));
                discount.setDiscountEnd(LocalDate.parse(rs.getString("discount_end")));
                discount.setCatName(rs.getString("category_name"));
                discounts.add(discount);
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
        return discounts;
    }

    @Override
    public void delete(DiscountDTO d) {
        String sql = "DELETE FROM discounts WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }


    @Override
    public void deleteAll() {
        String sql = "DELETE FROM discounts";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }

        sql = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'discounts'";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }
}
