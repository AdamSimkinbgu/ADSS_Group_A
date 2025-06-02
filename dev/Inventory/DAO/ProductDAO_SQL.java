package Inventory.DAO;

import Inventory.DTO.ProductDTO;
import Inventory.type.Position;
import Inventory.util.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO_SQL implements ProductDAO {

    private Connection conn;

    public ProductDAO_SQL() {
        // Initialize the connection if needed, or leave it to be managed by the methods.
    }

    @Override
    public void Add(ProductDTO product) {
        String sql = """
        INSERT INTO products (
            product_id,
            product_name,
            manufacturer_name,
            product_price,
            minimal_amount_store,
            minimal_amount_stock,
            warehouse_shelf_x,
            warehouse_shelf_y,
            store_shelf_x,
            store_shelf_y
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, product.getproductId());
            ps.setString(2, product.getproductName());
            ps.setString(3, product.getmanufacturerName());
            ps.setFloat(4, product.getproductPrice());
            ps.setInt(5, product.getminimalAmountStore());
            ps.setInt(6, product.getminimalAmountStock());
            ps.setInt(7, product.getwareHouseShelf().line());
            ps.setInt(8, product.getwareHouseShelf().shelf());
            ps.setInt(9, product.getstoreShelf().line());
            ps.setInt(10, product.getstoreShelf().shelf());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public List<ProductDTO> GetAll() {
        String sql = "SELECT * FROM products";
        List<ProductDTO> products = new ArrayList<>();

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductDTO product = new ProductDTO(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("manufacturer_name"),
                        rs.getInt("minimal_amount_store"),
                        rs.getInt("minimal_amount_stock"),
                        rs.getFloat("product_price"),
                        new Position(rs.getInt("store_shelf_x"), rs.getInt("store_shelf_y")),
                        new Position(rs.getInt("warehouse_shelf_x"), rs.getInt("warehouse_shelf_y"))
                );
                products.add(product);
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void Set(ProductDTO product) {
        String sql = """
        UPDATE products SET 
            product_name = ?,
            manufacturer_name = ?,
            product_price = ?,
            minimal_amount_store = ?,
            minimal_amount_stock = ?,
            warehouse_shelf_x = ?,
            warehouse_shelf_y = ?,
            store_shelf_x = ?,
            store_shelf_y = ?
        WHERE product_id = ?
        """;

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getproductName());
            ps.setString(2, product.getmanufacturerName());
            ps.setFloat(3, product.getproductPrice());
            ps.setInt(4, product.getminimalAmountStore());
            ps.setInt(5, product.getminimalAmountStock());
            ps.setInt(6, product.getwareHouseShelf().line());
            ps.setInt(7, product.getwareHouseShelf().shelf());
            ps.setInt(8, product.getstoreShelf().line());
            ps.setInt(9, product.getstoreShelf().shelf());
            ps.setInt(10, product.getproductId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }


    @Override
    public void Delete(ProductDTO product) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, product.getproductId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }
}
