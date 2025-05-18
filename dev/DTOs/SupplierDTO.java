package DTOs;

import java.util.List;

public record SupplierDTO(String name, String taxNumber, AddressDTO address, List<ContactInfoDTO> contactInfo,
            PaymentDetailsDTO paymentDetails) {

}
