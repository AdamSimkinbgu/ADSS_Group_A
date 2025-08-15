package DTOs.SuppliersModuleDTOs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import DomainLayer.SuppliersDomainSubModule.Classes.Agreement;

public class AgreementDTO {
   private int supplierId;
   private int agreementId;
   private String supplierName;
   private boolean valid;
   private LocalDate agreementStartDate;
   private LocalDate agreementEndDate;
   private List<BillofQuantitiesItemDTO> billOfQuantitiesItems;

   public AgreementDTO() {
   }

   public AgreementDTO(Agreement agreement) {
      this.agreementId = agreement.getAgreementId();
      this.supplierId = agreement.getSupplierId();
      this.supplierName = agreement.getSupplierName();
      this.valid = agreement.isValid();
      this.agreementStartDate = agreement.getAgreementStartDate();
      this.agreementEndDate = agreement.getAgreementEndDate();
      this.billOfQuantitiesItems = agreement.getBillOfQuantitiesItems().stream()
            .map(item -> new BillofQuantitiesItemDTO(item, this.agreementId))
            .toList();
   }

   public AgreementDTO(int supplierId, // for the AgreementDAO
         int agreementId,
         LocalDate startDate,
         LocalDate endDate,
         boolean valid) {
      this.supplierId = supplierId;
      this.agreementId = agreementId;
      this.agreementStartDate = startDate;
      this.agreementEndDate = endDate;
      this.valid = valid;
      this.supplierName = "";
      this.billOfQuantitiesItems = new ArrayList<>();
   }

   public AgreementDTO(int supplierId, String supplierName,
         LocalDate agreementStartDate, LocalDate agreementEndDate,
         List<BillofQuantitiesItemDTO> billOfQuantitiesItems) {
      this.agreementId = -1; // Default value, will be set later
      this.supplierId = supplierId;
      this.supplierName = supplierName;
      this.valid = true;
      this.agreementStartDate = agreementStartDate;
      this.agreementEndDate = agreementEndDate;
      this.billOfQuantitiesItems = billOfQuantitiesItems != null
            ? new ArrayList<>(billOfQuantitiesItems)
            : new ArrayList<>();
   }

   public AgreementDTO(AgreementDTO other) {
      this.agreementId = other.agreementId;
      this.supplierId = other.supplierId;
      this.supplierName = other.supplierName;
      this.valid = other.valid;
      this.agreementStartDate = other.agreementStartDate;
      this.agreementEndDate = other.agreementEndDate;
      this.billOfQuantitiesItems = other.billOfQuantitiesItems != null
            ? List.copyOf(other.billOfQuantitiesItems)
            : List.of();
   }

   // Getters and Setters
   public int getAgreementId() {
      return agreementId;
   }

   public void setAgreementId(int agreementId) {
      this.agreementId = agreementId;
   }

   public int getSupplierId() {
      return supplierId;
   }

   public void setSupplierId(int supplierId) {
      this.supplierId = supplierId;
   }

   public String getSupplierName() {
      return supplierName;
   }

   public void setSupplierName(String supplierName) {
      this.supplierName = supplierName;
   }

   public boolean isValid() {
      return valid;
   }

   public void setValid(boolean valid) {
      this.valid = valid;
   }

   public LocalDate getAgreementStartDate() {
      return agreementStartDate;
   }

   public void setAgreementStartDate(LocalDate agreementStartDate) {
      this.agreementStartDate = agreementStartDate;
   }

   public LocalDate getAgreementEndDate() {
      return agreementEndDate;
   }

   public void setAgreementEndDate(LocalDate agreementEndDate) {
      this.agreementEndDate = agreementEndDate;
   }

   public List<BillofQuantitiesItemDTO> getBillOfQuantitiesItems() {
      return billOfQuantitiesItems;
   }

   public void setBillOfQuantitiesItems(List<BillofQuantitiesItemDTO> billOfQuantitiesItems) {
      this.billOfQuantitiesItems = billOfQuantitiesItems;
   }

   @Override
   public String toString() {
      DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
      String start = (agreementStartDate != null)
            ? agreementStartDate.format(df)
            : "N/A";
      String end = (agreementEndDate != null)
            ? agreementEndDate.format(df)
            : "N/A";

      StringBuilder sb = new StringBuilder();
      sb.append(String.format(
            "Agreement [%4d]  Supplier: %-15s (ID=%d)%n",
            agreementId,
            supplierName != null ? supplierName : "[no name]",
            supplierId));
      sb.append(String.format(
            "  Valid: %s    Period: %s → %s%n",
            valid ? "Yes" : "No",
            start,
            end));

      sb.append("  ────────────────────────────────────────────────\n");
      if (billOfQuantitiesItems == null || billOfQuantitiesItems.isEmpty()) {
         sb.append("  [No Bill-of-Quantities items]\n");
      } else {
         sb.append("  Bill-of-Quantities Items:\n");
         for (BillofQuantitiesItemDTO item : billOfQuantitiesItems) {
            // Indent each item’s toString() by two spaces
            String[] lines = item.toString().split("\\r?\\n");
            for (String line : lines) {
               sb.append("    ").append(line).append("\n");
            }
         }
         sb.append(String.format(
               "  (%d total BoQ item%s)%n",
               billOfQuantitiesItems.size(),
               billOfQuantitiesItems.size() == 1 ? "" : "s"));
      }

      sb.append("=====================================================\n");
      return sb.toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!(obj instanceof AgreementDTO that))
         return false;
      return agreementId == that.agreementId
            && supplierId == that.supplierId
            && valid == that.valid
            && Objects.equals(supplierName, that.supplierName)
            && Objects.equals(agreementStartDate, that.agreementStartDate)
            && Objects.equals(agreementEndDate, that.agreementEndDate)
            && Objects.equals(billOfQuantitiesItems, that.billOfQuantitiesItems);
   }
}
