package Suppliers.DTOs;

import java.time.LocalDate;
import java.util.List;

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

   public AgreementDTO(int supplierId, String supplierName,
         LocalDate agreementStartDate, LocalDate agreementEndDate,
         List<BillofQuantitiesItemDTO> billOfQuantitiesItems) {
      this.agreementId = -1; // Default value, will be set later
      this.supplierId = supplierId;
      this.supplierName = supplierName;
      this.valid = true;
      this.agreementStartDate = agreementStartDate;
      this.agreementEndDate = agreementEndDate;
      this.billOfQuantitiesItems = billOfQuantitiesItems;
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
      return "{\n" +
            "  \"agreementId\": " + agreementId + ",\n" +
            "  \"supplierId\": " + supplierId + ",\n" +
            "  \"supplierName\": \"" + supplierName + "\",\n" +
            "  \"valid\": " + valid + ",\n" +
            "  \"agreementStartDate\": \"" + agreementStartDate + "\",\n" +
            "  \"agreementEndDate\": \"" + agreementEndDate + "\",\n" +
            "  \"billOfQuantitiesItems\": " + billOfQuantitiesItems + "\n" +
            "}";
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      AgreementDTO that = (AgreementDTO) obj;
      return agreementId == that.agreementId &&
            supplierId == that.supplierId &&
            valid == that.valid &&
            supplierName.equals(that.supplierName) &&
            agreementStartDate.equals(that.agreementStartDate) &&
            agreementEndDate.equals(that.agreementEndDate);
   }
}
