package DomainLayer.TransportDomain.TransportSubModule;

import DTOs.TransportModuleDTOs.*;
import DataAccessLayer.TransportDAL.Interfaces.TransportDAO;
import DataAccessLayer.TransportDAL.JdbcTransportDAO;
import DomainLayer.TransportDomain.SiteSubModule.Site;
import DomainLayer.TransportDomain.SiteSubModule.SiteFacade;
import DomainLayer.TransportDomain.TruckSubModule.Truck;
import DomainLayer.TransportDomain.TruckSubModule.TruckFacade;
import DomainLayer.enums.enumTranProblem;
import Util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TransportsRepoImpl implements TransportsRepo {
    private TransportDAO transportDAO;

    private SiteFacade siteFacade;
    private TruckFacade truckFacade;

    private HashMap<Integer, TransportDoc> transports;
    private ArrayList<TransportDoc> queuedTransports;
    private int transportIDCounter;   ///  for transports hashmap/repository/collection
    private HashMap<Integer, ItemsDoc> itemsDocs; // to know an ItemsDoc's num is unique like required, and to address an ItemsDoc specifically.
    private HashMap<Long, Integer> driverIdToInTransportID;  //  employee is added to here if he is an active driver in an active Transport.

    public TransportsRepoImpl(SiteFacade sf, TruckFacade tf) throws SQLException {
        this.transportDAO = new JdbcTransportDAO(Database.getConnection());
        this.siteFacade = sf;
        this.truckFacade = tf;

        this.transportIDCounter = 0;
        this.transports = new HashMap<Integer, TransportDoc>();
        this.itemsDocs = new HashMap<Integer, ItemsDoc>();
        this.queuedTransports = new ArrayList<TransportDoc>();
        this.driverIdToInTransportID = new HashMap<>();
    }

    public TransportsRepoImpl(SiteFacade sf, TruckFacade tf, Connection connection) throws SQLException {
        this.transportDAO = new JdbcTransportDAO(connection);
        this.siteFacade = sf;
        this.truckFacade = tf;

        this.transportIDCounter = 0;
        this.transports = new HashMap<Integer, TransportDoc>();
        this.itemsDocs = new HashMap<Integer, ItemsDoc>();
        this.queuedTransports = new ArrayList<TransportDoc>();
        this.driverIdToInTransportID = new HashMap<>();
    }

    public HashMap<Integer, TransportDoc> getTransports() {return transports;}
    public void setTransports(HashMap<Integer, TransportDoc> transports) {this.transports = transports;}
    public int getTransportIDCounter() {return transportIDCounter;}
    public void setTransportIDCounter(int transportIDCounter) throws SQLException {
        this.transportIDCounter = transportIDCounter;
        this.transportDAO.updateCounter("transportIDCounter", transportIDCounter);
    }
    public HashMap<Integer, ItemsDoc> getItemsDocs() {return itemsDocs;}
    public void setItemsDocs(HashMap<Integer, ItemsDoc> itemsDocs) {this.itemsDocs = itemsDocs;}
    public ArrayList<TransportDoc> getQueuedTransports() {return queuedTransports;}
    public void setQueuedTransports(ArrayList<TransportDoc> queuedTransports) {this.queuedTransports = queuedTransports;}
    public HashMap<Long, Integer> getDriverIdToInTransportID() {return driverIdToInTransportID;}
    public void setDriverIdToInTransportID(HashMap<Long, Integer> driverIdToInTransportID) {this.driverIdToInTransportID = driverIdToInTransportID;}


    @Override
    public boolean loadDBData() throws SQLException {
        /// loading transports collection data
        for (TransportDTO tranDTO : this.transportDAO.getAllTransports(false)){
            this.transports.put(tranDTO.getTransport_ID(), convertTransportDTOToTransportDoc(tranDTO));

            for (ItemsDoc itemsDoc : this.transports.get(tranDTO.getTransport_ID()).getDests_Docs()){   // loading to itemsDocs
                this.itemsDocs.put(itemsDoc.getItemDoc_num(), itemsDoc);
            }
        }

        /// loading queuedTransports collection data
        for (TransportDTO queuedTranDTO : this.transportDAO.getAllTransports(true)){
            this.queuedTransports.add(convertTransportDTOToTransportDoc(queuedTranDTO));
        }
        for (TransportDoc tranDoc : this.queuedTransports){   // loading to itemsDocs
            for (ItemsDoc itemsDoc : tranDoc.getDests_Docs()){   // loading to itemsDocs
                this.itemsDocs.put(itemsDoc.getItemDoc_num(), itemsDoc);
            }
        }

        /// loading transportIDCounter value
        this.transportIDCounter = this.transportDAO.getCounterValue("transportIDCounter");

        /// loading itemsDocs collection data
        // already done above in this function when loading the transports and queuedTransports

        /// loading driverIdToInTransportID collection data
        for (ArrayList<Long> dIDTITIDDuo : this.transportDAO.getAllDriverIdToInTransportIDsDuos()){
            this.driverIdToInTransportID.put(dIDTITIDDuo.get(0), Math.toIntExact(dIDTITIDDuo.get(1)));
        }

        return true;
    }



    ///   queuedTransports collection related --> not needed because it seems that these handle scenarios are already covered in the transports related with the isQueued boolean there.
    ///   transport collection related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------

    @Override
    public boolean insertTransport(TransportDoc transport, boolean isQueued) throws SQLException {
        //  we can also insert each of the itemsDocs of it here but I used the insertItemsDocs function with false arg before and then this.
        if (isQueued){
            this.queuedTransports.add(transport);
        } else {
            this.transports.put(transport.getTran_Doc_ID(), transport);
        }
        TransportDTO transportDTOForInsertion = convertTransportDocToTransportDTO(transport);
        return transportDAO.insertTransport(transportDTOForInsertion, isQueued);
    }



    @Override
    public boolean deleteTransport(int transportId, int queuedIndexIfWasQueued) throws SQLException {
        if (queuedIndexIfWasQueued == -1) {   // delete regular transport in transports
            transports.remove(transportId);  // remove from BL collection
        } else {   // delete queued transport
            this.queuedTransports.remove(queuedIndexIfWasQueued - 1);  // remove from BL collection
        }
        return this.transportDAO.deleteTransportById(transportId);   // persist deletion
    }




    @Override
    public boolean updateTransport(TransportDoc transport) throws SQLException {
        return this.transportDAO.updateTransport(transport.getTran_Doc_ID(), convertTransportDocToTransportDTO(transport));
    }






    public boolean insertPersistTransportProblem(int transportID, enumTranProblem problem) throws SQLException {
        return this.transportDAO.insertTransportProblem(transportID, problem);
    }
    public boolean removePersistTransportProblem(int transportID, enumTranProblem problem) throws SQLException{
        return this.transportDAO.removeTransportProblem(transportID, problem);
    }









    ///   itemsDocs collection related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------
    @Override
    public boolean insertItemsDoc(ItemsDoc itemsDoc, boolean alsoInsertInDBNow) throws SQLException {
        this.itemsDocs.put(itemsDoc.getItemDoc_num(), itemsDoc);  //  insert to BL collection here
        if (alsoInsertInDBNow) {
            ItemsDocDTO itemsDocDTOForInsertion = convertItemsDocToItemsDocDTO(itemsDoc);
            return this.transportDAO.insertItemsDoc(itemsDocDTOForInsertion);  // persist insertion to the DB
        }
        return true;  // if only in BL collection
    }

    @Override
    public boolean removeItemsDoc(int itemsDocId, boolean alsoRemoveInDBNow) throws SQLException {
        this.itemsDocs.remove(itemsDocId);
        if (alsoRemoveInDBNow){  return this.transportDAO.deleteItemsDocById(itemsDocId);  }
        return true;
    }

    @Override
    public boolean updateItemsDocPersistency(int oldItemsDocId, ItemsDoc updatedItemsDoc) throws SQLException {
        return this.transportDAO.updateItemsDoc(oldItemsDocId, convertItemsDocToItemsDocDTO(updatedItemsDoc));
    }










    ///   driverIdToInTransportID collection related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------
    @Override
    public boolean insertDriverIdToInTransportID(long driverId, int tranDocId) throws SQLException {
        this.driverIdToInTransportID.put(driverId, tranDocId);
        return transportDAO.insertToDriverIdToInTransportID(driverId, tranDocId);
    }

    @Override
    public boolean removeFromDriverIdToInTransportID(long transportDriverId) throws SQLException {
        this.driverIdToInTransportID.remove(transportDriverId);
        return this.transportDAO.deleteFromDriverIdToInTransportID(transportDriverId);
    }






    ///   transportIDCounter counter related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------

    @Override
    public void incrementTransportIDCounter() throws SQLException {
        this.transportIDCounter++;
        this.transportDAO.updateCounter("transportIDCounter", transportIDCounter);
    }














    ///   object <--> objectDTO conversion functions related               <<<<---------------------     <<<----------------------------------    <<<---------------------------------

    @Override
    public TransportDTO convertTransportDocToTransportDTO(TransportDoc transportDoc) {
        ArrayList<ItemsDocDTO> listOfItemsDocDTOs = new ArrayList<ItemsDocDTO>();
        Site srcSite = transportDoc.getSrc_site();
        SiteDTO srcSiteDTO = new SiteDTO(srcSite.getAddress().getArea(), srcSite.getAddress().getAddress());

        for (ItemsDoc itemsDoc : transportDoc.getDests_Docs()){
            Site destSite = itemsDoc.getDest_site();
            SiteDTO destSiteDTO = new SiteDTO(destSite.getAddress().getArea(), destSite.getAddress().getAddress());

            ArrayList<ItemQuantityDTO> itemQuantityDTOS = new ArrayList<>();

            for (Item item : itemsDoc.getBadItems().keySet()){
                ItemDTO itemDTO = new ItemDTO(item.getName(), item.getWeight(), item.getCondition());
                itemQuantityDTOS.add(new ItemQuantityDTO(itemsDoc.getItemDoc_num(), itemDTO, itemsDoc.getBadItems().get(item)));
            }
            for (Item item : itemsDoc.getGoodItems().keySet()){
                ItemDTO itemDTO = new ItemDTO(item.getName(), item.getWeight(), item.getCondition());
                itemQuantityDTOS.add(new ItemQuantityDTO(itemsDoc.getItemDoc_num(), itemDTO, itemsDoc.getGoodItems().get(item)));
            }
            listOfItemsDocDTOs.add(new ItemsDocDTO(itemsDoc.getItemDoc_num(), srcSiteDTO, destSiteDTO, itemQuantityDTOS, itemsDoc.getEstimatedArrivalTime(), transportDoc.getTran_Doc_ID()));
        }

        TransportDTO transportDTO = new TransportDTO(transportDoc.getTran_Doc_ID(), transportDoc.getTransportTruck().getTruck_num(), transportDoc.getTransportDriverId(), srcSiteDTO, listOfItemsDocDTOs, transportDoc.getDeparture_dt(), transportDoc.getStatus(), transportDoc.getTruck_Depart_Weight(), transportDoc.getProblems());
        return transportDTO;
    }



    @Override
    public TransportDoc convertTransportDTOToTransportDoc(TransportDTO transport_DTO){
        long driverID = transport_DTO.getTransportDriverID();
        Truck truck = this.truckFacade.getTruckRepo().getTrucksWareHouse().get(transport_DTO.getTransportTruckNum());
        Site srcSite = this.siteFacade.getSiteRepo().getShippingAreas().get(transport_DTO.getSrc_site().getSiteAreaNum()).getSites().get(transport_DTO.getSrc_site().getSiteAddressString());

        TransportDoc tempTransport = new TransportDoc(transport_DTO.getStatus(), transport_DTO.getTransport_ID(), truck, driverID, srcSite, transport_DTO.getDeparture_dt(), transport_DTO.getTruck_Depart_Weight(), transport_DTO.getProblems());

        for (ItemsDocDTO itemsDocDTO : transport_DTO.getDests_Docs()){
            Site destSiteTemp = this.siteFacade.getSiteRepo().getShippingAreas().get(itemsDocDTO.getDest_siteDTO().getSiteAreaNum()).getSites().get(itemsDocDTO.getDest_siteDTO().getSiteAddressString());
            tempTransport.addDestSite(itemsDocDTO.getItemsDoc_num(), destSiteTemp);
            // calculates the arrival times of the itemsDocs accordingly

            for (ItemQuantityDTO itemQuantityDTO : itemsDocDTO.getItemQuantityDTOs()){
                tempTransport.addItem(itemsDocDTO.getItemsDoc_num(), itemQuantityDTO.getItem().getName(), itemQuantityDTO.getItem().getWeight(), itemQuantityDTO.getQuantity(), itemQuantityDTO.getItem().getCondition());
            }
        }    ///  adding every site and every item for each site

        return tempTransport;
    }






    private ItemsDocDTO convertItemsDocToItemsDocDTO(ItemsDoc itemsDoc) {
        Site srcSite = itemsDoc.getSrc_site();
        SiteDTO srcSiteDTO = new SiteDTO(srcSite.getAddress().getArea(), srcSite.getAddress().getAddress());

        Site destSite = itemsDoc.getDest_site();
        SiteDTO destSiteDTO = new SiteDTO(destSite.getAddress().getArea(), destSite.getAddress().getAddress());

        ArrayList<ItemQuantityDTO> itemQuantityDTOS = new ArrayList<>();
        for (Item item : itemsDoc.getBadItems().keySet()){
            ItemDTO itemDTO = new ItemDTO(item.getName(), item.getWeight(), item.getCondition());
            itemQuantityDTOS.add(new ItemQuantityDTO(itemsDoc.getItemDoc_num(), itemDTO, itemsDoc.getBadItems().get(item)));
        }
        for (Item item : itemsDoc.getGoodItems().keySet()){
            ItemDTO itemDTO = new ItemDTO(item.getName(), item.getWeight(), item.getCondition());
            itemQuantityDTOS.add(new ItemQuantityDTO(itemsDoc.getItemDoc_num(), itemDTO, itemsDoc.getGoodItems().get(item)));
        }

        ItemsDocDTO resDTO = new ItemsDocDTO(itemsDoc.getItemDoc_num(), srcSiteDTO, destSiteDTO, itemQuantityDTOS, itemsDoc.getEstimatedArrivalTime(), itemsDoc.getItemsDocInTransportID());
        return resDTO;
    }






//    private ItemsDoc convertItemsDocDTOToItemsDoc(ItemsDocDTO itemsDocDto) {
//        // if needed
//    }





}
