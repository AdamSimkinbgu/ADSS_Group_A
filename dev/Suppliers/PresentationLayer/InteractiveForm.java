// cli/forms/InteractiveForm.java
package Suppliers.PresentationLayer;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import Suppliers.DTOs.ContactInfoDTO;
import Suppliers.DTOs.PaymentDetailsDTO;
import Suppliers.DTOs.Enums.PaymentMethod;
import Suppliers.DTOs.Enums.PaymentTerm;

public abstract class InteractiveForm<T> {

    protected final View view; // your existing console abstraction

    protected InteractiveForm(View view) {
        this.view = view;
    }

    /** blocks until user cancels or we have a valid object */
    public Optional<T> fillBuild() {
        try {
            return Optional.of(build());
        } catch (Cancelled e) {
            view.showMessage("Cancelled.");
            return Optional.empty();
        }
    }

    public Optional<T> fillUpdate(T dto) {
        try {
            return Optional.of(update(dto));
        } catch (Cancelled e) {
            view.showMessage("Cancelled.");
            return Optional.empty();
        }
    }

    protected abstract T build() throws Cancelled;

    protected abstract T update(T dto) throws Cancelled;

    /* ---------- convenience helpers ---------- */

    protected String ask(String prompt) throws Cancelled {
        String line = view.readLine(prompt).trim();
        if (line.equalsIgnoreCase("cancel"))
            throw new Cancelled();
        return line;
    }

    protected String askNonEmpty(String prompt) throws Cancelled {
        String line = ask(prompt);
        while (line.isEmpty()) {
            view.showError("Input cannot be empty. Please try again.");
            line = ask(prompt);
        }
        return line;
    }

    protected static class Cancelled extends Exception {
    }

    protected int askInt(String prompt) throws Cancelled {
        String line = ask(prompt);
        while (!line.matches("\\d+")) {
            view.showError("Invalid input. Please enter a valid integer.");
            line = ask(prompt);
        }
        return Integer.parseInt(line);
    }

    protected BigDecimal askBigDecimal(String prompt) throws Cancelled {
        String line = ask(prompt);
        while (!line.matches("\\d+(\\.\\d+)?")) {
            view.showError("Invalid input. Please enter a valid decimal number.");
            line = ask(prompt);
        }
        return new BigDecimal(line);
    }

    protected boolean askBoolean(String prompt) throws Cancelled {
        String line = ask(prompt);
        // allow "y", "yes", "n", "no" (case insensitive)
        while (!line.equalsIgnoreCase("y") && !line.equalsIgnoreCase("yes")
                && !line.equalsIgnoreCase("n") && !line.equalsIgnoreCase("no")) {
            view.showError("Invalid input. Please enter 'y' or 'n'.");
            line = ask(prompt);
        }
        return line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes");
    }

    protected LocalDate askDate(String prompt) throws Cancelled {
        String line = ask(prompt);
        while (!line.matches("\\d{4}-\\d{2}-\\d{2}")) {
            view.showError("Invalid input. Please enter a valid date in YYYY-MM-DD format.");
            line = ask(prompt);
        }
        return LocalDate.parse(line);
    }

    protected EnumSet<DayOfWeek> askDaysOfWeek(String prompt) throws Cancelled {
        EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        // monday is 1, sunday is 7, shift it so sunday is 1, monday is 2, ..., saturday
        // is 7
        // the user enters a day and we check if it's valid
        while (true) {
            String line = ask(prompt);
            if (line.isEmpty()) {
                view.showError("Input cannot be empty. Please try again.");
                continue;
            }
            String[] parts = line.split(",");
            for (String part : parts) {
                part = part.trim().toUpperCase();
                try {
                    int dayNumber = Integer.parseInt(part);
                    if (dayNumber < 1 || dayNumber > 7) {
                        view.showError("Invalid day number: " + dayNumber + ". Please enter a number between 1 and 7.");
                        continue;
                    }
                    days.add(DayOfWeek.of((dayNumber % 7) + 6)); // shift to make Sunday the first day
                } catch (NumberFormatException e) {
                    try {
                        days.add(DayOfWeek.valueOf(part));
                    } catch (IllegalArgumentException ex) {
                        view.showError("Invalid day name: " + part + ". Please enter a valid day name.");
                    }
                }
            }
            if (!days.isEmpty()) {
                view.showMessage("Selected supply days: " + days);
                return days;
            } else {
                view.showError("No valid days entered. Please try again.");
            }
        }
    }

    protected String askEmail(String prompt) throws Cancelled {
        String email = ask(prompt);
        while (!email.matches("^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            view.showError("Invalid email format. Please try again.");
            email = ask(prompt);
        }
        return email;
    }

    protected String askPhone(String prompt) throws Cancelled {
        String phone = ask(prompt);
        while (!phone.matches("^\\+?[0-9\\-\\s]+$")) {
            view.showError("Invalid phone number format. Please try again.");
            phone = ask(prompt);
        }
        return phone;
    }

    protected PaymentDetailsDTO askPaymentDetails(String prompt) throws Cancelled {
        String bankAccount = askNonEmpty(" Bank account (6 digits): ");
        while (!bankAccount.matches("^\\d{6}$")) {
            view.showError("Invalid bank account number. Please enter exactly 6 digits.");
            bankAccount = askNonEmpty(" Bank account (6 digits): ");
        }

        String method = askNonEmpty(" Payment method (CASH, CREDIT_CARD, BANK_TRANSFER, CASH_ON_DELIVERY): ");
        while (!method.matches("CASH|CREDIT_CARD|BANK_TRANSFER|CASH_ON_DELIVERY")) {
            view.showError(
                    "Invalid payment method. Please enter one of: CASH, CREDIT_CARD, BANK_TRANSFER, CASH_ON_DELIVERY.");
            method = askNonEmpty(" Payment method (CASH, CREDIT_CARD, BANK_TRANSFER, CASH_ON_DELIVERY): ");
        }

        String term = askNonEmpty(" Payment term (N30, N60, N90, PREPAID, POSTPAID): ");
        while (!term.matches("N30|N60|N90|PREPAID|POSTPAID")) {
            view.showError("Invalid payment term. Please enter one of: N30, N60, N90, PREPAID, POSTPAID.");
            term = askNonEmpty(" Payment term (N30, N60, N90, PREPAID, POSTPAID): ");
        }

        return new PaymentDetailsDTO(bankAccount.trim(), PaymentMethod.valueOf(method.trim()),
                PaymentTerm.valueOf(term.trim()));
    }

    protected List<ContactInfoDTO> updateContacts(List<ContactInfoDTO> existingContacts) throws Cancelled {
        while (true) {
            String action = "";
            while (!action.equals("done")) {
                action = askNonEmpty(
                        "What do you want to do with contacts? (add/remove/update/done/list/cancel): ").trim()
                        .toLowerCase();
                switch (action) {
                    case "add":
                        ContactInfoDTO newContact = new ContactInfoDTO(
                                -1,
                                askNonEmpty("Enter contact name: "),
                                askEmail("Enter contact email: "),
                                askPhone("Enter contact phone: "));
                        existingContacts.add(newContact);
                        view.showMessage("Contact added: " + newContact);
                        break;
                    case "remove":
                        String nameToRemove = askNonEmpty("Enter the name of the contact to remove: ");
                        // add another one with the id 0 for removal because no supplier id is set to 0
                        // in the system
                        // -1 is used for new contacts and 0 will now be used for removal
                        ContactInfoDTO removeContact = new ContactInfoDTO(
                                0, nameToRemove, "removal@gmail.com", "0543211234");
                        existingContacts.add(removeContact);
                        break;
                    case "update":
                        String nameToUpdate = askNonEmpty("Enter the name of the contact to update: ");
                        Optional<ContactInfoDTO> contactToUpdate = existingContacts.stream()
                                .filter(contact -> contact.getName().equalsIgnoreCase(nameToUpdate)).findFirst();
                        if (contactToUpdate.isPresent()) {
                            while (true) {
                                String fieldToUpdate = askNonEmpty(
                                        "What do you want to update? (name/email/phone/back to return): ").trim()
                                        .toLowerCase();
                                switch (fieldToUpdate) {
                                    case "name":
                                        view.showMessage(
                                                "Please remove the contact and add a new one with the updated name.");
                                        break;
                                    case "email":
                                        String newEmail = askEmail("Enter new email: ");
                                        contactToUpdate.get().setEmail(newEmail);
                                        view.showMessage("Contact email updated to: " + newEmail);
                                        break;
                                    case "phone":
                                        String newPhone = askPhone("Enter new phone: ");
                                        contactToUpdate.get().setPhone(newPhone);
                                        view.showMessage("Contact phone updated to: " + newPhone);
                                        break;
                                    case "back":
                                    default:
                                        view.showError("Invalid input. Please try again.");
                                        continue;
                                }
                                break;
                            }
                        } else {
                            view.showError("Contact with name " + nameToUpdate + " not found.");
                        }
                        break;
                    case "done":
                        return existingContacts;
                    case "list":
                        if (existingContacts.isEmpty()) {
                            view.showMessage("No contacts available.");
                        } else {
                            view.showMessage("Current contacts:");
                            for (ContactInfoDTO contact : existingContacts) {
                                view.showMessage(contact.toString());
                            }
                        }
                        break;
                    default:
                        view.showError("Invalid action. Please enter add, remove, update, or cancel.");
                        break;
                }
            }
        }
    }

}
