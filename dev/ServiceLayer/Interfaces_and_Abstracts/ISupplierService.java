package ServiceLayer.Interfaces_and_Abstracts;

import java.util.List;
import java.util.UUID;

import DomainLayer.Supplier;
import DomainLayer.SupplierProduct;

/**
 * Service interface for Supplier-related operations.
 * Extends the generic CRUD contract with domain-specific methods.
 */
public interface ISupplierService {

    /**
     * Convenient factory to register a new supplier in one call.
     */
    String addSupplier(String creationJson);

    /**
     * List all products this supplier offers.
     */
    List<SupplierProduct> getAvailableProducts(UUID supplierId);
}