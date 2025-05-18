package DomainLayer.Classes;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import DomainLayer.Enums.PaymentMethod;
import DomainLayer.Enums.PaymentTerm;

/**
 * All payment‐related settings for a supplier in one immutable type.
 */
public class PaymentDetails implements Serializable {

      private final String bankAccountNumber;
      private final PaymentMethod paymentMethod;
      private final PaymentTerm paymentTerm;

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
