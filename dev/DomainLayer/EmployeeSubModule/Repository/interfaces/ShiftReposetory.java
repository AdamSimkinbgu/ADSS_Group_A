package DomainLayer.EmployeeSubModule.Repository.interfaces;

import DTOs.ShiftDTO;
import Util.Week;

import java.util.List;

/**
 * Interface for the Shift Repository.
 * Provides methods to manage shifts and access them by various criteria.
 */
public interface ShiftReposetory {
    /**
     * Creates a new shift.
     *
     * @param shiftDTO The shift DTO to create
     * @return true if the shift was created successfully, false otherwise
     */
    boolean create(ShiftDTO shiftDTO);

    /**
     * Updates an existing shift.
     *
     * @param shiftDTO The shift DTO to update
     * @return true if the shift was updated successfully, false otherwise
     */
    boolean update(ShiftDTO shiftDTO);

    /**
     * Deletes a shift.
     *
     * @param shiftId The ID of the shift to delete
     * @return true if the shift was deleted successfully, false otherwise
     */
    boolean delete(long shiftId);

    /**
     * Retrieves a shift by its ID.
     *
     * @param shiftId The ID of the shift to retrieve
     * @return The shift DTO with the given ID, or null if not found
     */
    ShiftDTO getById(long shiftId);

    /**
     * Retrieves all shifts.
     *
     * @return A list of all shift DTOs
     */
    List<ShiftDTO> getAll();

    /**
     * Retrieves all shifts for a specific branch.
     *
     * @param branchId The ID of the branch
     * @return A list of all shift DTOs for the branch
     */
    List<ShiftDTO> getAllByBranchId(long branchId);

    /**
     * Gets all shifts for a specific week.
     *
     * @param week The week to get shifts for
     * @return A list of ShiftDTOs for the week
     */
    List<ShiftDTO> getShiftsByWeek(Week week);

    /**
     * Gets all shifts for a specific week and branch.
     *
     * @param week The week to get shifts for
     * @param branchId The branch ID to filter by
     * @return A list of ShiftDTOs for the week and branch
     */
    List<ShiftDTO> getShiftsByWeekAndBranch(Week week, long branchId);
}
