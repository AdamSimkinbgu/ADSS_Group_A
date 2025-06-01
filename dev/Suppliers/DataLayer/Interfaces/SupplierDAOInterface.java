package Suppliers.DataLayer.Interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import Suppliers.DTOs.SupplierDTO;

public interface SupplierDAOInterface {
   SupplierDTO createSupplier(SupplierDTO supplier) throws SQLException;

   Optional<SupplierDTO> getSupplier(int id) throws SQLException;

   void updateSupplier(SupplierDTO supplier) throws SQLException;

   void deleteSupplier(int id) throws SQLException;

   List<SupplierDTO> getAllSuppliers() throws SQLException;

   boolean supplierExists(int id) throws SQLException;

}
