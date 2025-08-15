package DomainLayer.TransportDomain.SiteSubModule;

public class Site {
    private Address address;
    private String cName;
    private long cNumber;

    public Site(Address address, String cName, long cNumber) {
        this.address = address;
        this.cName = cName;
        this.cNumber = cNumber;
    }

    public void setAddress(Address address) {this.address = address;}
    public void setcName(String cName) {this.cName = cName;}
    public void setcNumber(long cNumber) {this.cNumber = cNumber;}
    public Address getAddress() {return address;}
    public String getcName() {return cName;}
    public long getcNumber() {return cNumber;}

    @Override
    public String toString() {
        String res = "";
        res += "Address: (" + address.toString() + "), Contact Name: " + cName + ", Contact Number: " + cNumber + ".";
        return res;
    }
}
