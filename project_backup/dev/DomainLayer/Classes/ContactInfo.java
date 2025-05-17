package DomainLayer.Classes;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactInfo {
   private String name;
   private String email;
   private String phone;

   /** No-arg ctor for Jackson’s setter‐based binding or merging. */
   public ContactInfo() {
   }

   /**
    * Full‐arg constructor for Jackson’s @JsonCreator binding.
    */
   @JsonCreator
   public ContactInfo(
         @JsonProperty(value = "name", required = true) String name,
         @JsonProperty(value = "email", required = true) String email,
         @JsonProperty(value = "phone", required = true) String phone) {
      setName(name);
      setEmail(email);
      setPhone(phone);
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public void setPhone(String phone) {
      if (phone == null || phone.isBlank()) {
         throw new IllegalArgumentException("Contact phone must not be blank");
      }
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

   @Override
   public String toString() {
      return "ContactInfo{" +
            "name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof ContactInfo))
         return false;
      ContactInfo that = (ContactInfo) o;
      return name.equals(that.name)
            && email.equals(that.email)
            && phone.equals(that.phone);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, email, phone);
   }

}
