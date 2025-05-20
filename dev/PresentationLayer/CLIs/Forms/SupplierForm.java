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

            /* payment */
            view.showMessage("-- Payment details --");
            PaymentDetailsDTO payment = new PaymentDetailsDTO(
                        askNonEmpty("Bank account: "),
                        PaymentMethod.valueOf(askNonEmpty("Method (CASH/CARD/...): ").toUpperCase()),
                        PaymentTerm.valueOf(askNonEmpty("Term (N30/…): ").toUpperCase()));

            /* contacts (0-n) */
            List<ContactInfoDTO> contacts = new ArrayList<>();
            while (view.readLine("Add contact? (y/n): ").equalsIgnoreCase("y")) {
                  contacts.add(new ContactInfoDTO(
                              askNonEmpty("  Name: "),
                              askNonEmpty("  Email: "),
                              askNonEmpty("  Phone: ")));
            }

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

}