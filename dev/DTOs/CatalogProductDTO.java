package DTOs;

import DomainLayer.Classes.SupplierProduct;

public record CatalogProductDTO(
      int productId,
      String name,
      String manufacturerName) {

   public CatalogProductDTO(SupplierProduct supplierProduct) {
      this(supplierProduct.getProductId(),
            supplierProduct.getName(),
            supplierProduct.getManufacturerName());
   }

   public CatalogProductDTO getCatalogProductDTO(SupplierProductDTO supplierProductDTO) {
      return new CatalogProductDTO(
            supplierProductDTO.getProductId(),
            supplierProductDTO.getName(),
            supplierProductDTO.getManufacturerName());
   }
}
