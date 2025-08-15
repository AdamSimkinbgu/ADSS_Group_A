package DataAccessLayer.TransportDAL;

import DTOs.TransportModuleDTOs.TruckDTO;
import DataAccessLayer.TransportDAL.Interfaces.TruckDAO;
import DomainLayer.enums.enumDriLicense;
import DomainLayer.enums.enumTranStatus;
import Util.CliUtil;
import Util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JdbcTruckDAO implements TruckDAO {
    private Connection connection;
    public JdbcTruckDAO(Connection connection) throws SQLException { this.connection = connection; }

    @Override
    public boolean insertTruck(TruckDTO truckForInsertion) throws SQLException {
        String sql = "INSERT INTO Trucks (truckNum, model, netWeight, maxCarryWeight, validLicense, inTransportID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, truckForInsertion.getTruck_num());
            ps.setString(2, truckForInsertion.getModel());
            ps.setDouble(3, truckForInsertion.getNet_weight());
            ps.setDouble(4, truckForInsertion.getMax_carry_weight());
            ps.setString(5, truckForInsertion.getValid_license().name());
            ps.setInt(6, truckForInsertion.getInTransportID());
//            ps.setBoolean(7, truckForInsertion.getIsDeleted());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) { return true; }
        }
        return false;
    }


    @Override
    public ArrayList<TruckDTO> getAllTrucks() throws SQLException {
        String sql = "SELECT * FROM Trucks ORDER BY truckNum";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<TruckDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new TruckDTO(
                            rs.getInt("truckNum"),
                            rs.getString("model"),
                            rs.getDouble("netWeight"),
                            rs.getDouble("maxCarryWeight"),
                            enumDriLicense.valueOf(rs.getString("validLicense")),
                            rs.getInt("inTransportID")
                    ));
                }
                return list;
            }
        }
    }


    @Override
    public boolean deleteTruck(int truckNum) throws SQLException {
        String sql = "DELETE FROM Trucks WHERE truckNum = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, truckNum);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }





    @Override
    public boolean updateTruck(int oldTruckNum, TruckDTO updatedTruck) throws SQLException {
        String sql = "UPDATE Trucks SET model = ?, netWeight = ?, maxCarryWeight = ?, " +
                "validLicense = ?, inTransportID = ? " +
                "WHERE truckNum = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, updatedTruck.getModel());
            ps.setDouble(2, updatedTruck.getNet_weight());
            ps.setDouble(3, updatedTruck.getMax_carry_weight());
            ps.setString(4, updatedTruck.getValid_license().name());
            ps.setInt(5, updatedTruck.getInTransportID());
//            ps.setBoolean(6, updatedTruck.getIsDeleted());

            ps.setInt(6, oldTruckNum);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                // update other stuff if needed, but not needed here
                return true;
            }
            return false;
        }
    }










}
