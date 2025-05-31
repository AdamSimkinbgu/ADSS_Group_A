package DTOs;

import DTOs.Enums.PaymentMethod;
import DTOs.Enums.PaymentTerm;
import DomainLayer.Classes.PaymentDetails;

public class PaymentDetailsDTO {

   private String bankAccountNumber;
   private PaymentMethod paymentMethod;
   private PaymentTerm paymentTerm;

   public PaymentDetailsDTO(String bankAccountNumber, PaymentMethod paymentMethod, PaymentTerm paymentTerm) {
      this.bankAccountNumber = bankAccountNumber;
      this.paymentMethod = paymentMethod;
      this.paymentTerm = paymentTerm;
   }

   public String getBankAccountNumber() {
      return bankAccountNumber;
   }

   public void setBankAccountNumber(String bankAccountNumber) {
      this.bankAccountNumber = bankAccountNumber;
   }

   public PaymentMethod getPaymentMethod() {
      return paymentMethod;
   }

   public void setPaymentMethod(PaymentMethod paymentMethod) {
      this.paymentMethod = paymentMethod;
   }

   public PaymentTerm getPaymentTerm() {
      return paymentTerm;
   }

   public void setPaymentTerm(PaymentTerm paymentTerm) {
      this.paymentTerm = paymentTerm;
   }

   @Override
   public String toString() {
      return String.format(
            "{\n" +
                  "  \"bankAccountNumber\": \"%s\",\n" +
                  "  \"paymentMethod\": \"%s\",\n" +
                  "  \"paymentTerm\": \"%s\"\n" +
                  "}",
            bankAccountNumber, paymentMethod, paymentTerm);
   }


   public static PaymentDetailsDTO fromPaymentDetails(PaymentDetails paymentDetails) {
      return new PaymentDetailsDTO(
            paymentDetails.getBankAccountNumber(),
            PaymentMethod.valueOf(paymentDetails.getPaymentMethod().name()),
            PaymentTerm.valueOf(paymentDetails.getPaymentTerm().name()));
   }

   public static PaymentDetailsDTO toPaymentDetailsDTO(String bankAccountNumber, PaymentMethod paymentMethod,
         PaymentTerm paymentTerm) {
      return new PaymentDetailsDTO(bankAccountNumber, paymentMethod, paymentTerm);
   }

}
