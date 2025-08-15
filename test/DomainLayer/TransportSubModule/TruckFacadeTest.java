package DomainLayer.TransportSubModule;

import Util.TestDatabase;
import DomainLayer.TransportDomain.TruckSubModule.Truck;
import DomainLayer.TransportDomain.TruckSubModule.TruckFacade;
import DomainLayer.enums.enumDriLicense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.management.openmbean.KeyAlreadyExistsException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TruckFacadeTest {

    private TruckFacade truckFacade;

    @BeforeEach
    void setUp() throws SQLException {
        truckFacade = new TruckFacade(TestDatabase.getConnection());
        truckFacade.loadDBData();
    }

    @Test
    void testAddTruckSuccessfully() throws KeyAlreadyExistsException, SQLException, ClassNotFoundException {
        int curr_sz = truckFacade.getTruckRepo().getTrucksWareHouse().size();
        truckFacade.addTruck(1, "Volvo", 4000, 10000, "C");
        assertEquals(curr_sz + 1 , truckFacade.getTruckRepo().getTrucksWareHouse().size());
        Truck truck = truckFacade.getTruckRepo().getTrucksWareHouse().get(1);
        assertNotNull(truck);
        assertEquals(1, truck.getTruck_num());
        assertEquals("Volvo", truck.getModel());
        assertEquals(4000, truck.getNet_weight());
        assertEquals(10000, truck.getMax_carry_weight());
        assertEquals(enumDriLicense.C, truck.getValid_license());
        truckFacade.removeTruck(1);
    }

    @Test
    void testAddTruckAlreadyExists() throws KeyAlreadyExistsException, SQLException, ClassNotFoundException {
        truckFacade.addTruck(1, "Volvo", 4000, 10000, "C");
        assertThrows(KeyAlreadyExistsException.class, () -> {
            truckFacade.addTruck(1, "Mercedes", 3500, 9000, "B");
        });
        truckFacade.removeTruck(1);
    }

    @Test
    void testRemoveTruckSuccessfully() throws Exception {
        int curr_sz = truckFacade.getTruckRepo().getTrucksWareHouse().size();
        truckFacade.addTruck(1, "Volvo", 4000, 10000, "C");
        assertEquals(curr_sz+1, truckFacade.getTruckRepo().getTrucksWareHouse().size());
        truckFacade.removeTruck(1);
        assertEquals(curr_sz, truckFacade.getTruckRepo().getTrucksWareHouse().size());
    }

    @Test
    void testRemoveTruckNotExist() {
        assertThrows(ClassNotFoundException.class, () -> {
            truckFacade.removeTruck(999);
        });
    }

    @Test
    void testRemoveTruckBusyInTransport() throws Exception {
        truckFacade.addTruck(1, "Volvo", 4000, 10000, "C");
        truckFacade.getTruckRepo().getTrucksWareHouse().get(1).setInTransportID(5); // simulate busy truck
        assertThrows(ArrayStoreException.class, () -> {
            truckFacade.removeTruck(1);
        });
        truckFacade.getTruckRepo().getTrucksWareHouse().get(1).setInTransportID(-1);
        truckFacade.removeTruck(1);
    }

    @Test
    void testShowAllTrucksNonEmpty() throws KeyAlreadyExistsException, SQLException, ClassNotFoundException {
        truckFacade.addTruck(1, "Volvo", 4000, 10000, "C");
        truckFacade.addTruck(2, "Mercedes", 3500, 9000, "B");
        String output = truckFacade.showAllTrucks();
        assertTrue(output.contains("Truck Num: 1"));
        assertTrue(output.contains("Truck Num: 2"));
        assertTrue(output.contains("Model: Volvo"));
        assertTrue(output.contains("Model: Mercedes"));
        truckFacade.removeTruck(1);
        truckFacade.removeTruck(2);
    }

}
