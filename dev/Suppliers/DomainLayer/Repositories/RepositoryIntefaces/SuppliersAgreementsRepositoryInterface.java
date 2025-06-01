package Suppliers.DomainLayer.Repositories.RepositoryIntefaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.DTOs.SupplierProductDTO;

public interface SuppliersAgreementsRepositoryInterface {
   void createSupplier(SupplierDTO supplier) throws SQLException;

   Optional<SupplierDTO> getSupplierById(int id) throws SQLException;

   void updateSupplier(SupplierDTO supplier) throws SQLException;

   void deleteSupplier(int id) throws SQLException;

   boolean supplierExists(int id) throws SQLException;

   List<SupplierDTO> getAllSuppliers() throws SQLException;

   void addAgreementToSupplier(AgreementDTO agreement, int supplierId) throws SQLException;

   void removeAgreementFromSupplier(int agreementId, int supplierId) throws SQLException;

   Optional<AgreementDTO> getAgreementById(int agreementId) throws SQLException;

   void updateAgreement(AgreementDTO agreement) throws SQLException;

   void deleteAgreement(int agreementId) throws SQLException;

   boolean agreementExists(int agreementId) throws SQLException;

   List<AgreementDTO> getAllAgreementsForSupplier(int supplierId) throws SQLException;

   List<AgreementDTO> getAllAgreements() throws SQLException;

   void createSupplierProduct(SupplierProductDTO supplierProduct) throws SQLException;

   Optional<SupplierProductDTO> getSupplierProductById(int supplierId, int productId) throws SQLException;

   void updateSupplierProduct(SupplierProductDTO supplierProduct) throws SQLException;

   void deleteSupplierProduct(int supplierId, int productId) throws SQLException;

   List<SupplierProductDTO> getAllSupplierProductsById(int supplierId) throws SQLException;

   boolean supplierProductExists(int supplierId, int productId) throws SQLException;

   List<SupplierProductDTO> getAllSupplierProducts() throws SQLException;

   List<Integer> getAllSuppliersForProductId(int productId) throws SQLException;

   List<Integer> getAllProductsForSupplierId(int supplierId) throws SQLException;

   List<CatalogProductDTO> getCatalogProducts() throws SQLException;
}
