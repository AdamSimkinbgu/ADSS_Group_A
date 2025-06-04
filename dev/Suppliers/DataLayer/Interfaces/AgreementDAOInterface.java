package Suppliers.DataLayer.Interfaces;

import java.util.List;
import java.util.Optional;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;

public interface AgreementDAOInterface {
   AgreementDTO createAgreement(AgreementDTO agreement);

   Optional<AgreementDTO> getAgreementById(int id);

   boolean updateAgreement(AgreementDTO agreement);

   boolean deleteAgreement(int id);

   List<AgreementDTO> getAllAgreements();

   List<AgreementDTO> getAllAgreementsForSupplier(int supplierId);

   List<BillofQuantitiesItemDTO> getBillOfQuantitiesItemsForAgreement(int agreementId);

   boolean agreementExists(int id);
}
