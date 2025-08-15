package DTOs.TransportModuleDTOs;

import DomainLayer.enums.enumTranProblem;
import DomainLayer.enums.enumTranStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TransportDTO {
    private int transport_ID;
    private enumTranStatus status;
    private LocalDateTime departure_dt;
    private int transportTruckNum;
    private long transportDriverID;
    private double truck_Depart_Weight;
    private SiteDTO src_site;
    private ArrayList<ItemsDocDTO> dests_Docs;    ///  <<<--------------  In Order of visit   <<<--------------------
    private ArrayList<enumTranProblem> problems;

    public TransportDTO() {}
    public TransportDTO(int transport_ID, int transportTruckNum, long transportDriverID, SiteDTO src_site, ArrayList<ItemsDocDTO> dests_Docs, LocalDateTime departure_dt_p, enumTranStatus status, double truck_Depart_Weight, ArrayList<enumTranProblem> problems) {
        this.transport_ID = transport_ID;
        this.status = status;
        this.transportTruckNum = transportTruckNum;
        this.transportDriverID = transportDriverID;
        this.truck_Depart_Weight = truck_Depart_Weight;
        this.src_site = src_site;
        this.dests_Docs = dests_Docs;
        this.departure_dt = departure_dt_p;
        this.problems = problems;
    }


    public int getTransport_ID() {return transport_ID;}
    public void setTransport_ID(int transport_ID) {this.transport_ID = transport_ID;}
    public enumTranStatus getStatus() {return status;}
    public void setStatus(enumTranStatus status) {this.status = status;}
    public int getTransportTruckNum() {return transportTruckNum;}
    public void setTransportTruckNum(int transportTruckNum) {this.transportTruckNum = transportTruckNum;}
    public long getTransportDriverID() {return transportDriverID;}
    public void setTransportDriverID(long transportDriverID) {this.transportDriverID = transportDriverID;}
    public double getTruck_Depart_Weight() {return truck_Depart_Weight;}
    public void setTruck_Depart_Weight(double truck_Depart_Weight) {this.truck_Depart_Weight = truck_Depart_Weight;}
    public SiteDTO getSrc_site() {return src_site;}
    public void setSrc_site(SiteDTO src_site) {this.src_site = src_site;}
    public ArrayList<ItemsDocDTO> getDests_Docs() {return dests_Docs;}
    public void setDests_Docs(ArrayList<ItemsDocDTO> dests_Docs) {this.dests_Docs = dests_Docs;}
    public LocalDateTime getDeparture_dt() {return departure_dt;}
    public void setDeparture_dt(LocalDateTime departure_dt) {this.departure_dt = departure_dt;}
    public ArrayList<enumTranProblem> getProblems() {return problems;}
    public void setProblems(ArrayList<enumTranProblem> problems) {this.problems = problems;}

    public String showAllTransportItemsDocs() {
        String res = "-- Transport Sites & Items: Source Site: " + this.src_site.toString() + "\n";
        for (ItemsDocDTO itemsDocDTO : dests_Docs) {
            res += itemsDocDTO.toString() + "\n";
        }
        return res;
    }

}
