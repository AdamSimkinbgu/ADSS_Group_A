package Suppliers.DataLayer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public final class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static final String DB_URL = "jdbc:sqlite:supply.db";
    private static Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            log.info("Connected to SQLite at {}", DB_URL);

            try (Statement st = conn.createStatement()) {
                // enforce FK rules in SQLite
                st.executeUpdate("PRAGMA foreign_keys = ON;");

                /* ───────────────────────── suppliers ───────────────────────── */
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS suppliers(
                                supplier_id        INTEGER PRIMARY KEY AUTOINCREMENT,
                                name               TEXT    NOT NULL,
                                tax_number         TEXT    NOT NULL,
                                self_supply        INTEGER NOT NULL CHECK(self_supply IN (0,1)),
                                supply_days_mask   TEXT    NOT NULL
                                                  CHECK(length(supply_days_mask)=7
                                                        AND supply_days_mask GLOB '[01]*'),
                                lead_supply_days   INTEGER NOT NULL CHECK(lead_supply_days >= 0),
                                street             TEXT    NOT NULL,
                                city               TEXT    NOT NULL,
                                building_number    TEXT    NOT NULL,
                                bank_account_number TEXT   NOT NULL,
                                payment_method     TEXT    NOT NULL
                                                  CHECK(payment_method IN
                                                        ('CASH','CASH_ON_DELIVERY',
                                                         'CREDIT_CARD','BANK_TRANSFER')),
                                payment_term       TEXT    NOT NULL
                                                  CHECK(payment_term IN ('N30','N60','N90','COD')),
                                UNIQUE(tax_number)
                            );
                        """);
                // create a trigger to detect deletions and adjust the supplier_id accordingly
                // st.executeUpdate("""
                // CREATE TRIGGER IF NOT EXISTS trg_supplier_autoincrement
                // AFTER INSERT ON suppliers
                // FOR EACH ROW
                // BEGIN
                // UPDATE suppliers
                // SET supplier_id = (
                // COALESCE(
                // (SELECT MAX(supplier_id) + 1 FROM suppliers),
                // 1
                // )
                // )
                // WHERE rowid = NEW.rowid; -- patch only the row we just inserted
                // END;
                // """);
                // st.executeUpdate("""
                // CREATE TRIGGER IF NOT EXISTS trg_supplier_reseq_after_delete
                // AFTER DELETE ON suppliers
                // FOR EACH ROW
                // BEGIN
                // UPDATE suppliers
                // SET supplier_id = supplier_id - 1
                // WHERE supplier_id > OLD.supplier_id;
                // END;
                // """);
                // st.executeUpdate("""
                // CREATE INDEX IF NOT EXISTS idx_suppliers_tax_number
                // ON suppliers(tax_number);
                // """);

                /* ───────────────────────── contacts ───────────────────────── */
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS contact_info(
                                supplier_id INTEGER NOT NULL,
                                phone       TEXT    NOT NULL,
                                name        TEXT    NOT NULL,
                                email       TEXT    NOT NULL,
                                PRIMARY KEY (supplier_id, phone),
                                UNIQUE (supplier_id, phone),
                                FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
                            );
                        """);

                /* ───────────────────── supplier_products ───────────────────── */
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS supplier_products(
                                product_id             INTEGER PRIMARY KEY AUTOINCREMENT,
                                supplier_id            INTEGER NOT NULL,
                                supplier_catalog_number TEXT   NOT NULL,
                                manufacturer_name      TEXT   NOT NULL,
                                name                   TEXT   NOT NULL,
                                price                  REAL   NOT NULL CHECK(price >= 0),
                                weight                 REAL   NOT NULL CHECK(weight >= 0),
                                days_to_expiry         INTEGER NOT NULL CHECK(days_to_expiry >= 0),
                                FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
                            );
                        """);
                // st.executeUpdate("""
                // CREATE INDEX IF NOT EXISTS idx_supplier_products_supplier
                // ON supplier_products(supplier_id);
                // """);
                // // create a trigger to detect deletions and adjust the product_id accordingly
                // st.executeUpdate("""
                // CREATE TRIGGER IF NOT EXISTS trg_product_autoincrement
                // AFTER INSERT ON supplier_products
                // FOR EACH ROW
                // BEGIN
                // UPDATE supplier_products
                // SET product_id = (
                // COALESCE(
                // (SELECT MAX(product_id) + 1 FROM supplier_products),
                // 1
                // )
                // )
                // WHERE rowid = NEW.rowid; -- patch only the row we just inserted
                // END;
                // """);
                // st.executeUpdate("""
                // CREATE TRIGGER IF NOT EXISTS trg_product_reseq_after_delete
                // AFTER DELETE ON supplier_products
                // FOR EACH ROW
                // BEGIN
                // UPDATE supplier_products
                // SET product_id = product_id - 1
                // WHERE product_id > OLD.product_id;
                // END;
                // """);

                /* ───────────────────────── agreements ──────────────────────── */
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS agreements(
                                agreement_id          INTEGER PRIMARY KEY AUTOINCREMENT,
                                supplier_id           INTEGER NOT NULL,
                                agreement_start_date  TEXT    NOT NULL,
                                agreement_end_date    TEXT    NOT NULL,
                                valid                 INTEGER NOT NULL DEFAULT 1
                                                     CHECK(valid IN (0,1)),
                                CHECK(agreement_end_date >= agreement_start_date),
                                FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
                            );
                        """);
                // st.executeUpdate("""
                // CREATE TRIGGER IF NOT EXISTS trg_agreement_autoincrement
                // AFTER INSERT ON agreements
                // FOR EACH ROW
                // BEGIN
                // UPDATE agreements
                // SET agreement_id = (
                // COALESCE(
                // (SELECT MAX(agreement_id) + 1 FROM agreements),
                // 1
                // )
                // )
                // WHERE rowid = NEW.rowid; -- patch only the row we just inserted
                // END;
                // """);
                // st.executeUpdate("""
                // CREATE TRIGGER IF NOT EXISTS trg_agreement_reseq_after_delete
                // AFTER DELETE ON agreements
                // FOR EACH ROW
                // BEGIN
                // UPDATE agreements
                // SET agreement_id = agreement_id - 1
                // WHERE agreement_id > OLD.agreement_id;
                // END;
                // """);
                // st.executeUpdate("""
                // CREATE INDEX IF NOT EXISTS idx_agreements_supplier
                // ON agreements(supplier_id);
                // """);

                /* ───────────────────────── boq_items ───────────────────────── */
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS boq_items(
                                agreement_id     INTEGER NOT NULL,
                                line_in_bill     INTEGER,
                                product_id       INTEGER NOT NULL,
                                quantity         INTEGER NOT NULL CHECK(quantity > 0),
                                discount_percent REAL    NOT NULL
                                                CHECK(discount_percent BETWEEN 0 AND 100),
                                PRIMARY KEY (agreement_id, line_in_bill),
                                FOREIGN KEY(agreement_id) REFERENCES agreements(agreement_id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
                                FOREIGN KEY(product_id)   REFERENCES supplier_products(product_id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
                            );
                        """);
                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_boq_autoline
                            AFTER INSERT ON boq_items
                            FOR EACH ROW
                            WHEN NEW.line_in_bill IS NULL
                            BEGIN
                                UPDATE boq_items
                                SET    line_in_bill = (
                                        COALESCE(
                                            (SELECT MAX(line_in_bill) + 1
                                                FROM boq_items
                                                WHERE agreement_id = NEW.agreement_id),
                                            1
                                        )
                                    )
                                WHERE  rowid = NEW.rowid;   -- patch only the row we just inserted
                            END;
                        """);
                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_boq_reseq_after_delete
                            AFTER DELETE ON boq_items
                            FOR EACH ROW
                            BEGIN
                                UPDATE boq_items
                                SET line_in_bill = line_in_bill - 1
                                WHERE agreement_id = OLD.agreement_id
                                AND line_in_bill  > OLD.line_in_bill;
                            END;
                        """);

                log.info("Ensured database schema exists");
            }
        } catch (Exception e) {
            log.error("Database initialization failed", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void deleteAllData() {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM boq_items;");
            st.executeUpdate("DELETE FROM agreements;");
            st.executeUpdate("DELETE FROM supplier_products;");
            st.executeUpdate("DELETE FROM contact_info;");
            st.executeUpdate("DELETE FROM suppliers;");
            st.executeUpdate("DELETE FROM sqlite_sequence;"); // reset autoincrement
            // counters
            log.info("All data deleted from the database");
        } catch (SQLException e) {
            log.error("Failed to delete all data", e);
        }
    }

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return conn;
    }
}