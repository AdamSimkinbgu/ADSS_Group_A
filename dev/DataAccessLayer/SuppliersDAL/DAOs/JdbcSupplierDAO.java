package DataAccessLayer.SuppliersDAL.DAOs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DTOs.SuppliersModuleDTOs.ContactInfoDTO;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DataAccessLayer.SuppliersDAL.Interfaces.SupplierDAOInterface;
import DataAccessLayer.SuppliersDAL.util.Database;

public class JdbcSupplierDAO extends BaseDAO implements SupplierDAOInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcSupplierDAO.class);

    @Override
    public SupplierDTO createSupplier(SupplierDTO supplier) {
        if (supplier == null) {
            LOGGER.error("Invalid supplier data: {}", supplier);
            throw new IllegalArgumentException("Supplier cannot be null and must have a valid ID");
        }
        LOGGER.debug("Creating supplier: {}", supplier.getName());
        String sql = "INSERT INTO suppliers (name, tax_number, self_supply, supply_days_mask, lead_supply_days, street, city, building_number,  bank_account_number, payment_method, payment_term) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
                sql,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, supplier.getName());
            preparedStatement.setString(2, supplier.getTaxNumber());
            preparedStatement.setInt(3, supplier.getSelfSupply() ? 1 : 0);
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
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    supplier.setId(id);
                    LOGGER.debug("Supplier created successfully with ID: {}", id);
                } else {
                    LOGGER.error("Creating supplier failed, no ID obtained.");
                    throw new SQLException("Creating supplier failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error handling SQL exception: {}", e.getMessage());
            handleSQLException(e);
        }
        return supplier;
    }

    @Override
    public Optional<SupplierDTO> getSupplier(int id) {
        if (id < 0) {
            LOGGER.error("Invalid supplier ID: {}", id);
            throw new IllegalArgumentException("Supplier ID must be positive");
        }
        LOGGER.debug("Retrieving supplier with ID: {}", id);
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        try (PreparedStatement preparedStatement = Database.getConnection()
                .prepareStatement(sql)) {
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
                LOGGER.debug("Supplier retrieved successfully: {}", supplier.getId());
                return Optional.of(supplier);
            } else {
                LOGGER.debug("No supplier found with ID: {}", id);
                return Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.error("Error handling SQL exception: {}", e.getMessage());
            handleSQLException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateSupplier(SupplierDTO supplier) {
        if (supplier == null || supplier.getId() < 0) {
            LOGGER.error("Invalid supplier data: {}", supplier);
            throw new IllegalArgumentException("Supplier cannot be null and must have a valid ID");
        }
        LOGGER.debug("Updating supplier: {}", supplier.getId());
        String sql = "UPDATE suppliers SET supplier_id = ?, name = ?, tax_number = ?, self_supply = ?, supply_days_mask = ?, lead_supply_days = ?, street = ?, city = ?, building_number = ?, bank_account_number = ?, payment_method = ?, payment_term = ? WHERE supplier_id = ?";
        try (PreparedStatement preparedStatement = Database.getConnection()
                .prepareStatement(sql)) {
            preparedStatement.setInt(1, supplier.getId());
            preparedStatement.setString(2, supplier.getName());
            preparedStatement.setString(3, supplier.getTaxNumber());
            preparedStatement.setInt(4, supplier.getSelfSupply() ? 1 : 0);
            preparedStatement.setString(5, supplier.getSupplyDaysMask());
            preparedStatement.setInt(6, supplier.getLeadSupplyDays());
            preparedStatement.setString(7, supplier.getAddress().getStreet());
            preparedStatement.setString(8, supplier.getAddress().getCity());
            preparedStatement.setString(9, supplier.getAddress().getBuildingNumber());
            preparedStatement.setString(10, supplier.getPaymentDetails().getBankAccountNumber());
            preparedStatement.setString(11, supplier.getPaymentDetails().getPaymentMethod().name());
            preparedStatement.setString(12, supplier.getPaymentDetails().getPaymentTerm().name());
            preparedStatement.setInt(13, supplier.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.debug("Supplier updated successfully: {}", supplier.getId());
                return true;
            } else {
                LOGGER.debug("No rows affected when updating supplier: {}", supplier.getId());
            }
        } catch (SQLException e) {
            LOGGER.error("Error handling SQL exception: {}", e.getMessage());
            handleSQLException(e);
        }
        return false;
    }

    @Override
    public boolean deleteSupplier(int id) {
        if (id < 0) {
            LOGGER.error("Invalid supplier ID: {}", id);
            throw new IllegalArgumentException("Supplier ID must be positive");
        }
        LOGGER.debug("Deleting supplier with ID: {}", id);
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        try (PreparedStatement preparedStatement = Database.getConnection()
                .prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.debug("Supplier deleted successfully with ID: {}", id);
                return true;
            } else {
                LOGGER.debug("No supplier found with ID: {}", id);
            }
        } catch (SQLException e) {
            LOGGER.error("Error handling SQL exception: {}", e.getMessage());
            handleSQLException(e);
        }
        return false;
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        LOGGER.debug("Retrieving all suppliers");
        List<SupplierDTO> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";
        List<ContactInfoDTO> contactsForSupplier = new ArrayList<>();

        String contactSql = "SELECT * FROM contact_info WHERE supplier_id = ?";
        try (PreparedStatement preparedStatement = Database.getConnection()
                .prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

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
                try (PreparedStatement contactStatement = Database.getConnection()
                        .prepareStatement(contactSql)) {
                    contactStatement.setInt(1, supplier.getId());
                    ResultSet contactResultSet = contactStatement.executeQuery();
                    while (contactResultSet.next()) {
                        ContactInfoDTO contact = new ContactInfoDTO(
                                contactResultSet.getInt("supplier_id"),
                                contactResultSet.getString("name"),
                                contactResultSet.getString("email"),
                                contactResultSet.getString("phone"));
                        if (contact.getSupplierId() == supplier.getId()) {
                            contactsForSupplier.add(contact);
                        }
                    }
                }
                supplier.setContacts(contactsForSupplier);

                suppliers.add(supplier);
                contactsForSupplier = new ArrayList<>();
            }
            LOGGER.debug("Retrieved {} suppliers", suppliers.size());
        } catch (SQLException e) {
            LOGGER.error("Error handling SQL exception: {}", e.getMessage());
            handleSQLException(e);
        }

        return suppliers;
    }

    @Override
    public boolean supplierExists(int id) {
        if (id < 0) {
            LOGGER.error("Invalid supplier ID: {}", id);
            throw new IllegalArgumentException("Supplier ID must be positive");
        }
        LOGGER.debug("Checking if supplier exists with ID: {}", id);
        String sql = "SELECT COUNT(*) FROM suppliers WHERE supplier_id = ?";
        try (PreparedStatement preparedStatement = Database.getConnection()
                .prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                boolean exists = resultSet.getInt(1) > 0;
                LOGGER.debug("Supplier exists: {}", exists);
                return exists;
            } else {
                LOGGER.debug("No result found when checking supplier existence for ID: {}", id);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Error handling SQL exception: {}", e.getMessage());
            handleSQLException(e);
        }
        return false;
    }

}