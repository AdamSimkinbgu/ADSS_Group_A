package DTOs.SuppliersModuleDTOs;

public class AddressDTO {
   private String buildingNumber;
   private String street;
   private String city;

   public AddressDTO(String street, String city, String buildingNumber) {
      this.street = street;
      this.city = city;
      this.buildingNumber = buildingNumber;
   }

   public AddressDTO(AddressDTO address) {
      this.street = address.getStreet();
      this.city = address.getCity();
      this.buildingNumber = address.getBuildingNumber();
   }

   public AddressDTO() {
      // make it funny and nerdy
      this.street = "1234 Elm Street";
      this.city = "Springfield";
      this.buildingNumber = "#1234";

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
      // Example: “#1234, Elm Street, Springfield”
      String bldg = (buildingNumber != null) ? buildingNumber : "";
      String str = (street != null) ? street : "";
      String cty = (city != null) ? city : "";
      return String.format("%s %s, %s", bldg, str, cty).trim();
   }

   public static AddressDTO toAddress(AddressDTO addressDTO) {
      return new AddressDTO(
            addressDTO.getStreet(),
            addressDTO.getCity(),
            addressDTO.getBuildingNumber());
   }

   public static AddressDTO fromAddress(AddressDTO address) {
      return new AddressDTO(
            address.getStreet(),
            address.getCity(),
            address.getBuildingNumber());
   }

}
