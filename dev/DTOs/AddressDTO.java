package DTOs;

import DomainLayer.Classes.Address;

public record AddressDTO(String street, String city, String buildingNumber) {

   public static AddressDTO fromAddress(Address address) {
      return new AddressDTO(
            address.getStreet(),
            address.getCity(),
            address.getBuildingNumber());
   }

}
