package DomainLayer.TransportDomain.SiteSubModule;

import javax.management.AttributeNotFoundException;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.naming.ContextNotEmptyException;
import java.sql.Connection;
import java.sql.SQLException;

public class SiteFacade {
    private SiteRepo siteRepo;

    public SiteFacade() throws SQLException {  this.siteRepo = new SiteRepoImpl();  }

    public SiteFacade(Connection connection) throws SQLException {  this.siteRepo = new SiteRepoImpl(connection);  }

    public SiteRepo getSiteRepo() {return siteRepo;}

    public void loadDBData() throws SQLException {  this.siteRepo.loadDBData();  }

    public void addShippingArea(int areaNum, String areaName) throws KeyAlreadyExistsException, SQLException {
        if (siteRepo.getShippingAreas().containsKey(areaNum)) {  throw new KeyAlreadyExistsException("Shipping Area Number already exists");  }
        siteRepo.insertShippingArea(new ShippingArea(areaNum, areaName));
    }

    public void deleteShippingArea(int areaNum) throws AttributeNotFoundException, ContextNotEmptyException, SQLException {
        if(!siteRepo.getShippingAreas().containsKey(areaNum)){
            throw new AttributeNotFoundException("Can't delete a Shipping Area that Doesn't exist.");
        } else if (!siteRepo.getShippingAreas().get(areaNum).getSites().isEmpty()) {
            throw new ContextNotEmptyException("Can't Delete a shipping area that has Sites in it.");
        }
        siteRepo.deleteShippingArea(areaNum);  ///  regular removal if everything is GOODA
    }


    public void setShippingAreaNum(int oldAreaNum, int newAreaNum) throws KeyAlreadyExistsException, ClassNotFoundException, SQLException {
        if (siteRepo.getShippingAreas().containsKey(newAreaNum)) {
            throw new KeyAlreadyExistsException("New Shipping Area Number already exists");
        } else if (!siteRepo.getShippingAreas().containsKey(oldAreaNum)) {
            throw new ClassNotFoundException("Old Shipping Area Number does not exist");
        }
        for (String site_address_string : siteRepo.getShippingAreas().get(oldAreaNum).getSites().keySet()) {
            siteRepo.getShippingAreas().get(oldAreaNum).getSites().get(site_address_string).getAddress().setArea(newAreaNum);
            siteRepo.updateSitePersistence(oldAreaNum, site_address_string, siteRepo.getShippingAreas().get(oldAreaNum).getSites().get(site_address_string));
        }   //  could've just gone over the sites but continuing for now.
        ShippingArea temp = siteRepo.getShippingAreas().get(oldAreaNum);
        siteRepo.getShippingAreas().put(newAreaNum, temp);
        siteRepo.getShippingAreas().remove(oldAreaNum);
        siteRepo.getShippingAreas().get(newAreaNum).setArea_number(newAreaNum);
        siteRepo.updateShippingAreaPersistence(oldAreaNum, siteRepo.getShippingAreas().get(newAreaNum));  //  to persist to DB.
    }

    public void setShippingAreaName(int areaNum, String newAreaName) throws ClassNotFoundException, SQLException {
        if (!siteRepo.getShippingAreas().containsKey(areaNum)) {  throw new ClassNotFoundException("Can't set name to a non existent Area number");  }
        siteRepo.getShippingAreas().get(areaNum).setArea_name(newAreaName);
        siteRepo.updateShippingAreaPersistence(areaNum, siteRepo.getShippingAreas().get(areaNum));
    }












    public void addSiteTOArea(int areaNum, String address, String cName, long cNumber) throws ClassNotFoundException, KeyAlreadyExistsException, SQLException {
        if (!siteRepo.getShippingAreas().containsKey(areaNum)) {
            throw new ClassNotFoundException("Can't add a a site to a non existent Area number");
        } else if (siteRepo.getShippingAreas().get(areaNum).getSites().containsKey(address)) {
            throw new KeyAlreadyExistsException("Site Address already exists in that area number");
        }
        siteRepo.insertSite(new Site(new Address(areaNum, address), cName, cNumber));
    }

    public void deleteSiteFromArea(int areaNum, String address) throws ClassNotFoundException, SQLException {
        if (!siteRepo.getShippingAreas().containsKey(areaNum)) {
            throw new ClassNotFoundException("Can't delete from a non existent Area number");
        }else if(!siteRepo.getShippingAreas().get(areaNum).getSites().containsKey(address)){
            throw new ClassNotFoundException("Can't delete a non existent site address string");
        }
        siteRepo.deleteSite(areaNum, address);
    }



    public boolean doesSiteExist(Integer currSiteAreaNum, String currDestinationAddress) {
        if (siteRepo.getShippingAreas().containsKey(currSiteAreaNum)) {
            if(siteRepo.getShippingAreas().get(currSiteAreaNum).getSites().containsKey(currDestinationAddress)){
                return true;
            }
        }
        return false;
    }


    public void setSiteAddress(int areaNum, String oldAddress, String newAddress) throws ClassNotFoundException, KeyAlreadyExistsException, SQLException {
        if (!siteRepo.getShippingAreas().containsKey(areaNum)) {
            throw new ClassNotFoundException("Can't edit from a non existent Area number");
        } else if (!siteRepo.getShippingAreas().get(areaNum).getSites().containsKey(oldAddress)) {
            throw new ClassNotFoundException("Can't edit from a non existent Address String");
        } else if (siteRepo.getShippingAreas().get(areaNum).getSites().containsKey(newAddress)) {
            throw new KeyAlreadyExistsException("Site Address String already exists in that area number");
        }
        Site temp = siteRepo.getShippingAreas().get(areaNum).getSites().get(oldAddress);
        temp.getAddress().setAddress(newAddress);
        siteRepo.getShippingAreas().get(areaNum).getSites().put(newAddress, temp);
        siteRepo.getShippingAreas().get(areaNum).getSites().remove(oldAddress);
        siteRepo.updateSitePersistence(areaNum, oldAddress, siteRepo.getShippingAreas().get(areaNum).getSites().get(newAddress));  // persisting
    }


    public void setSiteAreaNum(int oldAreaNum, int newAreaNum, String address) throws ClassNotFoundException, KeyAlreadyExistsException, SQLException {
        if (!siteRepo.getShippingAreas().containsKey(oldAreaNum)) {
            throw new ClassNotFoundException("Can't edit from a non existent Area number");
        } else if (!siteRepo.getShippingAreas().get(oldAreaNum).getSites().containsKey(address)) {
            throw new ClassNotFoundException("Can't edit from a non existent Address String");
        } else if (!siteRepo.getShippingAreas().containsKey(newAreaNum)) {
            throw new ClassNotFoundException("Can't change to a non existent Area number");
        } else if (siteRepo.getShippingAreas().get(newAreaNum).getSites().containsKey(address)) {
            throw new KeyAlreadyExistsException("The Address String of the Site you are trying to move already exists in the destination area number");
        }
        Site temp = siteRepo.getShippingAreas().get(oldAreaNum).getSites().get(address);
        temp.getAddress().setArea(newAreaNum);
        siteRepo.getShippingAreas().get(newAreaNum).getSites().put(address, temp);
        siteRepo.getShippingAreas().get(oldAreaNum).getSites().remove(address);
        siteRepo.updateSitePersistence(oldAreaNum, address, siteRepo.getShippingAreas().get(newAreaNum).getSites().get(address));  // persisting
    }



    public void setSiteContName(int areaNum, String address, String newContname) throws ClassNotFoundException, SQLException {
        if (!siteRepo.getShippingAreas().containsKey(areaNum)) {
            throw new ClassNotFoundException("Can't edit from a non existent Area number");
        } else if (!siteRepo.getShippingAreas().get(areaNum).getSites().containsKey(address)) {
            throw new ClassNotFoundException("Can't edit from a non existent Address String");
        }
        siteRepo.getShippingAreas().get(areaNum).getSites().get(address).setcName(newContname);
        siteRepo.updateSitePersistence(areaNum, address, siteRepo.getShippingAreas().get(areaNum).getSites().get(address));  // persisting
    }

    public void setSiteContNum(int areaNum, String address, long newContNum) throws ClassNotFoundException, SQLException {
        if (!siteRepo.getShippingAreas().containsKey(areaNum)) {
            throw new ClassNotFoundException("Can't edit from a non existent Area number");
        } else if (!siteRepo.getShippingAreas().get(areaNum).getSites().containsKey(address)) {
            throw new ClassNotFoundException("Can't edit from a non existent Address String");
        }
        siteRepo.getShippingAreas().get(areaNum).getSites().get(address).setcNumber(newContNum);
        siteRepo.updateSitePersistence(areaNum, address, siteRepo.getShippingAreas().get(areaNum).getSites().get(address));  // persisting
    }




    public String showAllSites(){
        String res = "All Sites:\n";
        for (ShippingArea shippingArea : siteRepo.getShippingAreas().values()) {
            res += shippingArea.toString();   //  they already have \n between them
        }
        return res;
    }

    public String showAllShippingAreas(){
        String res = "All Shipping Areas:\n";
        for (ShippingArea shippingArea : siteRepo.getShippingAreas().values()) {
            res += shippingArea.toString();
        }
        res += "\n";
        return res;
    }


}
