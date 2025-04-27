/*
 * DomainLayer/Classes/Agreement.java
 */
package DomainLayer.Classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import DomainLayer.Enums.WeekofDay;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a contract (Agreement) with a Supplier.
 *
 */

public class Agreement implements Serializable {

    private UUID agreementId;
    private UUID supplierId;
    private String supplierName;
    private boolean valid;
    private boolean selfSupply;
    private EnumSet<WeekofDay> supplyDays;
    private LocalDate agreementStartDate;
    private LocalDate agreementEndDate;
    private List<BillofQuantitiesItem> billOfQuantities;
    private boolean hasFixedSupplyDays;

    /**
     * JSON constructor for Jackson.
     */
    @JsonCreator
    public Agreement(
            @JsonProperty("supplierId") UUID supplierId,
            @JsonProperty("supplierName") String supplierName,
            @JsonProperty("selfSupply") boolean selfSupply,
            @JsonProperty("supplyDays") EnumSet<WeekofDay> supplyDays,
            @JsonProperty("agreementStartDate") LocalDate agreementStartDate,
            @JsonProperty("agreementEndDate") LocalDate agreementEndDate,
            @JsonProperty("hasFixedSupplyDays") boolean hasFixedSupplyDays) {
        if (agreementEndDate.isBefore(agreementStartDate)) {
            throw new IllegalArgumentException("Agreement end date cannot be before start date.");
        }
        this.agreementId = UUID.randomUUID();
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.valid = true;
        this.selfSupply = selfSupply;
        this.supplyDays = (supplyDays != null && !supplyDays.isEmpty())
                ? EnumSet.copyOf(supplyDays)
                : EnumSet.noneOf(WeekofDay.class);
        this.agreementStartDate = agreementStartDate;
        this.agreementEndDate = agreementEndDate;
        this.hasFixedSupplyDays = hasFixedSupplyDays;
        this.billOfQuantities = new ArrayList<>();
    }

    // ───────────────────────────────────────────────────────────────────────
    // Getters
    // ───────────────────────────────────────────────────────────────────────

    public UUID getAgreementId() {
        return agreementId;
    }

    public UUID getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isSelfSupply() {
        return selfSupply;
    }

    public EnumSet<WeekofDay> getSupplyDays() {
        return supplyDays;
    }

    public LocalDate getAgreementStartDate() {
        return agreementStartDate;
    }

    public LocalDate getAgreementEndDate() {
        return agreementEndDate;
    }

    public List<BillofQuantitiesItem> getBillOfQuantities() {
        return billOfQuantities;
    }

    public boolean hasFixedSupplyDays() {
        return hasFixedSupplyDays;
    }

    // ───────────────────────────────────────────────────────────────────────
    // Setters
    // ───────────────────────────────────────────────────────────────────────

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setSelfSupply(boolean selfSupply) {
        this.selfSupply = selfSupply;
    }

    public void setSupplyDays(EnumSet<WeekofDay> supplyDays) {
        this.supplyDays = supplyDays;
    }

    public void setAgreementStartDate(LocalDate agreementStartDate) {
        this.agreementStartDate = agreementStartDate;
    }

    public void setAgreementEndDate(LocalDate agreementEndDate) {
        this.agreementEndDate = agreementEndDate;
    }

    public void setHasFixedSupplyDays(boolean hasFixedSupplyDays) {
        this.hasFixedSupplyDays = hasFixedSupplyDays;
    }

    public void setBillOfQuantities(List<BillofQuantitiesItem> billOfQuantities) {
        this.billOfQuantities = billOfQuantities;
    }

    // ───────────────────────────────────────────────────────────────────────
    // BoQ methods
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Add or update a BoQ entry storage; calculation moves to Order.
     */
    public void addBillOfQuantitiesItem(BillofQuantitiesItem item) {
        this.billOfQuantities.removeIf(b -> b.getId().equals(item.getId()));
        this.billOfQuantities.add(item);
    }

    public void removeBillOfQuantitiesItem(UUID itemId) {
        this.billOfQuantities.removeIf(item -> item.getId().equals(itemId));
    }
}
