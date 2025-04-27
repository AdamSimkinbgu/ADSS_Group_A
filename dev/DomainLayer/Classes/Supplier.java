
package DomainLayer.Classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
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
   private List<Agreement> agreements;

   /**
    * JSON constructor for Jackson.
    */
   @JsonCreator
   public Supplier(
         @JsonProperty("name") String name,
         @JsonProperty("taxNumber") String taxNumber,
         @JsonProperty("address") Address address,
         @JsonProperty("paymentDetails") PaymentDetails paymentDetails,
         @JsonProperty("contacts") List<ContactInfo> contacts,
         @JsonProperty("products") List<SupplierProduct> products,
         @JsonProperty("agreements") List<Agreement> agreements) {
      this.supplierId = UUID.randomUUID();
      this.name = name;
      this.taxNumber = taxNumber;
      this.address = address;
      this.paymentDetails = paymentDetails;
      this.contacts = contacts != null ? contacts : new ArrayList<>();
      this.products = products != null ? products : new ArrayList<>();
      this.agreements = agreements != null ? agreements : new ArrayList<>();
   }

   // ───────────────────────────────────────────────────────────────────────
   // Getters and setters
   // ───────────────────────────────────────────────────────────────────────

   @JsonIgnore
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

   public PaymentDetails getBankDetails() {
      return paymentDetails;
   }

   @JsonIgnore
   public void setBankDetails(PaymentDetails paymentDetails) {
      this.paymentDetails = paymentDetails;
   }

   public List<ContactInfo> getContacts() {
      return contacts;
   }

   public void setContacts(List<ContactInfo> contacts) {
      this.contacts = contacts;
   }

   public List<SupplierProduct> getProducts() {
      return products;
   }

   public void setProducts(List<SupplierProduct> products) {
      this.products = products;
   }

   public List<Agreement> getAgreements() {
      return agreements;
   }

   public void setAgreements(List<Agreement> agreements) {
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