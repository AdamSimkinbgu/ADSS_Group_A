package Tests;

import org.junit.jupiter.api.*;

import Suppliers.DataLayer.DAOs.DataAccessException;
import Suppliers.DataLayer.DAOs.JdbcOrderDAO;
import Suppliers.DataLayer.util.Database;
import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.AddressDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcOrderDAOTest {
   private Connection conn;
   private JdbcOrderDAO dao;

   @BeforeEach
   void setUp() throws SQLException {
      conn = Database.getConnection();
      Database.setDB_URL(Database.DB_TEST_URL);
      Database.seedDefaultData();
      dao = new JdbcOrderDAO();
   }

   @AfterEach
   void tearDown() throws SQLException {
      Database.deleteAllData();
      conn.close();
   }

   @Test
   void insertAndFetchOrder_roundTrip() throws SQLException {
      // Arrange: assume supplier_id=1 exists, build a minimal OrderDTO
      OrderDTO toInsert = new OrderDTO(
            /* supplierId = */ 1,
            /* orderDate = */ LocalDate.of(2025, 6, 1),
            /* address = */ new AddressDTO("123 Elm St", "Gotham", "10001"),
            /* contactPhoneNumber = */ "555-9999",
            /* items = */ List.of());

      // Act: insert & fetch
      OrderDTO created = dao.addOrder(toInsert);
      assertNotNull(created, "Created order should not be null");
      assertTrue(created.getOrderId() > 0);

      Optional<OrderDTO> fetchedOpt = dao.getOrder(created.getOrderId());
      assertTrue(fetchedOpt.isPresent());
      OrderDTO fetched = fetchedOpt.get();

      assertEquals(1, fetched.getSupplierId());
      // assertEquals("Gotham", fetched.getAddress().getCity());
      assertEquals(Suppliers.DTOs.Enums.OrderStatus.PENDING, fetched.getStatus());
   }

   // @Test
   // void insertingWithInvalidSupplier_throwsSQLException() {
   // OrderDTO bad = new OrderDTO(
   // /* orderId = */ -1,
   // /* supplierId = */ 9999,
   // /* name = */ "Nonexistent",
   // /* orderDate = */ LocalDate.now(),
   // /* creationDate = */ LocalDate.now(),
   // /* address = */ new AddressDTO("X St", "Y City", "000"),
   // /* contact = */ "555-0000",
   // /* no items = */ List.of(),
   // /* status = */ Suppliers.DTOs.Enums.OrderStatus.PENDING);
   // assertThrows(DataAccessException.class, () -> dao.addOrder(bad),
   // "Foreign key constraint should fail when supplier_id=9999 does not exist");
   // }
}