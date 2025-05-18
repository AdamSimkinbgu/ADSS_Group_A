// cli/forms/SupplierForm.java
package PresentationLayer.CLIs.Forms;

import DTOs.*;
import PresentationLayer.View;
import PresentationLayer.CLIs.InteractiveForm;

import java.util.ArrayList;
import java.util.List;

public final class SupplierForm extends InteractiveForm<SupplierDTO> {

   public SupplierForm(View view) {
      super(view);
   }

   @Override
   protected SupplierDTO build() throws Cancelled {

      String name = askNonEmpty("Name:");
      String taxNumber = askNonEmpty("Tax number:");

      /* address */
      view.showMessage("-- Address --");
      AddressDTO address = new AddressDTO(
            askNonEmpty("Street: "),
            askNonEmpty("City: "),
            askNonEmpty("Building no.: "));

      /* payment */
      view.showMessage("-- Payment details --");
      PaymentDetailsDTO payment = new PaymentDetailsDTO(
            askNonEmpty("Bank account: "),
            askNonEmpty("Method (CASH/CARD/...): ").toUpperCase(),
            askNonEmpty("Term (N30/…): ").toUpperCase());

      /* contacts (0-n) */
      List<ContactInfoDTO> contacts = new ArrayList<>();
      while (view.readLine("Add contact? (y/n): ").equalsIgnoreCase("y")) {
         contacts.add(new ContactInfoDTO(
               askNonEmpty("  Name: "),
               askNonEmpty("  Email: "),
               askNonEmpty("  Phone: ")));
      }

      return new SupplierDTO(name,
            taxNumber,
            address,
            contacts,
            payment);
   }
}