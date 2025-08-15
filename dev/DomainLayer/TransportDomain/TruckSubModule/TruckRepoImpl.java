package DomainLayer.TransportDomain.TruckSubModule;

import DTOs.TransportModuleDTOs.TruckDTO;
import DataAccessLayer.TransportDAL.Interfaces.TruckDAO;
import DataAccessLayer.TransportDAL.JdbcTruckDAO;
import Util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TruckRepoImpl implements TruckRepo {
    private TruckDAO truckDAO;
    private HashMap<Integer, Truck> trucksWareHouse;

    public TruckRepoImpl() throws SQLException {
        this.truckDAO = new JdbcTruckDAO(Database.getConnection());
        trucksWareHouse = new HashMap<>();
    }

    public TruckRepoImpl(Connection connection) throws SQLException {
        this.truckDAO = new JdbcTruckDAO(connection);
        trucksWareHouse = new HashMap<>();
    }

    public HashMap<Integer, Truck> getTrucksWareHouse() {  return trucksWareHouse;  }

    @Override
    public boolean loadDBData() throws SQLException {
        ArrayList<TruckDTO> allTrucksFromDB = truckDAO.getAllTrucks();
        for (TruckDTO truckDTO : allTrucksFromDB) {
            Truck truckPulledFromDB = convertTruckDTOToTruck(truckDTO);
            trucksWareHouse.put(truckPulledFromDB.getTruck_num(), truckPulledFromDB);
        }
        return true;
    }


    @Override
    public boolean insertTruck(Truck truckForInsertion) throws SQLException {
        this.trucksWareHouse.put(truckForInsertion.getTruck_num(), truckForInsertion);
        return truckDAO.insertTruck(convertTruckToTruckDTO(truckForInsertion));
    }

    @Override
    public boolean deleteTruck(int truckId) throws SQLException {
        this.trucksWareHouse.remove(truckId);
        return truckDAO.deleteTruck(truckId);
    }

    @Override
    public boolean updateTruckPersistence(Truck updatedTruck) throws SQLException {
        return this.truckDAO.updateTruck(updatedTruck.getTruck_num(), convertTruckToTruckDTO(updatedTruck));
    }


    //   Object <--> ObjectDTO conversion functions

    private TruckDTO convertTruckToTruckDTO(Truck t){
        return new TruckDTO(t.getTruck_num(), t.getModel(), t.getNet_weight(), t.getMax_carry_weight(), t.getValid_license(), t.getInTransportID());
    }   ///  from both of these I removed the isDeleted because it doesn't need to get persisted, it's only from truck objects roaming around docs.
    private Truck convertTruckDTOToTruck(TruckDTO t){
        return new Truck(t.getTruck_num(), t.getModel(), t.getNet_weight(), t.getMax_carry_weight(), t.getValid_license(), t.getInTransportID());  // isDeleted arg not needed because was already deleted from DB
    }





}
