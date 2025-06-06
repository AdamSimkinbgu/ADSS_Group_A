package Tests;

import Inventory.Domain.MainDomain;
import Inventory.Domain.ProductDomain;
import Inventory.DTO.*;
import Inventory.type.Position;
import Suppliers.DTOs.Enums.InitializeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTests {
    private MainDomain main;

    // @BeforeEach
    // public void setUp() {
    // main = new MainDomain();
    // }

    // @Test
    // public void testAddProductSuccessfully() {
    // ProductDTO p = new ProductDTO(8, "Bread", "Angel", 10, 20, 5.5f,
    // new Position(1, 2), new Position(3, 4));
    // main.AddProduct(p);
    // String report = main.GetMissingReport();
    // assertFalse(report.contains("Bread"));
    // }

    // @Test
    // public void testAddProductDuplicateNameThrowsException() {
    // ProductDTO p = new ProductDTO(7, "Bread", "Angel", 10, 20, 5.5f,
    // new Position(1, 2), new Position(3, 4));
    // main.AddProduct(p);
    // Exception exception = assertThrows(IllegalArgumentException.class, () ->
    // main.AddProduct(p));
    // assertTrue(exception.getMessage().contains("alredy in stock"));
    // }

    // @Test
    // public void testUpdateInventoryRestockValidProduct() {
    // ProductDTO p = new ProductDTO(1, "Butter", "Tnuva", 2, 2, 7.0f,
    // new Position(1, 1), new Position(2, 2));
    // main.AddProduct(p);
    // List<SupplyDTO> ls = new ArrayList<>();
    // ls.add(new SupplyDTO(1, 10, LocalDate.now().plusDays(7)));
    // main.DeliverOrder(ls);
    // assertDoesNotThrow(() -> main.UpdateInventoryRestock());
    // }

    // @Test
    // public void testUpdateInventoryRestockInvalidProductThrowsException() {
    // List<SupplyDTO> ls = new ArrayList<>();
    // ls.add(new SupplyDTO(99, 10, LocalDate.now().plusDays(7)));
    // Exception exception = assertThrows(IllegalArgumentException.class, () -> {
    // main.DeliverOrder(ls);
    // main.UpdateInventoryRestock();
    // });
    // assertTrue(exception.getMessage().contains("no product with this ip"));
    // }

    // @Test
    // public void testGetMissingReportWithMissingProduct() {
    // main.AddProduct("Cheese", "Tnuva", 0, 0, 6.0f, new Position(1, 1), new
    // Position(2, 2));
    // String report = main.GetMissingReport();
    // assertTrue(report.contains("Cheese"));
    // }

    // @Test
    // public void testGetBadReportWithNoBadProducts() {
    // main.AddProduct("Yogurt", "Tara", 10, 10, 4.5f,
    // new Position(1, 1), new Position(2, 2));
    // String report = main.GetBadReport();
    // assertFalse(report.contains("Yogurt"));
    // }

    // @Test
    // public void testAddBadProductAffectsReport() {
    // main.AddProduct("Eggs", "Local", 10, 10, 12.0f,
    // new Position(1, 1), new Position(2, 2));
    // main.AddBadProduct(0, 3);
    // String report = main.GetBadReport();
    // assertTrue(report.contains("Eggs"));
    // }

    // @Test
    // public void testMoveProductChangesStoreShelf() {
    // main.AddProduct("Tomato", "Farm", 5, 5, 3.0f,
    // new Position(0, 0), new Position(1, 1));
    // Position newPos = new Position(4, 4);
    // main.MoveProduct(0, true, newPos);
    // ProductDomain p = main.Search(0);
    // assertEquals(4, p.getstoreShelf().line());
    // assertEquals(4, p.getstoreShelf().shelf());
    // }

    // @Test
    // public void testSearchByIdReturnsCorrectInfo() {
    // main.AddProduct("Cucumber", "Farm", 5, 5, 2.5f,
    // new Position(1, 1), new Position(2, 2));
    // ProductDomain p = main.Search(0);
    // assertEquals("Cucumber", p.getproductName());
    // }

    // @Test
    // public void testMoveProductChangesWarehouseShelf() {
    // main.AddProduct("Apple", "Farm", 5, 5, 2.5f,
    // new Position(1, 1), new Position(2, 2));
    // Position newWarehousePos = new Position(5, 5);
    // main.MoveProduct(0, false, newWarehousePos);
    // ProductDomain p = main.Search(0);
    // assertEquals(5, p.getwareHouseShelf().line());
    // assertEquals(5, p.getwareHouseShelf().shelf());
    // }

    // // --------------- New tests added -----------------

    // @Test
    // public void testAddCategorySuccessfully() {
    // assertDoesNotThrow(() -> main.AddCategory("Drinks"));
    // }

    // @Test
    // public void testAddProductToCategorySuccessfully() {
    // main.AddCategory("Dairy");
    // ProductDTO p = new ProductDTO(9, "Milk", "Tnuva", 5, 20, 4.0f,
    // new Position(1, 2), new Position(3, 4));
    // main.AddProduct(p);
    // main.AddToCategory("Dairy", 9);
    // }

    // @Test
    // public void testAddDiscountToProduct() {
    // ProductDTO p = new ProductDTO(10, "Juice", "Prigat", 10, 20, 5.0f,
    // new Position(1, 1), new Position(2, 2));
    // main.AddProduct(p);
    // DiscountDTO d = new DiscountDTO("Summer Sale", 0.1f, LocalDate.now(),
    // LocalDate.now().plusDays(7), 10);
    // d.setpId(10);
    // assertDoesNotThrow(() -> main.AddDiscount(d));
    // }

    // @Test
    // public void testAddDiscountToCategory() {
    // main.AddCategory("Snacks");
    // DiscountDTO d = new DiscountDTO("Snack Discount", 0.2f, LocalDate.now(),
    // LocalDate.now().plusDays(5), 5);
    // d.setCatName("Snacks");
    // assertDoesNotThrow(() -> main.AddDiscount(d));
    // }

    // @Test
    // public void testAddBadProductInvalidId() {
    // Exception e = assertThrows(IllegalArgumentException.class, () ->
    // main.AddBadProduct(999, 5));
    // assertTrue(e.getMessage().contains("invalid product id"));
    // }

    // @Test
    // public void testUpdateInventorySaleWithDiscount() {
    // ProductDTO p = new ProductDTO(11, "Chips", "Osem", 10, 50, 3.0f,
    // new Position(1, 1), new Position(2, 2));
    // main.AddProduct(p);
    // DiscountDTO d = new DiscountDTO("Chips Discount", 0.5f, LocalDate.now(),
    // LocalDate.now().plusDays(2), 1);
    // d.setpId(11);
    // main.AddDiscount(d);

    // Map<Integer, Integer> products = new HashMap<>();
    // products.put(11, 2);
    // SaleDTO sale = new SaleDTO(products, LocalDate.now());
    // SaleDTO result = main.UpdateInventorySale(sale);
    // assertTrue(result.getsalePrice() < 6.0); // discounted price
    // }

    // @Test
    // public void testSearchByCategoryName() {
    // main.AddCategory("SoftDrinks");
    // ProductDTO p = new ProductDTO(12, "Cola", "CocaCola", 10, 50, 6.0f,
    // new Position(1, 1), new Position(2, 2));
    // main.AddProduct(p);
    // main.AddToCategory("SoftDrinks", 12);
    // assertFalse(main.Search("SoftDrinks").isEmpty());
    // }

    // @Test
    // public void testAddBadProductCausesMissing() {
    // main.AddProduct("Ketchup", "Heinz", 1, 5, 4.5f,
    // new Position(1, 1), new Position(2, 2));
    // String msg = main.AddBadProduct(0, 2);
    // assertTrue(msg.contains("Warning"));
    // }

    // @Test
    // public void testInventoryInitializationDefaultState() {
    // assertDoesNotThrow(() ->
    // main.InventoryInitialization(InitializeState.DEFAULT_STATE));
    // String inventory = main.GetCurrentInventoryReport();
    // assertTrue(inventory.contains("Milk"));
    // }

    // @Test
    // public void testMoveProductInvalidId() {
    // Exception e = assertThrows(IllegalArgumentException.class,
    // () -> main.MoveProduct(12345, true, new Position(3, 3)));
    // assertTrue(e.getMessage().contains("pId invalid"));
    // }

    // @Test
    // public void testAddProductWithSameNameDifferentIdFails() {
    // ProductDTO p1 = new ProductDTO(100, "Honey", "BrandA", 10, 20, 7.0f, new
    // Position(1, 1), new Position(2, 2));
    // ProductDTO p2 = new ProductDTO(101, "Honey", "BrandB", 10, 20, 7.0f, new
    // Position(1, 1), new Position(2, 2));
    // main.AddProduct(p1);
    // Exception e = assertThrows(IllegalArgumentException.class, () ->
    // main.AddProduct(p2));
    // assertTrue(e.getMessage().contains("alredy in stock"));
    // }
}