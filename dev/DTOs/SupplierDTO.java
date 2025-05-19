package DTOs;

import java.util.List;
import java.util.UUID;

public class SupplierDTO {
      private String name;
      private String taxNumber;
      private AddressDTO address;
      private PaymentDetailsDTO paymentDetails;
      private List<ContactInfoDTO> contacts;
      private List<SupplierProductDTO> products;
      private List<UUID> agreements;

      public SupplierDTO(
                  String name,
                  String taxNumber,
                  AddressDTO address,
                  PaymentDetailsDTO paymentDetails,
                  List<ContactInfoDTO> contacts,
                  List<SupplierProductDTO> products,
                  List<UUID> agreements) {
            this.name = name;
            this.taxNumber = taxNumber;
            this.address = address;
            this.paymentDetails = paymentDetails;
            this.contacts = contacts;
            this.products = products;
            this.agreements = agreements;
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

      public PaymentDetailsDTO getPaymentDetails() {
            return paymentDetails;
      }

      public void setPaymentDetails(PaymentDetailsDTO paymentDetails) {
            this.paymentDetails = paymentDetails;
      }

      public List<ContactInfoDTO> getContacts() {
            return contacts;
      }

      public void setContacts(List<ContactInfoDTO> contacts) {
            this.contacts = contacts;
      }

      public List<SupplierProductDTO> getProducts() {
            return products;
      }

      public void setProducts(List<SupplierProductDTO> products) {
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
            return "SupplierDTO{" +
                        "name='" + name + '\'' +
                        ", taxNumber='" + taxNumber + '\'' +
                        ", address=" + address +
                        ", paymentDetails=" + paymentDetails +
                        ", contacts=" + contacts +
                        ", products=" + products +
                        ", agreements=" + agreements +
                        '}';
      }

      @Override
      public boolean equals(Object o) {
            if (this == o)
                  return true;
            if (!(o instanceof SupplierDTO))
                  return false;

            SupplierDTO that = (SupplierDTO) o;

            if (!name.equals(that.name))
                  return false;
            if (!taxNumber.equals(that.taxNumber))
                  return false;
            if (!address.equals(that.address))
                  return false;
            if (!paymentDetails.equals(that.paymentDetails))
                  return false;
            if (!contacts.equals(that.contacts))
                  return false;
            if (!products.equals(that.products))
                  return false;
            return agreements.equals(that.agreements);
      }

      @Override
      public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + taxNumber.hashCode();
            result = 31 * result + address.hashCode();
            result = 31 * result + paymentDetails.hashCode();
            result = 31 * result + contacts.hashCode();
            result = 31 * result + products.hashCode();
            result = 31 * result + agreements.hashCode();
            return result;
      }
}
