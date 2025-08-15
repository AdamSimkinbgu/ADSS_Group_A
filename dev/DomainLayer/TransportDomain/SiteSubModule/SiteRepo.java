package DomainLayer.TransportDomain.SiteSubModule;

import java.sql.SQLException;
import java.util.HashMap;

public interface SiteRepo {

    //  Startup from DB function
    public boolean loadDBData() throws SQLException;

    //  Shipping Areas Related
    public boolean insertShippingArea(ShippingArea newShippingAreaToInsert) throws SQLException;    //  turn into DTO and insert to into DB
    public boolean deleteShippingArea(int shippingAreaToDeleteNum) throws SQLException;
    public boolean updateShippingAreaPersistence(int oldAreaNum, ShippingArea newShippingAreaToUpdate) throws SQLException;


    //  Sites Related
    public boolean insertSite(Site siteToInsert) throws SQLException;    //  turn into DTO and insert to into DB
    public boolean deleteSite(int siteToDeleteArea, String siteToDeleteAddressString) throws SQLException;
    public boolean updateSitePersistence(int oldSiteAreaNum, String OldSiteAddressString, Site updatedSite) throws SQLException;

    public HashMap<Integer, ShippingArea> getShippingAreas();
}
