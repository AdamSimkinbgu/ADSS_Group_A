package DTOs;

import DTOs.Enums.PaymentMethod;
import DTOs.Enums.PaymentTerm;
import DomainLayer.Classes.PaymentDetails;

public record PaymentDetailsDTO(String bankAccountNumber, PaymentMethod paymentMethod, PaymentTerm paymentTerm) {

   public static PaymentDetailsDTO fromPaymentDetails(PaymentDetails paymentDetails) {
      return new PaymentDetailsDTO(
            paymentDetails.getBankAccountNumber(),
            PaymentMethod.valueOf(paymentDetails.getPaymentMethod().name()),
            PaymentTerm.valueOf(paymentDetails.getPaymentTerm().name()));
   }

}
