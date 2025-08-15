package PresentationLayer.TransportPresentation;

import PresentationLayer.MainCLI;
import ServiceLayer.*;
import ServiceLayer.TransportServices.SiteService;
import ServiceLayer.TransportServices.StartUpStateService;
import ServiceLayer.TransportServices.TransportService;
import ServiceLayer.TransportServices.TruckService;
import Util.config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Scanner;

public class MainTranSysCLI {
    private TranDriverCLI drCont;
    private TranManCLI tranManCont;
    private StartUpController startUpCont;
    private EmployeeIntegrationService eis;
    private Scanner scanner;
    private ObjectMapper objectMapper;

    public MainTranSysCLI(TruckService ts, TransportService trs, SiteService sis, StartUpStateService starUpStService, EmployeeIntegrationService es, ObjectMapper oM) {
        this.scanner = new Scanner(System.in);
        this.objectMapper = oM;
        this.drCont = new TranDriverCLI(trs, this.scanner);
        this.tranManCont = new TranManCLI(ts, trs, sis, this.scanner, objectMapper);
        this.eis = es;
        this.startUpCont = new StartUpController(starUpStService);
    }

    public void transportModuleStartup(long loggedId) throws JsonProcessingException {
        this.startUpCont.startUpData();
        entranceToTransportModuleCLI(loggedId);
    }

    private void entranceToTransportModuleCLI(long loggedId) throws JsonProcessingException {
        while (true){
            boolean isDriver = this.eis.hasRole(loggedId, "DriverA") || this.eis.hasRole(loggedId, "DriverB") || this.eis.hasRole(loggedId, "DriverC") || this.eis.hasRole(loggedId, "DriverD") || this.eis.hasRole(loggedId, "DriverE");
            System.out.println("\n    --------    Welcome to the Transport System !    -------    \n");
            System.out.println(".1.  Enter my relevant menu.");
            System.out.println(".2.  Exit the Transport System.");
            System.out.println("Enter your choice: ");

            int choice = 2;
            try {
                choice = this.scanner.nextInt();
                scanner.nextLine(); // consume the leftover newline
            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }


            if(choice == 1){
                if (this.eis.hasRole(loggedId, config.ROLE_TRANSPORT_MANAGER) || this.eis.hasRole(loggedId, config.ROLE_ADMIN)) {
                    System.out.println("\n   --------    Welcome, Transport Manager    -------\n");  // welcome message upon login
                    tranManCont.transportManagerMainMenu(loggedId);
                } else if (isDriver) {
                    System.out.println("\n   --------    Welcome, Transport Driver.    -------\n");  // welcome message upon login
                    drCont.driverMainMenu(loggedId);
                }

            } else if (choice == 2){
                System.out.println("\nExiting The Transport System...\n");
                return;

            } else {
                System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
            }
            System.out.println();


        }
    }



}
