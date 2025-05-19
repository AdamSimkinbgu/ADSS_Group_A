
package DomainLayer.Classes;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import DTOs.*;

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

   public Supplier(
         String name,
         String taxNumber,
         AddressDTO address,
         PaymentDetailsDTO paymentDetails,
         List<ContactInfoDTO> contacts,
         List<SupplierProductDTO> products,
         List<UUID> agreements) {
      this.supplierId = UUID.nameUUIDFromBytes(
            (name + ":" + taxNumber)
                  .getBytes(StandardCharsets.UTF_8));
      this.name = name;
      this.taxNumber = taxNumber;
      this.address = new Address(address);
      this.paymentDetails = new PaymentDetails(paymentDetails);
   }

   public Supplier(
         UUID supplierId,
         String name,
         String taxNumber,
         AddressDTO address,
         PaymentDetailsDTO paymentDetails,
         List<ContactInfoDTO> contacts,
         List<SupplierProductDTO> products,
         List<UUID> agreements) {
      this.supplierId = supplierId;
      this.name = name;
      this.taxNumber = taxNumber;
      this.address = new Address(address);
      this.paymentDetails = new PaymentDetails(paymentDetails);
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

   public String getSupplierId() {
      return supplierId.toString();
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

   public AddressDTO getAddress() {
      return AddressDTO.fromAddress(address);
   }

   public void setAddress(Address address) {
      this.address = address;
   }

   public PaymentDetailsDTO getPaymentDetails() {
      return PaymentDetailsDTO.fromPaymentDetails(paymentDetails);
   }

   public void setPaymentDetails(PaymentDetails paymentDetails) {
      this.paymentDetails = paymentDetails;
   }

   public List<ContactInfoDTO> getContacts() {
      return ContactInfoDTO.fromContactInfoList(contacts);
   }

   public void setContacts(List<ContactInfo> newOnes) {
      if (newOnes == null)
         return;
      if (this.contacts == null) {
         this.contacts = new ArrayList<>();
      }
      this.contacts.addAll(newOnes);
   }

   public List<SupplierProductDTO> getProducts() {
      return SupplierProductDTO.fromSupplierProductList(products);
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