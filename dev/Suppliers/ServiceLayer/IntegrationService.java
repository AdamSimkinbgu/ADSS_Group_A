package Suppliers.ServiceLayer;

import Inventory.Service.MainService;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.OrderInfoDTO;
import Suppliers.DTOs.OrderPackageDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
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

    public ServiceResponse<?> createRegularOrder(HashMap<Integer, Integer> pOrder) {

        return orderService.createOrder(new OrderInfoDTO(LocalDate.now(), pOrder));
    }

    public ServiceResponse<?> createPeriodicOrder(HashMap<Integer, Integer> pOrder, DayOfWeek day) {
        if (pOrder == null || pOrder.isEmpty()) {
            return ServiceResponse.fail(List.of("Periodic order cannot be empty"));
        }
        if (day == null) {
            return ServiceResponse.fail(List.of("Day of the week cannot be null"));
        }
        ServiceResponse<?> response = orderService.createPeriodicOrder(new PeriodicOrderDTO(day, pOrder));
        if (response.isSuccess() && response.getValue() != null) {
            return ServiceResponse.ok(response.getValue());
        } else {
            return ServiceResponse.fail(response.getErrors());
        }
    }

    public ServiceResponse<?> createShortageOrder(Map<Integer, Integer> pOrder) {
        return orderService.createOrderByShortage(new OrderInfoDTO(LocalDate.now(), new HashMap<>(pOrder)));
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
        ServiceResponse<?> response = orderService.completeOrder(orderId);
        if (response.isSuccess()) {
            return ServiceResponse.ok("Order completed successfully");
        } else {
            return ServiceResponse.fail(response.getErrors());
        }
    }

    public ServiceResponse<?> deliverOrder(OrderPackageDTO order) {
        String msg = mainService.DeliverOrder(order);
        if (msg.equals("done")) {
            return ServiceResponse.ok("Order delivered successfully");
        } else {
            return ServiceResponse.fail(List.of(msg));
        }
    }

}
