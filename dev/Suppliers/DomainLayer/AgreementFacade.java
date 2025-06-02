package Suppliers.DomainLayer;

import java.util.*;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.DomainLayer.Classes.Agreement;

public class AgreementFacade extends BaseFacade {
    private final Map<Integer, List<Agreement>> supplierIdToAgreements = new HashMap<>();

    public AgreementDTO createAgreement(AgreementDTO agreementDTO) {
        Agreement agreement = new Agreement(agreementDTO);
        supplierIdToAgreements
                .computeIfAbsent(agreement.getSupplierId(), k -> new ArrayList<>());
        supplierIdToAgreements.get(agreement.getSupplierId()).add(agreement);
        return agreementDTO;
    }

    public boolean removeAgreement(int agreementID, int supplierID) {
        List<Agreement> supplierAgreements = supplierIdToAgreements.get(supplierID);
        if (supplierAgreements == null) {
            return false;
        }
        Iterator<Agreement> iterator = supplierAgreements.iterator();
        while (iterator.hasNext()) {
            Agreement agreement = iterator.next();
            if (agreement.getAgreementId() == agreementID) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean updateAgreement(AgreementDTO updated) {
        int agreementID = updated.getAgreementId();
        int supplierID = updated.getSupplierId();
        List<Agreement> supplierAgreements = supplierIdToAgreements.get(supplierID);
        if (supplierAgreements == null) {
            return false;
        }
        Agreement existingAgreement = supplierIdToAgreements
                .get(supplierID)
                .stream()
                .filter(a -> a.getAgreementId() == agreementID)
                .findFirst()
                .orElse(null);
        if (existingAgreement == null) {
            return false; // Agreement not found
        }
        // Update the existing agreement with the new details
        List<BeanPatch<AgreementDTO, Agreement, ?>> rules = List.of(
                BeanPatch.of(AgreementDTO::getAgreementStartDate, Agreement::getAgreementStartDate,
                        Agreement::setAgreementStartDate),
                BeanPatch.of(AgreementDTO::getAgreementEndDate, Agreement::getAgreementEndDate,
                        Agreement::setAgreementEndDate));
        rules.forEach(rule -> rule.apply(updated, existingAgreement));
        // Update the bill of quantities items
        // use the existing list of item ids to update the items in the agreement
        List<BillofQuantitiesItemDTO> updatedItems = updated.getBillOfQuantitiesItems();
        existingAgreement.setBillOfQuantitiesItemsUsingDTOs(updatedItems);
        return true;
    }

    public AgreementDTO getAgreementById(int agreementID) {
        for (List<Agreement> supplierAgreements : supplierIdToAgreements.values()) {
            for (Agreement agreement : supplierAgreements) {
                if (agreement.getAgreementId() == agreementID) {
                    return new AgreementDTO(
                            agreement.getSupplierId(),
                            agreement.getSupplierName(),
                            agreement.getAgreementStartDate(),
                            agreement.getAgreementEndDate(),
                            agreement.getBillOfQuantitiesItems().stream()
                                    .map(BillofQuantitiesItemDTO::new)
                                    .toList() // Convert to List<BillofQuantitiesItemDTO>
                    );
                }
            }
        }
        throw new IllegalArgumentException("Agreement with ID " + agreementID + " not found.");
    }

    public Agreement getActualAgreement(int agreementID) {
        for (List<Agreement> supplierAgreements : supplierIdToAgreements.values()) {
            for (Agreement agreement : supplierAgreements) {
                if (agreement.getAgreementId() == agreementID) {
                    return agreement; // Return the actual Agreement object
                }
            }
        }
        throw new IllegalArgumentException("Agreement with ID " + agreementID + " not found.");
    }

    public List<AgreementDTO> getAgreementsBySupplierId(int supplierId) {
        List<Agreement> supplierAgreements = supplierIdToAgreements.get(supplierId);
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
                    // make an array of BillofQuantitiesItemDTO from the agreement's items
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
