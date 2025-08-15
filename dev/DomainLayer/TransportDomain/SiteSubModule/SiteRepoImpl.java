package DomainLayer.TransportDomain.SiteSubModule;

import DTOs.TransportModuleDTOs.ShippingAreaDTO;
import DTOs.TransportModuleDTOs.SiteDTO;
import DataAccessLayer.TransportDAL.Interfaces.SiteDAO;
import DataAccessLayer.TransportDAL.JdbcSiteDAO;
import Util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class SiteRepoImpl implements SiteRepo {
    private SiteDAO siteDAO;
    private HashMap<Integer, ShippingArea> shippingAreas;

    public SiteRepoImpl() throws SQLException {
        this.siteDAO = new JdbcSiteDAO(Database.getConnection());
        shippingAreas = new HashMap<Integer, ShippingArea>();
    }

    public SiteRepoImpl(Connection connection) throws SQLException {
        this.siteDAO = new JdbcSiteDAO(connection);
        shippingAreas = new HashMap<Integer, ShippingArea>();
    }

    @Override
    public boolean loadDBData() throws SQLException {
        for (ShippingAreaDTO shippingAreaDTO : siteDAO.getAllShippingAreas()){
            this.shippingAreas.put(shippingAreaDTO.getArea_number(), convertShippingAreaDTOToShippingArea(shippingAreaDTO));
            // now we'll load all of the sites of the current shipping area into it
            for (SiteDTO siteDTO : siteDAO.getSitesInAreaNum(shippingAreaDTO.getArea_number())){
                this.shippingAreas.get(shippingAreaDTO.getArea_number()).addSite(siteDTO.getSiteAddressString(), siteDTO.getcName(), siteDTO.getcNumber());
            }
        }
        return true;
    }


    //  Shipping Areas Related       <<<----------------------------------------        <<----------------------------------


    @Override
    public boolean insertShippingArea(ShippingArea newShippingAreaToInsert) throws SQLException {
        shippingAreas.put(newShippingAreaToInsert.getArea_number(), newShippingAreaToInsert);
        return this.siteDAO.insertShippingArea(convertShippingAreaToShippingAreaDTO(newShippingAreaToInsert));
    }



    @Override
    public boolean deleteShippingArea(int shippingAreaToDeleteNum) throws SQLException {
        shippingAreas.remove(shippingAreaToDeleteNum);
        return siteDAO.deleteShippingAreaById(shippingAreaToDeleteNum);
    }



    @Override
    public boolean updateShippingAreaPersistence(int oldAreaNum, ShippingArea newShippingAreaToUpdate) throws SQLException {
        return siteDAO.updateShippingArea(oldAreaNum, convertShippingAreaToShippingAreaDTO(newShippingAreaToUpdate));
    }






    //  Sites Related       <<<----------------------------------------        <<----------------------------------


    @Override
    public boolean insertSite(Site siteToInsert) throws SQLException {
        shippingAreas.get(siteToInsert.getAddress().getArea()).addSite(siteToInsert.getAddress().getAddress(), siteToInsert.getcName(), siteToInsert.getcNumber());
        return siteDAO.insertSite(convertSiteToSiteDTO(siteToInsert));
    }


    @Override
    public boolean deleteSite(int siteToDeleteArea, String siteToDeleteAddressString) throws SQLException {
        shippingAreas.get(siteToDeleteArea).getSites().remove(siteToDeleteAddressString);
        return siteDAO.deleteSite(siteToDeleteArea, siteToDeleteAddressString);
    }


    @Override
    public boolean updateSitePersistence(int oldSiteAreaNum, String OldSiteAddressString, Site updatedSite) throws SQLException {
        return siteDAO.updateSite(oldSiteAreaNum, OldSiteAddressString, convertSiteToSiteDTO(updatedSite));
    }



    public HashMap<Integer, ShippingArea> getShippingAreas() {return shippingAreas;}

    //      Object <--> ObjectDTO conversion function           <<------------------------------


    private ShippingAreaDTO convertShippingAreaToShippingAreaDTO(ShippingArea shippingArea) {
        return new ShippingAreaDTO(shippingArea.getArea_number(), shippingArea.getArea_name());
    }
    private ShippingArea convertShippingAreaDTOToShippingArea(ShippingAreaDTO shippingAreaDTO) {
        return new ShippingArea(shippingAreaDTO.getArea_number(), shippingAreaDTO.getArea_name());
    }


    private SiteDTO convertSiteToSiteDTO(Site site) {
        return new SiteDTO(site.getAddress().getArea(), site.getAddress().getAddress(), site.getcName(), site.getcNumber());
    }
    private Site convertSiteDTOToSite(SiteDTO siteDTO) {
        return new Site(new Address(siteDTO.getSiteAreaNum(), siteDTO.getSiteAddressString()), siteDTO.getcName(), siteDTO.getcNumber());
    }




}
