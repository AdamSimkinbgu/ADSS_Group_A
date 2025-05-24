
package DomainLayer.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import DTOs.*;
import DTOs.Enums.DayofWeek;

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
   private EnumSet<DayofWeek> supplyDays;
   private PaymentDetails paymentDetails;
   private List<ContactInfo> contacts;
   private List<Integer> products;
   private List<Integer> agreements;

   public Supplier(SupplierDTO supplierDTO) {
      this.supplierId = nextSupplierID++;
      this.name = supplierDTO.getName();
      this.taxNumber = supplierDTO.getTaxNumber();
      this.address = new Address(supplierDTO.getAddressDTO());
      this.selfSupply = supplierDTO.getSelfSupply();
      this.supplyDays = (supplierDTO.getSupplyDays() != null && !supplierDTO.getSupplyDays().isEmpty())
            ? EnumSet.copyOf(supplierDTO.getSupplyDays())
            : EnumSet.noneOf(DayofWeek.class);
      this.paymentDetails = new PaymentDetails(supplierDTO.getPaymentDetailsDTO());
      this.contacts = new ArrayList<>();
      if (supplierDTO.getContactsInfoDTOList() != null) {
         for (ContactInfoDTO contact : supplierDTO.getContactsInfoDTOList()) {
            this.contacts.add(new ContactInfo(contact));
         }
      }
      this.products = new ArrayList<>();
      if (supplierDTO.getProducts() != null) {
         for (Integer productId : supplierDTO.getProducts().stream().map(SupplierProductDTO::getProductId).toList()) {
            this.products.add(productId);
         }
      }
      this.agreements = supplierDTO.getAgreements();
   }

   public Supplier(
         String name,
         String taxNumber,
         AddressDTO address,
         PaymentDetailsDTO paymentDetails,
         boolean selfSupply,
         EnumSet<DayofWeek> supplyDays,
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
            : EnumSet.noneOf(DayofWeek.class);
      this.contacts = new ArrayList<>();
      if (contacts != null) {
         for (ContactInfoDTO contact : contacts) {
            this.contacts.add(new ContactInfo(contact));
         }
      }
      this.products = new ArrayList<>();
      if (products != null) {
         for (SupplierProductDTO product : products) {
            this.products.add(product.getProductId());
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
         EnumSet<DayofWeek> supplyDays,
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
            : EnumSet.noneOf(DayofWeek.class);
      this.contacts = new ArrayList<>();
      if (contacts != null) {
         for (ContactInfoDTO contact : contacts) {
            this.contacts.add(new ContactInfo(contact));
         }
      }
      this.products = new ArrayList<>();
      if (products != null) {
         for (SupplierProductDTO product : products) {
            this.products.add(product.getProductId());
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

   public List<Integer> getProducts() {
      return products;
   }

   public void setProducts(List<Integer> products) {
      this.products = products;
   }

   public List<Integer> getAgreements() {
      return agreements;
   }

   public void setAgreements(List<Integer> agreements) {
      this.agreements = agreements;
   }

   public void addAgreement(int agreement) {
      if (this.agreements == null) {
         this.agreements = new ArrayList<>();
      }
      this.agreements.add(agreement);
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

   public boolean getSelfSupply() {
      return selfSupply;
   }

   public EnumSet<DayofWeek> getSupplyDays() {
      return supplyDays;
   }

   public void setSelfSupply(boolean selfSupply) {
      this.selfSupply = selfSupply;
   }

   public void setSupplyDays(EnumSet<DayofWeek> supplyDays) {
      this.supplyDays = supplyDays;
   }

   public void addProduct(int supplierProduct) {
      if (this.products == null) {
         this.products = new ArrayList<>();
      }
      this.products.add(supplierProduct);
   }

   public void removeProduct(int productId) {
      if (this.products != null) {
         this.products.removeIf(p -> p == productId);
      }
   }
}