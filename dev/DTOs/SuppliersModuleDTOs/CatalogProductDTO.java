package DTOs.SuppliersModuleDTOs;

import DomainLayer.SuppliersDomainSubModule.Classes.SupplierProduct;

public record CatalogProductDTO(
            int productId,
            String name,
            String manufacturerName) {

      public CatalogProductDTO(SupplierProduct supplierProduct) {
            this(supplierProduct.getProductId(),
                        supplierProduct.getName(),
                        supplierProduct.getManufacturerName());
      }

      public CatalogProductDTO(SupplierProductDTO supplierProductDTO) {
            this(supplierProductDTO.getProductId(),
                        supplierProductDTO.getName(),
                        supplierProductDTO.getManufacturerName());
      }

      public CatalogProductDTO getCatalogProductDTO(SupplierProductDTO supplierProductDTO) {
            return new CatalogProductDTO(
                        supplierProductDTO.getProductId(),
                        supplierProductDTO.getName(),
                        supplierProductDTO.getManufacturerName());
      }

      public int getProductId() {
            return productId;
      }

      public String getProductName() {
            return name;
      }

      public String getManufacturerName() {
            return manufacturerName;
      }

      @Override
      public String toString() {
            final int MAX_NAME = 20;
            final int MAX_MF = 12;
            String mf = (manufacturerName != null && manufacturerName.length() > MAX_MF)
                        ? manufacturerName.substring(0, MAX_MF - 3) + "..."
                        : (manufacturerName != null ? manufacturerName : "[no mf]");

            String nm = (name != null && name.length() > MAX_NAME)
                        ? name.substring(0, MAX_NAME - 3) + "..."
                        : (name != null ? name : "[no name]");

            return String.format(
                        "CatalogProd [ID=%6d]  MF: %-12s  Name: %-12s",
                        productId,
                        mf,
                        nm);
      }
}
