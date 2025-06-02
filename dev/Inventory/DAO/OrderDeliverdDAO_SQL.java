package Inventory.DAO;

import Inventory.DTO.SupplyDTO;
import Inventory.util.DataBase;
import Suppliers.DTOs.OrderPackageDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class OrderDeliverdDAO_SQL implements OrderDeliverdDAO {



    public OrderDeliverdDAO_SQL() {
        // Initialize the connection if needed, or leave it to be managed by the methods.
    }

    @Override
    public List<OrderPackageDTO> GetAll() {
        String orderSql = "SELECT * FROM orders";
        String itemsSql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderPackageDTO> orders = new ArrayList<>();

        try (Connection conn = DataBase.getConnection()) {
            try (PreparedStatement orderPs = conn.prepareStatement(orderSql);
                 ResultSet orderRs = orderPs.executeQuery()) {

                while (orderRs.next()) {
                    int orderId = orderRs.getInt("order_id");
                    LocalDate deliveryDate = LocalDate.parse(orderRs.getString("delivery_date"));
                    List<SupplyDTO> supplies = new ArrayList<>();

                    // Get order items using the same connection
                    try (PreparedStatement itemsPs = conn.prepareStatement(itemsSql)) {
                        itemsPs.setInt(1, orderId);
                        try (ResultSet itemsRs = itemsPs.executeQuery()) {
                            while (itemsRs.next()) {
                                SupplyDTO supply = new SupplyDTO();
                                supply.setProductID(itemsRs.getInt("product_id"));
                                supply.setQuantityWH(itemsRs.getInt("quantity"));
                                supplies.add(supply);
                            }
                        }
                    }

                    OrderPackageDTO order = new OrderPackageDTO(orderId, deliveryDate, supplies);
                    orders.add(order);
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void Add(OrderPackageDTO o) {
        String orderSql = "INSERT INTO orders (order_id, delivery_date) VALUES (?, ?)";
        String itemsSql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement orderPs = conn.prepareStatement(orderSql)) {

            // Insert order with specified ID
            orderPs.setInt(1, o.getOrderId());
            orderPs.setString(2, o.getDeliveryDate().toString());
            orderPs.executeUpdate();

            // Insert order items
            try (PreparedStatement itemsPs = conn.prepareStatement(itemsSql)) {
                for (SupplyDTO supply : o.getSupplies()) {
                    itemsPs.setInt(1, o.getOrderId());
                    itemsPs.setInt(2, supply.getProductID());
                    itemsPs.setInt(3, supply.getQuantityWH());
                    itemsPs.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

    @Override
    public void Remove(OrderPackageDTO o) {
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DataBase.getConnection()) {
            // First delete related items
            try (PreparedStatement ps = conn.prepareStatement(deleteItemsSql)) {
                ps.setInt(1, o.getOrderId());
                ps.executeUpdate();
            }

            // Then delete the order
            try (PreparedStatement ps = conn.prepareStatement(deleteOrderSql)) {
                ps.setInt(1, o.getOrderId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }


    @Override
    public void DeleteAll() {
        String deleteItemsSql = "DELETE FROM order_items";
        String deleteOrdersSql = "DELETE FROM orders";

        try (Connection conn = DataBase.getConnection()) {
            // First delete all items
            try (PreparedStatement ps = conn.prepareStatement(deleteItemsSql)) {
                ps.executeUpdate();
            }

            // Then delete all orders
            try (PreparedStatement ps = conn.prepareStatement(deleteOrdersSql)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception: " + e.getMessage());
        }
    }

}
