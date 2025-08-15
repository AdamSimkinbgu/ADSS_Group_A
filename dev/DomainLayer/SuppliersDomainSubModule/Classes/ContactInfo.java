package DomainLayer.SuppliersDomainSubModule.Classes;

import java.io.Serializable;
import java.util.Objects;

import DTOs.SuppliersModuleDTOs.ContactInfoDTO;

public class ContactInfo implements Serializable {
   private String name;
   private String email;
   private String phone;

   public ContactInfo(ContactInfoDTO contactInfo) {
      this.name = contactInfo.getName();
      this.email = contactInfo.getEmail();
      this.phone = contactInfo.getPhone();
   }

   public ContactInfo(
         String name,
         String email,
         String phone) {
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
      return "{" +
            "\"name\": \"" + name + "\"," +
            "\"email\": \"" + email + "\"," +
            "\"phone\": \"" + phone + "\"" +
            "}";
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
