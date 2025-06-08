package Tests;

import org.junit.jupiter.api.*;

import Suppliers.DataLayer.DAOs.DataAccessException;
import Suppliers.DataLayer.DAOs.JdbcContactInfoDAO;
import Suppliers.DataLayer.util.Database;
import Suppliers.DTOs.ContactInfoDTO;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcContactInfoDAOTest {
   private JdbcContactInfoDAO dao;

   @BeforeEach
   void setUp() throws SQLException {
      Database.setDB_URL(Database.DB_TEST_URL);
      Database.seedDefaultData();
      dao = new JdbcContactInfoDAO();
   }

   @AfterEach
   void tearDown() throws SQLException {
      Database.deleteAllData();
   }

   @Test
   void insertAndFetchContacts_roundTrip() throws SQLException {
      // Arrange: suppose supplier_id=1 already exists
      ContactInfoDTO toInsert = new ContactInfoDTO(
            /* supplierId = */ 1,
            /* name = */ "Bob",
            /* email = */ "bob@example.com",
            /* phone = */ "555-1234");

      // Act: insert & fetch
      ContactInfoDTO created = dao.createContactInfo(toInsert);
      assertNotNull(created, "Created contact should not be null");
      // (Primary Key is composite: supplier_id + name)

      List<ContactInfoDTO> all = dao.getContactInfosBySupplierId(1);
      assertFalse(all.isEmpty(), "Should find at least one contact for supplier 1");
      boolean found = all.stream()
            .anyMatch(c -> c.getName().equals("Bob") && c.getPhone().equals("555-1234"));
      assertTrue(found, "The inserted contact must appear in the results");
   }

   @Test
   void insertingDuplicateNameForSameSupplier_throwsSQLException() {
      // Arrange: name "Bob" for supplier_id=1 already inserted in the previous test
      // or seed
      ContactInfoDTO first = new ContactInfoDTO(1, "Bob", "bob1@example.com", "555-1234");
      ContactInfoDTO second = new ContactInfoDTO(1, "Bob", "bob2@example.com", "555-5678");

      // Act & Assert
      dao.createContactInfo(first);
      assertThrows(DataAccessException.class, () -> dao.createContactInfo(second),
            "Inserting two contacts with same (supplier_id,name) primary key should violate UNIQUE");
   }
}