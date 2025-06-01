package Suppliers.DataLayer.Interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.SupplierProductDTO;

public interface SupplierProductDAOInterface {
   SupplierProductDTO createSupplierProduct(SupplierProductDTO supplierProduct) throws SQLException;

   Optional<SupplierProductDTO> getSupplierProductById(int supplierId, int productId) throws SQLException;

   void updateSupplierProduct(SupplierProductDTO supplierProduct) throws SQLException;

   void deleteSupplierProduct(int supplierId, int productId) throws SQLException;

   List<SupplierProductDTO> getAllSupplierProducts() throws SQLException;

   List<SupplierProductDTO> getAllSupplierProductsForSupplier(int supplierId) throws SQLException;

   // only unique product ids, their name and their manufacturer name
   List<CatalogProductDTO> getCatalogProducts() throws SQLException;

   boolean supplierProductExists(int supplierId, int productId) throws SQLException;

   List<Integer> getAllSupplierIdsForProductId(int productId) throws SQLException;

   List<Integer> getAllProductIdsForSupplierId(int supplierId) throws SQLException;
}
