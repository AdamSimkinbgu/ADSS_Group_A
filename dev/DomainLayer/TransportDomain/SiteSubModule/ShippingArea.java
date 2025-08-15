package DomainLayer.TransportDomain.SiteSubModule;

import java.util.HashMap;

public class ShippingArea {
    private int area_number;
    private String area_name;
    private HashMap<String, Site> sites;

    public ShippingArea(int area_number, String area_name) {
        this.area_number = area_number;
        this.area_name = area_name;
        this.sites = new HashMap<String, Site>();
    }

    public int getArea_number() {return area_number;}
    public void setArea_number(int area_number) {this.area_number = area_number;}
    public String getArea_name() {return area_name;}
    public void setArea_name(String area_name) {this.area_name = area_name;}
    public HashMap<String, Site> getSites() {return sites;}
    public void setSites(HashMap<String, Site> sites) {this.sites = sites;}


    public void addSite(String address, String cName, long cNumber ){
        Site addition = new Site(new Address(this.area_number, address), cName, cNumber);
        sites.put(address, addition);
    }
    public void deleteSite(String address){ sites.remove(address); }

    @Override
    public String toString() {
        String res = "Shipping Area Number: " + area_number + ", Area Name: " + area_name + ", Sites in this Area:\n";
        for (Site site : sites.values()) {
            res += site.toString() + "\n";
        }
        return res;
    }
}
