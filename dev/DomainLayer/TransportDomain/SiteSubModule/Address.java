package DomainLayer.TransportDomain.SiteSubModule;

public class Address {
    private int area;
    private String address;

    public Address(int area, String address) {
        this.area = area;
        this.address = address;
    }

    public int getArea() {return area;}
    public void setArea(int area) {this.area = area;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    @Override
    public String toString() {
        String res = "";
        res += "Area #: " + area + ", Address: " + address + ".";
        return res;
    }
}
