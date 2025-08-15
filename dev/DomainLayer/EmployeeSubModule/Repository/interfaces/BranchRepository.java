package DomainLayer.EmployeeSubModule.Repository.interfaces;

import DTOs.BranchDTO;

import java.util.List;

/**
 * Repository interface for Branch entities.
 * This interface defines methods for accessing and manipulating Branch data.
 * Uses DTOs for data transfer between layers.
 */
public interface BranchRepository {

    /**
     * Retrieves a branch by its ID.
     *
     * @param branchId The ID of the branch to retrieve
     * @return The branch DTO with the given ID, or null if not found
     */
    BranchDTO getById(long branchId);

    /**
     * Retrieves all branches.
     *
     * @return A list of all branch DTOs
     */
    List<BranchDTO> getAll();

    /**
     * Retrieves all branches in a specific area.
     *
     * @param areaCode The area code to filter by
     * @return A list of branch DTOs in the specified area
     */
    List<BranchDTO> getByAreaCode(int areaCode);

    /**
     * Retrieves a branch by its manager ID.
     *
     * @param managerId The ID of the manager
     * @return The branch DTO managed by the specified manager, or null if not found
     */
    BranchDTO getByManager(String managerId);

    /**
     * Creates a new branch.
     *
     * @param branchDTO The branch DTO to create
     * @return true if the branch was created successfully, false otherwise
     */
    boolean create(BranchDTO branchDTO);

    /**
     * Updates an existing branch.
     *
     * @param branchDTO The branch DTO to update
     * @return true if the branch was updated successfully, false otherwise
     */
    boolean update(BranchDTO branchDTO);

    /**
     * Deletes a branch.
     *
     * @param branchId The ID of the branch to delete
     * @return true if the branch was deleted successfully, false otherwise
     */
    boolean delete(long branchId);

    /**
     * Checks if a branch exists.
     *
     * @param branchId The ID of the branch to check
     * @return true if the branch exists, false otherwise
     */
    boolean exists(long branchId);

    /**
     * Retrieves a branch by its address and area code.
     *
     * @param address The address of the branch
     * @param areaCode The area code of the branch
     * @return The branch DTO with the specified address and area code, or null if not found
     */
    BranchDTO getByAddressAndAreaCode(String address, int areaCode);
}
