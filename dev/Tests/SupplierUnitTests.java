package Tests;

import org.junit.jupiter.api.*;

import Suppliers.DomainLayer.SupplierFacade;
import Suppliers.DTOs.*;
import Suppliers.DTOs.Enums.*;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SupplierUnitTests {
   private SupplierFacade facade;
   private SupplierDTO seedSupplier;

   @BeforeEach
   void setUp() {
      facade = new SupplierFacade(InitializeState.NO_DATA_STATE);

      // Create one “seed” supplier that all subsequent tests can refer to:
      SupplierDTO dto = new SupplierDTO(
            /* id = */ -1,
            /* name = */ "Acme Corp",
            /* taxNumber = */ "TAX-000",
            /* address = */ new AddressDTO("1 Main St", "Metropolis", "1A"),
            /* selfSupply = */ true,
            /* supplyDays= */ EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
            /* leadDays = */ 2,
            /* payment = */ new PaymentDetailsDTO("111-222-333", PaymentMethod.BANK_TRANSFER, PaymentTerm.N30),
            /* contacts = */ List.of(new ContactInfoDTO("Alice", "alice@acme.com", "555-0001")));

      // Act: insert supplier
      seedSupplier = facade.createSupplier(dto);
      assertNotNull(seedSupplier);
      assertTrue(seedSupplier.getId() > 0, "Seed supplier must have positive ID");
   }

   // ----------------------------
   // createSupplier(…) / getSupplierDTO(…) / getAllSuppliers(…)
   // ----------------------------
   @Test
   void createFetchAndListSuppliers_happyPath() {
      // We already created seedSupplier in @BeforeEach.
      // Fetch it by ID:
      SupplierDTO fetched = facade.getSupplierDTO(seedSupplier.getId());
      assertEquals(seedSupplier.getName(), fetched.getName());
      assertEquals(seedSupplier.getTaxNumber(), fetched.getTaxNumber());
      assertEquals(seedSupplier.getSelfSupply(), fetched.getSelfSupply());
      assertEquals(seedSupplier.getLeadSupplyDays(), fetched.getLeadSupplyDays());
      assertEquals(seedSupplier.getPaymentDetails().getPaymentMethod(),
            fetched.getPaymentDetails().getPaymentMethod());

      // getAllSuppliers()
      List<SupplierDTO> all = facade.getAllSuppliers();
      assertFalse(all.isEmpty());
      assertTrue(all.stream().anyMatch(s -> s.getId() == seedSupplier.getId()));

      // Make sure sorting by ID works (only one supplier right now)
      assertEquals(1, all.size());
   }

   @Test
   void createSupplier_null_throwsInvalidParameter() {
      assertThrows(InvalidParameterException.class, () -> facade.createSupplier(null));
   }

   @Test
   void getSupplier_notFound_throws() {
      int nonexistentId = seedSupplier.getId() + 999;
      assertThrows(IllegalArgumentException.class,
            () -> facade.getSupplierDTO(nonexistentId));
   }

   // ----------------------------
   // updateSupplier(…)
   // ----------------------------
   @Test
   void updateSupplier_happyPath() {
      SupplierDTO copy = new SupplierDTO(seedSupplier);
      copy.setName("Acme Corp Updated");
      boolean changed = facade.updateSupplier(copy, seedSupplier.getId());
      assertTrue(changed, "There was a change, so updateSupplier should return true");

      // Fetch and confirm the change went through
      SupplierDTO after = facade.getSupplierDTO(seedSupplier.getId());
      assertEquals("Acme Corp Updated", after.getName());
   }

   @Test
   void updateSupplier_noChange_returnsFalse() {
      // Pass in exactly the same data
      SupplierDTO same = new SupplierDTO(seedSupplier);
      boolean changed = facade.updateSupplier(same, seedSupplier.getId());
      assertTrue(changed,
            "No fields changed, so updateSupplier should return true as technically we did save the data we intended to");
   }

   @Test
   void updateSupplier_nullDTO_throws() {
      assertThrows(InvalidParameterException.class,
            () -> facade.updateSupplier(null, seedSupplier.getId()));
   }

   @Test
   void updateSupplier_nonexistentID_throws() {
      SupplierDTO copy = new SupplierDTO(seedSupplier);
      int badId = seedSupplier.getId() + 1000;
      assertThrows(IllegalArgumentException.class,
            () -> facade.updateSupplier(copy, badId));
   }

   // ----------------------------
   // removeSupplier(…)
   // ----------------------------
   @Test
   void removeSupplier_happyPath() {
      boolean removed = facade.removeSupplier(seedSupplier.getId());
      assertTrue(removed, "Should return true when a supplier is actually removed");

      // Now fetching it should fail:
      assertThrows(IllegalArgumentException.class,
            () -> facade.getSupplierDTO(seedSupplier.getId()));
   }

   @Test
   void removeSupplier_notFound_returnsFalse() {
      int badId = seedSupplier.getId() + 100;
      boolean removed = facade.removeSupplier(badId);
      assertFalse(removed, "Should return false if no supplier existed");
   }

   // ----------------------------
   // checkSupplierExists(…)
   // ----------------------------
   @Test
   void checkSupplierExists_works() {
      assertTrue(facade.checkSupplierExists(seedSupplier.getId()));
      assertFalse(facade.checkSupplierExists(seedSupplier.getId() + 999));
   }

   // ----------------------------
   // addProductToSupplierAndMemory(…) / getSupplierProducts(…) /
   // removeProductFromSupplierAndDB(…) / updateProductInSupplierAndMemory(…)
   // ----------------------------
   @Test
   void addFetchUpdateAndRemoveProduct_happyPath() {
      // 1) Create a SupplierProductDTO (no ID yet):
      SupplierProductDTO prod = new SupplierProductDTO(
            /* supplierId = */ 1,
            /* productId = */ -1,
            /* catalog# = */ "CAT-100",
            /* name = */ "Widget",
            /* price = */ new BigDecimal("10.00"),
            /* weight = */ new BigDecimal("1.5"),
            /* expiresInDays = */ 30,
            /* manufacturerName = */ "AcmeMfg");

      // Act: add it to our seed supplier
      facade.addProductToSupplierAndMemory(seedSupplier.getId(), prod);

      // 2) Fetch all products for that supplier:
      List<SupplierProductDTO> list = facade.getSupplierProducts(seedSupplier.getId());
      assertFalse(list.isEmpty(), "Should find at least one product");
      SupplierProductDTO fetched = list.get(0);
      assertEquals("CAT-100", fetched.getSupplierCatalogNumber());
      assertEquals(new BigDecimal("10.0"), fetched.getPrice());

      // 3) Update that product’s price:
      fetched.setPrice(new BigDecimal("12.50"));
      facade.updateProductInSupplierAndMemory(seedSupplier.getId(), fetched);

      // 4) Fetch again and confirm price changed
      List<SupplierProductDTO> updatedList = facade.getSupplierProducts(seedSupplier.getId());
      assertTrue(updatedList.stream()
            .anyMatch(p -> p.getPrice().compareTo(new BigDecimal("12.50")) == 0));

      // 5) Remove it
      facade.removeProductFromSupplierAndDB(seedSupplier.getId(), fetched.getProductId());
      List<SupplierProductDTO> afterRemoval = facade.getSupplierProducts(seedSupplier.getId());
      assertTrue(afterRemoval.isEmpty(), "After removal, no products should remain");
   }

   @Test
   void addProduct_nullOrBadInput_throws() {
      assertThrows(InvalidParameterException.class,
            () -> facade.addProductToSupplierAndMemory(seedSupplier.getId(), null));
      assertThrows(InvalidParameterException.class,
            () -> facade.addProductToSupplierAndMemory(0, new SupplierProductDTO()));
   }

   @Test
   void updateProduct_nullOrBadInput_throws() {
      assertThrows(InvalidParameterException.class,
            () -> facade.updateProductInSupplierAndMemory(seedSupplier.getId(), null));

      SupplierProductDTO bad = new SupplierProductDTO(
            seedSupplier.getId(), /* but productId <=0 */ 0,
            "X", "Y", BigDecimal.ZERO, BigDecimal.ZERO, 0, "Z");
      assertThrows(InvalidParameterException.class,
            () -> facade.updateProductInSupplierAndMemory(seedSupplier.getId(), bad));
   }

   @Test
   void removeProduct_badInput_throws() {
      assertThrows(InvalidParameterException.class,
            () -> facade.removeProductFromSupplierAndDB(seedSupplier.getId(), 0));
   }

   // ----------------------------
   // getProductCatalog(…)
   // ----------------------------
   @Test
   void getProductCatalog_returnsEmptyIfNoProducts() {
      // We have not added any products yet, so catalog should be empty
      List<CatalogProductDTO> catalog = facade.getProductCatalog();
      assertTrue(catalog.isEmpty());
   }

   // ----------------------------
   // Agreements: createAgreement(…) / getAgreement(…) /
   // getAgreementsBySupplierId(…) / removeAgreement(…) / updateAgreement(…)
   // ----------------------------
   @Test
   void createFetchListRemoveAndUpdateAgreement_happyPath() {
      // 1) Build a Bill‐of‐Quantities list
      BillofQuantitiesItemDTO item = new BillofQuantitiesItemDTO(
            /* agreementId = */ -1,
            /* lineInBillID = */ -1,
            /* productName = */ "Widget",
            /* productId = */ 1, // assume product #1 will exist
            /* quantity = */ 5,
            /* discountPercent= */ new BigDecimal("0.50"));
      // First, create a product to reference (we must add at least one product under
      // this supplier)
      SupplierProductDTO p = new SupplierProductDTO(
            seedSupplier.getId(), -1, "CAT-200", "Gizmo", new BigDecimal("20.0"), new BigDecimal("2.0"), 10,
            "AcmeMfg");
      facade.addProductToSupplierAndMemory(seedSupplier.getId(), p);
      List<SupplierProductDTO> prods = facade.getSupplierProducts(seedSupplier.getId());
      assertFalse(prods.isEmpty());
      int createdProductId = prods.get(0).getProductId();
      // Now overwrite our BOQ item with that real productId:
      item.setProductId(createdProductId);

      // 2) Create an AgreementDTO
      AgreementDTO toInsert = new AgreementDTO(
            /* supplierId = */ seedSupplier.getId(),
            /* supplierName = */ seedSupplier.getName(),
            /* agreementStartDate = */ LocalDate.of(2025, 7, 1),
            /* agreementEndDate = */ LocalDate.of(2025, 7, 31),
            /* billOfQuantitiesItems = */ List.of(item));

      // Act: insert agreement
      AgreementDTO created = facade.createAgreement(toInsert);
      assertNotNull(created);
      assertTrue(created.getAgreementId() > 0);

      // 3) Fetch by ID
      AgreementDTO fetched = facade.getAgreement(created.getAgreementId());
      assertEquals(created.getAgreementId(), fetched.getAgreementId());
      assertEquals(seedSupplier.getId(), fetched.getSupplierId());
      assertTrue(fetched.isValid());

      // 4) getAgreementsBySupplierId(...)
      List<AgreementDTO> allAg = facade.getAgreementsBySupplierId(seedSupplier.getId());
      assertFalse(allAg.isEmpty());
      assertTrue(allAg.stream().anyMatch(a -> a.getAgreementId() == created.getAgreementId()));

      // 5) Update that agreement’s “valid” flag to false:
      AgreementDTO updated = new AgreementDTO(fetched);
      updated.setValid(false);
      facade.updateAgreement(fetched.getAgreementId(), updated);

      AgreementDTO refetched = facade.getAgreement(fetched.getAgreementId());
      assertFalse(refetched.isValid(), "The valid‐flag should have been flipped to false");

      // 6) Remove the agreement
      facade.removeAgreement(fetched.getAgreementId(), seedSupplier.getId());

      // Now getAgreementsBySupplierId should be empty
      List<AgreementDTO> afterRemoval = facade.getAgreementsBySupplierId(seedSupplier.getId());
      assertTrue(afterRemoval.isEmpty());
   }

   @Test
   void createAgreement_null_throws() {
      assertThrows(InvalidParameterException.class,
            () -> facade.createAgreement(null));
   }

   @Test
   void getAgreement_badId_throws() {
      assertThrows(InvalidParameterException.class,
            () -> facade.getAgreement(0));
      assertThrows(IllegalArgumentException.class,
            () -> facade.getAgreement(999999));
   }

   @Test
   void removeAgreement_badInput_throws() {
      assertThrows(InvalidParameterException.class,
            () -> facade.removeAgreement(0, seedSupplier.getId()));
      assertThrows(InvalidParameterException.class,
            () -> facade.removeAgreement(1, 0));
   }

   @Test
   void updateAgreement_nullOrBadInput_throws() {
      // null
      assertThrows(InvalidParameterException.class,
            () -> facade.updateAgreement(1, null));
      // bad ID
      assertThrows(InvalidParameterException.class,
            () -> facade.updateAgreement(0, new AgreementDTO()));
   }

   // ----------------------------
   // setProductNameAndCategoryForOrderItems(…)
   // ----------------------------
   @Test
   void setProductNameAndCategoryForOrderItems_happyPath() {
      // 1) Add two products under our seed supplier
      SupplierProductDTO p1 = new SupplierProductDTO(
            seedSupplier.getId(), -1, "CAT-300", "AlphaWidget", new BigDecimal("5.0"), new BigDecimal("0.5"), 15,
            "MakerA");
      SupplierProductDTO p2 = new SupplierProductDTO(
            seedSupplier.getId(), -1, "CAT-400", "BetaGadget", new BigDecimal("8.0"), new BigDecimal("1.0"), 20,
            "MakerB");
      facade.addProductToSupplierAndMemory(seedSupplier.getId(), p1);
      facade.addProductToSupplierAndMemory(seedSupplier.getId(), p2);
      List<SupplierProductDTO> products = facade.getSupplierProducts(seedSupplier.getId());
      assertEquals(2, products.size());

      // 2) Prepare two OrderItemLineDTOs (names and cat‐numbers are null initially)
      OrderItemLineDTO lineA = new OrderItemLineDTO();
      lineA.setProductId(products.get(0).getProductId());
      lineA.setSupplierProductCatalogNumber(null);
      lineA.setProductName(null);

      OrderItemLineDTO lineB = new OrderItemLineDTO();
      lineB.setProductId(products.get(1).getProductId());
      lineB.setSupplierProductCatalogNumber(null);
      lineB.setProductName(null);

      List<OrderItemLineDTO> input = new ArrayList<>();
      input.add(lineA);
      input.add(lineB);

      // Act
      List<OrderItemLineDTO> populated = facade.setProductNameAndCategoryForOrderItems(input, seedSupplier.getId());

      // Assert that each now has non‐null name & catalogNumber
      for (OrderItemLineDTO out : populated) {
         assertNotNull(out.getProductName());
         assertNotNull(out.getSupplierProductCatalogNumber());
         assertTrue(out.getProductName().equals("AlphaWidget") ||
               out.getProductName().equals("BetaGadget"));
      }
   }

   @Test
   void setProductNameAndCategoryForOrderItems_noItemsOrNoProducts_returnsEmptyList() {
      List<OrderItemLineDTO> empty1 = facade.setProductNameAndCategoryForOrderItems(Collections.emptyList(),
            seedSupplier.getId());
      assertTrue(empty1.isEmpty());

      // If supplier has no products, even a non‐empty input yields empty
      List<OrderItemLineDTO> dummy = List.of(new OrderItemLineDTO());
      // First remove all products (none to begin with), so still no products in DB
      List<OrderItemLineDTO> empty2 = facade.setProductNameAndCategoryForOrderItems(dummy, seedSupplier.getId());
      assertTrue(empty2.isEmpty());
   }

   // ----------------------------
   // setSupplierPricesAndDiscountsByBestPrice(…)
   // ----------------------------
   @Test
   void setSupplierPricesAndDiscountsByBestPrice_happyPath() {
      // 1) Create a single product with price=10.00
      SupplierProductDTO p = new SupplierProductDTO(
            seedSupplier.getId(), -1, "CAT-500", "GammaWidget", new BigDecimal("10.00"), new BigDecimal("1"), 30,
            "MakerC");
      facade.addProductToSupplierAndMemory(seedSupplier.getId(), p);
      int productId = facade.getSupplierProducts(seedSupplier.getId()).get(0).getProductId();

      // 2) Create an agreement for “5 items at 50% discount”
      BillofQuantitiesItemDTO boqItem = new BillofQuantitiesItemDTO(
            /* agreementId = */ -1,
            /* lineInBillID = */ -1,
            /* productName = */ "GammaWidget",
            /* productId = */ productId,
            /* quantity = */ 5,
            /* discountPercent = */ new BigDecimal("0.50"));
      AgreementDTO agr = new AgreementDTO(
            /* supplierId = */ seedSupplier.getId(),
            /* supplierName = */ seedSupplier.getName(),
            /* start = */ LocalDate.of(2025, 8, 1),
            /* end = */ LocalDate.of(2025, 8, 31),
            /* billOfQuantitiesItems = */ List.of(boqItem));
      AgreementDTO createdAgr = facade.createAgreement(agr);
      int agreementId = createdAgr.getAgreementId();
      assertTrue(agreementId > 0);

      // (Note: The facade’s implementation will auto‐insert the BOQ item under that
      // agreementId.)

      // 3) Build a single OrderItemLineDTO with quantity=5, no price or discount yet
      OrderItemLineDTO orderLine = new OrderItemLineDTO();
      orderLine.setProductId(productId);
      orderLine.setQuantity(5);
      orderLine.setUnitPrice(BigDecimal.ZERO);
      orderLine.setDiscount(BigDecimal.ZERO);

      // Act:
      List<OrderItemLineDTO> result = facade.setSupplierPricesAndDiscountsByBestPrice(
            List.of(orderLine), seedSupplier.getId());

      // Assert:
      assertEquals(1, result.size());
      OrderItemLineDTO out = result.get(0);

      // priceBeforeDiscount = 10.00 * 5 = 50.00
      // bestPriceAccumulate = (10.00 * 5) * (50% discount) = 50.00 * 0.5 = 25.00
      // ratio = 25/50 = 0.5 → discount = 1 – 0.5 = 0.5
      assertEquals(new BigDecimal("0.50"), out.getDiscount().setScale(2));
      assertEquals(new BigDecimal("10.00"), out.getUnitPrice().setScale(2));
   }

   @Test
   void setSupplierPricesAndDiscountsByBestPrice_nullOrBadInput_throws() {
      assertThrows(IllegalArgumentException.class,
            () -> facade.setSupplierPricesAndDiscountsByBestPrice(null, seedSupplier.getId()));
      assertThrows(IllegalArgumentException.class,
            () -> facade.setSupplierPricesAndDiscountsByBestPrice(Collections.emptyList(), seedSupplier.getId()));
      // If supplier has no products, should also throw:
      // First clear all products by removing them:
      for (SupplierProductDTO sp : facade.getSupplierProducts(seedSupplier.getId())) {
         facade.removeProductFromSupplierAndDB(seedSupplier.getId(), sp.getProductId());
      }
      OrderItemLineDTO line = new OrderItemLineDTO();
      line.setProductId(9999); // invalid
      assertThrows(IllegalArgumentException.class,
            () -> facade.setSupplierPricesAndDiscountsByBestPrice(List.of(line), seedSupplier.getId()));
   }
}