package DomainLayer.Classes;

import java.io.Serializable;

import DTOs.AddressDTO;

public class Address implements Serializable {
      private String street;
      private String city;
      private String buildingNumber;

      public Address(AddressDTO address) {
            this.street = address.street();
            this.city = address.city();
            this.buildingNumber = address.buildingNumber();
      }

      public Address(String street, String city, String buildingNumber) {
            this.street = street;
            this.city = city;
            this.buildingNumber = buildingNumber;
      }

      public String getStreet() {
            return street;
      }

      public String getCity() {
            return city;
      }

      public String getBuildingNumber() {
            return buildingNumber;
      }

      public String toString() {
            return "Address{" +
                        "street='" + street + '\'' +
                        ", city='" + city + '\'' +
                        ", buildingNumber='" + buildingNumber + '\'' +
                        '}';
      }

}
