
package DomainLayer.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import DTOs.*;
import DTOs.Enums.WeekofDay;

/**
 * Supplier entity representing a vendor in the system.
 */
public class Supplier implements Serializable {
   private static int nextSupplierID = 1;
   private int supplierId;
   private String name;
   private String taxNumber;
   private Address address;
   private boolean selfSupply;
   private EnumSet<WeekofDay> supplyDays;
   private PaymentDetails paymentDetails;
   private List<ContactInfo> contacts;
   private List<SupplierProduct> products;
   private List<Integer> agreements;

   public Supplier(SupplierDTO supplier) {
      this.supplierId = nextSupplierID++;
      this.name = supplier.getName();
      this.taxNumber = supplier.getTaxNumber();
      this.address = new Address(supplier.getAddress());
      this.paymentDetails = new PaymentDetails(supplier.getPaymentDetails());
      this.contacts = new ArrayList<>();
      if (supplier.getContacts() != null) {
         for (ContactInfoDTO contact : supplier.getContacts()) {
            this.contacts.add(new ContactInfo(contact));
         }
      }
      this.products = new ArrayList<>();
      if (supplier.getProducts() != null) {
         for (SupplierProductDTO product : supplier.getProducts()) {
            this.products.add(new SupplierProduct(product));
         }
      }
      this.products = new ArrayList<>();
      if (supplier.getProducts() != null) {
         for (SupplierProductDTO product : supplier.getProducts()) {
            this.products.add(new SupplierProduct(product));
         }
      }
      this.agreements = supplier.getAgreements();
   }

   public Supplier(
         String name,
         String taxNumber,
         AddressDTO address,
         PaymentDetailsDTO paymentDetails,
         boolean selfSupply,
         EnumSet<WeekofDay> supplyDays,
         List<ContactInfoDTO> contacts,
         List<SupplierProductDTO> products,
         List<Integer> agreements) {
      this.supplierId = nextSupplierID++;
      this.name = name;
      this.taxNumber = taxNumber;
      this.address = new Address(address);
      this.paymentDetails = new PaymentDetails(paymentDetails);
      this.selfSupply = selfSupply;
      this.supplyDays = (supplyDays != null && !supplyDays.isEmpty())
            ? EnumSet.copyOf(supplyDays)
            : EnumSet.noneOf(WeekofDay.class);
      this.contacts = new ArrayList<>();
      if (contacts != null) {
         for (ContactInfoDTO contact : contacts) {
            this.contacts.add(new ContactInfo(contact));
         }
      }
      this.products = new ArrayList<>();
      if (products != null) {
         for (SupplierProductDTO product : products) {
            this.products.add(new SupplierProduct(product));
         }
      }
      this.agreements = agreements;
   }

   public Supplier(
         int supplierId,
         String name,
         String taxNumber,
         AddressDTO address,
         PaymentDetailsDTO paymentDetails,
         boolean selfSupply,
         EnumSet<WeekofDay> supplyDays,
         List<ContactInfoDTO> contacts,
         List<SupplierProductDTO> products,
         List<Integer> agreements) {
      this.supplierId = supplierId;
      this.name = name;
      this.taxNumber = taxNumber;
      this.address = new Address(address);
      this.paymentDetails = new PaymentDetails(paymentDetails);
      this.selfSupply = selfSupply;
      this.supplyDays = (supplyDays != null && !supplyDays.isEmpty())
            ? EnumSet.copyOf(supplyDays)
            : EnumSet.noneOf(WeekofDay.class);
      this.contacts = new ArrayList<>();
      if (contacts != null) {
         for (ContactInfoDTO contact : contacts) {
            this.contacts.add(new ContactInfo(contact));
         }
      }
      this.products = new ArrayList<>();
      if (products != null) {
         for (SupplierProductDTO product : products) {
            this.products.add(new SupplierProduct(product));
         }
      }
      this.agreements = agreements;
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

   public Address getAddress() {
      return address;
   }

   public void setAddress(Address address) {
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

   public List<SupplierProduct> getProducts() {
      return products;
   }

   public void setProducts(List<SupplierProduct> products) {
      this.products = products;
   }

   public List<Integer> getAgreements() {
      return agreements;
   }

   public void setAgreements(List<Integer> agreements) {
      this.agreements = agreements;
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
            "   \"products\": " + products.toString() + ",\n" +
            "   \"agreements\": " + agreements.toString() + "\n" +
            "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof Supplier))
         return false;
      Supplier supplier = (Supplier) o;
      return supplierId == supplier.supplierId &&
            name.equals(supplier.name) &&
            taxNumber.equals(supplier.taxNumber);
   }

   public boolean isSelfSupply() {
      return selfSupply;
   }

   public EnumSet<WeekofDay> getSupplyDays() {
      return supplyDays;
   }

   public void setSelfSupply(boolean selfSupply) {
      this.selfSupply = selfSupply;
   }

   public void setSupplyDays(EnumSet<WeekofDay> supplyDays) {
      this.supplyDays = supplyDays;
   }
}