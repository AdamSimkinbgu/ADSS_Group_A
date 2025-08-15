package DataAccessLayer.TransportDAL.Interfaces;

import DTOs.TransportModuleDTOs.ItemQuantityDTO;
import DTOs.TransportModuleDTOs.TruckDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface TruckDAO {

    public boolean insertTruck(TruckDTO truckForInsertion) throws SQLException;
    public ArrayList<TruckDTO> getAllTrucks() throws SQLException;
    public boolean deleteTruck(int truckNum) throws SQLException;
    public boolean updateTruck(int oldTruckNum, TruckDTO updatedTruck) throws SQLException;

}
