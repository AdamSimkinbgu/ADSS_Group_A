package Suppliers.DTOs;

import java.util.List;

import Suppliers.DomainLayer.Classes.ContactInfo;

public class ContactInfoDTO {

      private int supplierId;
      private String name;
      private String email;
      private String phone;

      public ContactInfoDTO() {
      }

      public ContactInfoDTO(int supplierId, String name, String email, String phone) {
            this.supplierId = supplierId;
            this.name = name;
            this.email = email;
            this.phone = phone;
      }

      public ContactInfoDTO(String name, String email, String phone) {
            this.supplierId = -1; // Default value, can be set later if needed
            this.name = name;
            this.email = email;
            this.phone = phone;
      }

      public int getSupplierId() {
            return supplierId;
      }

      public String getName() {
            return name;
      }

      public String getEmail() {
            return email;
      }

      public String getPhone() {
            return phone;
      }

      public void setSupplierId(int supplierId) {
            this.supplierId = supplierId;
      }

      public void setName(String name) {
            this.name = name;
      }

      public void setEmail(String email) {
            this.email = email;
      }

      public void setPhone(String phone) {
            this.phone = phone;
      }

      @Override
      public String toString() {
            return String.format(
                        "{\n" +
                                    "  \"name\": \"%s\",\n" +
                                    "  \"email\": \"%s\",\n" +
                                    "  \"phone\": \"%s\"\n" +
                                    "}",
                        name,
                        email,
                        phone);
      }

      public static List<ContactInfoDTO> fromContactInfoList(List<ContactInfo> contacts) {
            return contacts.stream()
                        .map(contact -> new ContactInfoDTO(
                                    contact.getName(),
                                    contact.getEmail(),
                                    contact.getPhone()))
                        .toList();
      }

      public static List<ContactInfo> toContactInfoList(List<ContactInfoDTO> contacts) {
            return contacts.stream()
                        .map(contact -> new ContactInfo(
                                    contact.getName(),
                                    contact.getEmail(),
                                    contact.getPhone()))
                        .toList();
      }

      @Override
      public boolean equals(Object o) {
            if (this == o)
                  return true;
            if (!(o instanceof ContactInfoDTO))
                  return false;
            ContactInfoDTO that = (ContactInfoDTO) o;
            return supplierId == that.supplierId &&
                        name.equals(that.name) &&
                        email.equals(that.email) &&
                        phone.equals(that.phone);
      }
}
