package DomainLayer;

import java.util.*;

import DomainLayer.Classes.Agreement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class AgreementFacade extends BaseFacade {
    private final Map<UUID, Agreement> agreements = new HashMap<>();

    public UUID createAgreement(String json) {
        try {
            Agreement agr = mapper.readValue(json, Agreement.class);
            agreements.put(agr.getAgreementId(), agr);
            return agr.getAgreementId();
        } catch (MismatchedInputException e) {
            System.err.println("JSON parse error: " + e.getOriginalMessage());
            System.err.println("    at: " + e.getPathReference());
            e.printStackTrace();
            System.err.println("Offending JSON: " + json);
            throw new RuntimeException("Agreement JSON parse failed", e);

        } catch (JsonProcessingException e) {
            System.err.println("Jackson processing error: " + e.getOriginalMessage());
            e.printStackTrace();
            System.err.println("Offending JSON: " + json);
            throw new RuntimeException("Agreement JSON processing failed", e);

        } catch (Exception e) {
            System.err.println("Unexpected error in createAgreement: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Agreement creation failed", e);
        }
    }

    public boolean removeAgreement(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return agreements.remove(uuid) != null;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Agreement ID format: " + id, e);
        }
    }

    public boolean updateAgreement(Agreement updated) {
        UUID id = updated.getAgreementId();
        if (!agreements.containsKey(id)) {
            return false;
        }
        agreements.put(id, updated);
        return true;
    }

    public Agreement getAgreement(String id) {
        UUID uuid = UUID.fromString(id);
        return agreements.get(uuid);
    }

    public List<Agreement> getAgreementsWithFullDetail() {
        return new ArrayList<>(agreements.values());
    }

    public Agreement getAgreementById(String lookupJson) {
        try {
            JsonNode root = mapper.readTree(lookupJson);
            UUID id = UUID.fromString(root.get("agreementId").asText());
            return agreements.get(id);
        } catch (Exception e) {
            return null;
        }
    }
}
