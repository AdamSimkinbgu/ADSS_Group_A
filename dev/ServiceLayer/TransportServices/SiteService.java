package ServiceLayer.TransportServices;

import DomainLayer.TransportDomain.SiteSubModule.SiteFacade;
import ServiceLayer.EmployeeIntegrationService;
import ServiceLayer.exception.AuthorizationException;

import javax.management.AttributeNotFoundException;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.naming.ContextNotEmptyException;
import java.sql.SQLException;

public class SiteService {
    private EmployeeIntegrationService employeeIntegrationService;
    private SiteFacade sf;

    public SiteService(SiteFacade ssff, EmployeeIntegrationService eis) {
        this.sf = ssff;
        this.employeeIntegrationService = eis;
    }

    public String loadDBData() throws SQLException {
        try {
            sf.loadDBData();
        } catch (SQLException e) {
            return "SQL Error";
        } catch (Exception e) {
            return "Error";
        }
        return "Success";
    }


    public String addShippingArea(long loggedID, int areaNum, String areaName){
        try {
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "ADD_SHIPPING_AREA")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't add Shipping Areas"; }
            if (!this.employeeIntegrationService.hasRole(loggedID, "Admin") && !this.employeeIntegrationService.hasRole(loggedID, "Transport manager")){
                return "You don't have the required role to add Shipping Areas, you can only add Shipping Areas if you are an Admin or a Transport Manager";
            }
            if(areaName.isEmpty() || areaName.isBlank()){
                return "It seems some values you've entered are Empty or Blank, Please Insert proper values";
            }
            sf.addShippingArea(areaNum, areaName);
        }catch (KeyAlreadyExistsException e){
            return "Shipping Area Number already exists";
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

    public String deleteShippingArea(long loggedID, int areaNum){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "DELETE_SHIPPING_AREA")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            sf.deleteShippingArea(areaNum);
        } catch (AttributeNotFoundException e){
            return "Can't delete a Shipping Area that Doesn't exist.";
        } catch (ContextNotEmptyException e){
            return "Can't Delete a shipping area that has Sites in it.";
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



    public String setShippingAreaNum(long loggedID, int OldareaNum, int NewAreaNum){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_SHIPPING_AREA")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if(OldareaNum == NewAreaNum){
                return "The Edition process Finished because you set the same Area Number value as the value that is already there";
            }
            sf.setShippingAreaNum(OldareaNum, NewAreaNum);
        } catch (KeyAlreadyExistsException e){
            return "New Shipping Area Number already exists";
        } catch (ClassNotFoundException e){
            return "Old Shipping Area Number does not exist";
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


    public String setShippingAreaName(long loggedID, int areaNum, String NewareaName){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_SHIPPING_AREA")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if(NewareaName.isEmpty() || NewareaName.isBlank()){
                return "It seems some values you've entered are Empty or Blank, Please Insert proper values";
            }
            sf.setShippingAreaName(areaNum, NewareaName);
        } catch (ClassNotFoundException e){
            return "Can't set name of a non existent Area number";
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
















    public String addSite(long loggedID, int areaNum, String address, String cont_name, long Cont_Num){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "ADD_SITE")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (address.isEmpty() || cont_name.isEmpty() || address.isBlank() || cont_name.isBlank() || Cont_Num == 0) {
                return "It seems some values you've entered are Empty or Blank, Please Insert proper values";
            }
            sf.addSiteTOArea(areaNum, address, cont_name, Cont_Num);
        } catch (ClassNotFoundException e){
            return "Can't add a a site to a non existent Area number";
        } catch (KeyAlreadyExistsException e){
            return "Site Address already exists in that area number";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";
    }

    public String deleteSite(long loggedID, int areaNum, String address){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "DELETE_SITE")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (address.isEmpty() || address.isBlank()) {
                return "It seems some values you've entered are Empty or Blank, Please Insert proper values";
            }
            sf.deleteSiteFromArea(areaNum, address);
        } catch (ClassNotFoundException e){
            return "Can't Delete a site with a non existent Area number or a non existent address string";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";
    }


    public boolean doesSiteExist(long loggedID, Integer currSiteAreaNum, String currDestinationAddress) {
        return this.sf.doesSiteExist(currSiteAreaNum, currDestinationAddress);
    }


    public String setSiteAddress(long loggedID, int areaNum, String Oldaddress, String NewAddress){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_SITE")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (NewAddress.isEmpty() || Oldaddress.isEmpty() || NewAddress.isBlank() || Oldaddress.isBlank()) {
                return "It seems some values you've entered are Empty or Blank, Please Insert proper values";
            } else if (Oldaddress.equals(NewAddress)){
                return "The Edition process Finished because you set the same Address String value as the value that is already there";
            }
            sf.setSiteAddress(areaNum, Oldaddress, NewAddress);
        } catch (ClassNotFoundException e){
            return "Can't Edit a site with a non existent Area number or a non existent address string";
        } catch (KeyAlreadyExistsException e){
            return "Site Address String already exists in that area number";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";
    }

    public String setSiteAreaNum(long loggedID, int OldareaNum, int NewAreaNum, String address){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_SITE")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (address.isEmpty() || address.isBlank()) {
                return "It seems some values you've entered are Empty or Blank, Please Insert proper values";
            }else if (OldareaNum == NewAreaNum) {
                return "The Edition process Finished because you set the same Area Number value as the value that is already there";
            }
            sf.setSiteAreaNum(OldareaNum, NewAreaNum, address);
        } catch (ClassNotFoundException e){
            return "Can't Edit a site with a non existent Area number(as the old one or the new one) or a non existent address string";
        } catch (KeyAlreadyExistsException e){
            return "The Address String of the Site you are trying to move already exists in the destination area number";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";
    }

    public String setSiteContName(long loggedID, int areaNum, String address, String contName){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_SITE")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (address.isEmpty() || contName.isEmpty() || address.isBlank() || contName.isBlank()) {
                return "It seems some values you've entered are Empty or Blank, Please Insert proper values";
            }
            sf.setSiteContName(areaNum, address, contName);
        } catch (ClassNotFoundException e){
            return "Can't Edit a site with a non existent Area number or a non existent address string";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";
    }

    public String setSiteContNum(long loggedID, int areaNum, String address, long contNum){
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "EDIT_SITE")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            if (address.isEmpty() || address.isBlank()) {
                return "It seems some values you've entered are Empty or Blank, Please Insert proper values";
            }
            sf.setSiteContNum(areaNum, address, contNum);
        } catch (ClassNotFoundException e){
            return "Can't Edit a site with a non existent Area number or a non existent address string";
        } catch (SQLException e) {
            return "SQL Error";
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return "Success";
    }















    public String showAllSites(long loggedID){
        String res = "";
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "SHOW_SITES")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            res = sf.showAllSites();
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();  // or do nothing in this line
        }
        return res;
    }

    public String showAllShippingAreas(long loggedID){
        String res = "";
        try {
            if (!this.employeeIntegrationService.isActive(loggedID)){ return "You are not an active employee, you can't make this action !"; }
            if (!this.employeeIntegrationService.isEmployeeAuthorised(loggedID, "SHOW_SHIPPING_AREAS")){
                return "You are not authorized to make this action !\nPlease contact the System Admin regarding your permissions.\n";
            }
            res = sf.showAllShippingAreas();
        } catch (AuthorizationException e) {
            return "You are not authorized to make this action !";
        } catch (Exception e) {
            e.printStackTrace();  // or do nothing in this line
        }
        return res;
    }


}
