package DomainLayer;

import java.util.*;

import DTOs.AgreementDTO;
import DomainLayer.Classes.Agreement;

public class AgreementFacade extends BaseFacade {
    private final Map<Integer, Agreement> agreements = new HashMap<>();

    public AgreementDTO createAgreement(AgreementDTO agreementDTO) {
        Agreement agreement = new Agreement(agreementDTO);
        agreementDTO.setAgreementId(agreement.getAgreementId());
        agreements.put(agreement.getAgreementId(), agreement);
        return agreementDTO;
    }

    public boolean removeAgreement(int agreementID) {
        return false; // TODO: Implement this method
    }

    public boolean updateAgreement(AgreementDTO updated) {
        return false; // TODO: Implement this method
    }

    public Agreement getAgreement(int agreementID) {
        return null; // TODO: Implement this method
    }

    public List<AgreementDTO> getAgreementsWithFullDetail() {
        return null; // TODO: Implement this method
    }

    public AgreementDTO getAgreementById(String lookupJson) {
        return null; // TODO: Implement this method
    }
}
