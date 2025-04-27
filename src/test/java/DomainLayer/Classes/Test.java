package DomainLayer.Classes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import DomainLayer.AgreementFacade;
import DomainLayer.SupplierFacade;
import DomainLayer.Enums.*;
import ServiceLayer.SupplierService;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

class SupplierTest {
   private ObjectMapper mapper;
   private SupplierFacade supplierFacade;
   private AgreementFacade agreementFacade;

   @BeforeEach
   void setUp() {
      supplierFacade = new SupplierFacade();
      agreementFacade = new AgreementFacade();
      mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
   }

   @AfterEach
   void tearDown() {
      mapper = null;
      supplierFacade = null;
      agreementFacade = null;
   }

   @Test
   void testCreateSupplier_WithValidData_ShouldCreateSupplier() {
      ObjectNode supplierData = mapper.createObjectNode();
      supplierData.put("name", "Test Supplier");
      supplierData.put("taxNumber", "123456789");
      ObjectNode address = mapper.createObjectNode();
      address.put("street", "123 Test St");
      address.put("city", "Test City");
      address.put("buildingNumber", "Test State");
      ObjectNode paymentDetails = mapper.createObjectNode();
      paymentDetails.put("bankAccountNumber", "123456789");
      paymentDetails.put("paymentMethod", PaymentMethod.BANK_TRANSFER.toString());
      paymentDetails.put("paymentTerm", PaymentTerm.COD.toString());
      supplierData.put("paymentDetails", paymentDetails);
      supplierData.put("address", address);
      ArrayNode contactsArray = supplierData.putArray("contacts");
      ArrayNode productsArray = supplierData.putArray("products");
      ArrayNode agreementsArray = supplierData.putArray("agreements");

      try {
         SupplierService supplierService = new SupplierService(supplierFacade);
         String response = supplierService.execute("addSupplier", supplierData.toString());
      } catch (Exception e) {
         fail("Exception should not be thrown: " + e.getMessage());
      }
   }

   @Test
   void testCreateAddress_WithValidData_ShouldCreateAddress() {
      ObjectNode addressData = mapper.createObjectNode();
      addressData.put("street", "123 Test St");
      addressData.put("city", "Test City");
      addressData.put("buildingNumber", "Test State");

      try {
         Address address = mapper.treeToValue(addressData, Address.class);
         assertNotNull(address);
         assertEquals("123 Test St", address.getStreet());
         assertEquals("Test City", address.getCity());
         assertEquals("Test State", address.getBuildingNumber());
      } catch (Exception e) {
         fail("Exception should not be thrown: " + e.getMessage());
      }
   }

   @Test
   void testCreatePaymentDetails_WithValidData_ShouldCreatePaymentDetails() {
      ObjectNode paymentData = mapper.createObjectNode();
      paymentData.put("bankAccountNumber", "123456789");
      paymentData.put("paymentMethod", PaymentMethod.BANK_TRANSFER.toString());
      paymentData.put("paymentTerm", PaymentTerm.COD.toString());

      try {
         PaymentDetails paymentDetails = mapper.treeToValue(paymentData, PaymentDetails.class);
         assertNotNull(paymentDetails);
         assertEquals("123456789", paymentDetails.getBankAccountNumber());
         assertEquals(PaymentMethod.BANK_TRANSFER, paymentDetails.getPaymentMethod());
         assertEquals(PaymentTerm.COD, paymentDetails.getPaymentTerm());
      } catch (Exception e) {
         fail("Exception should not be thrown: " + e.getMessage());
      }
   }

   @Test
   void testCreatePaymentTerm_WithValidData_ShouldCreatePaymentTerm() throws Exception {
      // build the wrapper object
      ObjectNode wrapper = mapper.createObjectNode()
            .put("paymentTerm", PaymentTerm.COD.toString());

      // extract just the text node
      JsonNode termNode = wrapper.get("paymentTerm");

      // now Jackson can map that TextNode → enum
      PaymentTerm paymentTerm = mapper.treeToValue(termNode, PaymentTerm.class);

      assertNotNull(paymentTerm);
      assertEquals(PaymentTerm.COD, paymentTerm);
   }

   @Test
   void testCreatePaymentMethod_WithValidData_ShouldCreatePaymentMethod() throws Exception {
      ObjectNode wrapper = mapper.createObjectNode()
            .put("paymentMethod", PaymentMethod.BANK_TRANSFER.toString());

      // pull out the actual text node
      JsonNode valueNode = wrapper.get("paymentMethod");

      // now this is just a TextNode, which Jackson can map to the enum
      PaymentMethod paymentMethod = mapper.treeToValue(valueNode, PaymentMethod.class);

      assertNotNull(paymentMethod);
      assertEquals(PaymentMethod.BANK_TRANSFER, paymentMethod);
   }

   @Test
   void testCreateSupplier_WithInvalidData_ShouldReturnErrorKey() throws Exception {
      // Build payload without paymentDetails
      ObjectNode supplierData = mapper.createObjectNode()
            .put("name", "Test Supplier")
            .put("taxNumber", "123456789");
      ObjectNode address = mapper.createObjectNode()
            .put("street", "123 Test St")
            .put("city", "Test City")
            .put("buildingNumber", "Test State");
      supplierData.set("address", address);
      supplierData.putArray("contacts");
      supplierData.putArray("products");
      supplierData.putArray("agreements");

      // Invoke service
      SupplierService svc = new SupplierService(supplierFacade);
      String jsonResponse = svc.execute("addSupplier", supplierData.toString());

      // Parse and assert only that "error" exists
      JsonNode respNode = mapper.readTree(jsonResponse);
      assertTrue(respNode.has("error"), "Response should contain an 'error' field");
   }

   @Test
   void testConstructorWithNullCollections_InitializesEmptyLists() {
      Supplier s = new Supplier(
            "Acme Corp",
            "123456789",
            null,
            null,
            null,
            null,
            null);

      assertNotNull(s.getContacts(), "contacts list should never be null");
      assertTrue(s.getContacts().isEmpty(), "contacts should default to empty");

      assertNotNull(s.getProducts(), "products list should never be null");
      assertTrue(s.getProducts().isEmpty(), "products should default to empty");

      assertNotNull(s.getAgreements(), "agreements list should never be null");
      assertTrue(s.getAgreements().isEmpty(), "agreements should default to empty");
   }

   @Test
   void testSetSupplierId_WithValidUUIDString_Succeeds() {
      Supplier s = new Supplier("Name", "TAX", null, null, List.of(), List.of(), List.of());
      String uuidStr = UUID.randomUUID().toString();
      s.setSupplierId(uuidStr);
      assertEquals(UUID.fromString(uuidStr), s.getSupplierId());
   }

   @Test
   void testSetSupplierId_WithInvalidUUIDString_Throws() {
      Supplier s = new Supplier("Name", "TAX", null, null, List.of(), List.of(), List.of());
      assertThrows(IllegalArgumentException.class, () -> {
         s.setSupplierId("not-a-uuid");
      });
   }

   @Test
   void testToString_IncludesKeyFields() {
      Supplier s = new Supplier("Foo", "987654321", null, null, List.of(), List.of(), List.of());
      String repr = s.toString();
      assertTrue(repr.contains("Foo"), "toString should contain the name");
      assertTrue(repr.contains("987654321"), "toString should contain the taxNumber");
   }

   @Test
   void testJsonDeserialization_PopulatesFields() throws Exception {
      String json = """
            {
              "name":"TestCo",
              "taxNumber":"111222333",
              "address":{"street":"S1","city":"C1","buildingNumber":"B1"},
              "paymentDetails":{"paymentMethod":"BANK_TRANSFER","paymentTerm":"COD"},
              "contacts":[],
              "products":[],
              "agreements":[]
            }
            """;

      Supplier s = mapper.readValue(json, Supplier.class);
      assertEquals("TestCo", s.getName());
      assertEquals("111222333", s.getTaxNumber());
      // address & paymentDetails would need real getters; at least not null:
      assertNotNull(s.getAddress());
      assertNotNull(s.getBankDetails());
      // lists should be non-null (empty arrays)
      assertNotNull(s.getContacts());
      assertNotNull(s.getProducts());
      assertNotNull(s.getAgreements());
   }
}
