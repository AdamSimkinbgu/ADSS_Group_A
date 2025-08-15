package DomainLayer.TransportDomain.TruckSubModule;

import DomainLayer.enums.enumDriLicense;

public class Truck {
    private int truck_num;
    private String model;
    private double net_weight;  // in kilos
    private double max_carry_weight;  // in kilos
    private enumDriLicense valid_license;
    private int inTransportID;
    private boolean isDeleted;

    public Truck(int truck_num, String model, double net_weight, double max_carry_weight, enumDriLicense valid_license) {
        this.truck_num = truck_num;
        this.model = model;
        this.net_weight = net_weight;
        this.max_carry_weight = max_carry_weight;
        this.valid_license = valid_license;
        this.inTransportID = -1;  // not assigned yet
        this.isDeleted = false;
    }

    public Truck(int truck_num, String model, double net_weight, double max_carry_weight, enumDriLicense valid_license, int inTransportID) {
        this.truck_num = truck_num;
        this.model = model;
        this.net_weight = net_weight;
        this.max_carry_weight = max_carry_weight;
        this.valid_license = valid_license;
        this.inTransportID = inTransportID;   //  for loading from the DB
        this.isDeleted = false;    //  if loaded from DB so not deleted, this field is for objects lying around in docs after truck deletion
    }

    public void setTruck_num(int truck_num) {this.truck_num = truck_num;}
    public void setModel(String model) {this.model = model;}
    public void setNet_weight(double net_weight) {this.net_weight = net_weight;}
    public void setMax_carry_weight(double max_carry_weight) {this.max_carry_weight = max_carry_weight;}
    public void setValid_license(enumDriLicense valid_license) {this.valid_license = valid_license;}
    public int getTruck_num() {return truck_num;}
    public String getModel() {return model;}
    public double getNet_weight() {return net_weight;}
    public double getMax_carry_weight() {return max_carry_weight;}
    public enumDriLicense getValid_license() {return valid_license;}
    public int getInTransportID() {return inTransportID;}
    public void setInTransportID(int inTransportID) {this.inTransportID = inTransportID;}
    public boolean getIsDeleted() {return isDeleted;}
    public void setIsDeleted(boolean deleted) {isDeleted = deleted;}


    public String toString(){
        String res = "";
        res += "Truck Num: " + truck_num + ", Model: " + model + ", Net Weight: " + net_weight + ", Max Carry Weight: " + max_carry_weight + ", Valid License: " + valid_license.toString() + ", Availability: " + (this.inTransportID == -1 ? "Free" : "Occupied with Transport ID:" + this.inTransportID);
        return res;
    }

}
