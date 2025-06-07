package Suppliers.ServiceLayer.Interfaces_and_Abstracts;

import java.math.BigDecimal;
import java.time.LocalDate;

import Suppliers.DTOs.AddressDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.DTOs.ContactInfoDTO;
import Suppliers.DTOs.PaymentDetailsDTO;

public abstract class BaseValidator {

   /**
    * Given a name‐like input (already validated to contain only letters/spaces),
    * returns the same string in “Title Case” (each word starts with an uppercase
    * letter and the rest of its letters are lowercase).
    *
    * Examples:
    * normalizeName("aDaM SiMkIn") → "Adam Simkin"
    * normalizeName(" jOhN DOE ") → "John Doe"
    *
    * @param rawName a non‐null, non‐empty name string (letters and spaces only)
    * @return the input, but with each word capitalized (“Title Case”)
    */
   protected String normalizeName(String rawName) {
      if (rawName == null) {
         throw new IllegalArgumentException("rawName must not be null");
      }

      // 1) Trim leading/trailing whitespace:
      String trimmed = rawName.trim();
      if (trimmed.isEmpty()) {
         return "";
      }

      // 2) Split on one or more whitespace characters:
      String[] parts = trimmed.split("\\s+");

      // 3) For each chunk, uppercase the first letter + lowercase the rest:
      for (int i = 0; i < parts.length; i++) {
         String word = parts[i];
         if (word.length() == 0) {
            continue;
         }
         // Uppercase first character:
         char firstChar = Character.toUpperCase(word.charAt(0));
         // Lowercase the remainder (if any):
         String remainder = "";
         if (word.length() > 1) {
            remainder = word.substring(1).toLowerCase();
         }
         parts[i] = firstChar + remainder;
      }

      // 4) Re‐join with a single space:
      return String.join(" ", parts);
   }

   protected AddressDTO normalizeAddress(AddressDTO address) {
      if (address == null) {
         throw new IllegalArgumentException("AddressDTO must not be null");
      }
      // Normalize the street, city, and building number
      String normalizedStreet = address.getStreet() != null ? normalizeName(address.getStreet().trim()) : "";
      String normalizedCity = address.getCity() != null ? normalizeName(address.getCity().trim()) : "";
      String normalizedBuildingNumber = address.getBuildingNumber() != null
            ? normalizeName(address.getBuildingNumber().trim())
            : "";

      return new AddressDTO(normalizedStreet, normalizedCity, normalizedBuildingNumber);
   }

   protected String normalizeEmail(String email) {
      if (email == null) {
         throw new IllegalArgumentException("Email must not be null");
      }
      // Trim whitespace and convert to lowercase
      return email.trim().toLowerCase();
   }

   protected boolean isValidName(String name) {
      if (name == null || name.isEmpty()) {
         return false;
      }
      // Regex for names: letters and spaces only, 1-50 characters
      String nameRegex = "^[a-zA-Z0-9 ]{1,50}$";
      return name.matches(nameRegex);
   }

   protected boolean isValidEmail(String email) {
      if (email == null || email.isEmpty()) {
         return false;
      }
      // Simple regex for basic email validation
      String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
      return email.matches(emailRegex);
   }

   protected boolean isValidPhone(String phone) {
      if (phone == null || phone.isEmpty()) {
         return false;
      }
      // Regex for Israeli phone numbers in the format 05X-XXX-XXXX or 05XXXXXXXX
      String phoneRegex = "^05\\d{1}-?\\d{3}-?\\d{4}$";
      return phone.matches(phoneRegex);
   }

   protected String normalizePhone(String phone) {
      if (phone == null) {
         throw new IllegalArgumentException("Phone number must not be null");
      }
      // Remove any non-digit characters and format to 05X-XXX-XXXX
      String digitsOnly = phone.replaceAll("\\D", "");
      return String.format("%s-%s-%s", digitsOnly.substring(0, 3), digitsOnly.substring(3, 6), digitsOnly.substring(6));
   }

   protected boolean isValidTaxNumber(String taxNumber) {
      if (taxNumber == null || taxNumber.isEmpty()) {
         return false;
      }
      // Regex for a 9-digit Israeli tax number
      String taxNumberRegex = "^[0-9]{9}$";
      return taxNumber.matches(taxNumberRegex);
   }

   protected boolean isValidBankAccountNumber(String bankAccountNumber) {
      if (bankAccountNumber == null || bankAccountNumber.isEmpty()) {
         return false;
      }
      // Regex for a 6-digit bank account number
      String bankAccountRegex = "^[0-9]{6}$";
      return bankAccountNumber.matches(bankAccountRegex);
   }

   protected PaymentDetailsDTO normalizePaymentDetails(PaymentDetailsDTO paymentDetails) {
      if (paymentDetails == null) {
         throw new IllegalArgumentException("PaymentDetailsDTO must not be null");
      }
      // Normalize the bank account number
      String normalizedBankAccountNumber = paymentDetails.getBankAccountNumber() != null
            ? paymentDetails.getBankAccountNumber().trim()
            : "";
      return new PaymentDetailsDTO(normalizedBankAccountNumber, paymentDetails.getPaymentMethod(),
            paymentDetails.getPaymentTerm());
   }

   protected boolean isValidAddress(AddressDTO address) {
      if (address == null) {
         return false;
      }
      if (address.getCity() == null || address.getCity().isEmpty()) {
         return false;
      }
      if (address.getStreet() == null || address.getStreet().isEmpty()) {
         return false;
      }
      if (address.getBuildingNumber() == null || address.getBuildingNumber().isEmpty()) {
         return false;
      }
      // Additional checks can be added as needed
      return true;
   }

   protected boolean isValidPaymentDetails(PaymentDetailsDTO paymentDetails) {
      if (paymentDetails == null) {
         return false;
      }
      if (!isValidBankAccountNumber(paymentDetails.getBankAccountNumber())) {
         return false;
      }
      if (paymentDetails.getPaymentMethod() == null) {
         return false;
      }
      if (paymentDetails.getPaymentTerm() == null) {
         return false;
      }
      // Additional checks can be added as needed
      return true;
   }

   protected boolean isValidContactInfo(ContactInfoDTO contact) {
      if (contact == null) {
         return false;
      }

      if (!isValidName(contact.getName())) {
         return false;
      }
      if (!isValidEmail(contact.getEmail())) {
         return false;
      }
      if (!isValidPhone(contact.getPhone())) {
         return false;
      }

      return true;
   }

   protected ContactInfoDTO normalizeContactInfo(ContactInfoDTO contact) {
      if (contact == null) {
         throw new IllegalArgumentException("Contact info list must not be null");
      }
      contact.setName(normalizeName(contact.getName()));
      contact.setEmail(normalizeEmail(contact.getEmail()));
      contact.setPhone(contact.getPhone() != null ? normalizePhone(contact.getPhone()) : null);

      return contact;
   }

   protected boolean isValidSupplierProductName(String name) {
      if (name == null || name.isEmpty()) {
         return false;
      }
      // Regex for product names: letters, numbers, spaces, 1-50 characters
      String productNameRegex = "^[a-zA-Z0-9%&#()\\-\\s]{1,50}$";
      // Allow special characters like %, &, #, (, ), -, and spaces
      return name.matches(productNameRegex);
   }

   protected boolean isValidCatalogNumber(String catalogNumber) {
      if (catalogNumber == null || catalogNumber.isEmpty()) {
         return false;
      }
      // Regex for catalog numbers: letters, numbers, spaces, 1-20 characters
      String catalogNumberRegex = "^[a-zA-Z0-9]{1,20}$";
      // Allow letters, numbers, and spaces
      return catalogNumber.matches(catalogNumberRegex);
   }

   protected boolean areDatesValid(LocalDate startDate, LocalDate endDate) {
      if (startDate == null || endDate == null) {
         return false;
      }
      // Simple date format check (YYYY-MM-DD)
      String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
      if (!startDate.toString().matches(dateRegex) || !endDate.toString().matches(dateRegex)) {
         return false; // Dates must be in YYYY-MM-DD format
      }
      // Check if start date is before end date and not in the past
      LocalDate today = LocalDate.now();
      if (startDate.isBefore(today)) {
         return false; // Start date cannot be before today
      }
      if (startDate.isAfter(endDate)) {
         return false; // Start date must be before end date
      }
      return true;
   }

   protected boolean isBillofQuantitiesItemValid(BillofQuantitiesItemDTO item) {
      if (item == null) {
         return false;
      }
      if (item.getProductId() <= 0) {
         return false; // Product ID must be a positive integer
      }
      if (item.getQuantity() <= 0) {
         return false; // Quantity must be greater than 0
      }
      if (item.getDiscountPercent().compareTo(BigDecimal.ZERO) < 0
            || item.getDiscountPercent().compareTo(new BigDecimal("1")) > 0) {
         return false; // Discount percentage must be between 0 and 100
      }
      return true;
   }
}
