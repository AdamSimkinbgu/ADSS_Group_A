/*package Tests;

import Domain.MainDomain;
import type.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTests {
    private MainDomain main;

    @BeforeEach
    public void setUp() {
        main = new MainDomain();
    }

    @Test
    public void testAddProductSuccessfully() {
        main.AddProduct("Bread", "Angel", 10, 20, 5.5f, new Position(1, 2), new Position(3, 4));
        String report = main.GetMissingReport();
        assertFalse(report.contains("Bread"));
    }

    @Test
    public void testAddProductDuplicateNameThrowsException() {
        main.AddProduct("Bread", "Angel", 10, 20, 5.5f, new Position(1, 2), new Position(3, 4));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            main.AddProduct("Bread", "Another", 5, 10, 4.0f, new Position(2, 3), new Position(4, 5));
        });
        assertTrue(exception.getMessage().contains("alredy in stock"));
    }

    @Test
    public void testUpdateInventoryRestockValidProduct() {
        main.AddProduct("Butter", "Tnuva", 2, 2, 7.0f, new Position(1,1), new Position(2,2));
        ChronoLocalDate expDate = ChronoLocalDate.from(LocalDate.now().plusDays(7));
        assertDoesNotThrow(() -> main.UpdateInventoryRestock(0, 5, expDate));
    }

    @Test
    public void testUpdateInventoryRestockInvalidProductThrowsException() {
        ChronoLocalDate expDate = ChronoLocalDate.from(LocalDate.now().plusDays(7));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            main.UpdateInventoryRestock(99, 10, expDate);
        });
        assertTrue(exception.getMessage().contains("no product with this ip"));
    }

    @Test
    public void testGetMissingReportWithMissingProduct() {
        main.AddProduct("Cheese", "Tnuva", 0, 0, 6.0f, new Position(1,1), new Position(2,2));
        String report = main.GetMissingReport();
        assertTrue(report.contains("Cheese"));
    }

    @Test
    public void testGetBadReportWithNoBadProducts() {
        main.AddProduct("Yogurt", "Tara", 10, 10, 4.5f, new Position(1,1), new Position(2,2));
        String report = main.GetBadReport();
        assertFalse(report.contains("Yogurt"));
    }

    @Test
    public void testAddBadProductAffectsReport() {
        main.AddProduct("Eggs", "Local", 10, 10, 12.0f, new Position(1,1), new Position(2,2));
        main.AddBadProduct(0, 3);
        String report = main.GetBadReport();
        assertTrue(report.contains("Eggs"));
    }

    @Test
    public void testMoveProductChangesStoreShelf() {
        main.AddProduct("Tomato", "Farm", 5, 5, 3.0f, new Position(0,0), new Position(1,1));
        Position newPos = new Position(4,4);
        main.MoveProduct(0, true, newPos);
        
    }

    @Test
    public void testSearchByIdReturnsCorrectInfo() {
        main.AddProduct("Cucumber", "Farm", 5, 5, 2.5f, new Position(1,1), new Position(2,2));
        String result = main.Search(0);
    }

    @Test
    public void testSearchByNameReturnsCorrectInfo() {
        main.AddProduct("Lettuce", "Farm", 5, 5, 2.0f, new Position(1,1), new Position(2,2));
        String result = main.Search("Lettuce");
    }
}*/