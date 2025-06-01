package Inventory.util;

import java.sql.*;

public class DataBase {

    private static final String DB_URL = "jdbc:sqlite:stocks.db";
    private static Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);


            try (Statement st = conn.createStatement()) {
                        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS sale (
                saleId INTEGER PRIMARY KEY,
                date TEXT,
                price REAL
            );
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS product (
                productId INTEGER PRIMARY KEY,
                name TEXT,
                manufacture TEXT,
                sale_price REAL
            );
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS saleProduct (
                saleId INTEGER,
                productId INTEGER,
                quantity INTEGER,
                PRIMARY KEY (saleId, productId),
                FOREIGN KEY (saleId) REFERENCES sale(saleId),
                FOREIGN KEY (productId) REFERENCES product(productId)
            );
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS inventory (
                productId INTEGER PRIMARY KEY,
                quantityStore INTEGER,
                quantityWarehouse INTEGER,
                quantityBad INTEGER,
                FOREIGN KEY (productId) REFERENCES product(productId)
            );
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS discountproduct (
                productId INTEGER,
                startDate TEXT,
                endDate TEXT,
                percent REAL,
                FOREIGN KEY (productId) REFERENCES product(productId)
            );
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS category (
                name TEXT PRIMARY KEY
            );
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS discountSale (
                saleName TEXT,
                startDate TEXT,
                endDate TEXT,
                percent REAL,
                FOREIGN KEY (saleName) REFERENCES category(name)
            );
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS subCategory (
                parentCategory TEXT,
                subCategory TEXT,
                PRIMARY KEY (parentCategory, subCategory),
                FOREIGN KEY (parentCategory) REFERENCES category(name),
                FOREIGN KEY (subCategory) REFERENCES category(name)
            );
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS productCategory (
                parentCategory TEXT,
                productId INTEGER,
                PRIMARY KEY (parentCategory, productId),
                FOREIGN KEY (parentCategory) REFERENCES category(name),
                FOREIGN KEY (productId) REFERENCES product(productId)
            );
        """);

            }
        } catch (Exception e) {

            throw new ExceptionInInitializerError(e);
        }
    }

    private DataBase() {}

    public static Connection getConnection() throws SQLException {
        return conn;
    }
}