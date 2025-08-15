package DomainLayer.EmployeeSubModule.Repository;

import DataAccessLayer.EmployeeDAL.EmployeeDALFactory;
import DataAccessLayer.EmployeeDAL.EmployeeDAO;
import DTOs.EmployeeDTO;
import DomainLayer.EmployeeSubModule.Repository.interfaces.EmployeeRepository;
import Util.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the EmployeeRepository interface.
 * This class uses the EmployeeDAO to interact with the database.
 */
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeeDAO employeeDAO;

    /**
     * Constructor that initializes the repository with the EmployeeDAO.
     */
    public EmployeeRepositoryImpl() {
        try {
            this.employeeDAO = EmployeeDALFactory.getInstance().getEmployeeDAO();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize EmployeeRepository", e);
        }
    }

    /**
     * Constructor that accepts an EmployeeDAO for testing purposes.
     *
     * @param employeeDAO The EmployeeDAO to use
     */
    public EmployeeRepositoryImpl(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @Override
    public EmployeeDTO getById(long israeliId) {
        try {
            return employeeDAO.getById(israeliId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get employee by ID: " + israeliId, e);
        }
    }

    @Override
    public List<EmployeeDTO> getAll() {
        try {
            return employeeDAO.getAll();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all employees", e);
        }
    }

    @Override
    public List<EmployeeDTO> getByBranch(long branchId) {
        try {
            return employeeDAO.getByBranch(branchId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get employees by branch: " + branchId, e);
        }
    }

    @Override
    public boolean create(EmployeeDTO employeeDTO) {
        try {
            return employeeDAO.insert(employeeDTO);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create employee: " + employeeDTO.getIsraeliId(), e);
        }
    }

    @Override
    public boolean update(EmployeeDTO employeeDTO) {
        try {
            return employeeDAO.update(employeeDTO);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update employee: " + employeeDTO.getIsraeliId(), e);
        }
    }

    @Override
    public boolean delete(long israeliId) {
        try {
            return employeeDAO.delete(israeliId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete employee: " + israeliId, e);
        }
    }

    @Override
    public boolean exists(long israeliId) {
        return getById(israeliId) != null;
    }

    @Override
    public boolean isActive(long israeliId) {
        EmployeeDTO employeeDTO = getById(israeliId);
        return employeeDTO != null && employeeDTO.isActive();
    }

    @Override
    public boolean hasRole(long israeliId, String role) {
        EmployeeDTO employeeDTO = getById(israeliId);
        return employeeDTO != null && employeeDTO.getRoles().contains(role);
    }

    @Override
    public List<EmployeeDTO> getByRole(String role) {
        try {
            List<EmployeeDTO> employees = employeeDAO.getAll();
            return employees.stream()
                    .filter(e -> e.getRoles().contains(role))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get employees by role: " + role, e);
        }
    }

    @Override
    public List<EmployeeDTO> getDrivers(){
        try {
            List<EmployeeDTO> employees = employeeDAO.getAll();
            List<EmployeeDTO> drivers = new ArrayList<>();
            for (EmployeeDTO employeeDTO : employees) {
                if (this.hasRole(employeeDTO.getIsraeliId(), config.ROLE_DRIVER_A) || this.hasRole(employeeDTO.getIsraeliId(), config.ROLE_DRIVER_B) || this.hasRole(employeeDTO.getIsraeliId(), config.ROLE_DRIVER_C) || this.hasRole(employeeDTO.getIsraeliId(), config.ROLE_DRIVER_D) || this.hasRole(employeeDTO.getIsraeliId(), config.ROLE_DRIVER_E)){
                    drivers.add(employeeDTO);
                }
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get drivers", e);
        }
    }

}