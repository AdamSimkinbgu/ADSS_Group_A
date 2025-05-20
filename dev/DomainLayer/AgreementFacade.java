package DomainLayer;

import java.util.*;

import DomainLayer.Classes.Agreement;

public class AgreementFacade extends BaseFacade {
    private final Map<UUID, Agreement> agreements = new HashMap<>();

    public UUID createAgreement(String json) {
        return null; // TODO: Implement this method
    }

    public boolean removeAgreement(String id) {
        return false; // TODO: Implement this method
    }

    public boolean updateAgreement(Agreement updated) {
        return false; // TODO: Implement this method
    }

    public Agreement getAgreement(String id) {
        return null; // TODO: Implement this method
    }

    public List<Agreement> getAgreementsWithFullDetail() {
        return new ArrayList<>(agreements.values());
    }

    public Agreement getAgreementById(String lookupJson) {
        return null; // TODO: Implement this method
    }
}
