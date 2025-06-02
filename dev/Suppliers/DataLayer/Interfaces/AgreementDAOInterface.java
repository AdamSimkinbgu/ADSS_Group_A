package Suppliers.DataLayer.Interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;

public interface AgreementDAOInterface {
   AgreementDTO createAgreement(AgreementDTO agreement) throws SQLException;

   Optional<AgreementDTO> getAgreementById(int id) throws SQLException;

   void updateAgreement(AgreementDTO agreement) throws SQLException;

   void deleteAgreement(int id) throws SQLException;

   List<AgreementDTO> getAllAgreements() throws SQLException;

   List<AgreementDTO> getAllAgreementsForSupplier(int supplierId) throws SQLException;

   List<BillofQuantitiesItemDTO> getBillOfQuantitiesItemsForAgreement(int agreementId) throws SQLException;

   boolean agreementExists(int id) throws SQLException;
}
