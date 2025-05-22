package DTOs;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import DTOs.Enums.DayofWeek;
import DomainLayer.Classes.Address;
import DomainLayer.Classes.ContactInfo;
import DomainLayer.Classes.PaymentDetails;
import DomainLayer.Classes.Supplier;
import DomainLayer.Classes.SupplierProduct;

public class SupplierDTO {
      private int id;
      private String name;
      private String taxNumber;
      private AddressDTO address;
      private boolean selfSupply;
      private EnumSet<DayofWeek> supplyDays;
      private PaymentDetailsDTO paymentDetails;
      private List<ContactInfoDTO> contacts;
      private List<SupplierProductDTO> products;
      private List<Integer> agreements;

      public SupplierDTO(
                  int id,
                  String name,
                  String taxNumber,
                  AddressDTO address,
                  boolean selfSupply,
                  EnumSet<DayofWeek> supplyDays,
                  PaymentDetailsDTO paymentDetails,
                  List<ContactInfoDTO> contacts,
                  List<SupplierProductDTO> products,
                  List<Integer> agreements) {
            this.id = id;
            this.name = name;
            this.taxNumber = taxNumber;
            this.address = address;
            this.selfSupply = selfSupply;
            this.supplyDays = supplyDays;
            this.paymentDetails = paymentDetails;
            this.contacts = contacts;
            this.products = products;
            this.agreements = agreements;
      }

      public SupplierDTO(
                  String name,
                  String taxNumber,
                  AddressDTO address,
                  boolean selfSupply,
                  EnumSet<DayofWeek> supplyDays,
                  PaymentDetailsDTO paymentDetails,
                  List<ContactInfoDTO> contacts,
                  List<SupplierProductDTO> products,
                  List<Integer> agreements) {
            this.id = -1; // Default value for new suppliers
            this.name = name;
            this.taxNumber = taxNumber;
            this.address = address;
            this.selfSupply = selfSupply;
            this.supplyDays = supplyDays;
            this.paymentDetails = paymentDetails;
            this.contacts = contacts;
            this.products = products;
            this.agreements = agreements;
      }

      public SupplierDTO(Supplier supplier) {
            this.id = supplier.getSupplierId();
            this.name = supplier.getName();
            this.taxNumber = supplier.getTaxNumber();
            this.address = AddressDTO.fromAddress(supplier.getAddress());
            this.selfSupply = supplier.getSelfSupply();
            this.supplyDays = supplier.getSupplyDays();
            this.paymentDetails = PaymentDetailsDTO.fromPaymentDetails(supplier.getPaymentDetails());
            this.contacts = ContactInfoDTO.fromContactInfoList(supplier.getContacts());
            this.products = SupplierProductDTO.fromSupplierProductList(supplier.getProducts());
            this.agreements = supplier.getAgreements();
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

      public AddressDTO getAddressDTO() {
            return address;
      }

      public Address getAddress() {
            return new Address(address);
      }

      public void setAddress(AddressDTO address) {
            this.address = address;
      }

      public PaymentDetailsDTO getPaymentDetailsDTO() {
            return paymentDetails;
      }

      public PaymentDetails getPaymentDetails() {
            return new PaymentDetails(paymentDetails);
      }

      public void setPaymentDetails(PaymentDetailsDTO paymentDetails) {
            this.paymentDetails = paymentDetails;
      }

      public List<ContactInfoDTO> getContacts() {
            return contacts;
      }

      public List<ContactInfo> getContactsList() {
            return ContactInfoDTO.toContactInfoList(contacts);
      }

      public void setContacts(List<ContactInfoDTO> contacts) {
            this.contacts = contacts;
      }

      public List<SupplierProductDTO> getProducts() {
            return products;
      }

      public List<SupplierProduct> getProductsList() {
            return SupplierProductDTO.toSupplierProductList(products);
      }

      public void setProducts(List<SupplierProductDTO> products) {
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
                        "  \"id\": " + id + ",\n" +
                        "  \"name\": \"" + name + "\",\n" +
                        "  \"taxNumber\": \"" + taxNumber + "\",\n" +
                        "  \"address\": " + address.toString() + ",\n" +
                        "  \"selfSupply\": " + selfSupply + ",\n" +
                        "  \"supplyDays\": " + supplyDays.toString() + ",\n" +
                        "  \"paymentDetails\": " + paymentDetails.toString() + ",\n" +
                        "  \"contacts\": " + contacts.toString() + ",\n" +
                        "  \"products\": " + products.toString() + ",\n" +
                        "  \"agreements\": " + agreements.toString() + "\n" +
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

      public SupplierDTO addAgreement(AgreementDTO agreementDTO) {
            if (this.agreements == null) {
                  this.agreements = new ArrayList<>();
            }
            this.agreements.add(agreementDTO.getAgreementId());
            return this;
      }

      public SupplierDTO removeAgreement(int agreementId) {
            if (this.agreements != null) {
                  this.agreements.removeIf(agreement -> agreement == agreementId);
            }
            return this;
      }

}
