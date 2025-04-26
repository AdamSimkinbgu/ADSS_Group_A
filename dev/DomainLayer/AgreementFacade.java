package DomainLayer;

import java.util.*;

import DomainLayer.Classes.Agreement;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AgreementFacade {
    private final Map<UUID, Agreement> suppliers = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();

    /**
     *
     * @param json
     * @return
     */
    public  boolean addAgreement(String json) {
        try {
            Agreement agreement = mapper.readValue(json , Agreement.class);
            return  true ;
        } catch (com.fasterxml.jackson.databind.exc.MismatchedInputException e) {
            // this will show you exactly which JSON field was unexpected or
            // couldn’t map to the constructor
            System.err.println("JSON parse error: " + e.getOriginalMessage());
            System.err.println(" at: " + e.getPathReference());
            e.printStackTrace();
            throw new RuntimeException("Supplier JSON parse failed", e);
        } catch (Exception e) {
            // e.printStackTrace();
            throw new RuntimeException("Supplier JSON parse failed", e);
        }
    }



}
