package DomainLayer;

import java.util.*;

import DomainLayer.Classes.Agreement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class AgreementFacade {
    private final Map<UUID, Agreement> agreements = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Create and store a new Agreement from JSON.
     *
     * @return generated agreementId
     */
    public void createAgreement(String json) {
        try {
            System.out.println("Creating Agreement with JSON: " + json);
            Agreement agr = mapper.readValue(json, Agreement.class);
            agreements.put(agr.getAgreementId(), agr);
        } catch (MismatchedInputException e) {
            System.err.println("JSON parse error: " + e.getOriginalMessage());
            System.err.println(" at: " + e.getPathReference());
            throw new RuntimeException("Agreement JSON parse failed", e);
        } catch (Exception e) {
            throw new RuntimeException("Agreement JSON parse failed", e);
        }
    }

    /**
     * Removes an existing Agreement by its UUID string.
     * 
     * @param id UUID string of the Agreement
     * @return true if removed successfully
     */
    public boolean removeAgreement(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return agreements.remove(uuid) != null;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Agreement ID format: " + id, e);
        }
    }

    /**
     * Updates an existing Agreement via JSON merge.
     * 
     * @param partialJson JSON with fields to update (must include agreementId)
     * @return updated Agreement instance
     */
    public Agreement updateAgreement(String partialJson) {
        try {
            // Deserialize to temp object to get ID
            Agreement temp = mapper.readValue(partialJson, Agreement.class);
            UUID id = temp.getAgreementId();
            Agreement existing = agreements.get(id);
            if (existing == null) {
                throw new RuntimeException("Agreement not found: " + id);
            }
            // Merge JSON fields into existing object
            mapper.readerForUpdating(existing).readValue(partialJson);
            return existing;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Agreement", e);
        }
    }

    /**
     * Retrieves an Agreement by its UUID string.
     */
    public Agreement getAgreement(String id) {
        UUID uuid = UUID.fromString(id);
        return agreements.get(uuid);
    }

    /**
     * Lists all Agreements.
     */
    public Map<UUID, Agreement> listAllAgreements() {
        return new HashMap<>(agreements);
    }
}
