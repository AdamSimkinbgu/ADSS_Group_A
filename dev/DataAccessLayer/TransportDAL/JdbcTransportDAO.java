package DataAccessLayer.TransportDAL;

import DTOs.TransportModuleDTOs.ItemDTO;
import DTOs.TransportModuleDTOs.ItemQuantityDTO;
import DTOs.TransportModuleDTOs.ItemsDocDTO;
import DTOs.TransportModuleDTOs.TransportDTO;
import DataAccessLayer.TransportDAL.Interfaces.SiteDAO;
import DataAccessLayer.TransportDAL.Interfaces.TransportDAO;
import DomainLayer.enums.enumTranProblem;
import DomainLayer.enums.enumTranStatus;
import Util.CliUtil;
import Util.Database;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JdbcTransportDAO implements TransportDAO {
    private Connection connection;
    private SiteDAO siteDAO;
    public JdbcTransportDAO(Connection connection) throws SQLException {
        this.connection = connection;
        this.siteDAO = new JdbcSiteDAO(connection);
    }




    ///    ItemQs     <<<----------------------------      <<------------------


    @Override
    public boolean insertItemQ(ItemQuantityDTO itemQForInsertion) throws SQLException {
        String sql = "INSERT INTO ItemsQ (itemInItemsDocId, name, weight, condition, amount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemQForInsertion.getItemInItemsDocId());
            ps.setString(2, itemQForInsertion.getItem().getName());
            ps.setDouble(3, itemQForInsertion.getItem().getWeight());
            ps.setBoolean(4, itemQForInsertion.getItem().getCondition());
            ps.setInt(5, itemQForInsertion.getQuantity());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) { return true; }
        }
        return false;
    }



    @Override
    public ArrayList<ItemQuantityDTO> getItemQsOfItemsDocId(int ItemsDocId) throws SQLException {
        String sql = "SELECT * FROM ItemsQ WHERE itemInItemsDocId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ItemsDocId);

            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<ItemQuantityDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new ItemQuantityDTO(
                            rs.getInt("itemInItemsDocId"),
                            new ItemDTO(rs.getString("name"), rs.getDouble("weight"), rs.getBoolean("condition")),
                            rs.getInt("amount")));
                }
                return list;
            }
        }
    }



    @Override
    public boolean deleteItemQ(int itemInItemsDocId, String itemName, double itemWeight, boolean itemCond) throws SQLException {
        String sql = "DELETE FROM ItemsQ WHERE itemInItemsDocId = ? AND name = ? AND weight = ? AND condition = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemInItemsDocId);
            ps.setString(2, itemName);
            ps.setDouble(3, itemWeight);
            ps.setBoolean(4, itemCond);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }



    ///   used to update an Item's condition
    @Override
    public boolean updateItemQ(ItemQuantityDTO oldItemQ, ItemQuantityDTO newItemQ) throws SQLException {
        String sql = "UPDATE ItemsQ SET itemInItemsDocId = ?, name = ?, weight = ?, " +
                "condition = ?, amount = ? WHERE itemInItemsDocId = ? AND name = ? AND weight = ? AND condition = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // New values
            ps.setInt(1, newItemQ.getItemInItemsDocId());
            ps.setString(2, newItemQ.getItem().getName());
            ps.setDouble(3, newItemQ.getItem().getWeight());
            ps.setBoolean(4, newItemQ.getItem().getCondition());
            ps.setInt(5, newItemQ.getQuantity());

            // Old values (primary key match)
            ps.setInt(6, oldItemQ.getItemInItemsDocId());
            ps.setString(7, oldItemQ.getItem().getName());
            ps.setDouble(8, oldItemQ.getItem().getWeight());
            ps.setBoolean(9, oldItemQ.getItem().getCondition());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }






    ///    ItemsDocs     <<<----------------------------      <<------------------


    @Override
    public boolean insertItemsDoc(ItemsDocDTO itemsDocForInsertion) throws SQLException {
        String sql = "INSERT INTO ItemsDocs (itemsDocNum, ItemsDocInTransportID, srcSiteArea, srcSiteString, destSiteArea, destSiteString, estimatedArrivalTime) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemsDocForInsertion.getItemsDoc_num());
            ps.setInt(2, itemsDocForInsertion.getItemsDocInTransportID());
            ps.setInt(3, itemsDocForInsertion.getSrc_siteDTO().getSiteAreaNum());
            ps.setString(4, itemsDocForInsertion.getSrc_siteDTO().getSiteAddressString());
            ps.setInt(5, itemsDocForInsertion.getDest_siteDTO().getSiteAreaNum());
            ps.setString(6, itemsDocForInsertion.getDest_siteDTO().getSiteAddressString());
            ps.setTimestamp(7, Timestamp.valueOf(itemsDocForInsertion.getEstimatedArrivalTime()));   ///  I also found with c the reverse one, so ok.

            int affectedRows = ps.executeUpdate();
            // if ItemsDoc line was added, add it's ItemsQs:
            if (affectedRows > 0) {
                for (ItemQuantityDTO itemQuantityDTO : itemsDocForInsertion.getItemQuantityDTOs()){
                    insertItemQ(itemQuantityDTO);
                }
                return true;
            }
        }

        return false;
    }



    @Override
    public ArrayList<ItemsDocDTO> getItemsDocOfTransportId(int TransportId) throws SQLException {
        String sql = "SELECT * FROM ItemsDocs WHERE ItemsDocInTransportID = ? ORDER BY itemsDocNum";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, TransportId);

            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<ItemsDocDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new ItemsDocDTO(
                            rs.getInt("itemsDocNum"),
                            siteDAO.getSiteByAreaNumAndSiteAddressString(rs.getInt("srcSiteArea"), rs.getString("srcSiteString")),
                            siteDAO.getSiteByAreaNumAndSiteAddressString(rs.getInt("destSiteArea"), rs.getString("destSiteString")),
                            getItemQsOfItemsDocId(rs.getInt("itemsDocNum")),
                            rs.getTimestamp("estimatedArrivalTime").toLocalDateTime(),
                            TransportId
                    ));
                }
                return list;
            }
        }
    }




    private boolean deleteItemsDocItemQsByItemsDocId(int itemsDocId) throws SQLException {    //  helper function
        String sql = "DELETE FROM ItemsQ WHERE itemInItemsDocId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemsDocId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteItemsDocById(int itemsDocId) throws SQLException {
        // First delete related data
        deleteItemsDocItemQsByItemsDocId(itemsDocId);
        // Then delete
        String sql = "DELETE FROM ItemsDocs WHERE itemsDocNum = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemsDocId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }



    @Override
    public boolean updateItemsDoc(int oldItemsDocNum, ItemsDocDTO updatedItemsDoc) throws SQLException {
        String sql = "UPDATE ItemsDocs SET itemsDocNum = ?, ItemsDocInTransportID = ?, srcSiteArea = ?, " +
                "srcSiteString = ?, destSiteArea = ?, destSiteString = ?, estimatedArrivalTime = ? " +
                "WHERE itemsDocNum = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, updatedItemsDoc.getItemsDoc_num());
            ps.setInt(2, updatedItemsDoc.getItemsDocInTransportID());
            ps.setInt(3, updatedItemsDoc.getSrc_siteDTO().getSiteAreaNum());
            ps.setString(4, updatedItemsDoc.getSrc_siteDTO().getSiteAddressString());
            ps.setInt(5, updatedItemsDoc.getDest_siteDTO().getSiteAreaNum());
            ps.setString(6, updatedItemsDoc.getDest_siteDTO().getSiteAddressString());
            ps.setTimestamp(7, Timestamp.valueOf(updatedItemsDoc.getEstimatedArrivalTime()));

            ps.setInt(8, oldItemsDocNum);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                // Update item's itemInItemsDocId function not needed here becasue the setting on it are ON UPDATE CASCADE in the itemQs
                return true;
            }
            return false;
        }
    }




    ///    Transports     <<<----------------------------      <<------------------


    @Override
    public boolean insertTransport(TransportDTO transportForInsertion, boolean isQueued) throws SQLException {
        String sql = "INSERT INTO Transports (tranDocId, status, departure_dt, transportTruckNumber, transportDriverId, " +
                "truck_Depart_Weight, srcSiteArea, srcSiteString, isQueued) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transportForInsertion.getTransport_ID());
            ps.setString(2, transportForInsertion.getStatus().name());   ///  I also found with c the reverse one, so ok.
            ps.setTimestamp(3, Timestamp.valueOf(transportForInsertion.getDeparture_dt()));   ///  I also found with c the reverse one, so ok.
            ps.setInt(4, transportForInsertion.getTransportTruckNum());
            ps.setLong(5, transportForInsertion.getTransportDriverID());
            ps.setDouble(6, transportForInsertion.getTruck_Depart_Weight());
            ps.setInt(7, transportForInsertion.getSrc_site().getSiteAreaNum());
            ps.setString(8, transportForInsertion.getSrc_site().getSiteAddressString());
            ps.setBoolean(9, isQueued);

            int affectedRows = ps.executeUpdate();
            //  if insertion of transport row worked then:
            if (affectedRows > 0) {
                // add the itemsdocs inside it and the itemQs inside each one of them
                for (ItemsDocDTO itemsDocDTO : transportForInsertion.getDests_Docs()){
                    insertItemsDoc(itemsDocDTO);
                }

                // inserting the Transport's Problems
                insertTransportProblems(transportForInsertion);
                return true;
            }
        }
        return false;
    }


    public boolean insertTransportProblem(int transportID, enumTranProblem problem) throws SQLException {
        String probSql = "INSERT INTO TransportsProblems (problemOfTranDocId, problem) VALUES (?, ?)";
        try (PreparedStatement psprob = connection.prepareStatement(probSql)) {
            psprob.setInt(1, transportID);
            psprob.setString(2, problem.name());
            psprob.executeUpdate();
        }
        return true;
    }


    private boolean insertTransportProblems(TransportDTO transportDTO) throws SQLException {
        String probSql = "";  // just for initialization
        for (enumTranProblem problem : transportDTO.getProblems()) {
            probSql = "INSERT INTO TransportsProblems (problemOfTranDocId, problem) VALUES (?, ?)";
            try (PreparedStatement psprob = connection.prepareStatement(probSql)) {
                psprob.setInt(1, transportDTO.getTransport_ID());
                psprob.setString(2, problem.name());
                psprob.executeUpdate();
            }
        }
        return true;
    }



    private ArrayList<enumTranProblem> getProblemsOfTransportId(int transportId) throws SQLException {    //  helper function
        String sql = "SELECT * FROM TransportsProblems WHERE problemOfTranDocId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transportId);

            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<enumTranProblem> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(enumTranProblem.valueOf(rs.getString("problem")));
                }
                return list;
            }
        }
    }


    @Override
    public ArrayList<TransportDTO> getAllTransports(boolean isQueued) throws SQLException {
        String sql = "SELECT * FROM Transports WHERE isQueued = ? ORDER BY tranDocId";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, isQueued);

            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<TransportDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new TransportDTO(
                            rs.getInt("tranDocId"),
                            rs.getInt("transportTruckNumber"),
                            rs.getInt("transportDriverId"),
                            siteDAO.getSiteByAreaNumAndSiteAddressString(rs.getInt("srcSiteArea"), rs.getString("srcSiteString")),
                            getItemsDocOfTransportId(rs.getInt("tranDocId")),
                            rs.getTimestamp("departure_dt").toLocalDateTime(),
                            enumTranStatus.valueOf(rs.getString("status")),
                            rs.getDouble("truck_Depart_Weight"),
                            getProblemsOfTransportId(rs.getInt("tranDocId"))
                    ));
                }
                return list;
            }
        }
    }



    public boolean removeTransportProblem(int transportID, enumTranProblem problem) throws SQLException {
        String sql = "DELETE FROM TransportsProblems WHERE problemOfTranDocId = ? AND problem = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transportID);
            ps.setString(2, problem.name());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }


    private boolean deleteTransportsProblemsByTransportId(int transportId) throws SQLException {    //  helper function
        String sql = "DELETE FROM TransportsProblems WHERE problemOfTranDocId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transportId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    private boolean deleteTransportItemsDocsByTransportId(int transportId) throws SQLException {    //  helper function
        ArrayList<ItemsDocDTO> itemsDocDTOsOfTransport = getItemsDocOfTransportId(transportId);
        for (ItemsDocDTO itemsDocDTO : itemsDocDTOsOfTransport){
            deleteItemsDocById(itemsDocDTO.getItemsDoc_num());
        }
        return true;
    }

    @Override
    public boolean deleteTransportById(int transportId) throws SQLException {
        // First delete related data
        deleteTransportsProblemsByTransportId(transportId);
        deleteTransportItemsDocsByTransportId(transportId);
        // Then delete
        String sql = "DELETE FROM Transports WHERE tranDocId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transportId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }





    @Override
    public boolean updateTransport(int oldTransportId, TransportDTO updatedTransport) throws SQLException {
        String sql = "UPDATE Transports SET status = ?, departure_dt = ?, transportTruckNumber = ?, " +
                "transportDriverId = ?, truck_Depart_Weight = ?, srcSiteArea = ?, srcSiteString = ? " +
                "WHERE tranDocId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, updatedTransport.getStatus().name());
            ps.setTimestamp(2, Timestamp.valueOf(updatedTransport.getDeparture_dt()));
            ps.setInt(3, updatedTransport.getTransportTruckNum());
            ps.setLong(4, updatedTransport.getTransportDriverID());
            ps.setDouble(5, updatedTransport.getTruck_Depart_Weight());
            ps.setInt(6, updatedTransport.getSrc_site().getSiteAreaNum());
            ps.setString(7, updatedTransport.getSrc_site().getSiteAddressString());

            ps.setInt(8, oldTransportId);    // even though it cannot change but just because

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                // Update itemDoc's ItemsDocInTransportID function not needed here because the setting on it is ON UPDATE CASCADE in the ItemsDocs
                // also the TransportID cannot change so even better.

                // delete and insert problems (updating problems list)
                deleteTransportsProblemsByTransportId(oldTransportId);
                insertTransportProblems(updatedTransport);
                return true;
            }
            return false;
        }
    }







    ///            DriverIdToInTransportID      <<<---------------------------------------


    @Override
    public boolean insertToDriverIdToInTransportID(long driverId, int inTransportId) throws SQLException {
        String sql = "INSERT INTO DriverIdToInTransportID (transportDriverId, transportId) " +
                "VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, driverId);
            ps.setInt(2, inTransportId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }



    @Override
    public boolean deleteFromDriverIdToInTransportID(long driverId) throws SQLException {
        String sql = "DELETE FROM DriverIdToInTransportID WHERE transportDriverId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, driverId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }



    @Override
    public ArrayList<ArrayList<Long>> getAllDriverIdToInTransportIDsDuos() throws SQLException {
        String sql = "SELECT * FROM DriverIdToInTransportID ORDER BY transportDriverId";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<ArrayList<Long>> list = new ArrayList<>();
                while (rs.next()) {
                    ArrayList<Long> currDuo = new ArrayList<>();
                    currDuo.add(rs.getLong("transportDriverId"));
                    currDuo.add(rs.getLong("transportId"));
                    list.add(currDuo);
                }
                return list;
            }
        }
    }






    ///                 Counters      <<<---------------------------------------



    @Override
    public boolean insertCounter(String counterName, int counterValue) throws SQLException {
        String sql = "INSERT INTO Counters (CounterName, CounterValue) " +
                "VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, counterName);
            ps.setInt(2, counterValue);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }



    @Override
    public int getCounterValue(String counterName) throws SQLException {
        String sql = "SELECT * FROM Counters WHERE CounterName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, counterName);   //  should be only 1 with that CounterName.
            try (ResultSet rs = ps.executeQuery()) {
                int res = -1;
                if (rs.next()) { res = rs.getInt("CounterValue"); }
                return res;
            }
        }
    }



    @Override
    public boolean deleteCounter(String counterName) throws SQLException {
        String sql = "DELETE FROM Counters WHERE CounterName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, counterName);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }


    @Override
    public boolean updateCounter(String counterName, int newCounterValue) throws SQLException {
        String sql = "UPDATE Counters SET CounterValue = ? " +
                "WHERE CounterName = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newCounterValue);
            ps.setString(2, counterName);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }








}
