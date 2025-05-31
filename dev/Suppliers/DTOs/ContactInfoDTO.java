package Suppliers.DTOs;

import java.util.List;

import Suppliers.DomainLayer.Classes.ContactInfo;

public class ContactInfoDTO {

      private String name;
      private String email;
      private String phone;

      public ContactInfoDTO(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
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
}
