package Suppliers.ServiceLayer;

import Inventory.Service.MainService;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

import java.util.List;
import java.util.Map;

public class IntegrationService {
    private MainService mainService;

    private static IntegrationService integrationServiceInstance;

    public static IntegrationService getIntegrationServiceInstance(){
        if(integrationServiceInstance == null)integrationServiceInstance = new IntegrationService();
        return integrationServiceInstance;
    }

    private IntegrationService(){}

    public boolean setMainService(){
        if(mainService != null)return false;
        mainService = MainService.GetInstance();
        return true;
    }


    public List<CatalogProductDTO> getCatalog(){
        return null;
    }

    public ServiceResponse<?> createRegularOrder(Map<Integer,Integer> pOrder){
        return null;
    }

    public ServiceResponse<?> createPeriodicOrder(Map<Integer,Integer> pOrder, int day){
        return null;
    }

    public ServiceResponse<?> createShortageOrder(Map<Integer,Integer> pOrder){
        return null;
    }

    public ServiceResponse<?> viewPeriodicOrders(){
        return null;
    }

    public ServiceResponse<?> requestDeletePeriodicOrder(int poId){
        return null;
    }

}
