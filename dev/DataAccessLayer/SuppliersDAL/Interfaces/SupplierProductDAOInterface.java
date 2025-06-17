package DataAccessLayer.SuppliersDAL.Interfaces;

import java.util.List;
import java.util.Optional;

import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import DTOs.SuppliersModuleDTOs.SupplierProductDTO;

public interface SupplierProductDAOInterface {
   SupplierProductDTO createSupplierProduct(SupplierProductDTO supplierProduct);

   Optional<SupplierProductDTO> getSupplierProductById(int supplierId, int productId);

   boolean updateSupplierProduct(SupplierProductDTO supplierProduct);

   boolean deleteSupplierProduct(int supplierId, int productId);

   List<SupplierProductDTO> getAllSupplierProducts();

   List<SupplierProductDTO> getAllSupplierProductsForSupplier(int supplierId);

   // only unique product ids, their name and their manufacturer name
   List<CatalogProductDTO> getCatalogProducts();

   boolean supplierProductExists(int supplierId, int productId);

   List<Integer> getAllSupplierIdsForProductId(int productId);

   List<Integer> getAllProductIdsForSupplierId(int supplierId);
}
