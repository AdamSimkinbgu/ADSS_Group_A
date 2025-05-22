package DomainLayer;

import java.util.*;

import DTOs.AgreementDTO;
import DTOs.BillofQuantitiesItemDTO;
import DomainLayer.Classes.Agreement;

public class AgreementFacade extends BaseFacade {
    private final Map<Integer, List<Agreement>> agreements = new HashMap<>();

    public AgreementDTO createAgreement(AgreementDTO agreementDTO) {
        Agreement agreement = new Agreement(agreementDTO);
        agreementDTO.setAgreementId(agreement.getAgreementId());
        agreements
                .computeIfAbsent(agreement.getSupplierId(), k -> new ArrayList<>())
                .add(agreement);
        return agreementDTO;
    }

    public boolean removeAgreement(int agreementID) {
        for (List<Agreement> supplierAgreements : agreements.values()) {
            for (Iterator<Agreement> iterator = supplierAgreements.iterator(); iterator.hasNext();) {
                Agreement agreement = iterator.next();
                if (agreement.getAgreementId() == agreementID) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false; // Agreement not found
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

    public List<AgreementDTO> getAgreementsBySupplierId(int supplierId) {
        List<Agreement> supplierAgreements = agreements.get(supplierId);
        if (supplierAgreements == null) {
            return Collections.emptyList();
        }
        List<AgreementDTO> agreementDTOs = new ArrayList<>();
        for (Agreement agreement : supplierAgreements) {
            AgreementDTO dto = new AgreementDTO(
                    agreement.getSupplierId(),
                    agreement.getSupplierName(),
                    agreement.getAgreementStartDate(),
                    agreement.getAgreementEndDate(),
                    agreement.hasFixedSupplyDays(),
                    agreement.getBillOfQuantitiesItems().stream()
                            .map(BillofQuantitiesItemDTO::new)
                            .toList() // Convert to List<BillofQuantitiesItemDTO>
            );
            dto.setAgreementId(agreement.getAgreementId());
            agreementDTOs.add(dto);
        }
        return agreementDTOs;
    }
}
