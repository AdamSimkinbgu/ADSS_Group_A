//package Tests;
//
//import Domain.MainDomain;
//import Domain.ProductDomain;
//import DTO.*;
//import type.Position;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//import java.time.chrono.ChronoLocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class InventoryTests {
//    private MainDomain main;
//
//    @BeforeEach
//    public void setUp() {
//        main = new MainDomain();
//    }
//
//    @Test
//    public void testAddProductSuccessfully() {
//        ProductDTO p = new ProductDTO(8,"Bread", "Angel", 10, 20, 5.5f, new Position(1, 2), new Position(3, 4))
//        main.AddProduct(p);
//        String report = main.GetMissingReport();
//        assertFalse(report.contains("Bread"));
//    }
//
//    @Test
//    public void testAddProductDuplicateNameThrowsException() {
//        ProductDTO p = new ProductDTO(7,"Bread", "Angel", 10, 20, 5.5f, new Position(1, 2), new Position(3, 4))
//        main.AddProduct(p);
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            main.AddProduct(p);
//        });
//        assertTrue(exception.getMessage().contains("already in stock"));
//    }
//
//    @Test
//    public void testUpdateInventoryRestockValidProduct() {
//        ProductDTO p = new ProductDTO(1,"Butter", "Tnuva", 2, 2, 7.0f, new Position(1,1), new Position(2,2))
//        main.AddProduct(p);
//        LocalDate expDate = LocalDate.now().plusDays(7);
//
//        List<SupplyDTO> ls = new ArrayList<>();
//        ls.add(new SupplyDTO(99, 10, expDate));
//        main.DeliverOrder(ls);
//        assertDoesNotThrow(() -> main.UpdateInventoryRestock());
//    }
//
//    @Test
//    public void testUpdateInventoryRestockInvalidProductThrowsException() {
//        LocalDate expDate = LocalDate.now().plusDays(7);
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            List<SupplyDTO> ls = new ArrayList<>();
//            ls.add(new SupplyDTO(99, 10, expDate));
//            main.DeliverOrder(ls);
//            main.UpdateInventoryRestock();
//        });
//        assertTrue(exception.getMessage().contains("no product with this ip"));
//    }
//
//    @Test
//    public void testGetMissingReportWithMissingProduct() {
//        main.AddProduct("Cheese", "Tnuva", 0, 0, 6.0f, new Position(1,1), new Position(2,2));
//        String report = main.GetMissingReport();
//        assertFalse(report.contains("Cheese"));
//    }
//
//    @Test
//    public void testGetBadReportWithNoBadProducts() {
//        main.AddProduct("Yogurt", "Tara", 10, 10, 4.5f, new Position(1,1), new Position(2,2));
//        String report = main.GetBadReport();
//        assertFalse(report.contains("Yogurt"));
//    }
//
//    @Test
//    public void testAddBadProductAffectsReport() {
//        main.AddProduct("Eggs", "Local", 10, 10, 12.0f, new Position(1,1), new Position(2,2));
//        main.AddBadProduct(0, 3);
//        String report = main.GetBadReport();
//        assertFalse(report.contains("Eggs"));
//    }
//
//    @Test
//    public void testMoveProductChangesStoreShelf() {
//        main.AddProduct("Tomato", "Farm", 5, 5, 3.0f, new Position(0,0), new Position(1,1));
//        Position newPos = new Position(4,4);
//        main.MoveProduct(0, true, newPos);
//
//    }
//
//    @Test
//    public void testSearchByIdReturnsCorrectInfo() {
//        main.AddProduct("Cucumber", "Farm", 5, 5, 2.5f, new Position(1,1), new Position(2,2));
//        ProductDomain p = main.Search(0);
//        assertTrue(p.getproductName().contains("Cucumber"));
//    }
//
//    @Test
//    public void testMoveProductChangesWarehouseShelf() {
//        main.AddProduct("Apple", "Farm", 5, 5, 2.5f, new Position(1,1), new Position(2,2));
//        Position newWarehousePos = new Position(5,5);
//        main.MoveProduct(0, false, newWarehousePos);
//        ProductDomain p = main.Search(0);
//        assertEquals(5, p.getwareHouseShelf().line());
//        assertEquals(5, p.getwareHouseShelf().shelf());
//    }
//
//
//
//}