package DomainLayer.TransportDomain.TransportSubModule;

import DTOs.TransportModuleDTOs.TransportDTO;
import DomainLayer.enums.enumTranProblem;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public interface TransportsRepo {

    ///  Startup from DB function
    public boolean loadDBData() throws SQLException;

    ///   transport collection related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------
    public HashMap<Integer, TransportDoc> getTransports();
    public boolean insertTransport(TransportDoc transport, boolean isQueued) throws SQLException;
    public boolean deleteTransport(int transportId, int queuedIndexIfWasQueued) throws SQLException;
    public boolean updateTransport(TransportDoc transport) throws SQLException;

    ///  TransportsProblemsTable
    public boolean insertPersistTransportProblem(int transportID, enumTranProblem problem) throws SQLException;
    public boolean removePersistTransportProblem(int transportID, enumTranProblem problem) throws SQLException;


    ///   queuedTransports collection related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------
    public ArrayList<TransportDoc> getQueuedTransports();


    ///   itemsDocs collection related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------
    public HashMap<Integer, ItemsDoc> getItemsDocs();
    public boolean insertItemsDoc(ItemsDoc itemsDoc, boolean alsoInsertInDBNow) throws SQLException;
    public boolean removeItemsDoc(int itemsDocId, boolean alsoRemoveInDBNow) throws SQLException;
    public boolean updateItemsDocPersistency(int oldItemsDocId, ItemsDoc updatedItemsDoc) throws SQLException;


    ///   driverIdToInTransportID collection related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------
    public HashMap<Long, Integer> getDriverIdToInTransportID();
    public boolean insertDriverIdToInTransportID(long driverId, int tranDocId) throws SQLException;
    public boolean removeFromDriverIdToInTransportID(long transportDriverId) throws SQLException;


    ///   transportIDCounter counter related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------
    public int getTransportIDCounter();
    public void setTransportIDCounter(int transportIDCounter) throws SQLException;
    public void incrementTransportIDCounter() throws SQLException;




    ///   Object <--> ObjectDTO conversion functions related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------
    public TransportDTO convertTransportDocToTransportDTO(TransportDoc transportDoc) throws JsonProcessingException;
    public TransportDoc convertTransportDTOToTransportDoc(TransportDTO transport_DTO);


}
