package DataAccessLayer.SuppliersDAL.Interfaces;

import java.util.List;
import java.util.Optional;

import DTOs.SuppliersModuleDTOs.AgreementDTO;;

public interface AgreementDAOInterface {
   AgreementDTO createAgreement(AgreementDTO agreement);

   Optional<AgreementDTO> getAgreementById(int id);

   boolean updateAgreement(AgreementDTO agreement);

   boolean deleteAgreement(int id);

   List<AgreementDTO> getAllAgreements();

   List<AgreementDTO> getAllAgreementsForSupplier(int supplierId);

   boolean agreementExists(int id);

}
