package DomainLayer.Classes;

import DomainLayer.AgreementFacade;
import DomainLayer.SupplierFacade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class SupplierTest {
   private SupplierFacade supplierFacade;
   private AgreementFacade agreementFacade;

   @BeforeEach
   void setUp() {
      supplierFacade = new SupplierFacade(false, "");
      agreementFacade = new AgreementFacade();
   }

   @AfterEach
   void tearDown() {
      supplierFacade = null;
      agreementFacade = null;
   }

}
