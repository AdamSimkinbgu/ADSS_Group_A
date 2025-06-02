package Inventory.DAO;

import Inventory.DTO.SaleDTO;
import Inventory.util.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleDTO_SQL implements SaleDAO {



    public SaleDTO_SQL() {
        // Initialize the connection if needed, or leave it to be managed by the methods.
    }

    @Override
    public SaleDTO Add(SaleDTO sale) {
        String saleSql = "INSERT INTO sales (sale_price, sale_date) VALUES (?, ?)";
        String itemsSql = "INSERT INTO sale_items (sale_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement salePs = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {

            // Insert main sale record
            salePs.setFloat(1, sale.getSalePrice());
            salePs.setString(2, sale.getdate().toString());
            salePs.executeUpdate();

            // Get generated sale ID
            try (ResultSet generatedKeys = salePs.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sale.setId(generatedKeys.getInt(1));

                    // Insert sale items
                    try (PreparedStatement itemsPs = conn.prepareStatement(itemsSql)) {
                        for (Map.Entry<Integer, Integer> item : sale.getProducts().entrySet()) {
                            itemsPs.setInt(1, sale.getId());
                            itemsPs.setInt(2, item.getKey());
                            itemsPs.setInt(3, item.getValue());
                            itemsPs.executeUpdate();
                        }
                    }
                }
            }
            return sale;
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public List<SaleDTO> GetAll() {
        String sql = """
        SELECT s.*, si.product_id, si.quantity 
        FROM sales s 
        LEFT JOIN sale_items si ON s.sale_id = si.sale_id
        """;
        List<SaleDTO> sales = new ArrayList<>();
        Map<Integer, SaleDTO> saleMap = new HashMap<>();

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int saleId = rs.getInt("sale_id");

                SaleDTO sale = saleMap.get(saleId);
                if (sale == null) {
                    sale = new SaleDTO();
                    sale.setId(saleId);
                    sale.setsalePrice(rs.getFloat("sale_price"));
                    sale.setdate(LocalDate.parse(rs.getString("sale_date")));
                    sale.setproducts(new HashMap<>());
                    saleMap.put(saleId, sale);
                    sales.add(sale);
                }

                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                if (productId != 0) {  // Check if there are any items
                    sale.getProducts().put(productId, quantity);
                }
            }
            return sales;
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void Delete(int id) {
        String deleteItemsSql = "DELETE FROM sale_items WHERE sale_id = ?";
        String deleteSaleSql = "DELETE FROM sales WHERE sale_id = ?";

        try (Connection conn = DataBase.getConnection()) {
            // First delete related items
            try (PreparedStatement ps = conn.prepareStatement(deleteItemsSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Then delete the sale
            try (PreparedStatement ps = conn.prepareStatement(deleteSaleSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }


    @Override
    public void DeleteAll() {
        String deleteItemsSql = "DELETE FROM sale_items";
        String deleteSalesSql = "DELETE FROM sales";

        try (Connection conn = DataBase.getConnection()) {
            // Delete all sale items first
            try (PreparedStatement ps = conn.prepareStatement(deleteItemsSql)) {
                ps.executeUpdate();
            }

            // Then delete all sales
            try (PreparedStatement ps = conn.prepareStatement(deleteSalesSql)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

}
