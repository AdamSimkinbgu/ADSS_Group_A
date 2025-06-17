package Suppliers.PresentationLayer.Forms;

import Suppliers.DTOs.*;
import Suppliers.PresentationLayer.InteractiveForm;
import Suppliers.PresentationLayer.View;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public final class SupplierForm extends InteractiveForm<SupplierDTO> {

      public SupplierForm(View view) {
            super(view);
      }

      @Override
      protected SupplierDTO build() throws Cancelled {
            view.showMessage("Creating new supplier... (enter 'cancel' to cancel)");
            view.showMessage("Please fill in the following details:");
            String name = askNonEmpty("Name:");
            String taxNumber = askNonEmpty("Tax number:");

            /* address */
            view.showMessage("-- Address --");
            AddressDTO address = new AddressDTO(
                        askNonEmpty("Street: "),
                        askNonEmpty("City: "),
                        askNonEmpty("Building no.: "));

            /* self supply */
            boolean selfSupply = askBoolean("Self supply? (y/n): ");
            EnumSet<DayOfWeek> supplyDays = EnumSet.noneOf(DayOfWeek.class);
            if (selfSupply) {
                  view.showMessage("Self supply selected.");
                  supplyDays = askDaysOfWeek(
                              "Supply days (By words, e.g. MONDAY, TUESDAY, ... or numbers, e.g. 1 - MONDAY, 2 - TUESDAY, ..., 7 - SUNDAY): ");
            } else {
                  view.showMessage("Self supply not selected.");
            }

            int leadSupplyDays;
            while (true) {
                  try {
                        leadSupplyDays = askInt("Lead supply days (number of days before supply): ");
                        if (leadSupplyDays < 0) {
                              view.showError("Lead supply days cannot be negative. Please try again.");
                        } else {
                              break;
                        }
                  } catch (NumberFormatException e) {
                        view.showError("Invalid input. Please enter a valid number.");
                  }
            }

            /* payment details */
            PaymentDetailsDTO payment = askPaymentDetails(
                        "Payment details (Bank account, method, and term):\n" +
                                    "  Bank account: 6 digits\n" +
                                    "  Method: CASH, CREDIT_CARD, BANK_TRANSFER, CASH_ON_DELIVERY\n" +
                                    "  Term: N30, N60, N90, PREPAID, POSTPAID");

            /* contacts (0-n) */
            List<ContactInfoDTO> contacts = new ArrayList<>();
            while (view.readLine("Add contact? (y/n): ").equalsIgnoreCase("y")) {
                  contacts.add(new ContactInfoDTO(
                              askNonEmpty("  Name: "),
                              askNonEmpty("  Email: "),
                              askNonEmpty("  Phone: ")));
            }

            /* products (0-n) */
            // we no longer ask for products here, as they are managed separately

            return new SupplierDTO(
                        name,
                        taxNumber,
                        address,
                        selfSupply,
                        supplyDays,
                        leadSupplyDays,
                        payment,
                        contacts);
      }

      @Override
      protected SupplierDTO update(SupplierDTO supplierDTO) throws Cancelled {
            view.showMessage("Updating supplier... (enter 'cancel' to cancel)");
            view.showMessage("Current details: \n" + supplierDTO);
            // we have the dto, now we ask the user for what he wants to change
            switch (askNonEmpty(
                        "What do you want to change? (name, taxNumber, address, selfSupply, supplyDays, paymentDetails, contacts)")) {
                  case "name" -> supplierDTO.setName(askNonEmpty("New name: "));
                  case "taxNumber" -> supplierDTO.setTaxNumber(askNonEmpty("New tax number: "));
                  case "address" -> {
                        AddressDTO address = supplierDTO.getAddressDTO();
                        address.setStreet(askNonEmpty("New street: "));
                        address.setCity(askNonEmpty("New city: "));
                        address.setBuildingNumber(askNonEmpty("New building no.: "));
                  }
                  case "selfSupply" -> supplierDTO.setSelfSupply(askBoolean("New self supply? (y/n): "));
                  case "supplyDays" -> supplierDTO.setSupplyDays(
                              askDaysOfWeek(
                                          "Enter new Supply days (By words, e.g. 1 - MONDAY, 2 - TUESDAY, ..., 7 - SUNDAY): "));
                  case "leadSupplyDays" -> {
                        int leadSupplyDays;
                        while (true) {
                              try {
                                    leadSupplyDays = askInt("New lead supply days (number of days before supply): ");
                                    if (leadSupplyDays < 0) {
                                          view.showError("Lead supply days cannot be negative. Please try again.");
                                    } else {
                                          supplierDTO.setLeadSupplyDays(leadSupplyDays);
                                          break;
                                    }
                              } catch (NumberFormatException e) {
                                    view.showError("Invalid input. Please enter a valid number.");
                              }
                        }
                  }
                  case "paymentDetails" -> {
                        supplierDTO.setPaymentDetails(
                                    askPaymentDetails("New payment details (Bank account, method, and term):\n"));
                  }
                  case "contacts" -> {
                        supplierDTO.setContacts(
                                    updateContacts(supplierDTO.getContactsInfoDTOList()));
                  }
                  default -> view.showError("Invalid input, please try again.");
            }
            return supplierDTO;
      }
}
