package DTOs;

import java.util.List;

import DomainLayer.Classes.ContactInfo;

public record ContactInfoDTO(String name, String email, String phone) {

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
                                    contact.name(),
                                    contact.email(),
                                    contact.phone()))
                        .toList();
      }
}
