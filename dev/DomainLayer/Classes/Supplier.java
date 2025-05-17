
package DomainLayer.Classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Supplier entity representing a vendor in the system.
 */
public class Supplier implements Serializable {

   private UUID supplierId;
   private String name;
   private String taxNumber;
   private Address address;
   private PaymentDetails paymentDetails;
   private List<ContactInfo> contacts;
   private List<SupplierProduct> products;
   private List<UUID> agreements;

   public Supplier() {
   }

   public Supplier(
         UUID supplierId,
         String name,
         String taxNumber,
         Address address,
         PaymentDetails paymentDetails,
         List<ContactInfo> contacts,
         List<SupplierProduct> products,
         List<UUID> agreements) {
      this.supplierId = (supplierId != null)
            ? supplierId
            : UUID.nameUUIDFromBytes(
                  (name + ":" + taxNumber)
                        .getBytes(StandardCharsets.UTF_8));
      this.name = name;
      this.taxNumber = taxNumber;
      this.address = address;
      this.paymentDetails = paymentDetails;
      this.contacts = (contacts != null ? contacts : new ArrayList<>());
      this.products = (products != null ? products : new ArrayList<>());
      this.agreements = (agreements != null ? agreements : new ArrayList<>());
   }

   // ───────────────────────────────────────────────────────────────────────
   // Getters and setters
   // ───────────────────────────────────────────────────────────────────────

   public UUID getSupplierId() {
      return supplierId;
   }

   public void setSupplierId(UUID supplierId) {
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

   public List<UUID> getAgreements() {
      return agreements;
   }

   public void setAgreements(List<UUID> agreements) {
      this.agreements = agreements;
   }

   @Override
   public String toString() {
      return "Supplier{" +
            "supplierId=" + supplierId +
            ", name='" + name + '\'' +
            ", taxNumber='" + taxNumber + '\'' +
            ", address=" + address +
            ", paymentDetails=" + paymentDetails +
            ", contacts=" + contacts +
            ", products=" + products +
            ", agreements=" + agreements +
            '}';
   }
}