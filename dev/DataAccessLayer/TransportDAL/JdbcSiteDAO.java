package DataAccessLayer.TransportDAL;

import DTOs.TransportModuleDTOs.ShippingAreaDTO;
import DTOs.TransportModuleDTOs.SiteDTO;
import DataAccessLayer.TransportDAL.Interfaces.SiteDAO;
import Util.CliUtil;
import Util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDAO implements SiteDAO {
    private Connection connection;
    public JdbcSiteDAO(Connection connection) throws SQLException { this.connection = connection; }


    /// Sites              <<<-----------------------------------


    @Override
    public boolean insertSite(SiteDTO siteForInsertion) throws SQLException {
        String sql = "INSERT INTO Sites (areaNum, addressStr, contName, contNumber) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, siteForInsertion.getSiteAreaNum());
            ps.setString(2, siteForInsertion.getSiteAddressString());
            ps.setString(3, siteForInsertion.getcName());
            ps.setLong(4, siteForInsertion.getcNumber());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) { return true; }
        }
        return false;
    }



    @Override
    public boolean deleteSite(int siteArea, String addressString) throws SQLException {
        String sql = "DELETE FROM Sites WHERE areaNum = ? AND addressStr = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, siteArea);
            ps.setString(2, addressString);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }




    @Override
    public boolean updateSite(int oldSiteAreaNum, String oldSiteAddressString, SiteDTO updatedSite) throws SQLException {
        String sql = "UPDATE Sites SET areaNum = ?, addressStr = ?, contName = ?, " +
                "contNumber = ? WHERE areaNum = ? AND addressStr = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, updatedSite.getSiteAreaNum());
            ps.setString(2, updatedSite.getSiteAddressString());
            ps.setString(3, updatedSite.getcName());
            ps.setLong(4, updatedSite.getcNumber());

            ps.setInt(5, oldSiteAreaNum);
            ps.setString(6, oldSiteAddressString);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                // Update site details in other places not needed because these other places are set as ON UPDATE CASCADE
                return true;
            }
            return false;
        }
    }

    @Override
    public SiteDTO getSiteByAreaNumAndSiteAddressString(int SiteArea, String SiteString) throws SQLException{
        String sql = "SELECT * FROM Sites WHERE areaNum = ? AND addressStr = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, SiteArea);
            ps.setString(2, SiteString);

            try (ResultSet rs = ps.executeQuery()) {
                SiteDTO resSite = null;
                if (rs.next()) {
                    resSite = new SiteDTO(
                            rs.getInt("areaNum"),
                            rs.getString("addressStr"),
                            rs.getString("contName"),
                            rs.getLong("contNumber")
                    );
                    return resSite;
                }
                return null;
            }
        }
    }


    @Override
    public ArrayList<SiteDTO> getSitesInAreaNum(int areaNum) throws SQLException {
        String sql = "SELECT * FROM Sites WHERE areaNum = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, areaNum);

            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<SiteDTO> resList = new ArrayList<>();
                SiteDTO resSite = null;
                while (rs.next()) {
                    resSite = new SiteDTO(
                            rs.getInt("areaNum"),
                            rs.getString("addressStr"),
                            rs.getString("contName"),
                            rs.getLong("contNumber")
                    );
                    resList.add(resSite);
                }
                return resList;
            }
        }
    }





    ///  Shipping Areas              <<<-----------------------------------



    @Override
    public boolean insertShippingArea(ShippingAreaDTO shippingAreaForInsertion) throws SQLException {
        String sql = "INSERT INTO ShippingAreas (areaNumber, areaName) " +
                "VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, shippingAreaForInsertion.getArea_number());
            ps.setString(2, shippingAreaForInsertion.getArea_name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) { return true; }
        }
        return false;
    }



    @Override
    public ArrayList<ShippingAreaDTO> getAllShippingAreas() throws SQLException {
        String sql = "SELECT * FROM ShippingAreas ORDER BY areaNumber";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<ShippingAreaDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new ShippingAreaDTO(
                            rs.getInt("areaNumber"),
                            rs.getString("areaName")
                    ));
                }
                return list;
            }
        }
    }



    @Override
    public boolean deleteShippingAreaById(int areaNum) throws SQLException {
        String sql = "DELETE FROM ShippingAreas WHERE areaNumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, areaNum);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }





    @Override
    public boolean updateShippingArea(int oldShippingAreaNum, ShippingAreaDTO updatedShippingArea) throws SQLException {
        String sql = "UPDATE ShippingAreas SET areaNumber = ?, areaName = ? WHERE areaNumber = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, updatedShippingArea.getArea_number());
            pstmt.setString(2, updatedShippingArea.getArea_name());

            pstmt.setInt(3, oldShippingAreaNum);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Update ShippingArea details in other places not needed because these other places are set as ON UPDATE CASCADE
                return true;
            }
            return false;
        }
    }









}


