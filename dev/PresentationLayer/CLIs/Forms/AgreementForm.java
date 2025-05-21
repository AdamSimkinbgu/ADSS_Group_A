package PresentationLayer.CLIs.Forms;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import DTOs.AgreementDTO;
import DTOs.BillofQuantitiesItemDTO;
import PresentationLayer.View;
import PresentationLayer.CLIs.InteractiveForm;

public class AgreementForm extends InteractiveForm<AgreementDTO> {

   public AgreementForm(View view) {
      super(view);
   }

   @Override
   protected AgreementDTO build() throws Cancelled {
      view.showMessage("Creating a new Agreement... (enter 'cancel' to cancel)");
      view.showMessage("Please fill in the following details:");
      LocalDate startDate;
      while (true) {
         String agreementStartDate = askNonEmpty("Enter the agreement start date (DD-MM-YYYY):");
         if (agreementStartDate == null || agreementStartDate.isBlank()) {
            view.showError("Agreement start date must not be blank");
            return null;
         }
         try {
            // try to turn it from (DD-MM-YYYY) string to LocalDate
            String[] parts = agreementStartDate.split("-");
            if (parts.length != 3) {
               throw new IllegalArgumentException("Invalid date format");
            }
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            if (day < 1 || day > 31 || month < 1 || month > 12) {
               throw new IllegalArgumentException("Invalid date");
            }
            startDate = LocalDate.of(year, month, day);
            // need to move to validator
            if (startDate.isBefore(LocalDate.now())) {
               view.showError("Start date must be now or in the future");
               continue;
            }
            break;
         } catch (Exception e) {
            view.showError("Invalid date format. Please use DD-MM-YYYY.");
            continue;
         }
      }
      LocalDate endDate;
      while (true) {
         String agreementEndDate = askNonEmpty("Enter the agreement end date (DD-MM-YYYY):");
         if (agreementEndDate == null || agreementEndDate.isBlank()) {
            view.showError("Agreement end date must not be blank");
            return null;
         }
         try {
            // try to turn it from (DD-MM-YYYY) string to LocalDate
            String[] parts = agreementEndDate.split("-");
            if (parts.length != 3) {
               throw new IllegalArgumentException("Invalid date format");
            }
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            if (day < 1 || day > 31 || month < 1 || month > 12) {
               throw new IllegalArgumentException("Invalid date");
            }
            // need to move to validator
            endDate = LocalDate.of(year, month, day);
            if (endDate.isBefore(LocalDate.now())) {
               view.showError("End date must be now or in the future");
               continue;
            }
            if (endDate.isBefore(startDate)) {
               view.showError("End date must be after start date");
               continue;
            }
            break;
         } catch (Exception e) {
            view.showError("Invalid date format. Please use DD-MM-YYYY.");
            continue;
         }
      }
      List<BillofQuantitiesItemDTO> billOfQuantitiesItems = new ArrayList<>();
      while (true) {
         String addMore = askNonEmpty("Do you want to add a Bill of Quantities item? (y/n):").toLowerCase();
         if (addMore.contains("n") || addMore.contains("no") || addMore.contains("false") || addMore.contains("f")) {
            break;
         }
         String itemID = view.readLine("Enter the item ID:");
         if (itemID == null || itemID.isBlank()) {
            view.showError("Item ID must not be blank");
            return null;
         }
         int itemIDInt;
         try {
            itemIDInt = Integer.parseInt(itemID);
         } catch (NumberFormatException e) {
            view.showError("Item ID must be a number");
            return null;
         }
         String itemQuantityString = view.readLine("Enter the item quantity:");
         if (itemQuantityString == null || itemQuantityString.isBlank()) {
            view.showError("Item quantity must not be blank");
            return null;
         }
         int itemQuantity;
         try {
            itemQuantity = Integer.parseInt(itemQuantityString);
         } catch (NumberFormatException e) {
            view.showError("Item quantity must be a number");
            return null;
         }
         String itemDiscountString = view.readLine("Enter the item discount percentage:");
         if (itemDiscountString == null || itemDiscountString.isBlank()) {
            view.showError("Item discount percentage must not be blank");
            return null;
         }
         BigDecimal itemDiscount;
         try {
            itemDiscount = new BigDecimal(itemDiscountString);
         } catch (NumberFormatException e) {
            view.showError("Item discount percentage must be a number");
            return null;
         }
         billOfQuantitiesItems.add(new BillofQuantitiesItemDTO(
               -1, // lineInBillID will be set by the service
               "", // itemName will be set by the service
               itemIDInt,
               itemQuantity,
               itemDiscount));
      }

      AgreementDTO agreementDTO = new AgreementDTO(
            -1, // supplierId will be set by the service
            "",
            startDate,
            endDate,
            false,
            billOfQuantitiesItems);
      return agreementDTO;
   }

   @Override
   protected AgreementDTO update(AgreementDTO dto) throws Cancelled {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'update'");
   }

}
