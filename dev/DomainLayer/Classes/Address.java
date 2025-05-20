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

      @Override
      public boolean equals(Object o) {
            if (this == o)
                  return true;
            if (!(o instanceof Address))
                  return false;

            Address address = (Address) o;

            if (!street.equals(address.street))
                  return false;
            if (!city.equals(address.city))
                  return false;
            return buildingNumber.equals(address.buildingNumber);
      }

      @Override
      public String toString() {
            return "{" +
                        "\"street\": \"" + street + "\"," +
                        "\"city\": \"" + city + "\"," +
                        "\"buildingNumber\": \"" + buildingNumber + "\"" +
                        "}";
      }

}
