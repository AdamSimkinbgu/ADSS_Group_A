package DomainLayer.EmployeeSubModule.Repository;

import DataAccessLayer.EmployeeDAL.EmployeeDALFactory;
import DataAccessLayer.EmployeeDAL.ShiftDAO;
import DTOs.ShiftDTO;
import DomainLayer.EmployeeSubModule.Repository.interfaces.ShiftReposetory;
import DomainLayer.EmployeeSubModule.Shift;
import Util.Week;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ShiftRepositoryImpl implements ShiftReposetory {

    private final ShiftDAO shiftDAO;
    private final Set<Shift> shifts;
    private final Map<Week, Set<Shift>> weeklyShifts;

    /**
     * Loads all shifts from the database and organizes them into collections.
     */
    public ShiftRepositoryImpl() {
        try {
            this.shiftDAO = EmployeeDALFactory.getInstance().getShiftDAO();
            this.shifts = new HashSet<>();
            this.weeklyShifts = new TreeMap<>();
            loadShiftsFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize ShiftRepository", e);
        }
    }

    /**
     * Loads all shifts from the database into the in-memory collections.
     */
    private void loadShiftsFromDatabase() {
        try {
            List<ShiftDTO> shiftDTOs = shiftDAO.getAll();
            if (shiftDTOs != null && !shiftDTOs.isEmpty()) {
                for (ShiftDTO dto : shiftDTOs) {
                    Shift shift = convertDTOToShift(dto);
                    shifts.add(shift);
                    addShiftToWeekly(shift);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load shifts from database", e);
        }
    }

    /**
     * Converts a ShiftDTO to a Shift object.
     *
     * @param dto The ShiftDTO to convert
     * @return The converted Shift object
     */
    private Shift convertDTOToShift(ShiftDTO dto) {
        return new Shift(
            dto.getId(),
            dto.getShiftType(),
            dto.getShiftDate(),
            dto.getRolesRequired(),
            dto.getAssignedEmployees(),
            dto.getAvailableEmployees(),
            dto.isAssignedShiftManager(),
            dto.isOpen(),
            dto.getStartHour(),
            dto.getEndHour(),
            dto.getUpdateDate(),
            dto.getBranchId()
        );
    }

    /**
     * Adds a shift to the weekly shifts collection.
     * Organizes shifts by week and sorts them by date and shift type.
     *
     * @param shift The shift to add
     * @return true if the shift was added, false if it was already present
     */
    private boolean addShiftToWeekly(Shift shift) {
        Week week = Week.from(shift.getShiftDate());

        // Comparator: first by date, then by shift type (MORNING before EVENING)
        Comparator<Shift> byDateThenType = Comparator
                .comparing(Shift::getShiftDate)
                .thenComparing(Shift::getShiftType);

        // Add the shift to the proper week, with sorting
        return weeklyShifts
                .computeIfAbsent(week, k -> new TreeSet<>(byDateThenType))
                .add(shift);
    }

    /**
     * Adds multiple shifts to the weekly shifts collection.
     *
     * @param shiftsToAdd The shifts to add
     */
    private void addShiftsToWeekly(Set<Shift> shiftsToAdd) {
        if (shiftsToAdd == null || shiftsToAdd.isEmpty()) {
            return;
        }

        for (Shift shift : shiftsToAdd) {
            if (shift == null || shift.getShiftDate() == null) {
                throw new IllegalArgumentException("Shift or shift date cannot be null");
            }
            addShiftToWeekly(shift);
        }
    }

    @Override
    public boolean create(ShiftDTO shiftDTO) {
        try {
            // Persist to database
            boolean persisted = shiftDAO.insert(shiftDTO);

            if (persisted) {
                // Add to in-memory collections
                Shift shift = convertDTOToShift(shiftDTO);
                shifts.add(shift);
                addShiftToWeekly(shift);
            }

            return persisted;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create shift: " + shiftDTO.getId(), e);
        }
    }

    @Override
    public boolean update(ShiftDTO shiftDTO) {
        try {
            // Persist to database
            boolean persisted = shiftDAO.update(shiftDTO);

            if (persisted) {
                // Update in-memory collections
                // First remove the old shift
                shifts.removeIf(s -> s.getId() == shiftDTO.getId());

                // Then add the updated shift
                Shift shift = convertDTOToShift(shiftDTO);
                shifts.add(shift);

                // Update weekly shifts
                // First remove from all weeks
                for (Set<Shift> weekShifts : weeklyShifts.values()) {
                    weekShifts.removeIf(s -> s.getId() == shiftDTO.getId());
                }

                // Then add to the appropriate week
                addShiftToWeekly(shift);
            }

            return persisted;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update shift: " + shiftDTO.getId(), e);
        }
    }

    @Override
    public boolean delete(long shiftId) {
        try {
            // Persist to database
            boolean deleted = shiftDAO.delete(shiftId);

            if (deleted) {
                // Remove from in-memory collections
                shifts.removeIf(s -> s.getId() == shiftId);

                // Remove from weekly shifts
                for (Set<Shift> weekShifts : weeklyShifts.values()) {
                    weekShifts.removeIf(s -> s.getId() == shiftId);
                }
            }

            return deleted;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete shift: " + shiftId, e);
        }
    }

    @Override
    public ShiftDTO getById(long shiftId) {
        // First try to find in memory
        for (Shift shift : shifts) {
            if (shift.getId() == shiftId) {
                return convertShiftToDTO(shift);
            }
        }

        // If not found in memory, try the database
        try {
            ShiftDTO dto = shiftDAO.getById(shiftId);
            if (dto != null) {
                // Add to in-memory collections for future use
                Shift shift = convertDTOToShift(dto);
                shifts.add(shift);
                addShiftToWeekly(shift);
            }
            return dto;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get shift by ID: " + shiftId, e);
        }
    }

    @Override
    public List<ShiftDTO> getAll() {
        // If we have shifts in memory, convert and return them
        if (!shifts.isEmpty()) {
            return shifts.stream()
                .map(this::convertShiftToDTO)
                .toList();
        }

        // Otherwise, get from database
        try {
            List<ShiftDTO> dtos = shiftDAO.getAll();
            if (dtos != null && !dtos.isEmpty()) {
                // Add to in-memory collections for future use
                for (ShiftDTO dto : dtos) {
                    Shift shift = convertDTOToShift(dto);
                    shifts.add(shift);
                    addShiftToWeekly(shift);
                }
            }
            return dtos;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all shifts", e);
        }
    }

    /**
     * Retrieves all shifts for a specific branch.
     *
     * @param branchId The ID of the branch
     * @return A list of all shift DTOs for the branch
     */
    public List<ShiftDTO> getAllByBranchId(long branchId) {
        // First try to find in memory
        List<ShiftDTO> result = shifts.stream()
            .filter(shift -> shift.getBranchId() == branchId)
            .map(this::convertShiftToDTO)
            .toList();

        if (!result.isEmpty()) {
            return result;
        }

        // If not found in memory, try the database
        try {
            List<ShiftDTO> dtos = shiftDAO.getAllByBranchId(branchId);
            if (dtos != null && !dtos.isEmpty()) {
                // Add to in-memory collections for future use
                for (ShiftDTO dto : dtos) {
                    Shift shift = convertDTOToShift(dto);
                    shifts.add(shift);
                    addShiftToWeekly(shift);
                }
            }
            return dtos;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get shifts by branch ID: " + branchId, e);
        }
    }

    /**
     * Converts a Shift object to a ShiftDTO.
     *
     * @param shift The Shift to convert
     * @return The converted ShiftDTO
     */
    private ShiftDTO convertShiftToDTO(Shift shift) {
        return new ShiftDTO(
            shift.getId(),
            shift.getShiftType(),
            shift.getShiftDate(),
            shift.getRolesRequired(),
            shift.getAssignedEmployees(),
            shift.getAvailableEmployees(),
            shift.isAssignedShiftManager(),
            shift.isOpen(),
            shift.getStartHour(),
            shift.getEndHour(),
            shift.getCreateDate(),
            shift.getUpdateDate(),
            shift.getBranchId()
        );
    }

    /**
     * Gets all shifts for a specific week.
     *
     * @param week The week to get shifts for
     * @return A list of ShiftDTOs for the week
     */
    public List<ShiftDTO> getShiftsByWeek(Week week) {
        Set<Shift> weekShifts = weeklyShifts.get(week);
        if (weekShifts == null || weekShifts.isEmpty()) {
            return List.of(); // No shifts for this week
        }

        return weekShifts.stream()
            .map(this::convertShiftToDTO)
            .toList();
    }

    /**
     * Gets all shifts for a specific week and branch.
     *
     * @param week The week to get shifts for
     * @param branchId The branch ID to filter by
     * @return A list of ShiftDTOs for the week and branch
     */
    public List<ShiftDTO> getShiftsByWeekAndBranch(Week week, long branchId) {
        Set<Shift> weekShifts = weeklyShifts.get(week);
        if (weekShifts == null || weekShifts.isEmpty()) {
            return List.of(); // No shifts for this week
        }

        return weekShifts.stream()
            .filter(shift -> shift.getBranchId() == branchId)
            .map(this::convertShiftToDTO)
            .toList();
    }
}
