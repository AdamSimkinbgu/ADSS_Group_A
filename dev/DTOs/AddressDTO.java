package DTOs;

import DomainLayer.Classes.Address;

public class AddressDTO {
   private String buildingNumber;
   private String street;
   private String city;

   public AddressDTO(String street, String city, String buildingNumber) {
      this.street = street;
      this.city = city;
      this.buildingNumber = buildingNumber;
   }

   public String getBuildingNumber() {
      return buildingNumber;
   }

   public void setBuildingNumber(String buildingNumber) {
      this.buildingNumber = buildingNumber;
   }

   public String getStreet() {
      return street;
   }

   public void setStreet(String street) {
      this.street = street;
   }

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   @Override
   public String toString() {
      return String.format(
            "{\n" +
                  "  \"street\": \"%s\",\n" +
                  "  \"city\": \"%s\",\n" +
                  "  \"buildingNumber\": \"%s\"\n" +
                  "}",
            street, city, buildingNumber);
   }

   public static Address toAddress(AddressDTO addressDTO) {
      return new Address(
            addressDTO.getStreet(),
            addressDTO.getCity(),
            addressDTO.getBuildingNumber());
   }

   public static AddressDTO fromAddress(Address address) {
      return new AddressDTO(
            address.getStreet(),
            address.getCity(),
            address.getBuildingNumber());
   }

}
