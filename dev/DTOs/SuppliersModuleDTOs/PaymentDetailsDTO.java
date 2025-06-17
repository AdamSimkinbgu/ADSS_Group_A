package DTOs.SuppliersModuleDTOs;

import DTOs.SuppliersModuleDTOs.Enums.PaymentMethod;
import DTOs.SuppliersModuleDTOs.Enums.PaymentTerm;
import DomainLayer.SuppliersDomainSubModule.Classes.PaymentDetails;

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
      String acct = (bankAccountNumber != null)
            ? bankAccountNumber
            : "[no acct]";
      String pm = (paymentMethod != null)
            ? paymentMethod.name()
            : "[no pmethod]";
      String pt = (paymentTerm != null)
            ? paymentTerm.name()
            : "[no pterm]";

      return String.format(
            "PaymentDetails  Account: %-20s  Method: %-12s  Term: %-4s",
            acct,
            pm,
            pt);
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
