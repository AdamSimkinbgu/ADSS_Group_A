package Inventory.util;

import java.sql.*;

public class DataBase {

    private static final String DB_URL = "jdbc:sqlite:inventory.db";
    private static Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);


            try (Statement st = conn.createStatement()) {

                // Create tables if they do not exist
                // Products table
                st.executeUpdate("""                 
                CREATE TABLE IF NOT EXISTS products (
                        product_id INTEGER PRIMARY KEY,
                        product_name TEXT NOT NULL,
                        manufacturer_name TEXT NOT NULL,
                        product_price REAL NOT NULL,
                        minimal_amount_store INTEGER NOT NULL,
                        minimal_amount_stock INTEGER NOT NULL,
                        warehouse_shelf_x INTEGER NOT NULL,
                        warehouse_shelf_y INTEGER NOT NULL,
                        store_shelf_x INTEGER NOT NULL,
                        store_shelf_y INTEGER NOT NULL
                        );""");

                // Categories table
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS categories (
                        name TEXT PRIMARY KEY
                );""");

                // Subcategories relationship table
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS subcategories (
                        parent_name TEXT NOT NULL,
                        child_name TEXT NOT NULL,
                        PRIMARY KEY (parent_name, child_name),
                        FOREIGN KEY (parent_name) REFERENCES categories(name),
                        FOREIGN KEY (child_name) REFERENCES categories(name)
                );""");

                // Category_Products relationship table
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS category_products (
                        category_name TEXT NOT NULL,
                        product_id INTEGER NOT NULL,
                        PRIMARY KEY (category_name, product_id),
                        FOREIGN KEY (category_name) REFERENCES categories(name),
                        FOREIGN KEY (product_id) REFERENCES products(product_id)
                );""");

                //-- Discounts table
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS discounts (
                        discount_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        percent REAL NOT NULL,
                        discount_start TEXT NOT NULL,  -- Stores as YYYY-MM-DD
                        discount_end TEXT NOT NULL,    -- Stores as YYYY-MM-DD
                        product_id INTEGER,
                        category_name TEXT,
                        FOREIGN KEY (product_id) REFERENCES products(product_id),
                        FOREIGN KEY (category_name) REFERENCES categories(name)
                );""");

                //-- Supplies table
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS supplies (
                        supply_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        product_id INTEGER NOT NULL,
                        expire_date TEXT NOT NULL,     -- Stores as YYYY-MM-DD
                        quantity_warehouse INTEGER NOT NULL,
                        quantity_store INTEGER NOT NULL,
                        quantity_bad INTEGER NOT NULL,
                        FOREIGN KEY (product_id) REFERENCES products(product_id)
                );""");

                //-- Sales table
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS sales (
                        sale_id INTEGER PRIMARY KEY AUTOINCREMENT,
                sale_price REAL NOT NULL,
                sale_date TEXT NOT NULL        -- Stores as YYYY-MM-DD
                );""");

                //-- Sale_Items relationship table
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS sale_items (
                        sale_id INTEGER NOT NULL,
                        product_id INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        PRIMARY KEY (sale_id, product_id),
                        FOREIGN KEY (sale_id) REFERENCES sales(sale_id),
                        FOREIGN KEY (product_id) REFERENCES products(product_id)
                );""");

                //-- Orders table
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS orders (
                        order_id INTEGER PRIMARY KEY ,
                        delivery_date TEXT NOT NULL    -- Stores as YYYY-MM-DD
                );""");

                //-- Order_Items relationship table
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS order_items (
                        order_id INTEGER NOT NULL,
                        product_id INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        PRIMARY KEY (order_id, product_id),
                        FOREIGN KEY (order_id) REFERENCES orders(order_id),
                        FOREIGN KEY (product_id) REFERENCES products(product_id)
                );""");


            }
        } catch (Exception e) {

            throw new ExceptionInInitializerError(e);
        }
    }

    private DataBase() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}