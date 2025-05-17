package DomainLayer.Classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {
      private String street;
      private String city;
      private String buildingNumber;

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
