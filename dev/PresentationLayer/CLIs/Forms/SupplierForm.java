// cli/forms/SupplierForm.java
package PresentationLayer.CLIs.Forms;

import DTOs.*;
import DTOs.Enums.PaymentMethod;
import DTOs.Enums.PaymentTerm;
import DTOs.Enums.DayofWeek;
import PresentationLayer.View;
import PresentationLayer.CLIs.InteractiveForm;

import java.math.BigDecimal;
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
            boolean selfSupply;
            while (true) {
                  String selfSupplyUserString = askNonEmpty("Self supply? (y/n): ").toLowerCase();
                  if (selfSupplyUserString.contains("y") || selfSupplyUserString.contains("yes")
                              || selfSupplyUserString.contains("true") || selfSupplyUserString.contains("t")) {
                        selfSupply = true;
                        break;
                  } else if (selfSupplyUserString.contains("n") || selfSupplyUserString.contains("no")
                              || selfSupplyUserString.contains("false") || selfSupplyUserString.contains("f")) {
                        selfSupply = false;
                        break;
                  } else {
                        view.showError("Invalid input: " + selfSupplyUserString + ". Please enter 'y' or 'n'.");
                        // If the user types 'cancel' at the prompt, askNonEmpty should handle it by
                        // throwing Cancelled.
                  }
            }
            EnumSet<DayofWeek> supplyDays = EnumSet.noneOf(DayofWeek.class);
            if (selfSupply) {
                  view.showMessage("-- Supply days --");
                  String[] days = view.readLine("Select days (1-7, separated by spaces): ").split(" ");
                  for (String day : days) {
                        try {
                              int dayNumber = Integer.parseInt(day);
                              if (dayNumber < 1 || dayNumber > 7) {
                                    view.showError("Invalid day number: " + day);
                              } else if (supplyDays.contains(DayofWeek.values()[dayNumber - 1])) {
                                    supplyDays.remove(DayofWeek.values()[dayNumber - 1]);
                              } else {
                                    supplyDays.add(DayofWeek.values()[dayNumber - 1]);
                              }
                        } catch (NumberFormatException e) {
                              view.showError("Invalid input: " + day);
                        }
                  }
            }

            /* payment details */
            view.showMessage("-- Payment details --");
            PaymentDetailsDTO payment;
            while (true) {
                  try {
                        payment = new PaymentDetailsDTO(
                                    askNonEmpty("Bank account: "),
                                    PaymentMethod.valueOf(askNonEmpty("Method (CASH/CARD/...): ").toUpperCase()),
                                    PaymentTerm.valueOf(askNonEmpty("Term (N30/…): ").toUpperCase()));
                        break;
                  } catch (IllegalArgumentException e) {
                        view.showError("Invalid input: " + e.getMessage());
                  }
            }

            /* contacts (0-n) */
            List<ContactInfoDTO> contacts = new ArrayList<>();
            while (view.readLine("Add contact? (y/n): ").equalsIgnoreCase("y")) {
                  contacts.add(new ContactInfoDTO(
                              askNonEmpty("  Name: "),
                              askNonEmpty("  Email: "),
                              askNonEmpty("  Phone: ")));
            }

            /* products (0-n) */
            List<SupplierProductDTO> products = new ArrayList<>();
            view.showMessage("-- Products --");
            while (view.readLine("Add product? (y/n): ").equalsIgnoreCase("y")) {
                  try {
                        int productId = Integer.parseInt(askNonEmpty("  Product ID: "));
                        String supplierCatalogNumber = askNonEmpty("  Supplier catalog number: ");
                        String productName = askNonEmpty("  Product name: ");
                        BigDecimal price = new BigDecimal(askNonEmpty("  Price: "));
                        BigDecimal weight = new BigDecimal(askNonEmpty("  Weight: "));
                        String manufacturerName = askNonEmpty("  Manufacturer name: ");
                        products.add(new SupplierProductDTO(
                                    productId,
                                    supplierCatalogNumber,
                                    productName,
                                    price,
                                    weight,
                                    manufacturerName));
                  } catch (NumberFormatException e) {
                        view.showError("Invalid input: " + e.getMessage());
                  }
            }

            return new SupplierDTO(
                        name,
                        taxNumber,
                        address,
                        selfSupply,
                        supplyDays,
                        payment,
                        contacts,
                        products,
                        new ArrayList<>());
      }

      @Override
      protected SupplierDTO update(SupplierDTO supplierDTO) throws Cancelled {
            view.showMessage("Updating supplier... (enter 'cancel' to cancel)");
            // we have the dto, now we ask the user for what he wants to change
            switch (askNonEmpty(
                        "What do you want to change? (name, taxNumber, address, selfSupply, supplyDays, paymentDetails, contacts, products)")) {
                  case "name" -> supplierDTO.setName(askNonEmpty("New name: "));
                  case "taxNumber" -> supplierDTO.setTaxNumber(askNonEmpty("New tax number: "));
                  case "address" -> {
                        AddressDTO address = new AddressDTO(
                                    askNonEmpty("Street: "),
                                    askNonEmpty("City: "),
                                    askNonEmpty("Building no.: "));
                        supplierDTO.setAddress(address);
                  }
                  case "selfSupply" -> {
                        boolean selfSupply;
                        while (true) {
                              String selfSupplyUserString = askNonEmpty("Self supply? (y/n): ").toLowerCase();
                              if (selfSupplyUserString.contains("y") || selfSupplyUserString.contains("yes")
                                          || selfSupplyUserString.contains("true")
                                          || selfSupplyUserString.contains("t")) {
                                    selfSupply = true;
                                    break;
                              } else if (selfSupplyUserString.contains("n") || selfSupplyUserString.contains("no")
                                          || selfSupplyUserString.contains("false")
                                          || selfSupplyUserString.contains("f")) {
                                    selfSupply = false;
                                    break;
                              } else {
                                    view.showError("Invalid input: " + selfSupplyUserString
                                                + ". Please enter 'y' or 'n'.");
                              }
                        }
                        supplierDTO.setSelfSupply(selfSupply);
                  }
                  case "supplyDays" -> {
                        EnumSet<DayofWeek> supplyDays = EnumSet.noneOf(DayofWeek.class);
                        String[] days = view.readLine("Select days (1-7, separated by spaces): ").split(" ");
                        for (String day : days) {
                              try {
                                    int dayNumber = Integer.parseInt(day);
                                    if (dayNumber < 1 || dayNumber > 7) {
                                          view.showError("Invalid day number: " + day);
                                    } else if (supplyDays.contains(DayofWeek.values()[dayNumber - 1])) {
                                          supplyDays.remove(DayofWeek.values()[dayNumber - 1]);
                                    } else {
                                          supplyDays.add(DayofWeek.values()[dayNumber - 1]);
                                    }
                              } catch (NumberFormatException e) {
                                    view.showError("Invalid input: " + day + " - " + e.getMessage());
                              }
                        }
                        supplierDTO.setSupplyDays(supplyDays);
                  }
                  case "paymentDetails" -> {
                        PaymentDetailsDTO payment;
                        while (true) {
                              try {
                                    payment = new PaymentDetailsDTO(
                                                askNonEmpty("Bank account: "),
                                                PaymentMethod.valueOf(
                                                            askNonEmpty("Method (CASH/CARD/...): ").toUpperCase()),
                                                PaymentTerm.valueOf(askNonEmpty("Term (N30/…): ").toUpperCase()));
                                    break;
                              } catch (IllegalArgumentException e) {
                                    view.showError("Invalid input: " + e.getMessage());
                              }
                        }
                        supplierDTO.setPaymentDetails(payment);
                  }
                  case "contacts" -> {
                        List<ContactInfoDTO> contacts = new ArrayList<>();
                        while (view.readLine("Add contact? (y/n): ").equalsIgnoreCase("y")) {
                              contacts.add(new ContactInfoDTO(
                                          askNonEmpty("  Name: "),
                                          askNonEmpty("  Email: "),
                                          askNonEmpty("  Phone: ")));
                        }
                        supplierDTO.setContacts(contacts);
                  }
                  case "products" -> {
                        List<SupplierProductDTO> products = new ArrayList<>();
                        view.showMessage("-- Products --");
                        while (view.readLine("Add product? (y/n): ").equalsIgnoreCase("y")) {
                              try {
                                    int productId = Integer.parseInt(askNonEmpty("  Product ID: "));
                                    String supplierCatalogNumber = askNonEmpty("  Supplier catalog number: ");
                                    String productName = askNonEmpty("  Product name: ");
                                    BigDecimal price = new BigDecimal(askNonEmpty("  Price: "));
                                    BigDecimal weight = new BigDecimal(askNonEmpty("  Weight: "));
                                    String manufacturerName = askNonEmpty("  Manufacturer name: ");
                                    products.add(new SupplierProductDTO(
                                                productId,
                                                supplierCatalogNumber,
                                                productName,
                                                price,
                                                weight,
                                                manufacturerName));
                              } catch (NumberFormatException e) {
                                    view.showError("Invalid input: " + e.getMessage());
                              }
                        }
                        supplierDTO.setProducts(products);
                  }
                  default -> view.showError("Invalid input, please try again.");
            }
            return supplierDTO;
      }
}