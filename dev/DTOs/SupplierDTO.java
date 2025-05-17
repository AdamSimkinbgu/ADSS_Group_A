package DTOs;

public record SupplierDTO(String name, String taxNumber, AddressDTO address, ContactInfoDTO contactInfo,
      PaymentDetailsDTO paymentDetails) {

}
