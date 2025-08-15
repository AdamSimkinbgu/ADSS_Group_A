package DomainLayer.TransportDomain.TransportSubModule;

import DTOs.TransportModuleDTOs.*;
import DomainLayer.TransportDomain.SiteSubModule.Address;
import DomainLayer.TransportDomain.SiteSubModule.Site;
import DomainLayer.TransportDomain.SiteSubModule.SiteFacade;
import DomainLayer.TransportDomain.TruckSubModule.Truck;
import DomainLayer.TransportDomain.TruckSubModule.TruckFacade;
import DomainLayer.enums.enumDriLicense;
import DomainLayer.enums.enumTranProblem;
import DomainLayer.enums.enumTranStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.management.AttributeNotFoundException;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.naming.CommunicationException;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TransportController {
    private TransportsRepo transportsRepos;

    private SiteFacade siteFacade;
    private TruckFacade truckFacade;

    private ObjectMapper objectMapper;

    public TransportController(SiteFacade sF, TruckFacade tF, ObjectMapper oM) throws SQLException {
        this.transportsRepos = new TransportsRepoImpl(sF, tF);
        this.siteFacade = sF;
        this.truckFacade = tF;
        this.objectMapper = oM;
    }

    public TransportController(SiteFacade sF, TruckFacade tF, ObjectMapper oM, Connection connection) throws SQLException {
        this.transportsRepos = new TransportsRepoImpl(sF, tF, connection);
        this.siteFacade = sF;
        this.truckFacade = tF;
        this.objectMapper = oM;
    }

    public TransportsRepo getTransportsRepos() {return transportsRepos;}

    public void createTransport(String DTO_OfTransport, int queuedIndexIfWasQueued) throws JsonProcessingException, SQLException {  // time is decided when the Transport departs
        ///  NOTE: I already did all of the checks beforehand, so if we get to here, then we can successfully and legitimately create the Transport

        TransportDTO transport_DTO = this.objectMapper.readValue(DTO_OfTransport, TransportDTO.class);
        long driverId = transport_DTO.getTransportDriverID();  ///  NEW
        Truck truck = this.truckFacade.getTruckRepo().getTrucksWareHouse().get(transport_DTO.getTransportTruckNum());

        /// finding the srcSite
        Site srcSite = null;
        for (Site site : this.siteFacade.getSiteRepo().getShippingAreas().get(transport_DTO.getSrc_site().getSiteAreaNum()).getSites().values()){
            if (site.getAddress().getArea() == transport_DTO.getSrc_site().getSiteAreaNum() && site.getAddress().getAddress().equals(transport_DTO.getSrc_site().getSiteAddressString())){
                srcSite = site;
            }
        }

        int tra_id = -98; // just init, no meaning now
        if (queuedIndexIfWasQueued != -100){   // if was queued
            tra_id = this.transportsRepos.getQueuedTransports().get(queuedIndexIfWasQueued-1).getTran_Doc_ID();  // getting his already allocated Transport ID
            this.transportsRepos.deleteTransport(tra_id, queuedIndexIfWasQueued);   // removing him from the queue, from overall transports in the db as well.
        } else {   // if new Transport
            this.transportsRepos.incrementTransportIDCounter();
            tra_id = this.transportsRepos.getTransportIDCounter();
        }

        TransportDoc newTransportBeingCreated = new TransportDoc(enumTranStatus.BeingAssembled, tra_id, truck, driverId, srcSite, transport_DTO.getDeparture_dt());

        /// add the ItemsDocs and the Items that should be in them from the itemsDocDTOs:
        for (ItemsDocDTO itemsDocDTO : transport_DTO.getDests_Docs()){
            /// finding the current destSite
            Site destSite = this.siteFacade.getSiteRepo().getShippingAreas().get(itemsDocDTO.getDest_siteDTO().getSiteAreaNum()).getSites().get(itemsDocDTO.getDest_siteDTO().getSiteAddressString());

            ItemsDoc addition = newTransportBeingCreated.addDestSite(itemsDocDTO.getItemsDoc_num(), destSite);  //  all of the transport is being persisted with all of what's inside it later so ok.

            for (ItemQuantityDTO itemQuantityDTO : itemsDocDTO.getItemQuantityDTOs()){  /// add each item of that site from the DTO's data
                newTransportBeingCreated.addItem(itemsDocDTO.getItemsDoc_num(), itemQuantityDTO.getItem().getName(), itemQuantityDTO.getItem().getWeight(), itemQuantityDTO.getQuantity(), itemQuantityDTO.getItem().getCondition());
            }   //  all of the transport is being persisted with all of what's inside it later so ok.
        }

        /// finishing touches before adding Transport
        newTransportBeingCreated.setStatus(enumTranStatus.InTransit);
        newTransportBeingCreated.getTransportTruck().setInTransportID(newTransportBeingCreated.getTran_Doc_ID());
        this.truckFacade.getTruckRepo().updateTruckPersistence(newTransportBeingCreated.getTransportTruck());  // persisting truck
        this.transportsRepos.insertDriverIdToInTransportID(driverId, newTransportBeingCreated.getTran_Doc_ID());
        newTransportBeingCreated.setTruck_Depart_Weight(newTransportBeingCreated.calculateTransportItemsWeight());

        newTransportBeingCreated.setDeparture_dt(LocalDateTime.now());   // just setting the really updated time to be more precise.
        newTransportBeingCreated.calculateItemsDocsArrivalTimesInTransport();    // updating the arrival times inside of the itemsDocs before saving it

        for (ItemsDoc itemsDoc : newTransportBeingCreated.getDests_Docs()){
            this.transportsRepos.insertItemsDoc(itemsDoc, false);  // if this is a queued transport, the ItemsDocs will override themselves so ok.
        }
        this.transportsRepos.insertTransport(newTransportBeingCreated, false);  // NEW, THIS ALSO ADDS ALL THE ITEMSDOCS AND ITEMQs TO THE DB.
    }





    public void deleteTransport(int transportID) throws FileNotFoundException, SQLException {
        boolean containedInQueued = false;
        int indexOfTransport = 1;
        // checking if the transport is in the queue
        for (TransportDoc transportDoc : this.transportsRepos.getQueuedTransports()) {
            if (transportDoc.getTran_Doc_ID() == transportID) {
                containedInQueued = true;
                break;
            }
            indexOfTransport++;
        }

        TransportDoc toRemoveDoc = null;
        if (this.transportsRepos.getTransports().containsKey(transportID)){
            toRemoveDoc = this.transportsRepos.getTransports().get(transportID);
        } else if (containedInQueued) {
            toRemoveDoc = this.transportsRepos.getQueuedTransports().get(indexOfTransport-1);
        }else {
            throw new FileNotFoundException("No transport found with the Transport ID you've entered, so can't delete that Transport");
        }

        /// Transport's delete cleanups
        for (ItemsDoc itemsDocInRemovingOne : toRemoveDoc.getDests_Docs()){
            this.transportsRepos.removeItemsDoc(itemsDocInRemovingOne.getItemDoc_num(), false);
        }

        if (this.transportsRepos.getDriverIdToInTransportID().containsKey(toRemoveDoc.getTransportDriverId())){
            if (this.transportsRepos.getDriverIdToInTransportID().get(toRemoveDoc.getTransportDriverId()) == toRemoveDoc.getTran_Doc_ID()){
                this.transportsRepos.removeFromDriverIdToInTransportID(toRemoveDoc.getTransportDriverId());
            }
        }
        if (toRemoveDoc.getTransportTruck().getInTransportID() == toRemoveDoc.getTran_Doc_ID()){
            toRemoveDoc.getTransportTruck().setInTransportID(-1);  //  releasing the Truck if it's with this Transport
            this.truckFacade.getTruckRepo().updateTruckPersistence(toRemoveDoc.getTransportTruck());  // persisting the change in the truck's details.
        }
        toRemoveDoc.setStatus(enumTranStatus.Canceled);   // doesn't matter because we're deleting it

        if (containedInQueued){
            this.transportsRepos.deleteTransport(transportID, indexOfTransport);
        } else {
            this.transportsRepos.deleteTransport(transportID, -1);
        }
    }



    public void loadDBData() throws SQLException {  this.transportsRepos.loadDBData();  }



    public void setTransportStatus(int TranDocID, int menu_status_option, boolean isActiveHelper) throws FileNotFoundException, FileAlreadyExistsException, CommunicationException, CloneNotSupportedException, IndexOutOfBoundsException, SQLException {
        enumTranStatus newStatus = null;
        if (menu_status_option == 1){
            newStatus = enumTranStatus.BeingAssembled;
        } else if (menu_status_option == 2) {
            newStatus = enumTranStatus.Queued;
        } else if (menu_status_option == 3) {
            newStatus = enumTranStatus.InTransit;
        } else if (menu_status_option == 4) {
            newStatus = enumTranStatus.Completed;
        } else if (menu_status_option == 5) {
            newStatus = enumTranStatus.Canceled;
        } else if (menu_status_option == 6) {
            newStatus = enumTranStatus.BeingDelayed;
        }

        for (TransportDoc queuedTransportDoc : this.transportsRepos.getQueuedTransports()) {
            if (queuedTransportDoc.getTran_Doc_ID() == TranDocID){
                if (newStatus != enumTranStatus.Queued && newStatus != enumTranStatus.Canceled){
                    throw new StringIndexOutOfBoundsException("You cannot set a queued Transport's status as something other than Queued or Canceled.");
                } else {
                    queuedTransportDoc.setStatus(newStatus);
                    this.transportsRepos.updateTransport(queuedTransportDoc);  //  persisting change
                }
                return;
            }
        }

        if(!transportsRepos.getTransports().containsKey(TranDocID)){ throw new FileNotFoundException("The Transport ID you have entered doesn't exist in the Transports."); }
        TransportDoc transport = transportsRepos.getTransports().get(TranDocID);

        enumTranStatus currStatus = transportsRepos.getTransports().get(TranDocID).getStatus();

        if (currStatus == newStatus) { throw new FileAlreadyExistsException("The status you are trying to set already is the status of this Transport"); }


        ///   scenario 1
        if (currStatus == enumTranStatus.Canceled || currStatus == enumTranStatus.Completed || currStatus == enumTranStatus.Queued) {  // if currStatus is Not Active
            if (newStatus == enumTranStatus.BeingDelayed || newStatus == enumTranStatus.BeingAssembled || newStatus == enumTranStatus.InTransit) {  // if newStatus is Active

                // if the Truck or/and the Driver have been deleted // keep here, just use send down the this.employeeController.isActive(transport.getTransportDriverId()) var and use here.
                if (!isActiveHelper || transport.getTransportTruck().getIsDeleted()) {
                    throw new IndexOutOfBoundsException("the Truck or/and Driver of this Transport have been Deleted, you can view available Trucks or/and Drivers using the menu and set appropriately");
                }

                if (this.transportsRepos.getDriverIdToInTransportID().containsKey(transport.getTransportDriverId())) {
                    if (this.transportsRepos.getDriverIdToInTransportID().get(transport.getTransportDriverId()) != TranDocID){  // if it belongs to another Transport
                        TransportDoc otherTransport = transportsRepos.getTransports().get(this.transportsRepos.getDriverIdToInTransportID().get(transport.getTransportDriverId()));
                        if (otherTransport.getStatus() == enumTranStatus.BeingDelayed || otherTransport.getStatus() == enumTranStatus.BeingAssembled || otherTransport.getStatus() == enumTranStatus.InTransit) {  // if the other Transport is Active
                            throw new CommunicationException("cannot change Transport Status because it wants to change to an active one, but the Driver is already active in another Transport.");
                        }
                    }
                }

                if (transport.getTransportTruck().getInTransportID() != -1 && transport.getTransportTruck().getInTransportID() != TranDocID) {  // if it belongs to another Transport
                    TransportDoc otherTransport = transportsRepos.getTransports().get(transport.getTransportTruck().getInTransportID());
                    if (otherTransport.getStatus() == enumTranStatus.BeingDelayed || otherTransport.getStatus() == enumTranStatus.BeingAssembled || otherTransport.getStatus() == enumTranStatus.InTransit) {  // if the other Transport is Active
                        throw new CloneNotSupportedException("cannot change Transport Status because it wants to change to an active one, but the Truck is already active in another Transport.");
                    }
                }

                // if got to here in this case (the first 2 outer if's), then we can make these actions:
                transport.getTransportTruck().setInTransportID(TranDocID);
                this.truckFacade.getTruckRepo().updateTruckPersistence(transport.getTransportTruck());  //  persisting the change in the truck's details

                this.transportsRepos.insertDriverIdToInTransportID(transport.getTransportDriverId(), TranDocID);
                transport.setStatus(newStatus);
                this.transportsRepos.updateTransport(transport);
                return;
            }
        }

        //   seems that also checking if the Driver is an eligible site for him to drive that Transport's Truck is not necessary becasue when we set the transport we check this.

        /// scenario 2 & 3
        if (newStatus == enumTranStatus.Canceled || newStatus == enumTranStatus.Completed || newStatus == enumTranStatus.Queued) {  // if newStatus is Not Active
            if (currStatus == enumTranStatus.BeingDelayed || currStatus == enumTranStatus.BeingAssembled || currStatus == enumTranStatus.InTransit) {  // if currStatus is Active
                if (this.transportsRepos.getDriverIdToInTransportID().containsKey(transport.getTransportDriverId())) {
                    if (this.transportsRepos.getDriverIdToInTransportID().get(transport.getTransportDriverId()) == TranDocID) {   // if he is active in the transport
                        this.transportsRepos.removeFromDriverIdToInTransportID(transport.getTransportDriverId());  //  release him and persist change
                    }
                }
                if (transport.getTransportTruck().getInTransportID() == TranDocID) {   // if it is active in the transport
                    transport.getTransportTruck().setInTransportID(-1);  //  release it
                    this.truckFacade.getTruckRepo().updateTruckPersistence(transport.getTransportTruck());
                }
            }
        }

        transport.setStatus(newStatus);  //  if it wants to change from Active to Active OR from Non Active to Non Active.
        this.transportsRepos.updateTransport(transport);
    }














    public void setTransportTruck(int TranDocID, int truckNum, boolean hasRoleHelper) throws FileNotFoundException, ArrayIndexOutOfBoundsException, CommunicationException, FileAlreadyExistsException, CloneNotSupportedException, AbstractMethodError, ClassNotFoundException, SQLException {
        // check NewTruck - Driver Licenses Compatability
        if (!hasRoleHelper){
            throw new CommunicationException("The transport's driver doesn't have the fitting license for the new Truck you want to set.");
        } else if (!this.truckFacade.getTruckRepo().getTrucksWareHouse().containsKey(truckNum)){
            throw new ArrayIndexOutOfBoundsException("The Truck number you have entered doesn't exist.");
        } else if (truckFacade.getTruckRepo().getTrucksWareHouse().get(truckNum).getIsDeleted()) {   // if the Truck has been deleted
            throw new ClassNotFoundException("the Truck of this Transport have been Deleted, you can view available Trucks using the menu and set appropriately");
        }
        Truck truck = this.truckFacade.getTruckRepo().getTrucksWareHouse().get(truckNum);  // the truck in contention to be set in the Transport

        for (TransportDoc queuedTransportDoc : this.transportsRepos.getQueuedTransports()) {   // checking the queued Transports first
            if (TranDocID == queuedTransportDoc.getTran_Doc_ID()){
                //  also CHECK IF THE TRUCK CAN CARRY THE WEIGHT OF THAT TRANSPORT   <<-----------------------
                if (queuedTransportDoc.calculateTransportItemsWeight() > truck.getMax_carry_weight()){
                    throw new AbstractMethodError("The Truck you are trying to set to this Transport can't carry this Transport's Weight.");
                }
                queuedTransportDoc.setTransportTruck(truck);
                this.transportsRepos.updateTransport(queuedTransportDoc);
                return;
            }
        }
        if(!transportsRepos.getTransports().containsKey(TranDocID)){
            throw new FileNotFoundException("The Transport ID you have entered doesn't exist.");
        } else if (this.transportsRepos.getTransports().get(TranDocID).getTransportTruck().getTruck_num() == truckNum) {
            throw new FileAlreadyExistsException("This Truck is already the Truck of this Transport");
        }

        /// Note:  a Truck cannot be in more than 1 active Transport

        if(truck.getInTransportID() != -1){    // so it belong to another Transport Active right now, ----->>  We take care of the Transport's status in the setTransportStatus() function
            if (this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.InTransit || this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.BeingDelayed || this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.BeingAssembled) {
                throw new CloneNotSupportedException("The Transport you are trying to set to is Active and The Truck you are trying to set is already Occupied with another Active Transport right now");
            }
        }
        // if we got to here so the truck is not in an active transport OR/AND the Transport we are trying to set is not active

        //  CHECK IF THE TRUCK CAN CARRY THE WEIGHT OF THAT TRANSPORT   <<-----------------------
        if (this.transportsRepos.getTransports().get(TranDocID).calculateTransportItemsWeight() > truck.getMax_carry_weight()){
            throw new AbstractMethodError("The Truck you are trying to set to this Transport can't carry this Transport's Weight.");
        }

        this.transportsRepos.getTransports().get(TranDocID).setTransportTruck(truck);  // more logic inside this function
        this.transportsRepos.updateTransport(this.transportsRepos.getTransports().get(TranDocID));   //  persisting this change

        if (this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.InTransit || this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.BeingAssembled || this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.BeingDelayed) {
            this.transportsRepos.getTransports().get(TranDocID).getTransportTruck().setInTransportID(TranDocID);   ///  only if the current Transport is active
            this.truckFacade.getTruckRepo().updateTruckPersistence(this.transportsRepos.getTransports().get(TranDocID).getTransportTruck());
        }

    }




    public void setTransportDriver(int TranDocID, long DriverID, boolean isNotDriver, boolean isActive, boolean hasRole) throws FileNotFoundException, ArrayIndexOutOfBoundsException, FileAlreadyExistsException, CloneNotSupportedException, CommunicationException, ClassNotFoundException, SQLException {
        if (isNotDriver){
            throw new ArrayIndexOutOfBoundsException("The Driver ID you have entered doesn't exist.");
        } else if (!isActive) {   // if the Driver has been deleted
            throw new ClassNotFoundException("the Driver of this Transport have been Deleted, you can view available Drivers using the menu and set appropriately");
        } else if (!hasRole){   // Check Truck - NewDriver Licenses Compatability
            throw new CommunicationException("The New Driver you are trying to set doesn't have the fitting license for the Truck that is in the Transport.");
        }

        for (TransportDoc queuedTransportDoc : this.transportsRepos.getQueuedTransports()) {   // checking the queued Transports first
            if (TranDocID == queuedTransportDoc.getTran_Doc_ID()){
                queuedTransportDoc.setTransportDriverId(DriverID);
                this.transportsRepos.updateTransport(queuedTransportDoc);
                return;
            }
        }

        if(!transportsRepos.getTransports().containsKey(TranDocID)){
            throw new FileNotFoundException("The Transport ID you have entered doesn't exist.");
        } else if (this.transportsRepos.getTransports().get(TranDocID).getTransportDriverId() == DriverID) {
            throw new FileAlreadyExistsException("This Driver is already the Driver of this Transport");
        }

        // Note:  a Driver cannot be in more than 1 active Transport
        if(this.transportsRepos.getDriverIdToInTransportID().containsKey(DriverID)){    // so it belong to another Transport Active right now, We take care of the Transport's status in the setTransportStatus() function
            if (this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.InTransit || this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.BeingDelayed || this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.BeingAssembled) {
                throw new CloneNotSupportedException("The Transport you are trying to set to is Active and The Driver you are trying to set is already Occupied with another Active Transport right now");
            }
        }
        // if we got to here so the driver is not in an active transport OR/AND the Transport we are trying to set is not active

        long DriverIDToRemoveFromTran = -1;
        for (long driverID : this.transportsRepos.getDriverIdToInTransportID().keySet()){ // changes the old driver to InTransportID = -1
            if (this.transportsRepos.getDriverIdToInTransportID().get(driverID) == TranDocID){ DriverIDToRemoveFromTran = driverID; break; }
        }
        if (DriverIDToRemoveFromTran != -1){  this.transportsRepos.removeFromDriverIdToInTransportID(DriverIDToRemoveFromTran);  }

        this.transportsRepos.getTransports().get(TranDocID).setTransportDriverId(DriverID);
        this.transportsRepos.updateTransport(this.transportsRepos.getTransports().get(TranDocID));  //  persisting change

        if (this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.InTransit || this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.BeingAssembled || this.transportsRepos.getTransports().get(TranDocID).getStatus() == enumTranStatus.BeingDelayed) {
            this.transportsRepos.insertDriverIdToInTransportID(DriverID, TranDocID);   ///  only if the current Transport is active
        }
    }


    public TruckFacade getTruckFacade() {return truckFacade;}


    public void isTruckDriverPairingGood(int truckNum, long driverID, boolean isNotDriver, boolean hasRole22) throws FileNotFoundException, ArrayIndexOutOfBoundsException, ClassNotFoundException, CloneNotSupportedException, CommunicationException {
        if (!this.truckFacade.getTruckRepo().getTrucksWareHouse().containsKey(truckNum)) {
            throw new FileNotFoundException("Truck Number entered doesn't exist");
        } else if (isNotDriver){
            throw new ArrayIndexOutOfBoundsException("The Driver ID you have entered doesn't exist");
        } else if (this.truckFacade.getTruckRepo().getTrucksWareHouse().get(truckNum).getInTransportID() != -1){
            throw new CloneNotSupportedException("The Truck you chose is partaking in another Active Transport right now");
        }
        // check if the driver has a license matching the truck's license
        if (!hasRole22){ throw new CommunicationException("The Driver you chose doesn't have the fitting license for the Truck you chose"); }
    }



    public String getTruckLicenseAsStringRole(int trucknum){
        String lice = "";
        enumDriLicense lic = this.truckFacade.getTruckRepo().getTrucksWareHouse().get(trucknum).getValid_license();
        if (lic.equals(enumDriLicense.A)){
            lice = "DriverA";
        } else if (lic.equals(enumDriLicense.B)){
            lice = "DriverB";
        } else if (lic.equals(enumDriLicense.C)){
            lice = "DriverC";
        } else if (lic.equals(enumDriLicense.D)){
            lice = "DriverD";
        } else if (lic.equals(enumDriLicense.E)){
            lice = "DriverE";
        }
        return lice;
    }





    public void addTransportToWaitQueue(TransportDoc tempTransport) throws SQLException {
        if (tempTransport.getTran_Doc_ID() == -99){  // so it will add only new ones, not ones that have gotten checked again and were already added to the queue
            this.transportsRepos.incrementTransportIDCounter();
            tempTransport.setTran_Doc_ID(this.transportsRepos.getTransportIDCounter());
            tempTransport.setStatus(enumTranStatus.Queued);
            tempTransport.setTruck_Depart_Weight(tempTransport.calculateTransportItemsWeight());

            tempTransport.setDeparture_dt(LocalDateTime.now());   // just setting the really updated time to be more precise.
            tempTransport.calculateItemsDocsArrivalTimesInTransport();

            for (ItemsDoc itemsDoc : tempTransport.getDests_Docs()){
                this.transportsRepos.insertItemsDoc(itemsDoc, false);  // inserting whole transport here later
            }
            this.transportsRepos.insertTransport(tempTransport, true);



        }
    }



    public void addFromTransportDTOStringToWaitQueue(String DTO_OfTransport) throws JsonProcessingException, SQLException {
        TransportDTO transport_DTO = this.objectMapper.readValue(DTO_OfTransport, TransportDTO.class);
        TransportDoc tempTransport = this.transportsRepos.convertTransportDTOToTransportDoc(transport_DTO);
        this.addTransportToWaitQueue(tempTransport);
    }









    public String checkTransportValidity(String DTO_OfTransport, boolean hasRole11, boolean isThereMatchAtAllBetweenLicenses) throws JsonProcessingException, SQLException {  ///  returns: "Valid", "BadLicenses", "overallWeight-truckMaxCarryWeight", "Queue", "Occupied"
        TransportDTO transport_DTO = this.objectMapper.readValue(DTO_OfTransport, TransportDTO.class);
        TransportDoc tempTransport = this.transportsRepos.convertTransportDTOToTransportDoc(transport_DTO);
        String res = "Valid";

        if(!isThereMatchAtAllBetweenLicenses){
            this.addTransportToWaitQueue(tempTransport);
            return "Queue";
        }
        // else: continue to check other stuff
        /// /////////////////////////////////    <<-------------------------------------   checking if the Driver-Truck pairing is compatible
        if (!hasRole11){
            return "BadLicenses";
        }
        // else: continue to check another thing
        /// /////////////////////////////////    <<-------------------------------------   checking if the Driver-Truck pairing are both free
        if (isDriverActive(tempTransport.getTransportDriverId())){ return "Occupied"; }
        if (isTruckActive(tempTransport.getTransportTruck().getTruck_num())){ return "Occupied"; }
        // else: continue to check another thing
        /// /////////////////////////////////    <<-------------------------------------   checking Overall Weight

        double overallTransportWeight = tempTransport.calculateTransportItemsWeight();
        if (tempTransport.getTransportTruck().getMax_carry_weight() < overallTransportWeight){
            res = "" + overallTransportWeight + "-" + tempTransport.getTransportTruck().getMax_carry_weight();  // "overallWeight-truckMaxCarryWeight" format
        }
        return res;
    }




    public boolean isDriverActive(long driverID){
        if (this.transportsRepos.getDriverIdToInTransportID().containsKey(driverID)) {  // if driver is in another transport
            TransportDoc otherTransport = transportsRepos.getTransports().get(this.transportsRepos.getDriverIdToInTransportID().get(driverID));
            if(otherTransport.getStatus() == enumTranStatus.BeingAssembled || otherTransport.getStatus() == enumTranStatus.InTransit || otherTransport.getStatus() == enumTranStatus.BeingDelayed){  // if other Transport is Active
                return true;   // because the driver is Occupied in another Active Transport
            }
        }
        return false;
    }


    public boolean isTruckActive(int truck_num){
        Truck truck = this.truckFacade.getTruckRepo().getTrucksWareHouse().get(truck_num);
        if (truck.getInTransportID() != -1) {  // if truck is in another transport
            TransportDoc otherTransport = transportsRepos.getTransports().get(truck.getInTransportID());
            if(otherTransport.getStatus() == enumTranStatus.BeingAssembled || otherTransport.getStatus() == enumTranStatus.InTransit || otherTransport.getStatus() == enumTranStatus.BeingDelayed){  // if other Transport is Active
                return true;   // because the truck is Occupied in another Active Transport
            }
        }
        return false;
    }










    public String getTransportAsDTOJson(int tranId) throws JsonProcessingException {
        for (TransportDoc transportDoc : this.transportsRepos.getTransports().values()){
            if (transportDoc.getTran_Doc_ID() == tranId){
                return this.objectMapper.writeValueAsString(this.transportsRepos.convertTransportDocToTransportDTO(transportDoc));
            }
        }
        for (TransportDoc queuedTransportDoc : this.transportsRepos.getQueuedTransports()){
            if (queuedTransportDoc.getTran_Doc_ID() == tranId){
                return this.objectMapper.writeValueAsString(this.transportsRepos.convertTransportDocToTransportDTO(queuedTransportDoc));
            }
        }
        return null;
    }




    // used for the checkIfATransportCanGo function
    public String getAQueuedTransportAsDTOJson(int index) throws IndexOutOfBoundsException, AttributeNotFoundException, JsonProcessingException {
        if(!this.transportsRepos.getQueuedTransports().isEmpty()){
            if (index > this.transportsRepos.getQueuedTransports().size()) {   //  the index is going to be 1, 2...
                throw new IndexOutOfBoundsException("The index you've entered in invalid. (it's above the last index which is " + this.transportsRepos.getQueuedTransports().size() + ")");
            }
            TransportDoc queuedTransport = transportsRepos.getQueuedTransports().get(index-1);
            TransportDTO transportDTO = this.transportsRepos.convertTransportDocToTransportDTO(queuedTransport);
            String transportDTOAsJson = objectMapper.writeValueAsString(transportDTO);

            return transportDTOAsJson;
        }else {
            throw new AttributeNotFoundException("There are no Queued Transports.");
        }
    }


















    public void addTransportProblem(int TransportID, int menu_Problem_option) throws FileNotFoundException, FileAlreadyExistsException, SQLException {
        enumTranProblem probEnum = null;
        if(menu_Problem_option == 1){
            probEnum = enumTranProblem.Puncture;
        }else if (menu_Problem_option == 2){
            probEnum = enumTranProblem.HeavyTraffic;
        }else if (menu_Problem_option == 3){
            probEnum = enumTranProblem.RoadAccident;
        }else if (menu_Problem_option == 4){
            probEnum = enumTranProblem.UnresponsiveContact;
        }else if (menu_Problem_option == 5){
            probEnum = enumTranProblem.TruckVehicleProblem;
        }else if (menu_Problem_option == 6){
            probEnum = enumTranProblem.EmptyTruckGasTank;
        }

        for (TransportDoc queuedTransport : this.transportsRepos.getQueuedTransports()){  // checking if in queuedTransports
            if (TransportID == queuedTransport.getTran_Doc_ID()){
                if (queuedTransport.addTransportProblem(probEnum) == -1){
                    throw new FileAlreadyExistsException("The problem you entered already exists in this Transport");
                }
                this.transportsRepos.insertPersistTransportProblem(TransportID, probEnum);
                return;
            }
        }

        if (!this.transportsRepos.getTransports().containsKey(TransportID)) {
            throw new FileNotFoundException("Transport ID doesn't exist in the Transports.");
        }

        if (this.transportsRepos.getTransports().get(TransportID).addTransportProblem(probEnum) == -1){
            throw new FileAlreadyExistsException("The problem you entered already exists in this Transport");
        }
        this.transportsRepos.insertPersistTransportProblem(TransportID, probEnum);
    }




    public void removeTransportProblem(int TransportID, int menu_Problem_option) throws FileNotFoundException, FileAlreadyExistsException, SQLException {
        enumTranProblem probEnum = null;
        if(menu_Problem_option == 1){
            probEnum = enumTranProblem.Puncture;
        }else if (menu_Problem_option == 2){
            probEnum = enumTranProblem.HeavyTraffic;
        }else if (menu_Problem_option == 3){
            probEnum = enumTranProblem.RoadAccident;
        }else if (menu_Problem_option == 4){
            probEnum = enumTranProblem.UnresponsiveContact;
        }else if (menu_Problem_option == 5){
            probEnum = enumTranProblem.TruckVehicleProblem;
        }else if (menu_Problem_option == 6){
            probEnum = enumTranProblem.EmptyTruckGasTank;
        }

        for (TransportDoc queuedTransport : this.transportsRepos.getQueuedTransports()){
            if (TransportID == queuedTransport.getTran_Doc_ID()){
                if (queuedTransport.removeTransportProblem(probEnum) == -1){
                    throw new FileAlreadyExistsException("The problem you entered already doesn't exists in this Transport");
                }
                this.transportsRepos.removePersistTransportProblem(TransportID, probEnum);
                return;
            }
        }

        if (!this.transportsRepos.getTransports().containsKey(TransportID)) {
            throw new FileNotFoundException("Transport ID doesn't exist.");
        }

        if (this.transportsRepos.getTransports().get(TransportID).removeTransportProblem(probEnum) == -1){
            throw new FileAlreadyExistsException("The problem you entered already doesn't exists in this Transport");
        }
        this.transportsRepos.removePersistTransportProblem(TransportID, probEnum);
    }






    public boolean doesTranIDExist(int TranID){
        boolean exists = this.transportsRepos.getTransports().containsKey(TranID);
        for (TransportDoc queuedTransport : this.transportsRepos.getQueuedTransports()){
            if (TranID == queuedTransport.getTran_Doc_ID()){
                return true;
            }
        }
        return exists;
    }






    public void addDestSiteToTransport(int tran_ID, int itemsDoc_num, int destSiteArea, String destSiteAddress) throws FileNotFoundException, FileAlreadyExistsException, CommunicationException, IndexOutOfBoundsException, ClassNotFoundException, SQLException {
        if (this.transportsRepos.getItemsDocs().containsKey(itemsDoc_num)) {
            throw new FileAlreadyExistsException("The Site's Items Document Number you are trying to add already exists.");
        } else if (!this.siteFacade.getSiteRepo().getShippingAreas().containsKey(destSiteArea)) {
            throw new IndexOutOfBoundsException("Cannot add a Site with a non existent area number.");
        } else if (!this.siteFacade.getSiteRepo().getShippingAreas().get(destSiteArea).getSites().containsKey(destSiteAddress)) {
            throw new ClassNotFoundException("Cannot add a site with a not found address String in its area.");
        }
        Site forContacctInfo = this.siteFacade.getSiteRepo().getShippingAreas().get(destSiteArea).getSites().get(destSiteAddress);
        String contName = forContacctInfo.getcName();
        long contNum = forContacctInfo.getcNumber();

        TransportDoc temp = null;
        int queuedIndex = 1;
        for (TransportDoc queuedTransport : this.transportsRepos.getQueuedTransports()){   //  first checking in the queuedTransports
            if (tran_ID == queuedTransport.getTran_Doc_ID()){
                ItemsDoc addition = queuedTransport.addDestSite(itemsDoc_num, new Site(new Address(destSiteArea, destSiteAddress), contName, contNum));
                // ALSO recalculates arrival times within ItemsDocs of this Transport in the TransportDoc.
                if (addition == null){  throw new CommunicationException("Destination Site already in this Transport, you can add items to that site instead.");  }
                this.transportsRepos.insertItemsDoc(addition, false);  //  this line won't update all the other itemsDocs arrival times in the DB, only in BL.
                temp = queuedTransport;
                break;
            }
            queuedIndex++;
        }
        if (temp != null){
            this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), queuedIndex);
            this.transportsRepos.insertTransport(temp, true);
            //  so the order of the rows in the database of the itemsDocs will be in the correct arrival order, and with the updated arrival times.
            return;
        }

        if (!transportsRepos.getTransports().containsKey(tran_ID)) { throw new FileNotFoundException("The Transport ID you've entered doesn't exist in the Transports."); }
        temp = this.transportsRepos.getTransports().get(tran_ID);
        ItemsDoc addition = temp.addDestSite(itemsDoc_num, new Site(new Address(destSiteArea, destSiteAddress), contName, contNum));
        // recalculates arrival times within ItemsDocs of this Transport in the TransportDoc.
        if (addition == null){  throw new CommunicationException("Destination Site already in this Transport, you can add items to that site instead.");  }

        this.transportsRepos.insertItemsDoc(addition, false);  //  this line won't update all the other itemsDocs arrival times in the DB, only in BL.
        this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), -1);
        this.transportsRepos.insertTransport(temp, false);
        //  so the order of the rows in the database of the itemsDocs will be in the correct arrival order, and with the updated arrival times.
    }




    public void removeDestSiteFromTransport(int tran_ID, int itemsDoc_num) throws FileNotFoundException, CommunicationException, ClassNotFoundException, SQLException {
        if (!this.transportsRepos.getItemsDocs().containsKey(itemsDoc_num)) {
            throw new CommunicationException("The Site's Items Document Number you are trying to remove doesn't exist in the system.");
        }

        TransportDoc temp = null;
        int queuedIndex = 1;
        for (TransportDoc queuedTransport : this.transportsRepos.getQueuedTransports()){
            if (tran_ID == queuedTransport.getTran_Doc_ID()){
                if (queuedTransport.removeDestSite(itemsDoc_num) == -1){  throw new ClassNotFoundException("The Site's Items Document Number is not in that Transport");  }
                // also recalculates arrival times within ItemsDocs of this Transport in the TransportDoc.
                this.transportsRepos.removeItemsDoc(itemsDoc_num, false);  //  this line won't update all the other itemsDocs arrival times in the DB, only in BL.
                temp = queuedTransport;
                break;
            }
            queuedIndex++;
        }
        if (temp != null){
            this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), queuedIndex);
            this.transportsRepos.insertTransport(temp, true);
            //  so the order of the rows in the database of the itemsDocs will be in the correct arrival order, and with the updated arrival times.
            return;
        }

        if (!transportsRepos.getTransports().containsKey(tran_ID)) {  throw new FileNotFoundException("The Transport ID you've entered doesn't exist.");  }

        temp = this.transportsRepos.getTransports().get(tran_ID);
        if (temp.removeDestSite(itemsDoc_num) == -1){  throw new ClassNotFoundException("The Site's Items Document Number is not in that Transport");  }
        // also recalculates arrival times within ItemsDocs of this Transport in the TransportDoc.

        this.transportsRepos.removeItemsDoc(itemsDoc_num, false);  //  this line won't update all the other itemsDocs arrival times in the DB, only in BL.
        this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), -1);
        this.transportsRepos.insertTransport(temp, false);
        //  so the order of the rows in the database of the itemsDocs will be in the correct arrival order, and with the updated arrival times.
    }





    public void setSiteArrivalIndexInTransport(int transportID, int siteArea, String siteAddress, int index) throws FileNotFoundException, ClassNotFoundException, AbstractMethodError, CommunicationException, SQLException {
        if (!this.siteFacade.getSiteRepo().getShippingAreas().containsKey(siteArea)) {
            throw new IndexOutOfBoundsException("You entered a Site with a non existent area number.");
        } else if (!this.siteFacade.getSiteRepo().getShippingAreas().get(siteArea).getSites().containsKey(siteAddress)) {
            throw new CommunicationException("You entered a site address String that doesn't exist in that area.");
        }

        TransportDoc temp = null;
        int indexInQueueIteration = 1;
        for (TransportDoc queuedTransport : this.transportsRepos.getQueuedTransports()){  //  checking the queuedTransports first here
            if (transportID == queuedTransport.getTran_Doc_ID()){

                boolean siteResidesInQueuedTransport = false;
                for (ItemsDoc itemsDoc : queuedTransport.getDests_Docs()){
                    if (itemsDoc.getDest_site().getAddress().getArea() == siteArea && itemsDoc.getDest_site().getAddress().getAddress().equals(siteAddress)){
                        siteResidesInQueuedTransport = true;
                    }
                }

                if(!siteResidesInQueuedTransport){
                    throw new ClassNotFoundException("Site not found inside of that transport");
                } else if (index > queuedTransport.getDests_Docs().size()) {   // if the index is valid    //  index should be 1, 2, ....
                    throw new AbstractMethodError("The Index entered is bigger than the amount of sites in the Transport, so can't put that site in that bigger index");
                }

                // also recalculates arrival times within ItemsDocs of this Transport in the TransportDoc.
                queuedTransport.setSiteArrivalIndexInTransport(siteArea, siteAddress, index);
                temp = queuedTransport;
                break;
            }
            indexInQueueIteration++;
        }
        if (temp != null){
            this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), indexInQueueIteration);
            this.transportsRepos.insertTransport(temp, true);
            //  so the order of the rows in the database of the itemsDocs will be in the correct arrival order, and with the updated arrival times.
            return;
        }

        if (!this.transportsRepos.getTransports().containsKey(transportID)){ throw new FileNotFoundException("The transport ID given was not found"); }

        boolean siteResidesInTransport = false;
        for (ItemsDoc itemsDoc : this.transportsRepos.getTransports().get(transportID).getDests_Docs()){
            if (itemsDoc.getDest_site().getAddress().getArea() == siteArea && itemsDoc.getDest_site().getAddress().getAddress().equals(siteAddress)){
                siteResidesInTransport = true;
            }
        }

        if(!siteResidesInTransport){
            throw new ClassNotFoundException("Site not found inside of that transport");
        } else if (index > this.transportsRepos.getTransports().get(transportID).getDests_Docs().size()) {   // if the index is valid    //  index should be 1, 2, ....
            throw new AbstractMethodError("The Index entered is bigger than the amount of sites in the Transport, so can't put that site in that bigger index");
        }

        temp = this.transportsRepos.getTransports().get(transportID);
        temp.setSiteArrivalIndexInTransport(siteArea, siteAddress, index); // also recalculates arrival times within ItemsDocs of this Transport in the TransportDoc.
        this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), -1);
        this.transportsRepos.insertTransport(temp, false);
        //  so the order of the rows in the database of the itemsDocs will be in the correct arrival order, and with the updated arrival times.
    }




    public boolean doesAddressExistInTransport(int transportID, int siteArea, String siteAddress) {
        for (TransportDoc queuedTransport : this.transportsRepos.getQueuedTransports()){
            if (transportID == queuedTransport.getTran_Doc_ID()){
                for (ItemsDoc itemsDoc : queuedTransport.getDests_Docs()){
                    if (itemsDoc.getDest_site().getAddress().getArea() == siteArea && itemsDoc.getDest_site().getAddress().getAddress().equals(siteAddress)){
                        return true;
                    }
                }
            }
        }
        //  now searching in the regular transports
        if (!this.transportsRepos.getTransports().containsKey(transportID)) { return false; }
        for (ItemsDoc itemsDoc : this.transportsRepos.getTransports().get(transportID).getDests_Docs()){
            if (itemsDoc.getDest_site().getAddress().getArea() == siteArea && itemsDoc.getDest_site().getAddress().getAddress().equals(siteAddress)){
                return true;
            }
        }
        return false;
    }




    public boolean doesItemsDocIDExistInTransport(int itemsDocNum, int tranId) {     ///   helper function
        for (TransportDoc queuedTransport : this.transportsRepos.getQueuedTransports()){
            if (queuedTransport.getTran_Doc_ID() == tranId){
                for (ItemsDoc itemsDoc : queuedTransport.getDests_Docs()){
                    if (itemsDocNum == itemsDoc.getItemDoc_num()){
                        return true;
                    }
                }
            }
        }
        if (this.transportsRepos.getTransports().containsKey(tranId)){
            for (ItemsDoc itemsDoc : this.transportsRepos.getTransports().get(tranId).getDests_Docs()){
                if (itemsDoc.getItemDoc_num() == itemsDocNum){
                    return true;
                }
            }
        }
        return false;
    }




    public void changeAnItemsDocNum(int oldItemsDocNum, int newItemsDocNum) throws FileNotFoundException, KeyAlreadyExistsException, SQLException {
        if (!this.transportsRepos.getItemsDocs().containsKey(oldItemsDocNum)) {
            throw new FileNotFoundException();
        } else if (this.transportsRepos.getItemsDocs().containsKey(newItemsDocNum)) {  throw new KeyAlreadyExistsException();  }

        ItemsDoc temp = this.transportsRepos.getItemsDocs().get(oldItemsDocNum);
        temp.setItemDoc_num(newItemsDocNum);

        this.transportsRepos.getItemsDocs().put(newItemsDocNum, temp);

        this.transportsRepos.getItemsDocs().remove(oldItemsDocNum);

        this.transportsRepos.updateItemsDocPersistency(oldItemsDocNum, temp);
    }




    public boolean checkValidItemsDocID(int currItemsDocNum) {
        if (this.transportsRepos.getItemsDocs().containsKey(currItemsDocNum)) {
            return false;
        }
        return true;
    }



    //NOTE: only if he actively drives a transport, for: a driver can edit items condition in a transport he is driving now.
    public void checkIfDriverDrivesThisItemsDoc(long id, int itemsDocId, boolean isNotDriver) throws FileNotFoundException, IllegalAccessException, ClassNotFoundException {
        if (!this.transportsRepos.getItemsDocs().containsKey(itemsDocId)) { throw new FileNotFoundException("Items Document ID not found."); }
        if (isNotDriver){ throw new ClassNotFoundException("Driver ID doesn't exist."); }

        boolean driverDrivesThisItemsDoc = false;
        for (TransportDoc transportDoc : this.transportsRepos.getTransports().values()) {
            if (transportDoc.getTransportDriverId() == id && this.transportsRepos.getDriverIdToInTransportID().get(id) == transportDoc.getTran_Doc_ID()){
                // now here we know that he actively drives this transport.
                for (ItemsDoc itemsDoc : transportDoc.getDests_Docs()) {
                    if (itemsDoc.getItemDoc_num() == itemsDocId) {
                        driverDrivesThisItemsDoc = true;
                        break; // because found
                    }
                }
            }
            if (driverDrivesThisItemsDoc){ break;}   // because found
        }

        if (!driverDrivesThisItemsDoc) { throw new IllegalAccessException("Driver doesn't drive this Items Document's Transport"); }
    }


















    public void addItem(int itemsDoc_num, String itemName, double itemWeight, int amount, boolean cond) throws FileNotFoundException, IndexOutOfBoundsException, SQLException {
        if (!this.transportsRepos.getItemsDocs().containsKey(itemsDoc_num)) { throw new FileNotFoundException("Item's Document ID not found within sent Transports"); }

        for (TransportDoc queuedtransportDoc : this.transportsRepos.getQueuedTransports()) {   // checking in the queuedTransports first if there. // weight checking
            for (ItemsDoc itemsDoc : queuedtransportDoc.getDests_Docs()) {
                if (itemsDoc.getItemDoc_num() == itemsDoc_num) {
                    if (queuedtransportDoc.getTruck_Depart_Weight() + (itemWeight*amount) > queuedtransportDoc.getTransportTruck().getMax_carry_weight()){
                        throw new IndexOutOfBoundsException("Cannot add Item to transport because the new weight exceeds the maximum carry weight");
                    }
                }
            }
        }
        for (TransportDoc transportDoc : this.transportsRepos.getTransports().values()) { // weight checking in the regular transports hashmap, if there
            for (ItemsDoc itemsDoc : transportDoc.getDests_Docs()) {
                if (itemsDoc.getItemDoc_num() == itemsDoc_num) {
                    if (transportDoc.getTruck_Depart_Weight() + (itemWeight*amount) > transportDoc.getTransportTruck().getMax_carry_weight()){
                        throw new IndexOutOfBoundsException("Cannot add Item to transport because the new weight exceeds the maximum carry weight");
                    }
                }
            }
        }

        // adding the item to the ItemsDoc
        int res = this.transportsRepos.getItemsDocs().get(itemsDoc_num).addItem(itemName, itemWeight, cond, amount);

        TransportDoc temp = null;   ///  so the removal and insertion of the Transport will be outside of a loop over the elements to prevent problems
        // setting the new weight of the Transport if in the queuedTransports
        int queuedIndex = 1;
        for (TransportDoc queuedTransportDoc : this.transportsRepos.getQueuedTransports()) {
            for (ItemsDoc itemsDoc : queuedTransportDoc.getDests_Docs()) {
                if (itemsDoc.getItemDoc_num() == itemsDoc_num) {
                    queuedTransportDoc.setTruck_Depart_Weight(queuedTransportDoc.getTruck_Depart_Weight() + (itemWeight*amount));  // updating the weight
                    temp = queuedTransportDoc;
                    break;
                }
            }
            if (temp != null){  break;  }
            queuedIndex++;
        }
        if (temp != null){
            this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), queuedIndex);
            this.transportsRepos.insertTransport(temp, true);
            return;
        }

        // setting the new weight of the Transport if in the regular transports hashmap
        for (TransportDoc transportDoc : this.transportsRepos.getTransports().values()) {
            for (ItemsDoc itemsDoc : transportDoc.getDests_Docs()) {
                if (itemsDoc.getItemDoc_num() == itemsDoc_num) {
                    transportDoc.setTruck_Depart_Weight(transportDoc.getTruck_Depart_Weight() + (itemWeight*amount));  // updating the weight
                    temp = transportDoc;
                    break;
                }
            }
            if (temp != null){  break;  }
        }
        if (temp != null){
            this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), -1);
            this.transportsRepos.insertTransport(temp, false);
        }

    }




    public void removeItem(int itemsDoc_num, String itemName, double itemWeight, int amount, boolean cond) throws FileNotFoundException, ClassNotFoundException, SQLException {
        if (!this.transportsRepos.getItemsDocs().containsKey(itemsDoc_num)) { throw new FileNotFoundException("Item's Document ID not found within sent Transports"); }

        int amount_removed = this.transportsRepos.getItemsDocs().get(itemsDoc_num).removeItem(itemName, itemWeight, cond, amount);
        if (amount_removed == -1){ throw new ClassNotFoundException("Item to remove not found in that Items Document"); }

        TransportDoc temp = null;   ///  so the removal and insertion of the Transport will be outside of a loop over the elements to prevent problems
        // checking first in the queued transports, and decreasing weight
        int queuedIndex = 1;
        for (TransportDoc queuedTransportDoc : this.transportsRepos.getQueuedTransports()) {
            for (ItemsDoc itemsDoc : queuedTransportDoc.getDests_Docs()) {
                if (itemsDoc.getItemDoc_num() == itemsDoc_num) {
                    queuedTransportDoc.setTruck_Depart_Weight(queuedTransportDoc.getTruck_Depart_Weight() - (itemWeight*amount_removed));  // updating the weight
                    temp = queuedTransportDoc;
                    break;
                }
            }
            if (temp != null){  break;  }
            queuedIndex++;
        }
        if (temp != null){
            this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), queuedIndex);
            this.transportsRepos.insertTransport(temp, true);
            return;
        }

        // checking in the regular transports, and decreasing weight
        for (TransportDoc transportDoc : this.transportsRepos.getTransports().values()) {
            for (ItemsDoc itemsDoc : transportDoc.getDests_Docs()) {
                if (itemsDoc.getItemDoc_num() == itemsDoc_num) {
                    transportDoc.setTruck_Depart_Weight(transportDoc.getTruck_Depart_Weight() - (itemWeight*amount_removed));  // updating the weight
                    temp = transportDoc;
                    break;
                }
            }
            if (temp != null){  break;  }
        }
        if (temp != null){
            this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), -1);
            this.transportsRepos.insertTransport(temp, false);
        }

    }


    public void setItemCond(int itemsDoc_num, String itemName, double itemWeight, int amount, boolean newCond) throws FileNotFoundException, ClassNotFoundException, SQLException {
        if (!this.transportsRepos.getItemsDocs().containsKey(itemsDoc_num)) { throw new FileNotFoundException("Item's Document ID not found within sent Transports"); }
        ItemsDoc tempItemsDoc = this.transportsRepos.getItemsDocs().get(itemsDoc_num);
        int res = tempItemsDoc.setItemCond(itemName, itemWeight, amount, newCond);
        if (res == -1){ throw new ClassNotFoundException("Item to change condition to was not found in that Items Document"); }

        TransportDoc temp = null;   ///  so the removal and insertion of the Transport will be outside of a loop over the elements to prevent problems
        // checking first in the queued transports, and decreasing weight
        int queuedIndex = 1;
        for (TransportDoc queuedTransportDoc : this.transportsRepos.getQueuedTransports()) {
            for (ItemsDoc itemsDoc : queuedTransportDoc.getDests_Docs()) {
                if (itemsDoc.getItemDoc_num() == itemsDoc_num) {
                    temp = queuedTransportDoc;
                    break;
                }
            }
            if (temp != null){  break;  }
            queuedIndex++;
        }
        if (temp != null){
            this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), queuedIndex);
            this.transportsRepos.insertTransport(temp, true);
            return;
        }

        // so in the regular transports
        temp = this.transportsRepos.getTransports().get(tempItemsDoc.getItemsDocInTransportID());
        if (temp != null){
            this.transportsRepos.deleteTransport(temp.getTran_Doc_ID(), -1);
            this.transportsRepos.insertTransport(temp, false);
        }
        //  removing and adding so that the balance of the good and bad items in the itemsDoc will remain, and that the order of the itemsDocs will also remain.
    }










    public String showTransportsOfDriver(long id, boolean isNotDriver) throws ArrayStoreException {
        if (isNotDriver){ throw new ArrayStoreException("The Driver(ID) to show Transports for was not found"); }
        String res = "All Transports (all statuses) That Driver with id " + id + " is written in:\n";
        for (TransportDoc t : this.transportsRepos.getTransports().values()){
            if (t.getTransportDriverId() == id){
                res += t.toString() + "\n";
            }
        }
        res += "\nQueued Transports of Driver(Values of Drivers/Trucks/Time here are the values that were set when first trying to create the Transport):\n";
        for (TransportDoc t : this.transportsRepos.getQueuedTransports()){
            if (t.getTransportDriverId() == id){
                res += "(Queued Transport) " + t.toString() + "\n";
            }
        }
        res += "\n";
        return res;
    }



    public String showAllQueuedTransports() {
        String resOfQueuedTransports = "Queued Transports (Values of Drivers/Trucks/Time here are the values that were set when first trying to create the Transport):\n";
        resOfQueuedTransports += "(1)-oldest entry ...... (" + this.transportsRepos.getQueuedTransports().size() + ")-latest entry\n";
        int counter = 1;
        for (TransportDoc transportDoc : this.transportsRepos.getQueuedTransports()) {
            resOfQueuedTransports += "" + counter + ")" + transportDoc.toString() + "\n";
            counter++;
        }
        resOfQueuedTransports += "\n";
        return resOfQueuedTransports;
    }


    public String showAllTransports(){
        String resOfAllTransports = "All Transports:\n";
        for (TransportDoc t : this.transportsRepos.getTransports().values()){
            resOfAllTransports += t.toString() + "\n";
        }
        resOfAllTransports += "Queued Transports (Values of Drivers/Trucks/Time here are the values set when first trying to create the Transport):\n";
        for (TransportDoc t : this.transportsRepos.getQueuedTransports()){
            resOfAllTransports += "(Queued Transport) " + t.toString() + "\n";
        }
        resOfAllTransports += "\n";
        return resOfAllTransports;
    }



}
