package DomainLayer.Classes;

import java.io.Serializable;

import DTOs.PaymentDetailsDTO;
import DTOs.Enums.PaymentMethod;
import DTOs.Enums.PaymentTerm;

/**
 * All payment‐related settings for a supplier in one immutable type.
 */
public class PaymentDetails implements Serializable {

      private final String bankAccountNumber;
      private final PaymentMethod paymentMethod;
      private final PaymentTerm paymentTerm;

      public PaymentDetails(PaymentDetailsDTO paymentDetails) {
            this.bankAccountNumber = paymentDetails.bankAccountNumber();
            this.paymentMethod = paymentDetails.paymentMethod();
            this.paymentTerm = paymentDetails.paymentTerm();
      }

      public PaymentDetails(
                  String bankAccountNumber,
                  PaymentMethod paymentMethod,
                  PaymentTerm paymentTerm) {
            this.bankAccountNumber = bankAccountNumber;
            this.paymentMethod = paymentMethod;
            this.paymentTerm = paymentTerm;
      }

      public PaymentMethod getPaymentMethod() {
            return paymentMethod;
      }

      public PaymentTerm getPaymentTerm() {
            return paymentTerm;
      }

      public String getBankAccountNumber() {
            return bankAccountNumber;
      }

      public String toString() {
            return "PaymentDetails{" +
                        "bankAccountNumber='" + bankAccountNumber + '\'' +
                        ", paymentMethod=" + paymentMethod +
                        ", paymentTerm=" + paymentTerm +
                        '}';
      }
}
