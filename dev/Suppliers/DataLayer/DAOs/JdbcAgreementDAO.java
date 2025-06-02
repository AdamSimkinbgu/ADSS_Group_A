package Suppliers.DataLayer.DAOs;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.DataLayer.Interfaces.AgreementDAOInterface;
import Suppliers.DataLayer.util.Database;

public class JdbcAgreementDAO implements AgreementDAOInterface {
   private final Logger LOGGER = LoggerFactory.getLogger(JdbcAgreementDAO.class);
   private final static JdbcBillofQuantitiesItemsDAO billofQuantitiesItemsDAO = new JdbcBillofQuantitiesItemsDAO();

   @Override
   public AgreementDTO createAgreement(AgreementDTO agreement) throws SQLException {
      // we need to also insert the bill of quantities items
      if (agreement == null || agreement.getSupplierId() < 0) {
         LOGGER.error("Invalid agreement data: {}", agreement);
         throw new IllegalArgumentException("Invalid agreement data");
      }
      String sql = "INSERT INTO agreements (supplier_id, agreement_start_date, "
            + "agreement_end_date, valid) VALUES (?, ?, ?, ?)";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         pstmt.setInt(1, agreement.getSupplierId());
         pstmt.setString(2, Date.valueOf(agreement.getAgreementStartDate()).toString());
         pstmt.setString(3, Date.valueOf(agreement.getAgreementEndDate()).toString());
         pstmt.setBoolean(4, agreement.isValid());

         int affectedRows = pstmt.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Creating agreement failed, no rows affected.");
            throw new SQLException("Creating agreement failed, no rows affected.");
         }

         try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               int id = generatedKeys.getInt(1);
               agreement.setAgreementId(id);
               LOGGER.info("Created agreement with ID: {}", id);
            } else {
               LOGGER.error("Creating agreement failed, no ID obtained.");
               throw new SQLException("Creating agreement failed, no ID obtained.");
            }
         }

         // Insert bill of quantities items
         for (BillofQuantitiesItemDTO item : agreement.getBillOfQuantitiesItems()) {
            item.setAgreementId(agreement.getAgreementId());
            billofQuantitiesItemsDAO.createBillofQuantitiesItem(item);
         }
         LOGGER.info("Inserted {} bill of quantities items for agreement ID: {}",
               agreement.getBillOfQuantitiesItems().size(), agreement.getAgreementId());

      }
      return agreement;
   }

   @Override
   public Optional<AgreementDTO> getAgreementById(int id) throws SQLException {
      if (id < 0) {
         LOGGER.error("Invalid agreement ID: {}", id);
         throw new IllegalArgumentException("Invalid agreement ID");
      }
      String sql = "SELECT * FROM agreements WHERE agreement_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, id);
         try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
               AgreementDTO agreement = new AgreementDTO();
               agreement.setAgreementId(rs.getInt("agreement_id"));
               agreement.setSupplierId(rs.getInt("supplier_id"));
               agreement.setAgreementStartDate(LocalDate.parse(rs.getString("agreement_start_date")));
               agreement.setAgreementEndDate(LocalDate.parse(rs.getString("agreement_end_date")));
               agreement.setValid(rs.getBoolean("valid"));

               // Load bill of quantities items
               List<BillofQuantitiesItemDTO> items = billofQuantitiesItemsDAO.getAllBillofQuantitiesItems(id);
               agreement.setBillOfQuantitiesItems(items);

               LOGGER.info("Retrieved agreement with ID: {}", id);
               return Optional.of(agreement);
            } else {
               LOGGER.warn("No agreement found with ID: {}", id);
               return Optional.empty();
            }
         }
      }
   }

   @Override
   public void updateAgreement(AgreementDTO agreement) throws SQLException {
      if (agreement == null || agreement.getAgreementId() < 0) {
         LOGGER.error("Invalid agreement data: {}", agreement);
         throw new IllegalArgumentException("Invalid agreement data");
      }
      String sql = "UPDATE agreements SET supplier_id = ?, agreement_start_date = ?, "
            + "agreement_end_date = ?, valid = ? WHERE agreement_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, agreement.getSupplierId());
         pstmt.setDate(2, Date.valueOf(agreement.getAgreementStartDate()));
         pstmt.setDate(3, Date.valueOf(agreement.getAgreementEndDate()));
         pstmt.setBoolean(4, agreement.isValid());
         pstmt.setInt(5, agreement.getAgreementId());

         int affectedRows = pstmt.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.warn("No agreement found with ID: {}", agreement.getAgreementId());
            throw new SQLException("No agreement found with ID: " + agreement.getAgreementId());
         } else {
            LOGGER.info("Updated agreement with ID: {}", agreement.getAgreementId());
         }

         // Update bill of quantities items
         for (BillofQuantitiesItemDTO item : agreement.getBillOfQuantitiesItems()) {
            item.setAgreementId(agreement.getAgreementId());
            billofQuantitiesItemsDAO.updateBillofQuantitiesItem(item);
         }
      }
   }

   @Override
   public void deleteAgreement(int id) throws SQLException {
      if (id < 0) {
         LOGGER.error("Invalid agreement ID: {}", id);
         throw new IllegalArgumentException("Invalid agreement ID");
      }
      String sql = "DELETE FROM agreements WHERE agreement_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, id);
         int affectedRows = pstmt.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.warn("No agreement found with ID: {}", id);
            throw new SQLException("No agreement found with ID: " + id);
         } else {
            LOGGER.info("Deleted agreement with ID: {}", id);
         }
      }
   }

   @Override
   public List<AgreementDTO> getAllAgreements() throws SQLException {
      String sql = "SELECT * FROM agreements";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
         List<AgreementDTO> agreements = new ArrayList<>();
         while (rs.next()) {
            AgreementDTO agreement = new AgreementDTO();
            agreement.setAgreementId(rs.getInt("agreement_id"));
            agreement.setSupplierId(rs.getInt("supplier_id"));
            agreement.setAgreementStartDate(rs.getDate("agreement_start_date").toLocalDate());
            agreement.setAgreementEndDate(rs.getDate("agreement_end_date").toLocalDate());
            agreement.setValid(rs.getBoolean("valid"));

            // Load bill of quantities items
            List<BillofQuantitiesItemDTO> items = billofQuantitiesItemsDAO
                  .getAllBillofQuantitiesItems(agreement.getAgreementId());
            agreement.setBillOfQuantitiesItems(items);

            agreements.add(agreement);
         }
         LOGGER.info("Retrieved {} agreements", agreements.size());
         return agreements;
      }
   }

   @Override
   public List<AgreementDTO> getAllAgreementsForSupplier(int supplierId) throws SQLException {
      if (supplierId < 0) {
         LOGGER.error("Invalid supplier ID: {}", supplierId);
         throw new IllegalArgumentException("Invalid supplier ID");
      }
      String sql = "SELECT * FROM agreements WHERE supplier_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, supplierId);
         try (ResultSet rs = pstmt.executeQuery()) {
            List<AgreementDTO> agreements = new ArrayList<>();
            while (rs.next()) {
               AgreementDTO agreement = new AgreementDTO();
               agreement.setAgreementId(rs.getInt("agreement_id"));
               agreement.setSupplierId(rs.getInt("supplier_id"));
               agreement.setAgreementStartDate(rs.getDate("agreement_start_date").toLocalDate());
               agreement.setAgreementEndDate(rs.getDate("agreement_end_date").toLocalDate());
               agreement.setValid(rs.getBoolean("valid"));

               // Load bill of quantities items
               List<BillofQuantitiesItemDTO> items = billofQuantitiesItemsDAO
                     .getAllBillofQuantitiesItems(agreement.getAgreementId());
               agreement.setBillOfQuantitiesItems(items);

               agreements.add(agreement);
            }
            LOGGER.info("Retrieved {} agreements for supplier ID: {}", agreements.size(), supplierId);
            return agreements;
         }
      }
   }

   @Override
   public boolean agreementExists(int id) throws SQLException {
      if (id < 0) {
         LOGGER.error("Invalid agreement ID: {}", id);
         throw new IllegalArgumentException("Invalid agreement ID");
      }
      String sql = "SELECT 1 FROM agreements WHERE agreement_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, id);
         try (ResultSet rs = pstmt.executeQuery()) {
            boolean exists = rs.next();
            LOGGER.info("Agreement with ID: {} exists: {}", id, exists);
            return exists;
         }
      }
   }

   @Override
   public List<BillofQuantitiesItemDTO> getBillOfQuantitiesItemsForAgreement(int agreementId) throws SQLException {
      if (agreementId < 0) {
         LOGGER.error("Invalid agreement ID: {}", agreementId);
         throw new IllegalArgumentException("Invalid agreement ID");
      }
      // Fetch all bill of quantities items for the given agreement ID
      return billofQuantitiesItemsDAO.getAllBillofQuantitiesItems(agreementId);
   }
}