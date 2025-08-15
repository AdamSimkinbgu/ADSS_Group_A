package DataAccessLayer.TransportDAL.Interfaces;

import DTOs.TransportModuleDTOs.ShippingAreaDTO;
import DTOs.TransportModuleDTOs.SiteDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface SiteDAO {

    /// Sites
    public boolean insertSite(SiteDTO siteForInsertion) throws SQLException;
    public boolean deleteSite(int siteArea, String addressString) throws SQLException;
    public boolean updateSite(int oldSiteAreaNum, String oldSiteAddressString, SiteDTO updatedSite) throws SQLException;
    public SiteDTO getSiteByAreaNumAndSiteAddressString(int SiteArea, String SiteString) throws SQLException;
    public ArrayList<SiteDTO> getSitesInAreaNum(int areaNum) throws SQLException;

    ///  Shipping Areas
    public boolean insertShippingArea(ShippingAreaDTO shippingAreaForInsertion) throws SQLException;
    public boolean deleteShippingAreaById(int areaNum) throws SQLException;
    public boolean updateShippingArea(int oldShippingAreaNum, ShippingAreaDTO updatedShippingArea) throws SQLException;
    public ArrayList<ShippingAreaDTO> getAllShippingAreas() throws SQLException;

}
