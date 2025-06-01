package Suppliers.DataLayer.DAOs;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.SupplierDTO;
import Suppliers.DataLayer.Interfaces.SupplierDAOInterface;
import Suppliers.DataLayer.util.Database;

public class JdbcSupplierDAO implements SupplierDAOInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcSupplierDAO.class);

    @Override
    public SupplierDTO createSupplier(SupplierDTO supplier) throws SQLException {
        if (supplier == null || supplier.getId() < 0) {
            LOGGER.error("Invalid supplier data: {}", supplier);
            throw new IllegalArgumentException("Supplier cannot be null and must have a valid ID");
        }
        LOGGER.info("Creating supplier: {}", supplier);
        String sql = "INSERT INTO suppliers (name, tax_number, street, city, building_number, self_supply, supply_days_mask, lead_supply_days, bank_account_number, payment_method, payment_term) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, supplier.getName());
            preparedStatement.setString(2, supplier.getTaxNumber());
            preparedStatement.setString(3, supplier.getSelfSupply() ? "1" : "0");
            preparedStatement.setString(4, supplier.getSupplyDaysMask());
            preparedStatement.setInt(5, supplier.getLeadSupplyDays());
            preparedStatement.setString(6, supplier.getAddress().getStreet());
            preparedStatement.setString(7, supplier.getAddress().getCity());
            preparedStatement.setString(8, supplier.getAddress().getBuildingNumber());
            preparedStatement.setString(9, supplier.getPaymentDetails().getBankAccountNumber());
            preparedStatement.setString(10, supplier.getPaymentDetails().getPaymentMethod().name());
            preparedStatement.setString(11, supplier.getPaymentDetails().getPaymentTerm().name());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.error("Creating supplier failed, no rows affected.");
                throw new SQLException("Creating supplier failed, no rows affected.");
            }
            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    supplier.setId(id);
                    LOGGER.info("Supplier created successfully with ID: {}", id);
                } else {
                    LOGGER.error("Creating supplier failed, no ID obtained.");
                    throw new SQLException("Creating supplier failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error creating supplier: {}", supplier, e);
            throw e; // rethrow the exception for further handling
        }
        return supplier;
    }

    @Override
    public Optional<SupplierDTO> getSupplier(int id) throws SQLException {
        if (id < 0) {
            LOGGER.error("Invalid supplier ID: {}", id);
            throw new IllegalArgumentException("Supplier ID must be positive");
        }
        LOGGER.info("Retrieving supplier with ID: {}", id);
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                SupplierDTO supplier = new SupplierDTO(
                        resultSet.getInt("supplier_id"),
                        resultSet.getString("name"),
                        resultSet.getString("tax_number"),
                        resultSet.getString("street"),
                        resultSet.getString("city"),
                        resultSet.getString("building_number"),
                        resultSet.getBoolean("self_supply"),
                        resultSet.getString("supply_days_mask"),
                        resultSet.getInt("lead_supply_days"),
                        resultSet.getString("bank_account_number"),
                        resultSet.getString("payment_method"),
                        resultSet.getString("payment_term"));
                LOGGER.info("Supplier retrieved successfully: {}", supplier);
                return Optional.of(supplier);
            } else {
                LOGGER.warn("No supplier found with ID: {}", id);
                return Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving supplier with ID: {}", id, e);
            throw e; // rethrow the exception for further handling
        }
    }

    @Override
    public void updateSupplier(SupplierDTO supplier) throws SQLException {
        if (supplier == null || supplier.getId() < 0) {
            LOGGER.error("Invalid supplier data: {}", supplier);
            throw new IllegalArgumentException("Supplier cannot be null and must have a valid ID");
        }
        LOGGER.info("Updating supplier: {}", supplier);
        String sql = "UPDATE suppliers SET supplier_id = ?, name = ?, tax_number = ?, self_supply = ?, supply_days_mask = ?, lead_supply_days = ?, street = ?, city = ?, building_number = ?, bank_account_number = ?, payment_method = ?, payment_term = ? WHERE supplier_id = ?";
        try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, supplier.getId());
            preparedStatement.setString(2, supplier.getName());
            preparedStatement.setString(3, supplier.getTaxNumber());
            preparedStatement.setBoolean(4, supplier.getSelfSupply());
            preparedStatement.setString(5, supplier.getSupplyDaysMask());
            preparedStatement.setInt(6, supplier.getLeadSupplyDays());
            preparedStatement.setString(7, supplier.getAddress().getStreet());
            preparedStatement.setString(8, supplier.getAddress().getCity());
            preparedStatement.setString(9, supplier.getAddress().getBuildingNumber());
            preparedStatement.setString(10, supplier.getPaymentDetails().getBankAccountNumber());
            preparedStatement.setString(11, supplier.getPaymentDetails().getPaymentMethod().name());
            preparedStatement.setString(12, supplier.getPaymentDetails().getPaymentTerm().name());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Supplier updated successfully: {}", supplier);
            } else {
                LOGGER.warn("No rows affected when updating supplier: {}", supplier);
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating supplier: {}", supplier, e);
            throw e; // rethrow the exception for further handling
        }
    }

    @Override
    public void deleteSupplier(int id) throws SQLException {
        if (id < 0) {
            LOGGER.error("Invalid supplier ID: {}", id);
            throw new IllegalArgumentException("Supplier ID must be positive");
        }
        LOGGER.info("Deleting supplier with ID: {}", id);
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Supplier deleted successfully with ID: {}", id);
            } else {
                LOGGER.warn("No supplier found with ID: {}", id);
            }
        } catch (SQLException e) {
            LOGGER.error("Error deleting supplier with ID: {}", id, e);
            throw e; // rethrow the exception for further handling
        }
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() throws SQLException {
        LOGGER.info("Retrieving all suppliers");
        String sql = "SELECT * FROM suppliers";
        try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            List<SupplierDTO> suppliers = new ArrayList<>();
            while (resultSet.next()) {
                SupplierDTO supplier = new SupplierDTO(
                        resultSet.getInt("supplier_id"),
                        resultSet.getString("name"),
                        resultSet.getString("tax_number"),
                        resultSet.getString("street"),
                        resultSet.getString("city"),
                        resultSet.getString("building_number"),
                        resultSet.getBoolean("self_supply"),
                        resultSet.getString("supply_days_mask"),
                        resultSet.getInt("lead_supply_days"),
                        resultSet.getString("bank_account_number"),
                        resultSet.getString("payment_method"),
                        resultSet.getString("payment_term"));
                suppliers.add(supplier);
            }
            LOGGER.info("Retrieved {} suppliers", suppliers.size());
            return suppliers;
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all suppliers", e);
            throw e; // rethrow the exception for further handling
        }
    }

    @Override
    public boolean supplierExists(int id) throws SQLException {
        if (id < 0) {
            LOGGER.error("Invalid supplier ID: {}", id);
            throw new IllegalArgumentException("Supplier ID must be positive");
        }
        LOGGER.info("Checking if supplier exists with ID: {}", id);
        String sql = "SELECT COUNT(*) FROM suppliers WHERE supplier_id = ?";
        try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                boolean exists = resultSet.getInt(1) > 0;
                LOGGER.info("Supplier exists: {}", exists);
                return exists;
            } else {
                LOGGER.warn("No result found when checking supplier existence for ID: {}", id);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking if supplier exists with ID: {}", id, e);
            throw e; // rethrow the exception for further handling
        }
    }
}