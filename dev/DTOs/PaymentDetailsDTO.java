package DTOs;

import DTOs.Enums.PaymentMethod;
import DTOs.Enums.PaymentTerm;

public record PaymentDetailsDTO(String bankAccountNumber, PaymentMethod paymentMethod, PaymentTerm paymentTerm) {

}
