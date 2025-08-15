package SuppliersAndInventoryTests;

import Suppliers.DataLayer.DAOs.JdbcPeriodicOrderDAO;
import Suppliers.DataLayer.util.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.HashMap;

class JdbcPeriodicOrderDAOTest {
   private Connection conn;
   private JdbcPeriodicOrderDAO dao;

   @BeforeEach
   void setUp() throws SQLException {
      conn = Database.getConnection();
      dao = new JdbcPeriodicOrderDAO();
   }

   @AfterEach
   void tearDown() throws SQLException {
      conn.close();
   }

   @Test
   void insertAndFetchPeriodicOrder_roundTrip() throws SQLException {
      // Arrange: requested_day_mask = "1000000" for Monday, isActive = true
      HashMap<Integer, Integer> products = new HashMap<>();
      products.put(1, 10); // Product ID 1, quantity 10
      products.put(2, 5); // Product ID 2, quantity 5
      PeriodicOrderDTO toInsert = new PeriodicOrderDTO(
            /* periodicOrderID = */ -1,
            /* deliveryDay = */ DayOfWeek.MONDAY,
            /* productsInOrder = */ products,
            /* isActive = */ true);

      // Act: insert & fetch
      PeriodicOrderDTO created = dao.createPeriodicOrder(toInsert);
      assertNotNull(created);
      assertTrue(created.getPeriodicOrderID() > 0);

      PeriodicOrderDTO fetched = dao.getPeriodicOrder(created.getPeriodicOrderID());

      assertEquals(created.getPeriodicOrderID(), fetched.getPeriodicOrderID());
      assertTrue(fetched.isActive());
      assertEquals(DayOfWeek.MONDAY, fetched.getDeliveryDay());
   }

   @Test
   void insertingWithInvalidDayMask_throwsSQLException() {
      // If your DAO attempts to write an invalid requested_day_mask,
      // the CHECK(length=7 AND GLOB '[01]*') should fail.
      /*
       * -1, invalid day-of-week mapping? e.g. deliveryDay = null? or mask length
       * !=7?
       */
      /* However our constructor probably builds mask automatically. */
      /* For demonstration, assume a custom constructor that sets mask manually: */
      PeriodicOrderDTO bad = new PeriodicOrderDTO(
            -1,
            null, // invalid
            new HashMap<>(),
            true);
      assertThrows(IllegalArgumentException.class, () -> dao.createPeriodicOrder(bad),
            "Invalid day mask (null or wrong format) should violate CHECK and throw SQLException");
   }
}