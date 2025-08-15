package DomainLayer.EmployeeSubModule.Repository;

import DataAccessLayer.EmployeeDAL.BranchDAO;
import DataAccessLayer.EmployeeDAL.EmployeeDALFactory;
import DTOs.BranchDTO;
import DomainLayer.EmployeeSubModule.Repository.interfaces.BranchRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of the BranchRepository interface.
 * This class uses the BranchDAO to interact with the database.
 */
public class BranchRepositoryImpl implements BranchRepository {
    
    private final BranchDAO branchDAO;
    
    /**
     * Constructor that initializes the repository with the BranchDAO.
     */
    public BranchRepositoryImpl() {
        try {
            this.branchDAO = EmployeeDALFactory.getInstance().getBranchDAO();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize BranchRepository", e);
        }
    }
    
    /**
     * Constructor that accepts a BranchDAO for testing purposes.
     *
     * @param branchDAO The BranchDAO to use
     */
    public BranchRepositoryImpl(BranchDAO branchDAO) {
        this.branchDAO = branchDAO;
    }
    
    @Override
    public BranchDTO getById(long branchId) {
        try {
            return branchDAO.getById(branchId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get branch by ID: " + branchId, e);
        }
    }
    
    @Override
    public List<BranchDTO> getAll() {
        try {
            return branchDAO.getAll();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all branches", e);
        }
    }
    
    @Override
    public List<BranchDTO> getByAreaCode(int areaCode) {
        try {
            return branchDAO.getByAreaCode(areaCode);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get branches by area code: " + areaCode, e);
        }
    }
    
    @Override
    public BranchDTO getByManager(String managerId) {
        try {
            return branchDAO.getByManager(managerId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get branch by manager: " + managerId, e);
        }
    }
    
    @Override
    public boolean create(BranchDTO branchDTO) {
        try {
            return branchDAO.insert(branchDTO);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create branch: " + branchDTO.getBranchId(), e);
        }
    }
    
    @Override
    public boolean update(BranchDTO branchDTO) {
        try {
            return branchDAO.update(branchDTO);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update branch: " + branchDTO.getBranchId(), e);
        }
    }
    
    @Override
    public boolean delete(long branchId) {
        try {
            return branchDAO.delete(branchId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete branch: " + branchId, e);
        }
    }
    
    @Override
    public boolean exists(long branchId) {
        return getById(branchId) != null;
    }
    
    @Override
    public BranchDTO getByAddressAndAreaCode(String address, int areaCode) {
        try {
            List<BranchDTO> branches = branchDAO.getAll();
            return branches.stream()
                    .filter(b -> b.getBranchAddress().equals(address) && b.getAreaCode() == areaCode)
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get branch by address and area code: " + address + ", " + areaCode, e);
        }
    }
}