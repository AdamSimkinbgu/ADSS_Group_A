package DomainLayer;

import DTOs.*;

public class AgreementSupplierController {

   private final AgreementFacade agreementFacade;
   private final SupplierFacade supplierFacade;

   public AgreementSupplierController(AgreementFacade agreementFacade, SupplierFacade supplierFacade) {
      this.agreementFacade = agreementFacade;
      this.supplierFacade = supplierFacade;
   }

   // Add methods to handle agreements and suppliers
   // For example, createAgreement, updateAgreement, removeAgreement, etc.
   // these methods need to interact with the AgreementFacade and SupplierFacade to
   // complete the functionality
   public AgreementDTO createAgreement(AgreementDTO agreementDTO) {
      if (agreementDTO == null) {
         throw new IllegalArgumentException("AgreementDTO cannot be null");
      }
      try {
         AgreementDTO createdAgreement = agreementFacade.createAgreement(agreementDTO);
         supplierFacade.addAgreementToSupplier(createdAgreement.getSupplierId(), createdAgreement.getAgreementId());
         return createdAgreement;
      } catch (Exception e) {
         throw new RuntimeException("Failed to create agreement: " + e.getMessage(), e);
      }
   }

}
