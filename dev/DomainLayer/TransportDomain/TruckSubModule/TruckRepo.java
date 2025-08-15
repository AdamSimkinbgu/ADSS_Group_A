package DomainLayer.TransportDomain.TruckSubModule;

import java.sql.SQLException;
import java.util.HashMap;

public interface TruckRepo {

    //  Startup from DB function
    public boolean loadDBData() throws SQLException;

    //  Truck related
    public boolean insertTruck(Truck truckForInsertion) throws SQLException;
    public boolean deleteTruck(int truckId) throws SQLException;
    public boolean updateTruckPersistence(Truck updatedTruck) throws SQLException;


    public HashMap<Integer, Truck> getTrucksWareHouse();

}
