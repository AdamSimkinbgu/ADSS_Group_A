package PresentationLayer.Controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.Classes.Agreement;
import DomainLayer.Classes.Order;
import DomainLayer.Classes.Supplier;
import DomainLayer.Enums.WeekofDay;
import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.AgreementService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import ServiceLayer.OrderService;
import ServiceLayer.SupplierService;

public class SupplierAggregateController extends AbstractController {
   private final SupplierService supplierService;
   private final AgreementService agreementService;
   private final OrderService orderService;

   private final Map<String, Runnable> supplierOpts = new HashMap<>();
   private final Map<String, Runnable> agreementOpts = new HashMap<>();
   private final Map<String, Runnable> productOpts = new HashMap<>();
   private final Map<String, Runnable> orderOpts = new HashMap<>();

   public SupplierAggregateController(
         View view,
         SupplierService supplierService,
         AgreementService agreementService,
         OrderService orderService) {
      super(view, supplierService);
      this.supplierService = supplierService;
      this.agreementService = agreementService;
      this.orderService = orderService;
      this.implemented = false;
   }

   @Override
   public List<String> showMenu() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'showMenu'");
   }
}
