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
                    CREATE TABLE IF NOT EXISTS users(
                        id   INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS purchases(
                        id           INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id      INTEGER NOT NULL,
                        symbol       TEXT NOT NULL,
                        quantity     INTEGER NOT NULL,
                        price        REAL    NOT NULL,
                        purchased_at TEXT    NOT NULL,
                        FOREIGN KEY(user_id) REFERENCES users(id)
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