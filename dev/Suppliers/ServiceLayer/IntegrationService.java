package Suppliers.ServiceLayer;

import Inventory.Service.MainService;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

import java.util.List;
import java.util.Map;

public class IntegrationService {
    private MainService mainService;

    private static IntegrationService integrationServiceInstance;
    private final SupplierService suppliersService;
    private final OrderService orderService;

    public static void setIntegrationServiceInstance(SupplierService suppliersService, OrderService orderService) {
        if (integrationServiceInstance == null)
            integrationServiceInstance = new IntegrationService(suppliersService, orderService);
    }

    public static IntegrationService getIntegrationServiceInstance() {
        return integrationServiceInstance;
    }

    private IntegrationService(SupplierService suppliersService, OrderService orderService) {
        this.suppliersService = suppliersService;
        this.orderService = orderService;
        this.mainService = null; // Main service is not set up by default
    }

    public boolean setMainService() {
        if (mainService != null)
            return false;
        mainService = MainService.GetInstance();
        return true;
    }

    public ServiceResponse<?> getCatalog() {
        // we get the catalog from the suppliers service
        ServiceResponse<List<CatalogProductDTO>> catalogResponse = suppliersService.getAllProducts();
        if (catalogResponse.isSuccess() && catalogResponse.getValue() != null) {
            return ServiceResponse.ok(catalogResponse.getValue());
        } else {
            return ServiceResponse.fail(catalogResponse.getErrors());
        }
    }

    public ServiceResponse<?> createRegularOrder(Map<Integer, Integer> pOrder) {
        return ServiceResponse.fail(List.of("Integration service not set up"));
    }

    public ServiceResponse<?> createPeriodicOrder(Map<Integer, Integer> pOrder, int day) {
        return ServiceResponse.fail(List.of("Integration service not set up"));
    }

    public ServiceResponse<?> createShortageOrder(Map<Integer, Integer> pOrder) {
        return ServiceResponse.fail(List.of("Integration service not set up"));
    }

    public ServiceResponse<?> viewPeriodicOrders() {
        ServiceResponse<?> response = orderService.getAllPeriodicOrders();
        if (response.isSuccess() && response.getValue() != null) {
            return ServiceResponse.ok(response.getValue());
        } else {
            return ServiceResponse.fail(response.getErrors());
        }
    }

    public ServiceResponse<?> requestDeletePeriodicOrder(int poId) {
        ServiceResponse<?> response = orderService.removePeriodicOrder(poId);
        if (response.isSuccess()) {
            return ServiceResponse.ok("Periodic order deleted successfully");
        } else {
            return ServiceResponse.fail(response.getErrors());
        }
    }

    public ServiceResponse<?> completeOrder(int orderId) {
        return ServiceResponse.fail(List.of("Integration service not set up"));
    }

}
