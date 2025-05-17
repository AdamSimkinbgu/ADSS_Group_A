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
import DomainLayer.Classes.Supplier;
import DomainLayer.Enums.WeekofDay;
import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.SupplierService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class SupplierAndAgreementController extends AbstractController {
   protected Map<String, Runnable> supplierOptions = new HashMap<>();
   protected Map<String, Runnable> agreementOptions = new HashMap<>();
   protected Map<String, Runnable> productOptions = new HashMap<>();

   public SupplierAndAgreementController(View view, SupplierService supplierService) {
      super(view, supplierService);
      this.implemented = false;
   }

   @Override
   public List<String> showMenu() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'showMenu'");
   }

}
