package Suppliers.DTOs;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import Suppliers.DTOs.Enums.PaymentMethod;
import Suppliers.DTOs.Enums.PaymentTerm;
import Suppliers.DomainLayer.Classes.Address;
import Suppliers.DomainLayer.Classes.ContactInfo;
import Suppliers.DomainLayer.Classes.PaymentDetails;
import Suppliers.DomainLayer.Classes.Supplier;

public class SupplierDTO {
      private int id;
      private String name;
      private String taxNumber;
      private AddressDTO address;
      private boolean selfSupply;
      private EnumSet<DayOfWeek> supplyDays;
      private int leadSupplyDays;
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
                  EnumSet<DayOfWeek> supplyDays,
                  int leadSupplyDays,
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
            this.leadSupplyDays = leadSupplyDays;
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
                  EnumSet<DayOfWeek> supplyDays,
                  int leadSupplyDays,
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
            this.leadSupplyDays = leadSupplyDays;
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
            this.leadSupplyDays = supplier.getLeadSupplyDays();
            this.paymentDetails = PaymentDetailsDTO.fromPaymentDetails(supplier.getPaymentDetails());
            this.contacts = ContactInfoDTO.fromContactInfoList(supplier.getContacts());
            // to add the products we need to add them after the creation of the supplier
            this.products = new ArrayList<>();
            this.agreements = supplier.getAgreements();
      }

      public SupplierDTO(int id, String name, String taxNumber,
                  String street, String city, String buildingNumber,
                  boolean selfSupply, String supplyDaysMask, int leadSupplyDays,
                  String bankAccountNumber, String paymentMethod, String paymentTerm) {
            this.id = id;
            this.name = name;
            this.taxNumber = taxNumber;
            this.address = new AddressDTO(street, city, buildingNumber);
            this.selfSupply = selfSupply;
            this.supplyDays = EnumSet.noneOf(DayOfWeek.class);
            if (supplyDaysMask != null && supplyDaysMask.length() == 7) {
                  for (int i = 0; i < supplyDaysMask.length(); i++) {
                        if (supplyDaysMask.charAt(i) == '1') {
                              DayOfWeek day = DayOfWeek.of((i + 1) % 7 + 1); // shift to make Sunday the first day
                              this.supplyDays.add(day);
                        }
                  }
            }
            this.leadSupplyDays = 0; // Default value, can be set later
            this.paymentDetails = new PaymentDetailsDTO(bankAccountNumber, PaymentMethod.valueOf(paymentMethod),
                        PaymentTerm.valueOf(paymentTerm));
            this.contacts = new ArrayList<>();
            this.products = new ArrayList<>();
            this.agreements = new ArrayList<>();
      }

      public int getId() {
            return id;
      }

      public void setId(int id) {
            this.id = id;
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

      public List<ContactInfoDTO> getContactsInfoDTOList() {
            return contacts;
      }

      public List<ContactInfo> getContactsInfoList() {
            return ContactInfoDTO.toContactInfoList(contacts);
      }

      public void setContacts(List<ContactInfoDTO> contacts) {
            this.contacts = contacts;
      }

      public List<SupplierProductDTO> getProducts() {
            return products;
      }

      public List<Integer> getProductIDs() {
            List<Integer> productIds = new ArrayList<>();
            for (SupplierProductDTO product : products) {
                  productIds.add(product.getProductId());
            }
            return productIds;
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

      public EnumSet<DayOfWeek> getSupplyDays() {
            return supplyDays;
      }

      public void setSelfSupply(boolean selfSupply) {
            this.selfSupply = selfSupply;
      }

      public void setSupplyDays(EnumSet<DayOfWeek> supplyDays) {
            this.supplyDays = supplyDays;
      }

      public SupplierDTO addAgreement(int agreementId) {
            if (this.agreements == null) {
                  this.agreements = new ArrayList<>();
            }
            this.agreements.add(agreementId);
            return this;
      }

      public SupplierDTO removeAgreement(int agreementId) {
            if (this.agreements != null) {
                  this.agreements.removeIf(agreement -> agreement == agreementId);
            }
            return this;
      }

      public int getLeadSupplyDays() {
            return leadSupplyDays;
      }

      public void setLeadSupplyDays(int leadSupplyDays) {
            this.leadSupplyDays = leadSupplyDays;
      }

      public String getSupplyDaysMask() {
            // convert EnumSet<DayOfWeek> to a string mask
            // 1 is monday but we need it to be sunday
            StringBuilder mask = new StringBuilder("0000000");
            for (DayOfWeek day : supplyDays) {
                  int index = (day.ordinal() + 1) % 7; // shift to make Sunday the first day
                  mask.setCharAt(index, '1');
            }
            return mask.toString();
      }

}
