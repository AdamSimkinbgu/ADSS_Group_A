package ServiceLayer.TransportServices;

import DTOs.EmployeeDTO;
import DTOs.TransportModuleDTOs.ItemsDocDTO;
import DTOs.TransportModuleDTOs.SiteDTO;
import DTOs.TransportModuleDTOs.TransportDTO;
import DomainLayer.TransportDomain.SiteSubModule.Address;
import DomainLayer.TransportDomain.TransportSubModule.TransportController;
import DomainLayer.TransportDomain.TransportSubModule.TransportDoc;
import ServiceLayer.EmployeeIntegrationService;
import ServiceLayer.exception.AuthorizationException;
import Util.config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.management.AttributeNotFoundException;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.naming.CommunicationException;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransportService {
    private EmployeeIntegrationService employeeIntegrationService;
    private TransportController tran_f;
    private ObjectMapper objectMapper;

    public TransportService(TransportController tf, EmployeeIntegrationService es, ObjectMapper oM) {
        this.employeeIntegrationService = es;
        this.tran_f = tf;
        this.objectMapper = oM;
    }

    public String loadDBData() throws SQLException {
        try {
            tran_f.loadDBData();
        } catch (SQLException e) {
            return "SQL Error";
        } catch (Exception e) {
            return "Error";
        }
        return "Success";
    }

    /// NOTE: This function is called only when a Transport has passed the Transport checks and can fully be registered.
    public String createTransport(long loggedID, String transportDTO, int queuedIndexIfWasQueued){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "CREATE_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }

            this.tran_f.createTransport(transportDTO, queuedIndexIfWasQueued);
        } catch (JsonProcessingException e) {
            return "JSON's Error Exception";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }

    public String deleteTransport(long loggedID, int transportID){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "DELETE_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (transportID < 0){ return "Can't Enter a negative Transport ID number"; }

            this.tran_f.deleteTransport(transportID);
        } catch (FileNotFoundException e) {
            return "No transport found with the Transport ID you've entered, so can't delete that Transport";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }







    public String setTransportStatus(long loggedID, int TranDocID, String menu_status_option){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            int intMenuStatusOption = Integer.parseInt(menu_status_option);
            if (intMenuStatusOption < 1 || intMenuStatusOption > 6){ return "Invalid menu status option - enter a number between 1 and 6"; }
            if (TranDocID < 0){ return "Can't Enter a negative Transport ID number"; }

            if (!this.tran_f.doesTranIDExist(TranDocID)){ throw new FileNotFoundException("The Transport ID you have entered doesn't exist."); }

            TransportDTO testingTransport = this.objectMapper.readValue(this.tran_f.getTransportAsDTOJson(TranDocID), TransportDTO.class);

            if (intMenuStatusOption == 1 || intMenuStatusOption == 3 || intMenuStatusOption == 6){   // active transport statusesPermissions
                if(!isTranDriverTimeAndPlaceValid(testingTransport)){
                    return "Cannot change this transport's status to an active one because, this transport has a Driver Unavailability issue.";
                } else if (!areWareHouseMenTimeAndPlacesValid(testingTransport)) {
                    return "Cannot change this transport's status to an active one because, this transport has a WareHousemen Unavailability issue.";
                }
            }

            ///    and then the other checks
            if (this.tran_f.getTransportsRepos().getTransports().get(TranDocID) != null){
                this.tran_f.setTransportStatus(TranDocID, intMenuStatusOption, this.employeeIntegrationService.isActive(this.tran_f.getTransportsRepos().getTransports().get(TranDocID).getTransportDriverId()));
                return "Success";  //  if All Good
            } else {
                for (TransportDoc queuedTransportDoc : tran_f.getTransportsRepos().getQueuedTransports()){
                    if (TranDocID == queuedTransportDoc.getTran_Doc_ID()){
                        this.tran_f.setTransportStatus(TranDocID, intMenuStatusOption, this.employeeIntegrationService.isActive(queuedTransportDoc.getTransportDriverId()));
                        return "Success";  //  if All Good
                    }
                }
                //  search the queuedTransports for that transport
            }
            return "The Transport ID you are trying to set a Status to, doesn't exist.";

        } catch (FileNotFoundException e) {
            return "The Transport ID you have entered doesn't exist.";
        } catch (StringIndexOutOfBoundsException e){
            return "You cannot set a queued Transport's status as something other than Queued or Canceled.";
        }catch (FileAlreadyExistsException e) {
            return "The status you are trying to set already is the status of this Transport";
        } catch (CommunicationException e) {
            return "cannot change Transport Status because it wants to change to an active one, but the Driver is already active in another Transport.";
        } catch (CloneNotSupportedException e) {
            return "cannot change Transport Status because it wants to change to an active one, but the Truck is already active in another Transport.";
        } catch (IndexOutOfBoundsException e) {
            return "the Truck or/and Driver of this Transport have been Deleted, you can view available Trucks or/and Drivers using the menu and set appropriately";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
//        return "Success";  //  if All Good
    }






    public String setTransportTruck(long loggedID, int TranDocID, int truckNum){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (TranDocID < 0 || truckNum < 0){ return "Transport Document number, Truck number values cannot be negative."; }

            if (this.tran_f.getTransportsRepos().getTransports().get(TranDocID) != null){
                this.tran_f.setTransportTruck(TranDocID, truckNum, this.employeeIntegrationService.hasRole(this.tran_f.getTransportsRepos().getTransports().get(TranDocID).getTransportDriverId(), this.tran_f.getTruckLicenseAsStringRole(truckNum)));
                return "Success";  //  if All Good
            } else {
                for (TransportDoc queuedTransportDoc : tran_f.getTransportsRepos().getQueuedTransports()){
                    if (TranDocID == queuedTransportDoc.getTran_Doc_ID()){
                        this.tran_f.setTransportTruck(TranDocID, truckNum, this.employeeIntegrationService.hasRole(queuedTransportDoc.getTransportDriverId(), this.tran_f.getTruckLicenseAsStringRole(truckNum)));
                        return "Success";  //  if All Good
                    }
                }
                //  search the queuedTransports for that transport
            }
            return "The Transport ID you are trying to set a Truck to doesn't exist.";

        } catch (FileNotFoundException e) {
            return "The Transport ID you have entered doesn't exist.";
        } catch (ArrayIndexOutOfBoundsException e) {
            return "The Truck number you have entered doesn't exist.";
        } catch (FileAlreadyExistsException e) {
            return "This Truck is already the Truck of this Transport";
        } catch (CloneNotSupportedException e) {
            return "The Transport you are trying to set to is Active and The Truck you are trying to set is already Occupied with another Active Transport right now";
        } catch (CommunicationException e) {
            return "The transport's driver doesn't have the fitting license for the new Truck you want to set.";
        } catch (AbstractMethodError e) {
            return "The Truck you are trying to set to this Transport can't carry this Transport's Weight.";
        } catch (ClassNotFoundException e) {
            return "the Truck of this Transport have been Deleted, you can view available Trucks using the menu and set appropriately";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
//        return "Success";  //  if All Good
    }



    public String setTransportDriver(long loggedID, int TranDocID, int DriverID){
        if (TranDocID < 0 || DriverID < 0){ return "Transport Document number, Driver ID values cannot be negative."; }
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }

            if (!this.tran_f.doesTranIDExist(TranDocID)){ throw new FileNotFoundException("The Transport ID you have entered doesn't exist."); }

            TransportDTO testingTransport = this.objectMapper.readValue(this.tran_f.getTransportAsDTOJson(TranDocID), TransportDTO.class);
            testingTransport.setTransportDriverID(DriverID);  // the change to check if valid

            if(!isTranDriverTimeAndPlaceValid(testingTransport)){ return "Cannot change this transport's Driver to that because, this transport will have a Driver Unavailability issue.\n(The new Driver probably isn't from the sites associated with this Transport)"; }
            ///    and then the other checks
            boolean isNotDriver = !this.employeeIntegrationService.hasRole(DriverID, "DriverA") && !this.employeeIntegrationService.hasRole(DriverID, "DriverB") && !this.employeeIntegrationService.hasRole(DriverID, "DriverC") && !this.employeeIntegrationService.hasRole(DriverID, "DriverD") && !this.employeeIntegrationService.hasRole(DriverID, "DriverE");

            String lice = "";
            if (this.tran_f.getTransportsRepos().getTransports().get(TranDocID) != null){
                lice = this.tran_f.getTruckLicenseAsStringRole(this.tran_f.getTransportsRepos().getTransports().get(TranDocID).getTransportTruck().getTruck_num());
            } else {
                for (TransportDoc queuedTransportDoc : tran_f.getTransportsRepos().getQueuedTransports()){
                    if (TranDocID == queuedTransportDoc.getTran_Doc_ID()){
                        lice = this.tran_f.getTruckLicenseAsStringRole(queuedTransportDoc.getTransportTruck().getTruck_num());
                    }
                }
                //  search the queuedTransports for that transport
            }
            if (lice.equals("")){ return "Transport ID non existent in system"; }

            this.tran_f.setTransportDriver(TranDocID, DriverID, isNotDriver, this.employeeIntegrationService.isActive(DriverID), this.employeeIntegrationService.hasRole(DriverID, lice));

        } catch (FileNotFoundException e) {
            return "The Transport ID you have entered doesn't exist.";
        } catch (ArrayIndexOutOfBoundsException e) {
            return "The Driver ID you have entered doesn't exist.";
        } catch (FileAlreadyExistsException e) {
            return "This Driver is already the Driver of this Transport";
        } catch (CloneNotSupportedException e) {
            return "The Transport you are trying to set to is Active and The Driver you are trying to set is already Occupied with another Active Transport right now";
        } catch (CommunicationException e) {
            return "The New Driver you are trying to set doesn't have the fitting license for the Truck that is in the Transport.";
        } catch (ClassNotFoundException e) {
            return "the Driver of this Transport have been Deleted, you can view available Drivers using the menu and set appropriately";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }





    ///   Note:  this is licenses wise and free wise.  NOT time and place wise.
    public String isTruckDriverPairingGood(long loggedID, int truckNum, int driverID) {
        if (truckNum < 0 || driverID < 0){ return "Truck number/Driver ID values cannot be negative."; }
        try {
            ArrayList<EmployeeDTO> employeesDTOs = new ArrayList<>();
            String[] receivedEmps = this.employeeIntegrationService.getAllDrivers();
            for (String emp : receivedEmps){
                employeesDTOs.add(this.objectMapper.readValue(emp, EmployeeDTO.class));
            }
            String lice = this.tran_f.getTruckLicenseAsStringRole(truckNum);
            boolean isThereAvailableDriverMatchingThisTruck = false;

            for(EmployeeDTO driver : employeesDTOs){
                if (this.employeeIntegrationService.hasRole(driver.getIsraeliId(), lice) && !this.tran_f.getTransportsRepos().getDriverIdToInTransportID().containsKey(driver.getIsraeliId())){  // if driver compatible and free
                    isThereAvailableDriverMatchingThisTruck = true;
                }
            }
            if (!isThereAvailableDriverMatchingThisTruck){
                throw new ClassNotFoundException("There isn't a Driver that is available right now and compatible, license wise, with the Truck you chose");
            }

            boolean isNotDriver = !this.employeeIntegrationService.hasRole(driverID, "DriverA") && !this.employeeIntegrationService.hasRole(driverID, "DriverB") && !this.employeeIntegrationService.hasRole(driverID, "DriverC") && !this.employeeIntegrationService.hasRole(driverID, "DriverD") && !this.employeeIntegrationService.hasRole(driverID, "DriverE");
            boolean hasRole22 = this.employeeIntegrationService.hasRole(driverID, lice);
            this.tran_f.isTruckDriverPairingGood(truckNum, driverID, isNotDriver, hasRole22);

        } catch (FileNotFoundException e) {
            return "Truck Number entered doesn't exist";
        } catch (ArrayIndexOutOfBoundsException e) {
            return "The Driver ID you have entered doesn't exist";
        } catch (CloneNotSupportedException e) {
            return "The Truck you chose is partaking in another Active Transport right now";
        } catch (ClassNotFoundException e) {
            return "There isn't a Driver that is available right now and compatible, license wise, with the Truck you chose";
        } catch (CommunicationException e) {
            return "The Driver you chose doesn't have the fitting license for the Truck you chose";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }












    private boolean isTranDriverTimeAndPlaceValid(TransportDTO transport_DTO){
        long transportDriverID = transport_DTO.getTransportDriverID();
        LocalDateTime transportDepar_t = transport_DTO.getDeparture_dt();
        String transportSrcAddressString = transport_DTO.getSrc_site().getSiteAddressString();
        int transportSrcAreaNum = transport_DTO.getSrc_site().getSiteAreaNum();
        /// check if the driver is valid time and place wise.

        try {
            // src check
            if (this.employeeIntegrationService.isBranch(transport_DTO.getSrc_site().getSiteAddressString(), transport_DTO.getSrc_site().getSiteAreaNum())){
                // check driver in src only if branch
                if (this.employeeIntegrationService.isDriverOnShiftAt(transportDriverID, transportDepar_t, transportSrcAddressString, transportSrcAreaNum)){
                    // if driver belongs to src site(which is apparently a branch because true) and is there at the right time
                    return true;
                }
            }

            // so we need to check if that driver is from any destination site    //Note: destination sites can only be branches        <<----------------------
            for (ItemsDocDTO itemsDocDTO : transport_DTO.getDests_Docs()){
                if (this.employeeIntegrationService.isDriverOnShiftAt(transportDriverID, transportDepar_t, itemsDocDTO.getDest_siteDTO().getSiteAddressString(), itemsDocDTO.getDest_siteDTO().getSiteAreaNum())){
                    return true; // make sure isDriverOnShiftAt return false if the site I gave him isn't a branch, just because. (even though dest sites are branches).
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }



    private boolean areWareHouseMenTimeAndPlacesValid(TransportDTO transportDto) {
        try {
            if (this.employeeIntegrationService.isBranch(transportDto.getSrc_site().getSiteAddressString(), transportDto.getSrc_site().getSiteAreaNum())){  // check warehouseMen only if branch
                if (!this.employeeIntegrationService.isWarehousemanOnShiftAt(transportDto.getDeparture_dt(), transportDto.getSrc_site().getSiteAddressString(), transportDto.getSrc_site().getSiteAreaNum())){
                    return false;   //  because there won't be any warehouseman to load the truck at the src site
                }
            }

            for (ItemsDocDTO itemsDocDTO : transportDto.getDests_Docs()){     //NOTE:    all dest sites are branches
                if (!this.employeeIntegrationService.isWarehousemanOnShiftAt(itemsDocDTO.getEstimatedArrivalTime(), itemsDocDTO.getDest_siteDTO().getSiteAddressString(), itemsDocDTO.getDest_siteDTO().getSiteAreaNum())){
                    return false;   //  because there won't be any warehouseman to offload the truck at the dest site
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    public boolean isBranch(String siteAddressString, int siteAreaNum){  return this.employeeIntegrationService.isBranch(siteAddressString, siteAreaNum);  }










    ///  returns: "Valid", "BadLicenses", "overallWeight-truckMaxCarryWeight", "Queue", "Occupied", "WareHouseManUnavailable", "DriverUnavailable"
    public String checkTransportValidity(long loggedID, String DTO_OfTransport) {
        String res = "Valid";
        try {
            /// /////////////////////////////////    <<-------------------------------------   checking if there's a Driver-Truck Pairing At All Right Now, from the Free ones
            ArrayList<EmployeeDTO> employeesDTOs = new ArrayList<>();
            for (String emp : this.employeeIntegrationService.getAllDrivers()){
                employeesDTOs.add(this.objectMapper.readValue(emp, EmployeeDTO.class));
            }

            ///  checking if there is a match at all, --> from those who are free right now, generally in all sites
            boolean isThereMatchAtAllBetweenLicenses = false;
            for (EmployeeDTO employee : employeesDTOs){
                for (int trucNum : this.tran_f.getTruckFacade().getTruckRepo().getTrucksWareHouse().keySet()){
                    if (this.employeeIntegrationService.hasRole(employee.getIsraeliId(), this.tran_f.getTruckLicenseAsStringRole(trucNum))){  //  if compatible
                        if ((!this.tran_f.isDriverActive(employee.getIsraeliId())) && (!this.tran_f.isTruckActive(trucNum))){   // searching only the free ones, like in the Requirements
                            isThereMatchAtAllBetweenLicenses = true;  // if found
                            break;   // because already found
                        }
                    }
                }
                if (isThereMatchAtAllBetweenLicenses){break;}  // if already found
            }
            // else: continue to check other stuff inside this function


            TransportDTO transport_DTO = this.objectMapper.readValue(DTO_OfTransport, TransportDTO.class);

            if(!isTranDriverTimeAndPlaceValid(transport_DTO)){
                this.tran_f.addFromTransportDTOStringToWaitQueue(DTO_OfTransport);
                return "DriverUnavailable";
            } else if (!areWareHouseMenTimeAndPlacesValid(transport_DTO)) {
                this.tran_f.addFromTransportDTOStringToWaitQueue(DTO_OfTransport);
                return "WareHouseManUnavailable";
            }

            boolean hasRole11 = this.employeeIntegrationService.hasRole(transport_DTO.getTransportDriverID(), this.tran_f.getTruckLicenseAsStringRole(transport_DTO.getTransportTruckNum()));
            res = this.tran_f.checkTransportValidity(DTO_OfTransport, hasRole11, isThereMatchAtAllBetweenLicenses);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "JsonProcessingException";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return res;  //  if All Good
    }








    public String getAQueuedTransportAsDTOJson(long loggedID, int index){
        String res = "";
        if (index < 1){ return "The index you've entered in invalid. (it's below the Starting index which is 1)"; }
        try {
            res = this.tran_f.getAQueuedTransportAsDTOJson(index);
        } catch (IndexOutOfBoundsException e) {
            return "index";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Json";
        } catch (AttributeNotFoundException e) {
            return "noQueued";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return res;
    }










    public String addDestSite(long loggedID, int tran_ID, int itemsDoc_num, int destSiteArea, String destSiteAddress) {
        try {
            if (tran_ID < 0 || itemsDoc_num < 0 || destSiteArea < 0){ return "The info numbers you have entered cannot be negative"; }
            if (destSiteAddress.isEmpty() || destSiteAddress.isBlank()){ return "The info strings you've entered cannot be empty"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (!tran_f.doesTranIDExist(tran_ID)){ return "The Transport ID you've entered doesn't exist."; }
            if (!employeeIntegrationService.isBranch(destSiteAddress, destSiteArea)){ return "Destination sites must be branches of Super Lee !"; }

            /// checking if the change will affect a warehouse men availability
            TransportDTO testingTransport = this.objectMapper.readValue(this.tran_f.getTransportAsDTOJson(tran_ID), TransportDTO.class);

            /// adding the ItemsDocDTO and after this we will check the whole TransportDTO for good time matching

            ItemsDocDTO newItemsDoc = new ItemsDocDTO(itemsDoc_num, testingTransport.getSrc_site(), new SiteDTO(destSiteArea, destSiteAddress), new ArrayList<>(), null, testingTransport.getTransport_ID());

            if (testingTransport.getDests_Docs().size() > 0){  // there are other sites in the transport
                if (testingTransport.getDests_Docs().get(testingTransport.getDests_Docs().size()-1).getDest_siteDTO().getSiteAreaNum() == destSiteArea){
                    newItemsDoc.setEstimatedArrivalTime(testingTransport.getDests_Docs().get(testingTransport.getDests_Docs().size()-1).getEstimatedArrivalTime().plusMinutes(30));
                } else {
                    newItemsDoc.setEstimatedArrivalTime(testingTransport.getDests_Docs().get(testingTransport.getDests_Docs().size()-1).getEstimatedArrivalTime().plusHours(1));
                }
            } else {   //  if there are no other sites in this Transport
                if (testingTransport.getSrc_site().getSiteAreaNum() == destSiteArea){
                    newItemsDoc.setEstimatedArrivalTime(testingTransport.getDeparture_dt().plusMinutes(30));
                } else {
                    newItemsDoc.setEstimatedArrivalTime(testingTransport.getDeparture_dt().plusHours(1));
                }
            }

            testingTransport.getDests_Docs().add(newItemsDoc);

            if (!areWareHouseMenTimeAndPlacesValid(testingTransport)) { return "Cannot add Site to this transport, adding this site will cause a WareHouseMan Unavailability issue"; }

            ///    all the other checks
            this.tran_f.addDestSiteToTransport(tran_ID, itemsDoc_num, destSiteArea, destSiteAddress);

        } catch (FileNotFoundException e) {
            return "The Transport ID you've entered doesn't exist.";
        } catch (FileAlreadyExistsException e) {
            return "The Site's Items Document Number you are trying to add already exists.";
        } catch (CommunicationException e) {
            return "Destination Site already in this Transport, you can add items to that site instead.";
        } catch (IndexOutOfBoundsException e) {
            return "Cannot add a Site with a non existent area number.";
        } catch (ClassNotFoundException e) {
            return "Cannot add a site with a not found address String in its area.";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }



    public String removeDestSite(long loggedID, int tran_ID, int itemsDoc_num){
        try {
            if (tran_ID < 0 || itemsDoc_num < 0){ return "The info you entered cannot be negative"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (!tran_f.doesTranIDExist(tran_ID)){ return "The Transport ID you've entered doesn't exist."; }

            if (!tran_f.doesItemsDocIDExistInTransport(itemsDoc_num, tran_ID)){ throw new ClassNotFoundException("The Site's Items Document Number is not in that Transport"); }

            /// checking if the change will affect availability factors
            TransportDTO testingTransport = this.objectMapper.readValue(this.tran_f.getTransportAsDTOJson(tran_ID), TransportDTO.class);

            /// removing the ItemsDocDTO and after this we will check the whole TransportDTO for good time matching

            int removal_index = 0, before_area = testingTransport.getSrc_site().getSiteAreaNum();
            boolean found_removable = false, beforeBeforeWasDifferentArea = false;
            for (ItemsDocDTO itemsDocDTO : testingTransport.getDests_Docs()){
                if (itemsDocDTO.getItemsDoc_num() == itemsDoc_num){
                    found_removable = true;
                    continue;
                }
                if (!found_removable){
                    removal_index++;
                } else {
                    // after was found, we update (reduce) the transport's following itemsDoc's arrival times
                    if (itemsDocDTO.getDest_siteDTO().getSiteAreaNum() == before_area){
                        if (beforeBeforeWasDifferentArea){
                            itemsDocDTO.setEstimatedArrivalTime(itemsDocDTO.getEstimatedArrivalTime().minusMinutes(90));  // 2 waiting times deducted
                        } else {
                            itemsDocDTO.setEstimatedArrivalTime(itemsDocDTO.getEstimatedArrivalTime().minusHours(1));  // 2 waiting times deducted
                        }
                    } else {
                        if (beforeBeforeWasDifferentArea){
                            itemsDocDTO.setEstimatedArrivalTime(itemsDocDTO.getEstimatedArrivalTime().minusHours(2));  // 2 waiting times deducted
                        } else {
                            itemsDocDTO.setEstimatedArrivalTime(itemsDocDTO.getEstimatedArrivalTime().minusMinutes(90));  // 2 waiting times deducted
                        }

                    }
                }
                if (itemsDocDTO.getDest_siteDTO().getSiteAreaNum() != before_area){
                    beforeBeforeWasDifferentArea = true;
                } else { beforeBeforeWasDifferentArea = false; }
                before_area = itemsDocDTO.getDest_siteDTO().getSiteAreaNum();
            }

            testingTransport.getDests_Docs().remove(removal_index);  //  removing the ItemsDocDTO from the TransportDTO

            // now we removed the ItemsDocDTO and adjusted the following arrival times, and now we'll check time and place validities:
            if(!isTranDriverTimeAndPlaceValid(testingTransport)){
                return "Cannot remove Dest Site from this transport, removing this site will cause a Driver Unavailability issue (the driver is probably from that site)";
            } else if (!areWareHouseMenTimeAndPlacesValid(testingTransport)) {
                return "Cannot remove Dest Site from this transport, removing this site will cause a WareHouseMan Unavailability issue";
            }

            ///    all the other checks
            this.tran_f.removeDestSiteFromTransport(tran_ID, itemsDoc_num);

        } catch (FileNotFoundException e) {
            return "The Transport ID you've entered doesn't exist.";
        } catch (CommunicationException e) {
            return "The Site's Items Document Number you are trying to remove doesn't exist in the system.";
        } catch (ClassNotFoundException e) {
            return "The Site's Items Document Number is not in that Transport";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }





    public String setSiteArrivalIndexInTransport(long loggedID, int transportID, int siteArea, String siteAddress, String index){
        try {
            int intIndex = Integer.parseInt(index);
            if (intIndex < 0){    //  index should be 1, 2, ....
                return "The Site Index in the arrival order cannot be negative";
            }
            if(transportID < 0 || siteArea < 0){ return "The Transport ID and the Site Area cannot be negative"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (!tran_f.doesTranIDExist(transportID)){ return "The Transport ID you've entered doesn't exist."; }

            if (!tran_f.doesAddressExistInTransport(transportID, siteArea, siteAddress)){ throw new ClassNotFoundException("The Site's Items Document Number is not in that Transport"); }


            /// checking if the change will affect availability factors
            TransportDTO testingTransport = this.objectMapper.readValue(this.tran_f.getTransportAsDTOJson(transportID), TransportDTO.class);

            /// ***  removing  *** the ItemsDocDTO and after this we will add again and check the whole TransportDTO for good time matching

            int removal_index = 0, before_area = testingTransport.getSrc_site().getSiteAreaNum();
            boolean found_removable = false, beforeBeforeWasDifferentArea = false;
            for (ItemsDocDTO itemsDocDTO : testingTransport.getDests_Docs()){
                if (itemsDocDTO.getDest_siteDTO().getSiteAreaNum() == siteArea && itemsDocDTO.getDest_siteDTO().getSiteAddressString().equals(siteAddress)){
                    found_removable = true;
                    continue;
                }
                if (!found_removable){
                    removal_index++;
                } else {
                    // after was found, we update (reduce) the transport's following itemsDoc's arrival times
                    if (itemsDocDTO.getDest_siteDTO().getSiteAreaNum() == before_area){
                        if (beforeBeforeWasDifferentArea){
                            itemsDocDTO.setEstimatedArrivalTime(itemsDocDTO.getEstimatedArrivalTime().minusMinutes(90));  // 2 waiting times deducted
                        } else {
                            itemsDocDTO.setEstimatedArrivalTime(itemsDocDTO.getEstimatedArrivalTime().minusHours(1));  // 2 waiting times deducted
                        }
                    } else {
                        if (beforeBeforeWasDifferentArea){
                            itemsDocDTO.setEstimatedArrivalTime(itemsDocDTO.getEstimatedArrivalTime().minusHours(2));  // 2 waiting times deducted
                        } else {
                            itemsDocDTO.setEstimatedArrivalTime(itemsDocDTO.getEstimatedArrivalTime().minusMinutes(90));  // 2 waiting times deducted
                        }

                    }
                }
                if (itemsDocDTO.getDest_siteDTO().getSiteAreaNum() != before_area){
                    beforeBeforeWasDifferentArea = true;
                } else { beforeBeforeWasDifferentArea = false; }
                before_area = itemsDocDTO.getDest_siteDTO().getSiteAreaNum();
            }

            ItemsDocDTO itemsDoc_removed = testingTransport.getDests_Docs().remove(removal_index);  //  removing the ItemsDocDTO from the TransportDTO
            int itemsDoc_num = itemsDoc_removed.getItemsDoc_num();  //  removing the ItemsDocDTO from the TransportDTO


            ///      from here we are ***  adding  *** that ItemsDoc into the correct index and updating the arrival times accordingly        <<<--------------------------
            LocalDateTime beforeElementArrivalTime;
            if (intIndex == 1) { // If inserting as first destination
                beforeElementArrivalTime = testingTransport.getDeparture_dt();
            } else {
                beforeElementArrivalTime = testingTransport.getDests_Docs().get(intIndex - 2).getEstimatedArrivalTime();
            }

            // Create new itemsDoc with same data but recalculated arrival time
            ItemsDocDTO newItemsDoc = new ItemsDocDTO(itemsDoc_num, testingTransport.getSrc_site(), new SiteDTO(siteArea, siteAddress),
                    itemsDoc_removed.getItemQuantityDTOs(), null, testingTransport.getTransport_ID());

            // Calculate arrival time based on previous site
            int prevArea = (intIndex == 1) ? testingTransport.getSrc_site().getSiteAreaNum()
                    : testingTransport.getDests_Docs().get(intIndex - 2).getDest_siteDTO().getSiteAreaNum();

            if (siteArea == prevArea) {
                newItemsDoc.setEstimatedArrivalTime(beforeElementArrivalTime.plusMinutes(30));
            } else {
                newItemsDoc.setEstimatedArrivalTime(beforeElementArrivalTime.plusHours(1));
            }

            // Insert the new ItemsDoc
            testingTransport.getDests_Docs().add(intIndex - 1, newItemsDoc);

            // Update arrival times for following destinations
            LocalDateTime lastArrivalTime = newItemsDoc.getEstimatedArrivalTime();
            int lastArea = siteArea;

            for (int i = intIndex; i < testingTransport.getDests_Docs().size(); i++) {    //  adding to the arrival times of the following ItemsDocs after the new insertion index
                ItemsDocDTO currDoc = testingTransport.getDests_Docs().get(i);
                if (currDoc.getDest_siteDTO().getSiteAreaNum() == lastArea) {
                    currDoc.setEstimatedArrivalTime(lastArrivalTime.plusMinutes(30));
                } else {
                    currDoc.setEstimatedArrivalTime(lastArrivalTime.plusHours(1));
                }
                lastArrivalTime = currDoc.getEstimatedArrivalTime();
                lastArea = currDoc.getDest_siteDTO().getSiteAreaNum();
            }

            // Check validity of new schedule
            if (!isTranDriverTimeAndPlaceValid(testingTransport)) {
                return "Cannot change Site's arrival order in this Transport, the new order will cause a Driver Unavailability issue";
            } else if (!areWareHouseMenTimeAndPlacesValid(testingTransport)) {
                return "Cannot change Site's arrival order in this Transport, the new order will cause a WareHouseMan Unavailability issue";
            }

            ///    all the other checks
            this.tran_f.setSiteArrivalIndexInTransport(transportID, siteArea, siteAddress, intIndex);

        } catch (IndexOutOfBoundsException e) {
            return "You entered a Site with a non existent area number.";
        } catch (CommunicationException e) {
            return "You entered a site address String that doesn't exist in that area.";
        }catch (FileNotFoundException e) {
            return "The transport ID given was not found";
        } catch (ClassNotFoundException e) {
            return "Site not found inside of that transport";
        } catch (AbstractMethodError e) {
            return "The Index entered is bigger than the amount of sites in the Transport, so can't put that site in that bigger index";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }




    public String changeAnItemsDocNum(long loggedID, int oldItemsDocNum, int newItemsDocNum) {
        try {
            if (oldItemsDocNum < 0 || newItemsDocNum < 0) { return "You entered an invalid item number. (cannot be a negative number)"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (oldItemsDocNum == newItemsDocNum) {  return "Changing Process finished because before and after values are the same";  }
            tran_f.changeAnItemsDocNum(oldItemsDocNum, newItemsDocNum);
        } catch (FileNotFoundException e) {
            return "Old Items Document ID Non Existent";
        } catch (KeyAlreadyExistsException e) {
            return "New Items Document ID Already Exists !";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }


    public boolean checkValidItemsDocID(long loggedID, int currItemsDocNum) {  // very basic check
        if (currItemsDocNum < 0){ return false; }
        boolean res = false;
        try {
            res = this.tran_f.checkValidItemsDocID(currItemsDocNum);  // return what the business layer said
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    /// this is for when a driver is in the system and wants to edit an item's condition in a transport he is associated in.
    public String checkIfDriverDrivesThisItemsDoc(long loggedID, int itemsDocId) {
        try {
            if (loggedID < 0 || itemsDocId < 0){ return "The IDs you enter cannot be negative"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT_ITEM_CONDITION")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            boolean isNotDriver = !this.employeeIntegrationService.hasRole(loggedID, "DriverA") && !this.employeeIntegrationService.hasRole(loggedID, "DriverB") && !this.employeeIntegrationService.hasRole(loggedID, "DriverC") && !this.employeeIntegrationService.hasRole(loggedID, "DriverD") && !this.employeeIntegrationService.hasRole(loggedID, "DriverE");
            tran_f.checkIfDriverDrivesThisItemsDoc(loggedID, itemsDocId, isNotDriver);
        } catch (FileNotFoundException e) {
            return "Items Document ID not found.";
        }catch (ClassNotFoundException e) {
            return "Driver ID doesn't exist.";
        }catch (IllegalAccessException e) {
            return "Driver doesn't drive this Items Document's Transport";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Yes";  //  if Yes
    }









    public String addTransportProblem(long loggedID, int TransportID, String menu_Problem_option){
        try {
            int intMenuProblemOption = Integer.parseInt(menu_Problem_option);
            if (intMenuProblemOption < 1 || intMenuProblemOption > 6){ return "The Problem option number you have entered is out of existing problem's numbers bounds"; }
            if (TransportID < 0){ return "The Transport ID you've entered is invalid (it's negative)"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            this.tran_f.addTransportProblem(TransportID, intMenuProblemOption);
        } catch (FileNotFoundException e) {
            return "Transport ID doesn't exist.";
        } catch (FileAlreadyExistsException e) {
            return "The problem you entered already exists in this Transport";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }


    public String removeTransportProblem(long loggedID, int TransportID, String menu_Problem_option){
        try {
            int intMenuProblemOption = Integer.parseInt(menu_Problem_option);
            if (intMenuProblemOption < 1 || intMenuProblemOption > 6){ return "The Problem option number you have entered is out of existing problem's numbers bounds"; }
            if (TransportID < 0){ return "The Transport ID you've entered is invalid (it's negative)"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            this.tran_f.removeTransportProblem(TransportID, intMenuProblemOption);
        } catch (FileNotFoundException e) {
            return "Transport ID doesn't exist.";
        } catch (FileAlreadyExistsException e) {
            return "The problem you entered already doesn't exists in this Transport";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }






























    public String addItem(long loggedID, int itemsDocNum, String itemName, double itemWeight, int amount, boolean cond){
        try {
            if (itemName.isEmpty() || itemName.isBlank()){ return "Item's name cannot be empty"; }
            if (itemsDocNum < 0 || itemWeight < 0 || amount < 0){ return "Item's document number/weight/amount cannot be negative"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "ADD_ITEM_TO_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            this.tran_f.addItem(itemsDocNum, itemName, itemWeight, amount, cond);
        } catch (FileNotFoundException e) {
            return "Item's Document ID not found";
        } catch (IndexOutOfBoundsException e) {
            return "Cannot add Item to transport because the new weight exceeds the maximum carry weight";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }


    public String removeItem(long loggedID, int itemsDocNum, String itemName, double itemWeight, int amount, boolean cond){
        try {
            if (itemName.isEmpty() || itemName.isBlank()){ return "Item's name cannot be empty"; }
            if (itemsDocNum < 0 || itemWeight < 0 || amount < 0){ return "Item's document number/weight/amount cannot be negative"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "DELETE_ITEM_FROM_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            this.tran_f.removeItem(itemsDocNum, itemName, itemWeight, amount, cond);
        } catch (FileNotFoundException e) {
            return "Item's Document ID not found";
        } catch (ClassNotFoundException e) {
            return "Item to remove not found in that Items Document";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }


    public String setItemCond(long loggedID, int itemsDocNum, String itemName, double itemWeight, int amount, boolean cond){
        try {
            if (itemName.isEmpty() || itemName.isBlank()){ return "Item's name cannot be empty"; }
            if (itemsDocNum < 0 || itemWeight < 0 || amount < 0){ return "Item's document number/weight/amount cannot be negative"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_TRANSPORT_ITEM_CONDITION")){  //TODO:  add to TranMan and Admin <----
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }

            this.tran_f.setItemCond(itemsDocNum, itemName, itemWeight, amount, cond);
        } catch (FileNotFoundException e) {
            return "Item's Document ID not found";
        } catch (ClassNotFoundException e) {
            return "Item to change condition to was not found in that Items Document";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";  //  if All Good
    }










    public String showTransportsOfDriver(long id) {
        String res = "";
        try {
            if (!this.employeeIntegrationService.isActive(id)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(id, "VIEW_RELEVANT_TRANSPORTS")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (id < 0){ return "The Driver(ID) you want to show is invalid (it's negative)"; }
            boolean isNotDriver = !this.employeeIntegrationService.hasRole(id, "DriverA") && !this.employeeIntegrationService.hasRole(id, "DriverB") && !this.employeeIntegrationService.hasRole(id, "DriverC") && !this.employeeIntegrationService.hasRole(id, "DriverD") && !this.employeeIntegrationService.hasRole(id, "DriverE");
            res = tran_f.showTransportsOfDriver(id, isNotDriver);
        } catch (ArrayStoreException e) {
            return "The Driver(ID) to show Transports for was not found";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }


    public String showAllQueuedTransports(long loggedID) {
        String resOfAllQueuedTransports = "";
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "VIEW_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            resOfAllQueuedTransports = tran_f.showAllQueuedTransports();
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e){
            e.printStackTrace();
        }
        return resOfAllQueuedTransports;
    }


    public String showAllTransports(long loggedID) {
        String resOfAllTransports = "";
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "VIEW_TRANSPORT")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            resOfAllTransports = tran_f.showAllTransports();
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e){
            e.printStackTrace();
        }
        return resOfAllTransports;
    }


    private ArrayList<String> getDriverLicenseStringFromEmpDTO(EmployeeDTO emp) {
        ArrayList<String> driverLicenseStrings = new ArrayList<>();
        if (this.employeeIntegrationService.hasRole(emp.getIsraeliId(), config.ROLE_DRIVER_A)){  driverLicenseStrings.add("A");  }
        if (this.employeeIntegrationService.hasRole(emp.getIsraeliId(), config.ROLE_DRIVER_B)){  driverLicenseStrings.add("B");  }
        if (this.employeeIntegrationService.hasRole(emp.getIsraeliId(), config.ROLE_DRIVER_C)){  driverLicenseStrings.add("C");  }
        if (this.employeeIntegrationService.hasRole(emp.getIsraeliId(), config.ROLE_DRIVER_D)){  driverLicenseStrings.add("D");  }
        if (this.employeeIntegrationService.hasRole(emp.getIsraeliId(), config.ROLE_DRIVER_E)){  driverLicenseStrings.add("E");  }
        return driverLicenseStrings;
    }


    public String showAllDrivers(long loggedID) throws JsonProcessingException {
        String res = "Showing All Drivers:\n\n";
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "VIEW_TRANSPORT")){  // a Transport manager who can view transports
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }

            List<EmployeeDTO> employeesDTOs = new ArrayList<>();

            for (String emp : this.employeeIntegrationService.getAllDrivers()){  employeesDTOs.add(this.objectMapper.readValue(emp, EmployeeDTO.class));  }

            for (EmployeeDTO emp : employeesDTOs){
                res += emp.toString() + "\nDriving Licenses: ";
                for (String licenseStr : getDriverLicenseStringFromEmpDTO(emp)){
                    res +=  licenseStr + ", ";
                }
                if (res.endsWith(", ")) { res = res.substring(0, res.length() - 2); }
                res += ".\n";
//                res = res.substring(0, res.length() - 2) + ".\n";
                res += "Availability: " + (tran_f.getTransportsRepos().getDriverIdToInTransportID().containsKey(emp.getIsraeliId()) ? ("Occupied in Transport #" + tran_f.getTransportsRepos().getDriverIdToInTransportID().get(emp.getIsraeliId())) : "Free") + ".\n\n";
            }

        } catch (JsonProcessingException e) {
            return "JSON Error: Error parsing Employees' JSON to EmployeeDTO";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            return "Error viewing All Drivers";
        }
        return res;
    }




}