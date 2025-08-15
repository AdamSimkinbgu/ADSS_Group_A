package ServiceLayer.TransportServices;

import DomainLayer.TransportDomain.TruckSubModule.TruckFacade;
import ServiceLayer.EmployeeIntegrationService;
import ServiceLayer.exception.AuthorizationException;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.sql.SQLException;

public class TruckService {
    private EmployeeIntegrationService employeeIntegrationService;
    private TruckFacade tru_f;

    public TruckService(TruckFacade ttff, EmployeeIntegrationService eis) {
        this.tru_f = ttff;
        this.employeeIntegrationService = eis;
    }

    public String loadDBData() throws SQLException {
        try {
            this.tru_f.loadDBData();
        } catch (SQLException e) {
            return "SQL Error";
        } catch (Exception e) {
            return "Error";
        }
        return "Success";
    }


    public String addTruck(long loggedID, int num, String model, double net_wei, double max_carry, String license){
        try {
            if (num < 0 || net_wei < 0 || max_carry < 0){ return "The Truck's Number/Net.Weight/MaxCarryWeight you enter cannot be negative"; }
            if (model.isEmpty() || model.isBlank() || license.isEmpty() || license.isBlank()){ return "The Truck's Model/license you enter cannot be empty"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "ADD_TRUCK")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (!(license.equals("A") || license.equals("B") || license.equals("C") || license.equals("D") || license.equals("E"))){
                return "The Truck's license needs to be either A, B, C, or D or E";
            }
            this.tru_f.addTruck(num, model, net_wei, max_carry, license);
        } catch (KeyAlreadyExistsException e) {
            return "Truck Number already exists in the Warehouse";
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



    public String removeTruck(long loggedID, int num){
        try {
            if (num < 0){ return "The Truck Number you enter cannot be negative"; }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "DELETE_TRUCK")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            this.tru_f.removeTruck(num);
        } catch (ClassNotFoundException e) {
            return "Truck Number doesn't exist in the Warehouse";
        } catch (ArrayStoreException e) {
            return "Truck cannot be deleted because it is written in an Active Transport, if you want you can remove it from its transports or remove the Transports";
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




    public String showTrucks(long loggedID){
        String res = "";
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "SHOW_TRUCKS")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }

            res = tru_f.showAllTrucks();
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }









    public TruckFacade getTru_f() {return tru_f;}


}
