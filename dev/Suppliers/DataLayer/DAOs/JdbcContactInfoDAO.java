package Suppliers.DataLayer.DAOs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.ContactInfoDTO;
import Suppliers.DataLayer.Interfaces.ContactInfoDAOInterface;
import Suppliers.DataLayer.util.Database;

public class JdbcContactInfoDAO extends BaseDAO implements ContactInfoDAOInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(JdbcContactInfoDAO.class);

   @Override
   public ContactInfoDTO createContactInfo(ContactInfoDTO contactInfo) {
      if (contactInfo == null) {
         LOGGER.error("ContactInfoDTO is null");
         throw new IllegalArgumentException("ContactInfoDTO cannot be null");
      }
      if (contactInfo.getSupplierId() <= 0) {
         LOGGER.error("Invalid supplier ID: {}", contactInfo.getSupplierId());
         throw new IllegalArgumentException("Invalid supplier ID");
      }
      LOGGER.info("Creating contact info for supplier ID: {}", contactInfo.getSupplierId());
      String sql = "INSERT INTO contact_info (supplier_id, name, email, phone) VALUES (?, ?, ?, ?)";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, contactInfo.getSupplierId());
         preparedStatement.setString(2, contactInfo.getName());
         preparedStatement.setString(3, contactInfo.getEmail());
         preparedStatement.setString(4, contactInfo.getPhone());
         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected > 0) {
            LOGGER.info("Contact info created successfully for supplier ID: {}", contactInfo.getSupplierId());
            return contactInfo;
         } else {
            LOGGER.warn("No rows affected when creating contact info for supplier ID: {}", contactInfo.getSupplierId());
            throw new SQLException("Failed to create contact info, no rows affected");
         }
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      return null;
   }

   @Override
   public boolean updateContactInfo(ContactInfoDTO contactInfo) {

      if (contactInfo == null) {
         LOGGER.error("ContactInfoDTO is null");
         throw new IllegalArgumentException("ContactInfoDTO cannot be null");
      }
      if (contactInfo.getSupplierId() <= 0) {
         LOGGER.error("Invalid supplier ID: {}", contactInfo.getSupplierId());
         throw new IllegalArgumentException("Invalid supplier ID");
      }
      LOGGER.info("Updating contact info for supplier ID: {}", contactInfo.getSupplierId());
      String sql = "UPDATE contact_info SET name = ?, email = ?, phone = ? WHERE supplier_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setString(1, contactInfo.getName());
         preparedStatement.setString(2, contactInfo.getEmail());
         preparedStatement.setString(3, contactInfo.getPhone());
         preparedStatement.setInt(4, contactInfo.getSupplierId());
         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected > 0) {
            LOGGER.info("Contact info updated successfully for supplier ID: {}", contactInfo.getSupplierId());
            return true;
         } else {
            LOGGER.warn("No rows affected when updating contact info for supplier ID: {}", contactInfo.getSupplierId());
            return false;
         }
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      return false;
   }

   @Override
   public boolean deleteContactInfo(int supplierId, String name) {

      if (supplierId <= 0) {
         LOGGER.error("Invalid contact info ID: {}", supplierId);
         throw new IllegalArgumentException("Invalid contact info ID");
      }
      LOGGER.info("Deleting contact info with ID: {}", supplierId);
      String sql = "DELETE FROM contact_info WHERE supplier_id = ? AND name = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, supplierId);
         preparedStatement.setString(2, name);
         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected > 0) {
            LOGGER.info("Contact info deleted successfully for supplier ID: {}", supplierId);
            return true;
         } else {
            LOGGER.warn("No rows affected when deleting contact info for supplier ID: {}", supplierId);
            return false;
         }
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      return false;
   }

   @Override
   public List<ContactInfoDTO> getContactInfosBySupplierId(int supplierId) {
      if (supplierId <= 0) {
         LOGGER.error("Invalid supplier ID: {}", supplierId);
         throw new IllegalArgumentException("Invalid supplier ID");
      }
      LOGGER.info("Retrieving contact info for supplier ID: {}", supplierId);
      String sql = "SELECT * FROM contact_info WHERE supplier_id = ?";
      List<ContactInfoDTO> contactInfos = new ArrayList<>();
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, supplierId);
         ResultSet resultSet = preparedStatement.executeQuery();
         while (resultSet.next()) {
            ContactInfoDTO contactInfo = new ContactInfoDTO();
            contactInfo.setSupplierId(resultSet.getInt("supplier_id"));
            contactInfo.setName(resultSet.getString("name"));
            contactInfo.setEmail(resultSet.getString("email"));
            contactInfo.setPhone(resultSet.getString("phone"));
            contactInfos.add(contactInfo);
         }
         LOGGER.info("Retrieved {} contact info entries for supplier ID: {}", contactInfos.size(), supplierId);
         return contactInfos;
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      LOGGER.warn("No contact info found for supplier ID: {}", supplierId);
      return new ArrayList<>();
   }

}
