

import org.junit.jupiter.api.*;

import Suppliers.DTOs.PaymentDetailsDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.DTOs.Enums.PaymentMethod;
import Suppliers.DTOs.Enums.PaymentTerm;
import Suppliers.DataLayer.DAOs.DataAccessException;
import Suppliers.DataLayer.DAOs.JdbcSupplierDAO;
import Suppliers.DataLayer.util.Database;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcSupplierDAOTest {
      private JdbcSupplierDAO dao;

      @BeforeEach
      void setUp() throws SQLException {
            Database.setDB_URL(Database.DB_TEST_URL);
            Database.seedDefaultData();
            dao = new JdbcSupplierDAO();
      }

      @AfterEach
      void tearDown() throws SQLException {
            Database.deleteAllData();
      }

      @Test
      void insertAndFetchSupplier_roundTrip() {
            // Arrange: build a SupplierDTO with no ID
            SupplierDTO dto = new SupplierDTO(
                        /* id = */ -1,
                        /* name = */ "Acme Corp",
                        /* tax = */ "123456789",
                        /* address = */ new Suppliers.DTOs.AddressDTO("1 Main St", "Metropolis", "12345"),
                        /* selfSupply = */ true,
                        /* days = */ java.util.EnumSet.of(java.time.DayOfWeek.MONDAY),
                        /* leadDays = */ 2,
                        /* payment = */ new PaymentDetailsDTO("111222333", PaymentMethod.valueOf("BANK_TRANSFER"),
                                    PaymentTerm.valueOf("N30")),
                        /* contacts= */ List
                                    .of(new Suppliers.DTOs.ContactInfoDTO("Alice", "alice@example.com", "555-0001")));

            // Act: insert & get new ID
            SupplierDTO newSupplier = dao.createSupplier(dto);
            assertNotNull(newSupplier, "New supplier should not be null");
            assertTrue(newSupplier.getId() > 0, "New supplier ID should be positive");

            // Fetch by ID
            Optional<SupplierDTO> fetchedOptional = dao.getSupplier(newSupplier.getId());
            assertTrue(fetchedOptional.isPresent(), "Supplier should be found by ID");
            SupplierDTO fetched = fetchedOptional.get();
            assertNotNull(fetched);
            assertEquals("Acme Corp", fetched.getName());
            assertEquals("123456789", fetched.getTaxNumber());
            assertTrue(fetched.getSelfSupply());
            assertEquals(2, fetched.getLeadSupplyDays());
            assertEquals("111222333", fetched.getPaymentDetails().getBankAccountNumber());
            // ... (you can assert more fields as desired)
      }

      @Test
      void insertingDuplicateTaxNumber_throwsDataAccessException() {
            SupplierDTO dto1 = new SupplierDTO(
                        -1, "Supplier A", "TAX123",
                        new Suppliers.DTOs.AddressDTO("Street", "City", "1"),
                        false, java.util.EnumSet.noneOf(java.time.DayOfWeek.class),
                        0,
                        new Suppliers.DTOs.PaymentDetailsDTO("415236", PaymentMethod.valueOf("CASH"),
                                    PaymentTerm.valueOf("COD")),
                        List.of());
            // Insert first
            SupplierDTO newSupplier1 = dao.createSupplier(dto1);
            assertNotNull(newSupplier1, "First insert should return a valid SupplierDTO");
            assertTrue(newSupplier1.getId() > 0);

            // Insert second with same tax number
            SupplierDTO dto2 = new SupplierDTO(
                        -1, "Supplier B", "TAX123",
                        new Suppliers.DTOs.AddressDTO("Road", "Town", "2"),
                        true, java.util.EnumSet.noneOf(java.time.DayOfWeek.class),
                        1,
                        new Suppliers.DTOs.PaymentDetailsDTO("415236", PaymentMethod.valueOf("CREDIT_CARD"),
                                    PaymentTerm.valueOf("N60")),
                        List.of());

            assertThrows(DataAccessException.class, () -> dao.createSupplier(dto2),
                        "Inserting with duplicate tax_number should trigger UNIQUE constraint");
      }
}