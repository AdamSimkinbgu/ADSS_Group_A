package Tests;

import org.junit.jupiter.api.*;

import Suppliers.DataLayer.DAOs.DataAccessException;
import Suppliers.DataLayer.DAOs.JdbcOrderItemLineDAO;
import Suppliers.DataLayer.util.Database;
import Suppliers.DTOs.OrderItemLineDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcOrderItemLineDAOTest {
   private Connection conn;
   private JdbcOrderItemLineDAO dao;

   @BeforeEach
   void setUp() throws SQLException {
      conn = Database.getConnection();
      dao = new JdbcOrderItemLineDAO();
   }

   @AfterEach
   void tearDown() throws SQLException {
      conn.close();
   }

   // @Test
   // void insertAndFetchLines_roundTrip() throws SQLException {
   // // Arrange: we need an existing order_id; assume order_id=1 was created in
   // seed
   // OrderItemLineDTO toInsert = new OrderItemLineDTO(
   // /* orderID = */ 1,
   // /* productId = */ 1,
   // /* supplierCatalogNumber = */ "CAT-001",
   // /* quantity = */ 5,
   // /* unitPrice = */ new BigDecimal("3.50"),
   // /* productName = */ "Widget A",
   // /* discount = */ new BigDecimal("0"));

   // // Act: insert & fetch new line
   // OrderItemLineDTO created = dao.addOrderItemLine(toInsert);
   // assertNotNull(created);
   // assertTrue(created.getOrderItemLineID() > 0);

   // List<OrderItemLineDTO> lines = dao.getAllOrderItemLinesForOrder(1);
   // assertFalse(lines.isEmpty());
   // boolean found = lines.stream()
   // .anyMatch(l -> l.getProductId() == created.getProductId());
   // assertTrue(found, "Inserted order item line must appear in the list for
   // order_id=1");
   // }

   @Test
   void insertingNegativeQuantity_throwsSQLException() {
      OrderItemLineDTO bad = new OrderItemLineDTO(
            /* orderID = */ 1,
            /* productId = */ 1,
            /* catalog# = */ "CAT-001",
            /* quantity = */ 0,
            /* unitPrice = */ new BigDecimal("3.00"),
            /* name = */ "Bad",
            /* discount = */ new BigDecimal("0"));
      assertThrows(DataAccessException.class, () -> dao.addOrderItemLine(bad),
            "Quantity=0 should violate CHECK(quantity>0) and throw SQLException");
   }
}