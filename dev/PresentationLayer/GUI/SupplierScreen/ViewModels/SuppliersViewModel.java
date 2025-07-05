package PresentationLayer.GUI.SupplierScreen.ViewModels;

import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;

/**
 * View model for the Suppliers screen. Provides simple wrappers to service
 * operations.
 */
public class SuppliersViewModel {
   private final SupplierService supplierService;
   private final AgreementService agreementService;
   private final OrderService orderService;

   public SuppliersViewModel(SupplierService supplierService,
         AgreementService agreementService,
         OrderService orderService) {
      this.supplierService = supplierService;
      this.agreementService = agreementService;
      this.orderService = orderService;
   }

   public void handleSuppliers() {
      var res = supplierService.getAllSuppliers();
      if (res.isSuccess()) {
         System.out.println("Suppliers: " + res.getValue());
      } else {
         System.out.println("Error fetching suppliers: " + res.getErrors());
      }
   }

   public void handleProducts() {
      var res = supplierService.getAllProducts();
      if (res.isSuccess()) {
         System.out.println("Products: " + res.getValue());
      } else {
         System.out.println("Error fetching products: " + res.getErrors());
      }
   }

   public void handleAgreements() {
      var res = agreementService.getAgreementsBySupplierId(1);
      if (res.isSuccess()) {
         System.out.println("Agreements: " + res.getValue());
      } else {
         System.out.println("Error fetching agreements: " + res.getErrors());
      }
   }

   public void handleOrders() {
      var res = orderService.getAllOrders();
      if (res.isSuccess()) {
         System.out.println("Orders: " + res.getValue());
      } else {
         System.out.println("Error fetching orders: " + res.getErrors());
      }
   }
}