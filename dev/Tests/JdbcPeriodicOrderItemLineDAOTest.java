import Suppliers.DTOs.PeriodicOrderItemLineDTO;
import Suppliers.DataLayer.DAOs.DataAccessException;
import Suppliers.DataLayer.DAOs.JdbcPeriodicOrderItemLineDAO;
import Suppliers.DataLayer.util.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JdbcPeriodicOrderItemLineDAOTest {
   private Connection conn;
   private JdbcPeriodicOrderItemLineDAO dao;

   @BeforeEach
   void setUp() throws SQLException {
      conn = Database.getConnection();
      dao = new JdbcPeriodicOrderItemLineDAO();
   }

   @AfterEach
   void tearDown() throws SQLException {
      conn.close();
   }

   // @Test
   // void insertAndFetchLines_roundTrip() throws SQLException {
   // // Arrange: periodic_order_id = 1 must exist
   // PeriodicOrderItemLineDTO toInsert = new PeriodicOrderItemLineDTO(
   // /* periodicOrderItemLineId = */ -1,
   // /* periodicOrderId = */ 1,
   // /* productId = */ 1,
   // /* quantity = */ 20);

   // // Act
   // PeriodicOrderItemLineDTO created = dao.addPeriodicOrderItemLine(toInsert);
   // assertNotNull(created);
   // assertTrue(created.getPeriodicOrderItemLineId() > 0);

   // List<PeriodicOrderItemLineDTO> all =
   // dao.getAllPeriodicOrderItemLinesForPeriodicOrder(1);

   // assertFalse(all.isEmpty());
   // boolean found = all.stream()
   // .anyMatch(l -> l.getPeriodicOrderItemLineId() ==
   // created.getPeriodicOrderItemLineId());
   // assertTrue(found, "Inserted periodic‐order item line should appear");
   // }

   @Test
   void insertingZeroQuantity_throwsSQLException() {
      PeriodicOrderItemLineDTO bad = new PeriodicOrderItemLineDTO(
            /* id = */ -1,
            /* periodicOrderId = */ 1,
            /* productId = */ 1,
            /* quantity = */ 0);
      assertThrows(DataAccessException.class, () -> dao.addPeriodicOrderItemLine(bad),
            "Quantity=0 should violate CHECK(quantity>0)");
   }
}