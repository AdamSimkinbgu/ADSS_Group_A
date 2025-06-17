
package Suppliers.DomainLayer.Classes;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import Suppliers.DTOs.*;

/**
 * Supplier entity representing a vendor in the system.
 */
public class Supplier implements Serializable {
   private int supplierId;
   private String name;
   private String taxNumber;
   private AddressDTO address;
   private boolean selfSupply;
   private EnumSet<DayOfWeek> supplyDays;
   private int leadSupplyDays;
   private PaymentDetails paymentDetails;
   private List<ContactInfo> contacts;

   public Supplier() {
   }

   public Supplier(SupplierDTO supplierDTO) {
      this.supplierId = supplierDTO.getId(); // SQLite will auto-increment this
      this.name = supplierDTO.getName();
      this.taxNumber = supplierDTO.getTaxNumber();
      this.address = new AddressDTO(supplierDTO.getAddressDTO());
      this.selfSupply = supplierDTO.getSelfSupply();
      this.supplyDays = (supplierDTO.getSupplyDays() != null && !supplierDTO.getSupplyDays().isEmpty())
            ? EnumSet.copyOf(supplierDTO.getSupplyDays())
            : EnumSet.noneOf(DayOfWeek.class);
      this.leadSupplyDays = supplierDTO.getLeadSupplyDays();
      this.paymentDetails = new PaymentDetails(supplierDTO.getPaymentDetailsDTO());
      this.contacts = new ArrayList<>();
      if (supplierDTO.getContactsInfoDTOList() != null) {
         for (ContactInfoDTO contact : supplierDTO.getContactsInfoDTOList()) {
            this.contacts.add(new ContactInfo(contact));
         }
      }
   }

   public Supplier(
         String name,
         String taxNumber,
         AddressDTO address,
         PaymentDetailsDTO paymentDetails,
         boolean selfSupply,
         EnumSet<DayOfWeek> supplyDays,
         int leadSupplyDays,
         List<ContactInfoDTO> contacts) {
      this.supplierId = -1; // SQLite will auto-increment this
      this.name = name;
      this.taxNumber = taxNumber;
      this.address = new AddressDTO(address);
      this.paymentDetails = new PaymentDetails(paymentDetails);
      this.selfSupply = selfSupply;
      this.supplyDays = (supplyDays != null && !supplyDays.isEmpty())
            ? EnumSet.copyOf(supplyDays)
            : EnumSet.noneOf(DayOfWeek.class);
      this.leadSupplyDays = leadSupplyDays;
      this.contacts = new ArrayList<>();
      if (contacts != null) {
         for (ContactInfoDTO contact : contacts) {
            this.contacts.add(new ContactInfo(contact));
         }
      }
   }

   public Supplier(
         int supplierId,
         String name,
         String taxNumber,
         AddressDTO address,
         PaymentDetailsDTO paymentDetails,
         boolean selfSupply,
         EnumSet<DayOfWeek> supplyDays,
         int leadSupplyDays,
         List<ContactInfoDTO> contacts) {
      this.supplierId = supplierId;
      this.name = name;
      this.taxNumber = taxNumber;
      this.address = new AddressDTO(address);
      this.paymentDetails = new PaymentDetails(paymentDetails);
      this.selfSupply = selfSupply;
      this.supplyDays = (supplyDays != null && !supplyDays.isEmpty())
            ? EnumSet.copyOf(supplyDays)
            : EnumSet.noneOf(DayOfWeek.class);
      this.leadSupplyDays = leadSupplyDays;
      this.contacts = new ArrayList<>();
      if (contacts != null) {
         for (ContactInfoDTO contact : contacts) {
            this.contacts.add(new ContactInfo(contact));
         }
      }
   }

   // ───────────────────────────────────────────────────────────────────────
   // Getters and setters
   // ───────────────────────────────────────────────────────────────────────

   public int getSupplierId() {
      return supplierId;
   }

   public void setSupplierId(int supplierId) {
      this.supplierId = supplierId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getTaxNumber() {
      return taxNumber;
   }

   public void setTaxNumber(String taxNumber) {
      this.taxNumber = taxNumber;
   }

   public AddressDTO getAddress() {
      return address;
   }

   public void setAddress(AddressDTO address) {
      this.address = address;
   }

   public PaymentDetails getPaymentDetails() {
      return paymentDetails;
   }

   public void setPaymentDetails(PaymentDetails paymentDetails) {
      this.paymentDetails = paymentDetails;
   }

   public List<ContactInfo> getContacts() {
      return contacts;
   }

   public void setContacts(List<ContactInfo> newOnes) {
      if (newOnes == null)
         return;
      if (this.contacts == null) {
         this.contacts = new ArrayList<>();
      }
      this.contacts.addAll(newOnes);
   }

   @Override
   public String toString() {
      // pretty json format
      return "{\n" +
            "   \"supplierId\": " + supplierId + ",\n" +
            "   \"name\": \"" + name + "\",\n" +
            "   \"taxNumber\": \"" + taxNumber + "\",\n" +
            "   \"address\": " + address.toString() + ",\n" +
            "   \"paymentDetails\": " + paymentDetails.toString() + ",\n" +
            "   \"contacts\": " + contacts.toString() + ",\n" +
            "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof Supplier))
         return false;
      Supplier that = (Supplier) o;
      return supplierId == that.supplierId &&
            selfSupply == that.selfSupply &&
            leadSupplyDays == that.leadSupplyDays &&
            name.equals(that.name) &&
            taxNumber.equals(that.taxNumber) &&
            address.equals(that.address) &&
            paymentDetails.equals(that.paymentDetails) &&
            contacts.equals(that.contacts) &&
            supplyDays.equals(that.supplyDays);
   }

   public boolean getSelfSupply() {
      return selfSupply;
   }

   public EnumSet<DayOfWeek> getSupplyDays() {
      return supplyDays;
   }

   public void setSelfSupply(boolean selfSupply) {
      this.selfSupply = selfSupply;
   }

   public void setSupplyDays(EnumSet<DayOfWeek> supplyDays) {
      this.supplyDays = supplyDays;
   }

   public int getLeadSupplyDays() {
      return leadSupplyDays;
   }

   public void setLeadSupplyDays(int leadSupplyDays) {
      this.leadSupplyDays = leadSupplyDays;
   }

}