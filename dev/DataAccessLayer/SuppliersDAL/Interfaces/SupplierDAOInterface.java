package DataAccessLayer.SuppliersDAL.Interfaces;

import java.util.List;
import java.util.Optional;

import DTOs.SuppliersModuleDTOs.SupplierDTO;

public interface SupplierDAOInterface {
   SupplierDTO createSupplier(SupplierDTO supplier);

   Optional<SupplierDTO> getSupplier(int id);

   boolean updateSupplier(SupplierDTO supplier);

   boolean deleteSupplier(int id);

   List<SupplierDTO> getAllSuppliers();

   boolean supplierExists(int id);
}
