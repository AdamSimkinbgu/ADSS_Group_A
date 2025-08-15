package ServiceLayer.TransportServices;

import DTOs.TransportModuleDTOs.*;
import DomainLayer.enums.enumTranStatus;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class StartUpStateService {
    private TransportService tranSer;
    private TruckService truckSer;
    private SiteService siteSer;

    public StartUpStateService(TransportService tranSer, TruckService truckSer, SiteService siteSer) {
        this.tranSer = tranSer;
        this.truckSer = truckSer;
        this.siteSer = siteSer;
    }

    ///      This Service is Servicing us by just creating the starting data we put here for it to Create as Initial System Data.    <<-----------------------
    public String loadData() {
        try {
            //  Loading data from DB
            this.siteSer.loadDBData();
            this.truckSer.loadDBData();
            this.tranSer.loadDBData();

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
        return "SUCCESS";
    }




}
