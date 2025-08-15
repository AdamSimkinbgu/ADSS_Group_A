package DomainLayer.TransportDomain.TruckSubModule;

import DomainLayer.enums.enumDriLicense;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.sql.Connection;
import java.sql.SQLException;

public class TruckFacade {
    private TruckRepo truckRepo;

    public TruckFacade() throws SQLException {  truckRepo = new TruckRepoImpl();  }

    public TruckFacade(Connection connection) throws SQLException {  truckRepo = new TruckRepoImpl(connection);  }

    public void loadDBData() throws SQLException {  truckRepo.loadDBData();  }

    public void addTruck(int num, String model, double net_wei, double max_carry, String license) throws KeyAlreadyExistsException, SQLException {
        if (this.truckRepo.getTrucksWareHouse().containsKey(num)){ throw new KeyAlreadyExistsException("Truck Number already exists in the Warehouse"); }
        enumDriLicense val_license = null;
        if (license.equals("A")){
            val_license = enumDriLicense.A;
        } else if (license.equals("B")){
            val_license = enumDriLicense.B;
        } else if (license.equals("C")){
            val_license = enumDriLicense.C;
        } else if (license.equals("D")){
            val_license = enumDriLicense.D;
        } else if (license.equals("E")){
            val_license = enumDriLicense.E;
        }
        truckRepo.insertTruck(new Truck(num, model, net_wei, max_carry, val_license));
    }


    public void removeTruck(int num) throws ClassNotFoundException, ArrayStoreException, SQLException {
        if (!this.truckRepo.getTrucksWareHouse().containsKey(num)){
            throw new ClassNotFoundException("Truck Number doesn't exist in the Warehouse");
        } else if (this.truckRepo.getTrucksWareHouse().get(num).getInTransportID() != -1) {   //  if Truck is busy in an active Transport
            throw new ArrayStoreException("Truck cannot be deleted because it is written in an Active Transport, if you want you can remove it from its transports or remove the Transports");
        }
        this.truckRepo.getTrucksWareHouse().get(num).setIsDeleted(true);
        truckRepo.deleteTruck(num);
    }


    public String showAllTrucks(){
        String res = "Trucks Warehouse:\n";
        for (Truck truck : truckRepo.getTrucksWareHouse().values()) {
            res += truck.toString() + "\n";
        }
        return res;
    }

    public TruckRepo getTruckRepo() {return truckRepo;}

}
