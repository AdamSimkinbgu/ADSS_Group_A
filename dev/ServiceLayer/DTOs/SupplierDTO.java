package ServiceLayer.DTOs;

import java.util.List;

import DomainLayer.Classes.Address;
import DomainLayer.Classes.PaymentDetails;

public class SupplierDTO {
        private String name;
        private String taxNumber;
        private Address address;
        private PaymentDetails paymentDetail;

        public SupplierDTO(List<String> fields) {
                if (fields.size() != 8) {
                        throw new IllegalArgumentException("Invalid number of fields");
                }
                this.name = fields.get(0);
                this.taxNumber = fields.get(1);
                this.address = new Address(fields.get(2), fields.get(3), fields.get(4));
        }

        public String getName() {
                return name;
        }

        public String getTaxNumber() {
                return taxNumber;
        }

        public Address getAddress() {
                return address;
        }

        public PaymentDetails getPaymentDetail() {
                return paymentDetail;
        }

        public List<String> getFieldNames() {
                return List.of(Class.class.getDeclaredFields())
                                .stream()
                                .map(field -> field.getName())
                                .toList();
        }
}