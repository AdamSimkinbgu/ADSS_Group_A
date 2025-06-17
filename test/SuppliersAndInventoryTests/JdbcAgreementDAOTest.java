package Tests;

import org.junit.jupiter.api.*;

import DTOs.SuppliersModuleDTOs.AgreementDTO;
import Suppliers.DataLayer.DAOs.JdbcAgreementDAO;
import Suppliers.DataLayer.DAOs.JdbcSupplierDAO;
import Suppliers.DataLayer.util.Database;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcAgreementDAOTest {
   private JdbcAgreementDAO dao;
   private JdbcSupplierDAO daoSup;

   @BeforeAll
   static void initDatabase() throws SQLException {
      Database.setDB_URL(Database.DB_TEST_URL);
   }

   @BeforeEach
   void setUp() throws SQLException {
      Database.seedDefaultData();
      dao = new JdbcAgreementDAO();
      daoSup = new JdbcSupplierDAO();
   }

   @AfterEach
   void tearDown() throws SQLException {
      Database.deleteAllData();
   }

   @Test
   void insertAndFetchAgreement_roundTrip() {
      // Arrange: build a minimal AgreementDTO (id = -1 means not yet in DB)
      AgreementDTO toInsert = new AgreementDTO(
            /* supplierId = */ 1,
            /* supplierName = */ "Supplier X",
            /* agreementStartDate = */ LocalDate.of(2025, 6, 1),
            /* agreementEndDate = */ LocalDate.of(2025, 6, 10),
            /* BillOfQuantitiesItems = */ List.of());

      // Act: insert & get new ID
      // precondition: ensure supplier with ID 1 exists in the test database
      assertTrue(daoSup.supplierExists(1), "Supplier with ID 1 should exist for this test");
      AgreementDTO created = dao.createAgreement(toInsert);
      assertNotNull(created, "Created agreement should not be null");
      assertTrue(created.getAgreementId() > 0, "New agreement ID should be positive");

      // Fetch by ID
      Optional<AgreementDTO> fetchedOpt = dao.getAgreementById(created.getAgreementId());
      assertTrue(fetchedOpt.isPresent(), "Agreement should be found by ID");
      AgreementDTO fetched = fetchedOpt.get();

      // Assert basic fields
      assertEquals(created.getAgreementId(), fetched.getAgreementId());
      assertEquals(1, fetched.getSupplierId());
      assertTrue(fetched.isValid());
      assertEquals(LocalDate.of(2025, 6, 1), fetched.getAgreementStartDate());
      assertEquals(LocalDate.of(2025, 6, 10), fetched.getAgreementEndDate());
   }

   @Test
   void insertingWithSupplierThatWasNotAssigned_throwsDataAccessException() {
      // Attempt to insert an agreement for a supplier_id that does not exist
      // (e.g.,
      // 9999)
      AgreementDTO bad = new AgreementDTO(
            /* supplierId = */ -1,
            /* supplierName = */ "Nonexistent",
            /* start = */ LocalDate.of(2025, 1, 1),
            /* end = */ LocalDate.of(2025, 1, 2),
            /* items = */ List.of());
      assertThrows(IllegalArgumentException.class, () -> dao.createAgreement(bad),
            "Inserting an agreement with nonexistent supplier_id should fail FK constraint");
   }
}