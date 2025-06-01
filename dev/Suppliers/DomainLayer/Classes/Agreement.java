/*
 * DomainLayer/Classes/Agreement.java
 */
package Suppliers.DomainLayer.Classes;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;

/**
 * Entity representing a contract (Agreement) with a Supplier.
 *
 */

public class Agreement implements Serializable {
    private static int nextAgreementID = 10;
    private int agreementId;
    private int supplierId;
    private String supplierName;
    private boolean valid;
    private LocalDate agreementStartDate;
    private LocalDate agreementEndDate;
    private List<BillofQuantitiesItem> billOfQuantitiesItems;

    public Agreement(
            int supplierId,
            String supplierName,
            LocalDate agreementStartDate,
            LocalDate agreementEndDate,
            List<BillofQuantitiesItem> billOfQuantitiesItems) {
        if (agreementEndDate.isBefore(agreementStartDate)) {
            throw new IllegalArgumentException("End date before start date");
        }
        this.agreementId = nextAgreementID++;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.valid = true;
        this.agreementStartDate = agreementStartDate;
        this.agreementEndDate = agreementEndDate;
        this.billOfQuantitiesItems = billOfQuantitiesItems;
    }

    public Agreement(AgreementDTO agreementDTO) {
        this.agreementId = nextAgreementID++;
        this.supplierId = agreementDTO.getSupplierId();
        this.supplierName = agreementDTO.getSupplierName();
        this.valid = true;
        this.agreementStartDate = agreementDTO.getAgreementStartDate();
        this.agreementEndDate = agreementDTO.getAgreementEndDate();
        this.billOfQuantitiesItems = new ArrayList<>();
        for (BillofQuantitiesItemDTO itemDTO : agreementDTO.getBillOfQuantitiesItems()) {
            BillofQuantitiesItem item = new BillofQuantitiesItem(
                    itemDTO.getAgreementId(),
                    itemDTO.getLineInBillID(), // Default value for line in bill ID
                    itemDTO.getProductName(),
                    itemDTO.getProductId(),
                    itemDTO.getQuantity(),
                    itemDTO.getDiscountPercent());
            addBillOfQuantitiesItem(item); // will set the line in bill ID correctly
        }
    }

    // ───────────────────────────────────────────────────────────────────────
    // Getters
    // ───────────────────────────────────────────────────────────────────────

    public int getAgreementId() {
        return agreementId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public boolean isValid() {
        return valid;
    }

    public LocalDate getAgreementStartDate() {
        return agreementStartDate;
    }

    public LocalDate getAgreementEndDate() {
        return agreementEndDate;
    }

    public List<BillofQuantitiesItem> getBillOfQuantitiesItems() {
        return billOfQuantitiesItems;
    }

    public List<BillofQuantitiesItemDTO> getBillOfQuantitiesItemsAsDTOs() {
        List<BillofQuantitiesItemDTO> dtos = new ArrayList<>();
        for (BillofQuantitiesItem item : billOfQuantitiesItems) {
            dtos.add(new BillofQuantitiesItemDTO(
                    this.agreementId,
                    item.getLineInBillID(),
                    item.getProductName(),
                    item.getProductID(),
                    item.getQuantity(),
                    item.getDiscountPercent()));
        }
        return dtos;
    }

    // ───────────────────────────────────────────────────────────────────────
    // Setters
    // ───────────────────────────────────────────────────────────────────────

    public void setAgreementId(int agreementId) {
        this.agreementId = agreementId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setAgreementStartDate(LocalDate agreementStartDate) {
        this.agreementStartDate = agreementStartDate;
    }

    public void setAgreementEndDate(LocalDate agreementEndDate) {
        this.agreementEndDate = agreementEndDate;
    }

    public void setBillOfQuantitiesItems(List<BillofQuantitiesItem> billOfQuantities) {
        this.billOfQuantitiesItems = billOfQuantities;
    }

    // ───────────────────────────────────────────────────────────────────────
    // BoQ methods
    // ───────────────────────────────────────────────────────────────────────

    public void addBillOfQuantitiesItem(BillofQuantitiesItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Bill of Quantities item cannot be null");
        }
        if (this.billOfQuantitiesItems == null) {
            this.billOfQuantitiesItems = new ArrayList<>();
        }
        for (BillofQuantitiesItem existingItem : this.billOfQuantitiesItems) {
            if (existingItem.equals(item) && existingItem.getQuantity() == item.getQuantity()) {
                // Update the existing item with the new quantity
                existingItem.setDiscountPercent(item.getDiscountPercent());
                return;
            }
        }
        // If it doesn't exist, or if the quantity is different, add it to the list
        item.setLineInBillID(this.billOfQuantitiesItems.size() + 1); // increment line in bill ID because its new
        this.billOfQuantitiesItems.add(item);
    }

    public void removeBillOfQuantitiesItem(int itemId) {
        if (this.billOfQuantitiesItems != null) {
            this.billOfQuantitiesItems.removeIf(item -> item.getProductID() == itemId);
        }
    }

    @Override
    public String toString() {
        return "{\n" +
                "   \"agreementId\": " + agreementId + ",\n" +
                "   \"supplierId\": " + supplierId + ",\n" +
                "   \"supplierName\": \"" + supplierName + "\",\n" +
                "   \"valid\": " + valid + ",\n" +
                "   \"agreementStartDate\": \"" + agreementStartDate + "\",\n" +
                "   \"agreementEndDate\": \"" + agreementEndDate + "\",\n" +
                "   \"billOfQuantitiesItems\": " + billOfQuantitiesItems + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Agreement))
            return false;

        Agreement agreement = (Agreement) o;

        if (agreementId != agreement.agreementId)
            return false;
        if (supplierId != agreement.supplierId)
            return false;
        if (valid != agreement.valid)
            return false;
        if (!supplierName.equals(agreement.supplierName))
            return false;
        if (!agreementStartDate.equals(agreement.agreementStartDate))
            return false;
        if (!agreementEndDate.equals(agreement.agreementEndDate))
            return false;
        return billOfQuantitiesItems.equals(agreement.billOfQuantitiesItems);
    }

    public void setBillOfQuantitiesItemsUsingDTOs(List<BillofQuantitiesItemDTO> billOfQuantitiesItems) {
        // use hashcode to check for the boqdto and boq are equal and can take the same
        // line in bill ID
        this.billOfQuantitiesItems = new ArrayList<>();
        for (BillofQuantitiesItemDTO itemDTO : billOfQuantitiesItems) {
            BillofQuantitiesItem item = new BillofQuantitiesItem(
                    itemDTO.getAgreementId(),
                    itemDTO.getLineInBillID(), // Default value for line in bill ID
                    itemDTO.getProductName(),
                    itemDTO.getProductId(),
                    itemDTO.getQuantity(),
                    itemDTO.getDiscountPercent());
            addBillOfQuantitiesItem(item); // will set the line in bill ID correctly
        }
    }
}
