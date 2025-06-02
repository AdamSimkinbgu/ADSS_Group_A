package Inventory.DAO;

import Inventory.DTO.SupplyDTO;
import Inventory.util.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SupplyDTO_SQL implements SupplyDAO {


    public SupplyDTO_SQL() {
        // Initialize the connection if needed, or leave it to be managed by the methods.
    }

    @Override
    public SupplyDTO Get(int id) {
        String sql = "SELECT * FROM supplies WHERE supply_id = ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                SupplyDTO supply = new SupplyDTO();
                supply.setSId(rs.getInt("supply_id"));
                supply.setProductID(rs.getInt("product_id"));
                supply.setExpireDate(LocalDate.parse(rs.getString("expire_date")));
                supply.setQuantityWH(rs.getInt("quantity_warehouse"));
                supply.setQuantityS(rs.getInt("quantity_store"));
                supply.setQuantityB(rs.getInt("quantity_bad"));
                return supply;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public List<SupplyDTO> GetAll() {
        String sql = "SELECT * FROM supplies";
        List<SupplyDTO> supplies = new ArrayList<>();

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Iterate through the result set and populate the list of SupplyDTO
            while (rs.next()) {
                SupplyDTO supply = new SupplyDTO();
                supply.setSId(rs.getInt("supply_id"));
                supply.setProductID(rs.getInt("product_id"));
                supply.setExpireDate(LocalDate.parse(rs.getString("expire_date")));
                supply.setQuantityWH(rs.getInt("quantity_warehouse"));
                supply.setQuantityS(rs.getInt("quantity_store"));
                supply.setQuantityB(rs.getInt("quantity_bad"));
                supplies.add(supply);
            }
            return supplies;
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public SupplyDTO Add(SupplyDTO s) {
        String sql = """
        INSERT INTO supplies (
            product_id,
            expire_date,
            quantity_warehouse,
            quantity_store,
            quantity_bad
        ) VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, s.getProductID());
            ps.setString(2, s.getExpireDate().toString());
            ps.setInt(3, s.getQuantityWH());
            ps.setInt(4, s.getquantityS());
            ps.setInt(5, s.getquantityB());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    s.setSId(generatedKeys.getInt(1));
                }
            }

            return s;
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void Delete(int id) {
        String sql = "DELETE FROM supplies WHERE supply_id = ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void Set(SupplyDTO s) {
        String sql = """
        UPDATE supplies SET
            product_id = ?,
            expire_date = ?,
            quantity_warehouse = ?,
            quantity_store = ?,
            quantity_bad = ?
        WHERE supply_id = ?
        """;

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set the parameters for the prepared statement
            ps.setInt(1, s.getProductID());
            ps.setString(2, s.getExpireDate().toString());
            ps.setInt(3, s.getQuantityWH());
            ps.setInt(4, s.getquantityS());
            ps.setInt(5, s.getquantityB());
            ps.setInt(6, s.getsId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

}
