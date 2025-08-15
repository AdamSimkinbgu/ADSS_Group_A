package DataAccessLayer.TransportDAL.Interfaces;

import DTOs.TransportModuleDTOs.ItemQuantityDTO;
import DTOs.TransportModuleDTOs.ItemsDocDTO;
import DTOs.TransportModuleDTOs.TransportDTO;
import DomainLayer.enums.enumTranProblem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TransportDAO {

    /// ItemQs Table.
    public boolean insertItemQ(ItemQuantityDTO itemQForInsertion) throws SQLException;
    public boolean deleteItemQ(int itemInItemsDocId, String itemName, double itemWeight, boolean itemCond) throws SQLException;
    ///   to delete some of the item's amount, we'll use the updatedItemQ function.
    public boolean updateItemQ(ItemQuantityDTO oldItemQ, ItemQuantityDTO newItemQ) throws SQLException;  ///  according to the primary keys in the DTO, updated/set everything else (GENERAL UPDATER)
    public ArrayList<ItemQuantityDTO> getItemQsOfItemsDocId(int ItemsDocId) throws SQLException;

    /// ItemsDocs Table.
    public boolean insertItemsDoc(ItemsDocDTO itemsDocForInsertion) throws SQLException;
    public boolean deleteItemsDocById(int itemsDocId) throws SQLException;
    public boolean updateItemsDoc(int oldItemsDocNum, ItemsDocDTO updatedItemsDoc) throws SQLException;  ///  according to the primary keys in the DTO, updated/set everything else (GENERAL UPDATER)
    public ArrayList<ItemsDocDTO> getItemsDocOfTransportId(int TransportId) throws SQLException;

    /// Transports Table.
    public boolean insertTransport(TransportDTO transportForInsertion, boolean isQueued) throws SQLException;
    public boolean deleteTransportById(int transportId) throws SQLException;
    public boolean updateTransport(int oldTransportId, TransportDTO updatedTransport) throws SQLException;  ///  according to the primary keys in the DTO, updated/set everything else (GENERAL UPDATER)
    public ArrayList<TransportDTO> getAllTransports(boolean isQueued) throws SQLException;  ///  this getting all function can be used for LOADING DATA   <<----------  because gets everything

    public boolean insertTransportProblem(int transportID, enumTranProblem problem) throws SQLException;
    public boolean removeTransportProblem(int transportID, enumTranProblem problem) throws SQLException;

    /// DriverIdToInTransportID Table.
    public boolean insertToDriverIdToInTransportID(long driverId, int inTransportId) throws SQLException;
    public boolean deleteFromDriverIdToInTransportID(long driverId) throws SQLException;
    public ArrayList<ArrayList<Long>> getAllDriverIdToInTransportIDsDuos() throws SQLException;

    /// Counters Table.
    public boolean insertCounter(String counterName, int counterValue) throws SQLException;
    public boolean deleteCounter(String counterName) throws SQLException;
    public boolean updateCounter(String counterName, int newCounterValue) throws SQLException;
    public int getCounterValue(String counterName) throws SQLException;


}
