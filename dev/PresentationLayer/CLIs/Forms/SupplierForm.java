// cli/forms/SupplierForm.java
package PresentationLayer.CLIs.Forms;

import DTOs.*;
import DTOs.Enums.PaymentMethod;
import DTOs.Enums.PaymentTerm;
import DTOs.Enums.DayofWeek;
import PresentationLayer.View;
import PresentationLayer.CLIs.InteractiveForm;

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
            EnumSet<DayofWeek> supplyDays = EnumSet.noneOf(DayofWeek.class);
            if (selfSupply) {
                  view.showMessage("Self supply selected.");
                  supplyDays = askDaysOfWeek("Supply days (1 - Sunday, 2 - Monday, ...): ");
            } else {
                  view.showMessage("Self supply not selected.");
            }

            /* payment details */
            PaymentDetailsDTO payment = new PaymentDetailsDTO(
                        askNonEmpty("Bank account number: "),
                        PaymentMethod.valueOf(askNonEmpty("Payment method (CASH/CARD/...): ").toUpperCase()),
                        PaymentTerm.valueOf(askNonEmpty("Payment term (N30/…): ").toUpperCase()));

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
                        payment,
                        contacts,
                        new ArrayList<>(),
                        new ArrayList<>());
      }

      @Override
      protected SupplierDTO update(SupplierDTO supplierDTO) throws Cancelled {
            view.showMessage("Updating supplier... (enter 'cancel' to cancel)");
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
                              askDaysOfWeek("New supply days (1 - Sunday, 2 - Monday, ...): "));
                  case "paymentDetails" -> {
                        PaymentDetailsDTO payment = supplierDTO.getPaymentDetailsDTO();
                        payment.setBankAccountNumber(askNonEmpty("New bank account: "));
                        payment.setPaymentMethod(
                                    (PaymentMethod.valueOf(askNonEmpty("New method (CASH/CARD/...): ").toUpperCase())));
                        payment.setPaymentTerm(PaymentTerm.valueOf(askNonEmpty("New term (N30/…): ").toUpperCase()));
                  }
                  case "contacts" -> {
                        List<ContactInfoDTO> contacts = supplierDTO.getContactsInfoDTOList();
                        while (view.readLine("Add contact? (y/n): ").equalsIgnoreCase("y")) {
                              contacts.add(new ContactInfoDTO(
                                          askNonEmpty("  Name: "),
                                          askNonEmpty("  Email: "),
                                          askNonEmpty("  Phone: ")));
                        }
                  }
                  // case "products" -> {
                  // List<SupplierProductDTO> products = supplierDTO.getProducts();
                  // for (SupplierProductDTO product : products) {
                  // view.showMessage(product.toString());
                  // }
                  // int productId = askInt("Enter product ID to update: ");
                  // SupplierProductDTO product = products.stream()
                  // .filter(p -> p.getProductId() == productId)
                  // .findFirst()
                  // .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                  // switch (askNonEmpty(
                  // "What do you want to change? (supplierCatalogNumber, productName, price,
                  // weight, expiresInDays, manufacturerName)")) {
                  // case "supplierCatalogNumber" ->
                  // product.setSupplierCatalogNumber(askNonEmpty("New supplier catalog number:
                  // "));
                  // case "productName" -> product.setName(askNonEmpty("New product name: "));
                  // case "price" -> product.setPrice(askBigDecimal("New price: "));
                  // case "weight" -> product.setWeight(askBigDecimal("New weight: "));
                  // case "expiresInDays" -> product.setExpiresInDays(askInt("New expires in days:
                  // "));
                  // case "manufacturerName" -> product.setManufacturerName(askNonEmpty("New
                  // manufacturer name: "));
                  // }
                  // }
                  default -> view.showError("Invalid input, please try again.");
            }
            return supplierDTO;
      }
}
