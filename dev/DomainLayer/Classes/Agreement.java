package DomainLayer.Classes;

import DomainLayer.Enums.WeekofDay;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.*;
import java.io.Serializable;


public class Agreement implements Serializable {

    private UUID agreementId;
    private UUID supplierId;
    private String supplierName;
    private List <WeekofDay> supplyDays;
    private boolean selfSupply ;
    private LocalDate agreementStartDate;
    private LocalDate agreementEndDate;
    private ArrayList <Product>  supplyProducts;
    private boolean valid;

    private ArrayList<BillofQuantitiesItem> billOfQuantities; // Optional field for the Bill of Quantities (BoQ)
    private boolean hasFixedSupplyDays;

    /**
     * JSON constructor for Jackson.
     */
    @JsonCreator
    public Agreement(
            @JsonProperty("supplierId") UUID supplierId,
            @JsonProperty("supplierName") String supplierName,
            @JsonProperty("productNames") ArrayList<Product> supplyProducts,
            @JsonProperty("hasFixedSupplyDays") boolean hasFixedSupplyDays) {
        this.agreementId = UUID.randomUUID();
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.valid = true;
        this.supplyProducts = supplyProducts != null ? supplyProducts : new ArrayList<>();
        this.hasFixedSupplyDays = hasFixedSupplyDays;
        this.billOfQuantities = new ArrayList<>();
    }

    // ───────────────────────────────────────────────────────────────────────
    // Getters
    // ───────────────────────────────────────────────────────────────────────

    public UUID getAgreementId() { return agreementId; }
    public UUID getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public List<Product> getProducts() { return supplyProducts; }
    public boolean isValid() { return valid; }
    public List<BillofQuantitiesItem> getBillOfQuantities() { return billOfQuantities; }
    public boolean hasFixedSupplyDays() { return hasFixedSupplyDays; }




    // ───────────────────────────────────────────────────────────────────────
    // Setters
    // ───────────────────────────────────────────────────────────────────────
    public void setAgreementId(UUID agreementId) { this.agreementId = agreementId; }
    public void setSupplierId(UUID supplierId) { this.supplierId = supplierId; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setProducts(ArrayList <Product> products) { this.supplyProducts = products; }
    public void setBillOfQuantities(ArrayList <BillofQuantitiesItem> billOfQuantities) {this.billOfQuantities = billOfQuantities; }
    public void setHasFixedSupplyDays(boolean hasFixedSupplyDays) { this.hasFixedSupplyDays = hasFixedSupplyDays; }

    // Additional Methods to add/remove Bill of Quantities items
    public void addBillOfQuantitiesItem(BillofQuantitiesItem item) {
        this.billOfQuantities.add(item);
    }

    public void removeBillOfQuantitiesItem(UUID itemId) {
        this.billOfQuantities.removeIf(item -> item.getId().equals(itemId));
    }
}


