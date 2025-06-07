package Suppliers.DataLayer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public final class Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
    private static String DB_URL = "jdbc:sqlite:supply.db";
    private static Connection conn;
    public static final String DB_TEST_URL = "jdbc:sqlite:supplyTest.db";
    public static final String DB_DEV_URL = "jdbc:sqlite:supply.db";

    public static String getURL(String env) {
        if (env == null || env.isEmpty()) {
            return DB_URL;
        }
        switch (env.toLowerCase()) {
            case "test":
                return DB_TEST_URL;
            case "dev":
                return DB_DEV_URL;
            default:
                LOGGER.warn("Unknown environment: {}, using default DB URL", env);
                return DB_URL;
        }
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:supply.db");
            LOGGER.info("Connected to SQLite at {}", "jdbc:sqlite:supply.db");
            System.out.println("---------- SQLITE FILE INFO (after connection) ----------");
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
                                PRIMARY KEY (supplier_id, name),
                                UNIQUE (supplier_id, name),
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
                // ───────────────────────── orders ─────────────────────────
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS orders(
                               order_id             INTEGER PRIMARY KEY AUTOINCREMENT,
                               supplier_id          INTEGER NOT NULL,
                               order_date           TEXT    NOT NULL,
                               creation_date        TEXT    NOT NULL,
                               delivery_date        TEXT    NULL,
                               status               TEXT    NOT NULL
                                                     CHECK(status IN
                                                        ('PENDING','SENT','DELIVERED','COMPLETED','CANCELLED')),
                               periodic_order_id    INTEGER NULL,
                               FOREIGN KEY(supplier_id)
                                   REFERENCES suppliers(supplier_id) ON DELETE CASCADE,
                               FOREIGN KEY(periodic_order_id)
                                   REFERENCES periodic_orders(periodic_order_id) ON DELETE SET NULL
                            );
                        """);

                // ─────────────────────── order_item_lines ───────────────────────
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS order_item_lines(
                               order_id            INTEGER NOT NULL,
                               line_number         INTEGER,
                               product_id          INTEGER NOT NULL,
                               quantity            INTEGER NOT NULL CHECK(quantity > 0),
                               unit_price          REAL    NOT NULL CHECK(unit_price >= 0),
                               discount_pct        REAL    NOT NULL
                                                     CHECK(discount_pct BETWEEN 0 AND 1),
                               PRIMARY KEY (order_id, line_number),
                               FOREIGN KEY(order_id)
                                   REFERENCES orders(order_id) ON DELETE CASCADE,
                               FOREIGN KEY(product_id)
                                   REFERENCES supplier_products(product_id) ON DELETE RESTRICT
                            );
                        """);
                st.executeUpdate("""
                            CREATE INDEX IF NOT EXISTS idx_order_lines_order
                              ON order_item_lines(order_id);
                        """);

                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_orderitem_autoline
                            AFTER INSERT ON order_item_lines
                            FOR EACH ROW
                            WHEN NEW.line_number IS NULL
                            BEGIN
                            UPDATE order_item_lines
                                SET line_number = (
                                COALESCE(
                                    (SELECT MAX(line_number) + 1
                                    FROM order_item_lines
                                    WHERE order_id = NEW.order_id),
                                    1
                                )
                                )
                            WHERE rowid = NEW.rowid;  -- only patch the row we just inserted
                            END;
                        """);
                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_orderitem_reseq_after_delete
                            AFTER DELETE ON order_item_lines
                            FOR EACH ROW
                            BEGIN
                            UPDATE order_item_lines
                                SET line_number = line_number - 1
                                WHERE order_id = OLD.order_id
                                AND line_number > OLD.line_number;
                            END;
                        """);

                // ───────────────────── periodic_orders ─────────────────────
                st.executeUpdate(
                        """
                                    CREATE TABLE IF NOT EXISTS periodic_orders(
                                        periodic_order_id   INTEGER PRIMARY KEY AUTOINCREMENT,
                                        delivery_day           TEXT    NOT NULL,

                                        is_active           INTEGER NOT NULL DEFAULT 1
                                                            CHECK(is_active IN (0,1))
                                        -- CHECK(end_date >= start_date), -- we dont need this, because we dont have start and end dates
                                    );
                                """);

                // ───────────────── periodic_order_item_lines ─────────────────
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS periodic_order_item_lines(
                               periodic_order_id   INTEGER NOT NULL,
                               line_number         INTEGER,
                               product_id          INTEGER NOT NULL,
                               quantity            INTEGER NOT NULL CHECK(quantity > 0),
                               PRIMARY KEY (periodic_order_id, line_number),
                               FOREIGN KEY(periodic_order_id)
                                   REFERENCES periodic_orders(periodic_order_id) ON DELETE CASCADE,
                               FOREIGN KEY(product_id)
                                   REFERENCES supplier_products(product_id) ON DELETE RESTRICT
                            );
                        """);
                st.executeUpdate("""
                            CREATE INDEX IF NOT EXISTS idx_periodic_lines_product
                              ON periodic_order_item_lines(product_id);
                        """);

                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_periodic_lines_autonum
                            AFTER INSERT ON periodic_order_item_lines
                            FOR EACH ROW
                            WHEN NEW.line_number IS NULL
                            BEGIN
                                UPDATE periodic_order_item_lines
                                SET line_number =
                                    COALESCE(
                                        (
                                        SELECT MAX(line_number) + 1
                                            FROM periodic_order_item_lines
                                            WHERE periodic_order_id = NEW.periodic_order_id
                                        ),
                                        1
                                    )
                                WHERE rowid = NEW.rowid;
                            END;
                        """);

                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_periodic_lines_reseq_after_delete
                            AFTER DELETE ON periodic_order_item_lines
                            FOR EACH ROW
                            BEGIN
                                UPDATE periodic_order_item_lines
                                   SET line_number = line_number - 1
                                 WHERE periodic_order_id = OLD.periodic_order_id
                                   AND line_number > OLD.line_number;
                            END;
                        """);

                // ───────────────── inventory_notifications (optional) ─────────────────
                ResultSet rs = st.executeQuery("PRAGMA foreign_keys;");
                if (rs.next()) {
                    int foreignKeysEnabled = rs.getInt(1);
                    if (foreignKeysEnabled == 1) {
                        LOGGER.info("Foreign keys are enabled in the database");
                    } else {
                        LOGGER.warn("Foreign keys are NOT enabled in the database");
                    }
                }
                LOGGER.info("Ensured database schema exists");
            }
            conn.close();
        } catch (Exception e) {
            LOGGER.error("Database initialization failed", e);
            throw new ExceptionInInitializerError(e);
        }
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:supplyTest.db");
            LOGGER.info("Connected to SQLite at {}", "jdbc:sqlite:supplyTest.db");
            System.out.println("---------- SQLITE FILE INFO (after connection) ----------");
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
                                PRIMARY KEY (supplier_id, name),
                                UNIQUE (supplier_id, name),
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
                // ───────────────────────── orders ─────────────────────────
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS orders(
                               order_id             INTEGER PRIMARY KEY AUTOINCREMENT,
                               supplier_id          INTEGER NOT NULL,
                               order_date           TEXT    NOT NULL,
                               creation_date        TEXT    NOT NULL,
                               delivery_date        TEXT    NULL,
                               status               TEXT    NOT NULL
                                                     CHECK(status IN
                                                        ('PENDING','SENT','DELIVERED','COMPLETED','CANCELLED')),
                               periodic_order_id    INTEGER NULL,
                               FOREIGN KEY(supplier_id)
                                   REFERENCES suppliers(supplier_id) ON DELETE CASCADE,
                               FOREIGN KEY(periodic_order_id)
                                   REFERENCES periodic_orders(periodic_order_id) ON DELETE SET NULL
                            );
                        """);

                // ─────────────────────── order_item_lines ───────────────────────
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS order_item_lines(
                               order_id            INTEGER NOT NULL,
                               line_number         INTEGER,
                               product_id          INTEGER NOT NULL,
                               quantity            INTEGER NOT NULL CHECK(quantity > 0),
                               unit_price          REAL    NOT NULL CHECK(unit_price >= 0),
                               discount_pct        REAL    NOT NULL
                                                     CHECK(discount_pct BETWEEN 0 AND 1),
                               PRIMARY KEY (order_id, line_number),
                               FOREIGN KEY(order_id)
                                   REFERENCES orders(order_id) ON DELETE CASCADE,
                               FOREIGN KEY(product_id)
                                   REFERENCES supplier_products(product_id) ON DELETE RESTRICT
                            );
                        """);
                st.executeUpdate("""
                            CREATE INDEX IF NOT EXISTS idx_order_lines_order
                              ON order_item_lines(order_id);
                        """);

                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_orderitem_autoline
                            AFTER INSERT ON order_item_lines
                            FOR EACH ROW
                            WHEN NEW.line_number IS NULL
                            BEGIN
                            UPDATE order_item_lines
                                SET line_number = (
                                COALESCE(
                                    (SELECT MAX(line_number) + 1
                                    FROM order_item_lines
                                    WHERE order_id = NEW.order_id),
                                    1
                                )
                                )
                            WHERE rowid = NEW.rowid;  -- only patch the row we just inserted
                            END;
                        """);
                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_orderitem_reseq_after_delete
                            AFTER DELETE ON order_item_lines
                            FOR EACH ROW
                            BEGIN
                            UPDATE order_item_lines
                                SET line_number = line_number - 1
                                WHERE order_id = OLD.order_id
                                AND line_number > OLD.line_number;
                            END;
                        """);

                // ───────────────────── periodic_orders ─────────────────────
                st.executeUpdate(
                        """
                                    CREATE TABLE IF NOT EXISTS periodic_orders(
                                        periodic_order_id   INTEGER PRIMARY KEY AUTOINCREMENT,
                                        delivery_day           TEXT    NOT NULL,

                                        is_active           INTEGER NOT NULL DEFAULT 1
                                                            CHECK(is_active IN (0,1))
                                        -- CHECK(end_date >= start_date), -- we dont need this, because we dont have start and end dates
                                    );
                                """);

                // ───────────────── periodic_order_item_lines ─────────────────
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS periodic_order_item_lines(
                               periodic_order_id   INTEGER NOT NULL,
                               line_number         INTEGER,
                               product_id          INTEGER NOT NULL,
                               quantity            INTEGER NOT NULL CHECK(quantity > 0),
                               PRIMARY KEY (periodic_order_id, line_number),
                               FOREIGN KEY(periodic_order_id)
                                   REFERENCES periodic_orders(periodic_order_id) ON DELETE CASCADE,
                               FOREIGN KEY(product_id)
                                   REFERENCES supplier_products(product_id) ON DELETE RESTRICT
                            );
                        """);
                st.executeUpdate("""
                            CREATE INDEX IF NOT EXISTS idx_periodic_lines_product
                              ON periodic_order_item_lines(product_id);
                        """);

                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_periodic_lines_autonum
                            AFTER INSERT ON periodic_order_item_lines
                            FOR EACH ROW
                            WHEN NEW.line_number IS NULL
                            BEGIN
                                UPDATE periodic_order_item_lines
                                SET line_number =
                                    COALESCE(
                                        (
                                        SELECT MAX(line_number) + 1
                                            FROM periodic_order_item_lines
                                            WHERE periodic_order_id = NEW.periodic_order_id
                                        ),
                                        1
                                    )
                                WHERE rowid = NEW.rowid;
                            END;
                        """);

                st.executeUpdate("""
                            CREATE TRIGGER IF NOT EXISTS trg_periodic_lines_reseq_after_delete
                            AFTER DELETE ON periodic_order_item_lines
                            FOR EACH ROW
                            BEGIN
                                UPDATE periodic_order_item_lines
                                   SET line_number = line_number - 1
                                 WHERE periodic_order_id = OLD.periodic_order_id
                                   AND line_number > OLD.line_number;
                            END;
                        """);

                // ───────────────── inventory_notifications (optional) ─────────────────
                ResultSet rs = st.executeQuery("PRAGMA foreign_keys;");
                if (rs.next()) {
                    int foreignKeysEnabled = rs.getInt(1);
                    if (foreignKeysEnabled == 1) {
                        LOGGER.info("Foreign keys are enabled in the test database");
                    } else {
                        LOGGER.warn("Foreign keys are NOT enabled in the test database");
                    }
                }
                LOGGER.info("Ensured test database schema exists");
            }
            conn.close();
        } catch (Exception e) {
            LOGGER.error("Database initialization failed", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void deleteAllData() {
        try (Statement st = Database.getConnection().createStatement()) {
            st.executeUpdate("DELETE FROM periodic_order_item_lines;");
            st.executeUpdate("DELETE FROM boq_items;");
            st.executeUpdate("DELETE FROM order_item_lines;");
            st.executeUpdate("DELETE FROM agreements;");
            st.executeUpdate("DELETE FROM orders;");
            st.executeUpdate("DELETE FROM periodic_orders;");
            st.executeUpdate("DELETE FROM supplier_products;");
            st.executeUpdate("DELETE FROM contact_info;");
            st.executeUpdate("DELETE FROM suppliers;");
            st.executeUpdate("DELETE FROM sqlite_sequence;"); // reset autoincrement
            // counters
            LOGGER.info("All data deleted from the database");
        } catch (SQLException e) {
            LOGGER.error("Failed to delete all data", e);
        }
    }

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        // make sure the connection is using the correct URL
        if (conn == null || conn.isClosed() || !conn.getMetaData().getURL().equals(DB_URL)) {
            // close the previous connection if it exists
            if (conn != null && !conn.isClosed()) {
                conn.close();
                LOGGER.info("Closed previous connection to SQLite at {}", DB_URL);
            }
            // create a new connection
            conn = DriverManager.getConnection(DB_URL);
            LOGGER.info("Reconnected to SQLite at {}", DB_URL);
        }
        return conn;
    }

    public static String getDBUrl() {
        return DB_URL;
    }

    public static void setDB_URL(String dbUrl) {
        if (dbUrl != null && !dbUrl.isBlank()) {
            DB_URL = dbUrl;
            LOGGER.info("Database URL set to {}", DB_URL);
        } else {
            LOGGER.warn("Invalid database URL provided, keeping the previous one: {}", DB_URL);
        }
    }

    /**
     * Populates “suppliers”, “contacts”, “supplier_products”, “agreements” and
     * “boq_items”
     * with default rows—using INSERT OR IGNORE so that if you restart the app
     * multiple times,
     * you won’t duplicate existing seed data.
     */
    public static void seedDefaultData() {
        deleteAllData();
        try (Statement st = Database.getConnection().createStatement()) {
            // ────────────── 1. PRAGMA ──────────────
            st.executeUpdate("PRAGMA foreign_keys = ON;");

            // ────────────── 2. suppliers ──────────────
            st.executeUpdate(
                    """
                                INSERT OR IGNORE INTO suppliers(
                                    name, tax_number, self_supply, supply_days_mask, lead_supply_days,
                                    street, city, building_number, bank_account_number, payment_method, payment_term
                                ) VALUES
                                    ('Supplier 1','512345678',    1,'1010100',0,'Street 1','City 1','1','123456','CREDIT_CARD','N30'),
                                    ('Supplier 2','587654321',    0,'0000000',2,'Street 2','City 2','22','412353','CASH','COD'),
                                    ('Supplier 3','518273645',    1,'0000010',1,'Street 3','City 3','3','162534','CASH_ON_DELIVERY','N60'),
                                    ('Supplier 4','123987654',    0,'1110000',3,'Oak Street','Haifa','47','987654','BANK_TRANSFER','N90'),
                                    ('Supplier 5','999888777',    1,'0000011',0,'Pine Avenue','Beer-Sheva','12','555666','CASH_ON_DELIVERY','COD')
                                ;
                            """);
            LOGGER.info("Inserted default suppliers");
            // ────────────── 3. contacts ──────────────
            st.executeUpdate("""
                        INSERT OR IGNORE INTO contact_info(
                            supplier_id, phone, name, email
                        ) VALUES
                            (1,'054-000-0001','Danny','danny@example.com'),
                            (4,'055-000-0004','Alice Green','alice.green@example.com'),
                            (5,'055-000-0005','Bob Silver','bob.silver@example.com'),
                            (5,'055-000-0006','Carol Gold','carol.gold@example.com')
                        ;
                    """);
            LOGGER.info("Inserted default contacts");
            // ────────────── 4. supplier_products ──────────────
            st.executeUpdate("""
                        INSERT OR IGNORE INTO supplier_products(
                            supplier_id, supplier_catalog_number, manufacturer_name, name, price, weight, days_to_expiry
                        ) VALUES
                            (1,'123456','Yotvata',     'Milk 3%',           10.00, 1.0, 30),
                            (1,'654321','Telma',       'Cornflacks Cariot',  20.00, 2.0, 60),
                            (2,'789012','Tnuva',       'Cottage Cheese',     15.00, 1.5, 45),
                            (2,'210987','DeliMeat',    'Pastrami Sandwich',  25.00, 3.0, 90),
                            (3,'345678','Tnuva',       'Milk 3%',            30.00, 2.5, 15),
                            (3,'876543','Ossem',       'Bamba',              40.00, 4.0, 120),
                            (4,'444111','Galil',       'Olive Oil 1L',       25.00, 1.2, 180),
                            (4,'444112','Galil',       'Zaatar Mix 200g',   12.50,  0.3,  90),
                            (4,'444113','Galil',       'Pita Bread Pack (5)', 5.00,  0.5,   2),
                            (5,'555111','Neve',        'Fresh Milk 2L',       15.00,  2.0,   7),
                            (5,'555112','Neve',        'Labneh 250g',         8.00,  0.4,  14)
                        ;
                    """);
            LOGGER.info("Inserted default supplier products");
            // ────────────── 5. agreements ──────────────
            st.executeUpdate("""
                        INSERT OR IGNORE INTO agreements(
                            supplier_id, agreement_start_date, agreement_end_date, valid
                        ) VALUES
                            (1,'2025-06-01','2025-06-20',1),
                            (2,'2025-06-01','2025-07-05',1),
                            (3,'2025-07-01','2025-07-31',1),
                            (4,'2025-08-01','2025-12-31',1),
                            (5,'2025-06-15','2025-09-15',1),
                            (1,'2025-06-01','2025-12-31',1),
                            (2,'2025-06-01','2025-12-31',1),
                            (3,'2025-07-01','2025-12-31',1),
                            (4,'2025-08-01','2025-12-31',1),
                            (5,'2025-06-15','2025-12-31',1)
                        ;
                    """);
            LOGGER.info("Inserted default agreements");
            // ────────────── 6. boq_items ──────────────
            st.executeUpdate("""
                        INSERT OR IGNORE INTO boq_items(
                            agreement_id, line_in_bill, product_id, quantity, discount_percent
                        ) VALUES
                            (1,1,1,100,0.95),  -- “Milk 3%” from supplier 1
                            (1,2,2,50,0.9),  -- “Cornflacks Cariot” from supplier 1
                            (1,3,3,75,0.925),   -- “Cottage Cheese” from supplier 1
                            (2,1,3,75,0.925),   -- “Cottage Cheese” from supplier 2
                            (2,2,4,30,0.875),  -- “Pastrami Sandwich” from supplier 2
                            (3,1,5,   200,   0.9),  -- “Cottage Cheese” from supplier 2
                            (3,2,6,   120,    0.95),  -- “Pastrami Sandwich” from supplier 2
                            (3,3,1,    80,    0.78),  -- “Milk 3%” (product_id = 1, from supplier 1)
                            (4,1,7,   150,   0.8),  -- “Olive Oil 1L” (product_id = 7)
                            (4,2,8,   300,    0.6),  -- “Zaatar Mix 200g” (product_id = 8)
                            (4,3,9,   500,    0.5),   -- “Pita Bread Pack (5)” (product_id = 9)
                            (5,1,10,   250,    0.7),  -- “Fresh Milk 2L” (product_id = 10)
                            (5,2,11,   180,    0.75)   -- “Labneh 250g” (product_id = 11)
                        ;
                    """);
            LOGGER.info("Inserted default BOQ items");
            LOGGER.warn("Orders and periodic orders are not seeded by default");
        } catch (SQLException e) {
            LOGGER.error("Failed to seed default data", e);
        }
    }

    public static void provideStatisticsAboutTheCurrentStateOfTheWholeDatabase() {
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");
            System.out.println("Current database tables:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("name"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve database statistics", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM suppliers;");
            if (rs.next()) {
                System.out.println("Total suppliers: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve supplier count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM supplier_products;");
            if (rs.next()) {
                System.out.println("Total supplier products: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve supplier product count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM agreements;");
            if (rs.next()) {
                System.out.println("Total agreements: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve agreement count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM boq_items;");
            if (rs.next()) {
                System.out.println("Total BOQ items: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve BOQ item count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM orders;");
            if (rs.next()) {
                System.out.println("Total orders: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve order count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM periodic_orders;");
            if (rs.next()) {
                System.out.println("Total periodic orders: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve periodic order count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM contact_info;");
            if (rs.next()) {
                System.out.println("Total contacts: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve contact count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM order_item_lines;");
            if (rs.next()) {
                System.out.println("Total order item lines: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve order item line count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM periodic_order_item_lines;");
            if (rs.next()) {
                System.out.println("Total periodic order item lines: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve periodic order item line count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM sqlite_sequence;");
            if (rs.next()) {
                System.out.println("Total autoincrement sequences: " + rs.getInt("total"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve autoincrement sequence count", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT name, sql FROM sqlite_master WHERE type='table';");
            System.out.println("Table definitions:");
            while (rs.next()) {
                System.out.println(rs.getString("name") + ": " + rs.getString("sql"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve table definitions", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM suppliers LIMIT 5;");
            System.out.println("Sample suppliers data:");
            while (rs.next()) {
                System.out.println(rs.getInt("supplier_id") + ": " + rs.getString("name"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample suppliers data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM supplier_products LIMIT 5;");
            System.out.println("Sample supplier products data:");
            while (rs.next()) {
                System.out.println(rs.getInt("product_id") + ": " + rs.getString("name"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample supplier products data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM agreements LIMIT 5;");
            System.out.println("Sample agreements data:");
            while (rs.next()) {
                System.out.println(rs.getInt("agreement_id") + ": " + rs.getString("agreement_start_date"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample agreements data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM boq_items LIMIT 5;");
            System.out.println("Sample BOQ items data:");
            while (rs.next()) {
                System.out.println(rs.getInt("agreement_id") + ": " + rs.getInt("line_in_bill"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample BOQ items data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM orders LIMIT 5;");
            System.out.println("Sample orders data:");
            while (rs.next()) {
                System.out.println(rs.getInt("order_id") + ": " + rs.getString("order_date"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample orders data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM periodic_orders LIMIT 5;");
            System.out.println("Sample periodic orders data:");
            while (rs.next()) {
                System.out.println(rs.getInt("periodic_order_id") + ": " + rs.getString("requested_day_mask"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample periodic orders data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM contact_info LIMIT 5;");
            System.out.println("Sample contacts data:");
            while (rs.next()) {
                System.out.println(rs.getInt("supplier_id") + ": " + rs.getString("name"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample contacts data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM order_item_lines LIMIT 5;");
            System.out.println("Sample order item lines data:");
            while (rs.next()) {
                System.out.println(rs.getInt("order_id") + ": " + rs.getInt("line_number"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample order item lines data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM periodic_order_item_lines LIMIT 5;");
            System.out.println("Sample periodic order item lines data:");
            while (rs.next()) {
                System.out.println(rs.getInt("periodic_order_id") + ": " + rs.getInt("line_number"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample periodic order item lines data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM sqlite_sequence LIMIT 5;");
            System.out.println("Sample autoincrement sequences data:");
            while (rs.next()) {
                System.out.println(rs.getString("name") + ": " + rs.getInt("seq"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve sample autoincrement sequences data", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT name, sql FROM sqlite_master WHERE type='trigger';");
            System.out.println("Triggers in the database:");
            while (rs.next()) {
                System.out.println(rs.getString("name") + ": " + rs.getString("sql"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve triggers", e);
        }
        try (Statement st = Database.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM sqlite_master WHERE type='view';");
            System.out.println("Views in the database:");
            while (rs.next()) {
                System.out.println(rs.getString("name") + ": " + rs.getString("sql"));
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve views", e);
        }
    }
}
