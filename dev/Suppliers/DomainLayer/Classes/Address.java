package Suppliers.DomainLayer.Classes;

import java.io.Serializable;

import Suppliers.DTOs.AddressDTO;

public class Address implements Serializable {
      private String street;
      private String city;
      private String buildingNumber;

      public Address(AddressDTO address) {
            this.buildingNumber = address.getBuildingNumber();
            this.street = address.getStreet();
            this.city = address.getCity();
      }

      public Address(String street, String city, String buildingNumber) {
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
