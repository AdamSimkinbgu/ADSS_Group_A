package DomainLayer.EmployeeSubModule;

import DTOs.ShiftDTO;
import DomainLayer.EmployeeSubModule.Repository.interfaces.ShiftReposetory;
import DomainLayer.EmployeeSubModule.Repository.ShiftRepositoryImpl;
import DomainLayer.enums.ShiftType;
import DomainLayer.exception.InvalidInputException;
import DomainLayer.exception.ShiftNotFoundException;
import DomainLayer.exception.UnauthorizedPermissionException;
import Util.Week;
import Util.config;


import java.time.DayOfWeek;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.stream.Collectors;

public class ShiftController {
    private final EmployeeController empCon;
    private final ShiftReposetory shiftRepository;
    private long shiftIdCounter = 1;

    // Magic Number
    private final String ShiftManagerStr = config.ROLE_SHIFT_MANAGER; // TODO: check if this is the correct role name

    public ShiftController(EmployeeController employeeController, ShiftReposetory shiftRepository) {
        this.empCon = employeeController;
        this.shiftRepository = shiftRepository;

        // Initialize the shift ID counter
        initializeShiftIdCounter();
    }

    public ShiftController(AuthorisationController authorizationController, EmployeeController employeeController, ShiftReposetory shiftRepository) {
        //this.authorizationController = authorizationController;
        this.empCon = employeeController;
        this.shiftRepository = shiftRepository;

        // Initialize the shift ID counter
        initializeShiftIdCounter();
    }

    /**
     * Initializes the shift ID counter based on the highest shift ID in the repository.
     * This ensures that new shifts will have unique IDs.
     */
    private void initializeShiftIdCounter() {
        List<ShiftDTO> shiftDTOs = shiftRepository.getAll();
        if (shiftDTOs != null && !shiftDTOs.isEmpty()) {
            for (ShiftDTO dto : shiftDTOs) {
                // Update the shift ID counter to be greater than any existing shift ID
                if (dto.getId() >= shiftIdCounter) {
                    shiftIdCounter = dto.getId() + 1;
                }
            }
        }
    }

    /**
     * Serialize a single shift to a string format.
     * Shift -> ShiftDTO -> String
     * @param shift - the shift to serialize
     * @return the serialized string representation of the shift
     */
    public String serializeShift(Shift shift) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }
        ShiftDTO dto = new ShiftDTO(shift.getId(), shift.getShiftType(), shift.getShiftDate(), shift.getRolesRequired(), shift.getAssignedEmployees(), shift.getAvailableEmployees(), shift.isAssignedShiftManager(), shift.isOpen(), shift.getStartHour(), shift.getEndHour(), shift.getCreateDate(), shift.getUpdateDate(), shift.getBranchId());
        return dto.serialize();
    }

    /**
     * Serialize a set of shifts to a string format.
     * Set<Shift> -> Set<ShiftDTO> -> String
     * Each shift is serialized to a new line.
     * @param shifts - the set of shifts to serialize
     * @return the serialized string representation of the shifts
     */
    public String serializeSetShifts(Set<Shift> shifts) {
        StringBuilder sb = new StringBuilder();
        for (Shift shift : shifts) {
            String serializedShift = serializeShift(shift);
            sb.append(serializedShift).append("\n");
        }
        return sb.toString();
    }
    /**
     * Serialize a set of shifts to a string format.
     * The shifts are sorted by date and then by type.
     * List<Shift> -> List<ShiftDTO> -> String
     * Each shift is serialized to a new line.
     * @param shifts - the set of shifts to serialize
     * @return the serialized string representation of the shifts
     */
    public String serializeArrayShifts(List<Shift> shifts) {
        // Sort the list by date and then by type
        shifts = shifts.stream()
                .sorted(Comparator.comparing(Shift::getShiftDate).thenComparing(Shift::getShiftType))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (Shift shift : shifts) {
            String serializedShift = serializeShift(shift);
            sb.append(serializedShift).append("\n");
        }
        return sb.toString();
    }
    public String deserializeShift(String serializedShift) {
        if (serializedShift == null || serializedShift.isEmpty()) {
            throw new IllegalArgumentException("Serialized shift cannot be null or empty");
        }
        ShiftDTO dto = ShiftDTO.deserialize(serializedShift);
        return dto.toString();
    }

    /**
     * Deserialize a string representation of shifts to a list of Shift objects.
     * Each line in the string represents a serialized ShiftDTO.
     * String -> ShiftDTO -> Shift
     * @param serializedShifts - the serialized shifts as String
     * @return a list of Shift objects
     */
    public List<Shift> deserializeArrayShifts(String serializedShifts) {
        if (serializedShifts == null || serializedShifts.isEmpty()) {
            throw new IllegalArgumentException("Serialized shifts cannot be null or empty");
        }
        String[] serializedShiftArray = serializedShifts.split("\n");
        List<Shift> shiftsList = new ArrayList<>();
        for (String serializedShift : serializedShiftArray) {
            ShiftDTO dto = ShiftDTO.deserialize(serializedShift);
            Shift shift = new Shift(dto.getId(), dto.getShiftType(), dto.getShiftDate(), dto.getRolesRequired(), dto.getAssignedEmployees(), dto.getAvailableEmployees(), dto.isAssignedShiftManager(), dto.isOpen(),dto.getStartHour(),dto.getEndHour(),dto.getUpdateDate(),dto.getBranchId());
            shiftsList.add(shift);
        }
        return shiftsList;
    }

    protected Shift getShiftByIdAsShift(long doneBy, long shiftId) {
        String PERMISSION_REQUIRED = "GET_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get shift");
        }
        if (shiftId <= 0) {
            throw new IllegalArgumentException("Shift ID must be a positive number");
        }

        // Get the shift from the repository
        ShiftDTO shiftDTO = shiftRepository.getById(shiftId);

        if (shiftDTO == null) {
            return null;
        }

        // Convert DTO to Shift
        return new Shift(
            shiftDTO.getId(),
            shiftDTO.getShiftType(),
            shiftDTO.getShiftDate(),
            shiftDTO.getRolesRequired(),
            shiftDTO.getAssignedEmployees(),
            shiftDTO.getAvailableEmployees(),
            shiftDTO.isAssignedShiftManager(),
            shiftDTO.isOpen(),
            shiftDTO.getStartHour(),
            shiftDTO.getEndHour(),
            shiftDTO.getUpdateDate(),
            shiftDTO.getBranchId()
        );
    }

    /**
     * add a shift to the system - create a new shift for the branch of the employee
     * @param doneBy                 employee who is creating the shift
     * @param shiftType              morning or evening
     * @param date                   date of the shift
     * @param rolesRequired          number of employees of each role required for the shift
     * @param assignedEmployees      employees assigned to the shift
     * @param isAssignedShiftManager true if the shift has a shift manager assigned, false otherwise
     * @param isOpen                 true if the place is open
     * @param startHour              the hours of the shift
     * @param endHour                the end hour of the shift
     * @param updateDate             the date of the last update
     * @return true if the shift was created successfully, false otherwise
     */
    public boolean createShift(long doneBy,ShiftType shiftType, LocalDate date, Map<String, Integer> rolesRequired, Map<String, Set<Long>> assignedEmployees, Set<Long> availableEmployees, boolean isAssignedShiftManager, boolean isOpen,LocalTime startHour , LocalTime endHour ,LocalDate updateDate) {
        String PERMISSION_REQUIRED = "CREATE_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to create shift");
        }

        // Check if a shift with the same date and type already exists
        List<ShiftDTO> existingShifts = shiftRepository.getAll();
        boolean shiftExists = existingShifts.stream()
            .anyMatch(s -> s.getShiftDate().equals(date) && s.getShiftType().equals(shiftType));
        if (shiftExists) {
            throw new RuntimeException("Shift already exists");
        }

        if (shiftType == null || date == null || rolesRequired == null || assignedEmployees == null) {
            throw new IllegalArgumentException("Shift type, date, roles required, and assigned employees cannot be null");
        }
        if (startHour == null || endHour == null || startHour.isAfter(endHour)) {
            throw new IllegalArgumentException("Start hour and end hour cannot be null");
        }
        // Checks if a shift manager is assigned as a required role
        // TODO: test that in case of no shift manager there is no exception thrown
        if (!rolesRequired.containsKey(ShiftManagerStr) || rolesRequired.get(ShiftManagerStr) <= 0) {
            throw new InvalidInputException("At least one shift manager is required for every shift");
        }
        long branch = empCon.getEmployeeByIsraeliId(doneBy).getBranchId(); // Get the branch of the employee that is creating the shift
        initializeShiftIdCounter();

        // Create a new ShiftDTO
        ShiftDTO shiftDTO = new ShiftDTO(
            shiftIdCounter++, 
            shiftType, 
            date, 
            rolesRequired, 
            assignedEmployees, 
            availableEmployees, 
            isAssignedShiftManager, 
            isOpen, 
            startHour, 
            endHour, 
            LocalDate.now(), // Create date is now
            updateDate,
            branch
        );

        // Persist to database through repository
        return shiftRepository.create(shiftDTO);
    }


    /**
     * Create shifts from the next Sunday after the given date for one full week (Sunday to Saturday).
     * The shifts will be created for the branch of the employee creating the shifts.
     *
     * @param doneBy employee who is creating the shifts
     * @param startDate the date to start from (shifts will begin from the next Sunday)
     * @param rolesRequired number of employees of each role required for the shifts
     * @return true if all shifts were created successfully, false otherwise
     */
    public boolean createWeeklyShifts(long doneBy, LocalDate startDate, Map<String, Integer> rolesRequired) {
        final String PERMISSION_REQUIRED = "CREATE_SHIFT";

        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to create weekly shifts");
        }

        if (startDate == null || rolesRequired == null) {
            throw new IllegalArgumentException("Start date and roles required cannot be null");
        }
//        // Move to the next Sunday if not already Sunday
//        if (startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
//            int daysUntilSunday = (DayOfWeek.SUNDAY.getValue() - startDate.getDayOfWeek().getValue() + 7) % 7;
//            startDate = startDate.plusDays(daysUntilSunday);
//        }

        long branch = empCon.getEmployeeByIsraeliId(doneBy).getBranchId(); // Get the branch of the employee that is creating the shifts

        for (int day = 0; day < 7; day++) {
            LocalDate date = startDate.plusDays(day);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // Morning shift for all days except Saturday
            if (dayOfWeek != DayOfWeek.SATURDAY) {
                if (!AddNewShift(date, ShiftType.MORNING, rolesRequired, branch)) return false;
            }

            // Evening shift for Sunday to Thursday and Saturday (not Friday)
            if (dayOfWeek != DayOfWeek.FRIDAY) {
                if (!AddNewShift(date, ShiftType.EVENING, rolesRequired, branch)) return false;
            }
        }

        return true;
    }

    /**
     * create and add
     * @param date the date of the shift
     * @param type the type of the shift (morning or evening)
     * @param rolesRequired number of employees of each role required for the shift
     * @param branchId the branchId that this shift belongs to
     * @return true if the shift was added successfully, false otherwise
     */
    private boolean AddNewShift(LocalDate date, ShiftType type, Map<String, Integer> rolesRequired, long branchId) {
        // Check if a shift of this type already exists for the date
        List<ShiftDTO> existingShifts = shiftRepository.getAllByBranchId(branchId);
        boolean shiftExists = existingShifts.stream()
            .anyMatch(s -> s.getShiftDate().equals(date) && s.getShiftType() == type);
        if (shiftExists) {
            throw new RuntimeException(type + " shift already exists for " + date);
        }

        // Checks if a shift manager is assigned as a required role
        if (!rolesRequired.containsKey(ShiftManagerStr)) {
            throw new InvalidInputException("Shift manager is required for every shift");
        }

        Set<Long> availableEmployees = new HashSet<>();
        Map<String, Set<Long>> assignedEmployees = new HashMap<>();
        boolean isOpen = (type == ShiftType.MORNING && date.getDayOfWeek() != DayOfWeek.SATURDAY)
                || (type == ShiftType.EVENING && date.getDayOfWeek() != DayOfWeek.FRIDAY && date.getDayOfWeek() != DayOfWeek.SATURDAY);

        LocalTime startHour = type == ShiftType.MORNING ? config.START_HOUR_MORNING : config.START_HOUR_EVENING;
        LocalTime endHour = type == ShiftType.MORNING ? config.END_HOUR_MORNING : config.END_HOUR_EVENING;

        // Create a new ShiftDTO
        ShiftDTO shiftDTO = new ShiftDTO(
            shiftIdCounter++, 
            type, 
            date, 
            rolesRequired, 
            assignedEmployees, 
            availableEmployees, 
            false, 
            isOpen, 
            startHour, 
            endHour, 
            LocalDate.now(), 
            LocalDate.now(),
            branchId
        );

        // Persist to database through repository
        return shiftRepository.create(shiftDTO);
    }

    /**
     * remove a shift from the system
     * @param doneBy   employee who is removing the shift
     * @param shiftId id of the shift to remove
     * @return true if the shift was removed successfully, false otherwise
     */
    public boolean removeShiftByID(long doneBy, long shiftId) {
        String PERMISSION_REQUIRED = "REMOVE_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to remove shift");
        }
        if (shiftId <= 0) {
            throw new IllegalArgumentException("Shift ID must be a positive number");
        }

        // Check if the shift exists
        ShiftDTO shiftDTO = shiftRepository.getById(shiftId);
        if (shiftDTO == null) {
            throw new ShiftNotFoundException("Shift does not exist");
        }

        // Delete from repository
        return shiftRepository.delete(shiftId);
    }

    /**
     * Removes a shift from the list of shifts based on the specified date, shift type, and the branch of the employee making the request.
     *
     * @param doneBy    The ID of the employee requesting the shift removal.
     * @param date      The date of the shift to be removed.
     * @param shiftType The type of the shift to be removed (e.g., morning, evening).
     * @return {@code true} if the shift was successfully removed.
     * @throws UnauthorizedPermissionException if the employee does not have sufficient permission to remove a shift.
     * @throws ShiftNotFoundException if the shift does not exist.
     */
    public boolean removeShift(long doneBy, LocalDate date, ShiftType shiftType) {
        String PERMISSION_REQUIRED = "REMOVE_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to delete shift");
        }
        long branchId = empCon.getEmployeeByIsraeliId(doneBy).getBranchId(); // Get the branch of the employee that is requesting the shifts

        // Find the shift in the repository
        List<ShiftDTO> branchShifts = shiftRepository.getAllByBranchId(branchId);
        ShiftDTO shiftToRemove = branchShifts.stream()
                .filter(shift -> shift.getShiftDate().equals(date) && shift.getShiftType().equals(shiftType))
                .findFirst()
                .orElse(null);

        if (shiftToRemove == null) {
            throw new ShiftNotFoundException("Shift does not exist");
        }

        // Delete from repository
        return shiftRepository.delete(shiftToRemove.getId());
    }

    /**
     * update a shift in the system
     * @param doneBy                 employee who is updating the shift
     * @param shiftId                id of the shift to update
     * @param shiftType              morning or evening
     * @param date                   date of the shift
     * @param isAssignedShiftManager true if the shift has a shift manager assigned, false otherwise
     * @param isOpen                 true if the place is open
     * @param startHour              the start hour of the shift
     * @param endHour                the end hour of the shift
     * @param updateDate             the date of the last update
     * @return true if the shift was updated successfully, false otherwise
     */
    public boolean updateShift(long doneBy, long shiftId, ShiftType shiftType, LocalDate date, boolean isAssignedShiftManager, boolean isOpen, LocalTime startHour, LocalTime endHour, LocalDate updateDate) {
        String PERMISSION_REQUIRED = "UPDATE_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to update shift");
        }
        if (shiftId <= 0) {
            throw new IllegalArgumentException("Shift ID must be a positive number");
        }
        if (shiftType == null || date == null) {
            throw new IllegalArgumentException("Shift type, date cannot be null");
        }
//        if (date.isBefore(LocalDate.now())) {
//            throw new IllegalArgumentException("Date cannot be in the past");
//        }
        if (updateDate == null) {
            throw new IllegalArgumentException("Update date cannot be null");
        }
//        if (updateDate.isBefore(date)) {
//            throw new IllegalArgumentException("Update date cannot be before the shift date");
//        }
//        if (updateDate.isAfter(LocalDate.now())) {
//            throw new IllegalArgumentException("Update date cannot be in the future");
//        }
        if (isAssignedShiftManager) {
            throw new IllegalArgumentException("Shift manager must be assigned if isAssignedShiftManager is true");
        }

        // Get the shift from the repository
        ShiftDTO shiftDTO = shiftRepository.getById(shiftId);
        if (shiftDTO == null) {
            throw new ShiftNotFoundException("Shift does not exist");
        }

        // Update the shift DTO
        shiftDTO.setShiftType(shiftType);
        shiftDTO.setShiftDate(date);
        shiftDTO.setAssignedShiftManager(isAssignedShiftManager);
        shiftDTO.setOpen(isOpen);
        shiftDTO.setStartHour(startHour);
        shiftDTO.setEndHour(endHour);
        shiftDTO.setUpdateDate(updateDate);

        // Persist changes to database through repository
        return shiftRepository.update(shiftDTO);
    }

    /**
     * get a shift from the system
     * @param doneBy employee who is requesting the shift
     * @param shiftId id of the shift to get
     * @return the shift if it exists, null otherwise
     */
    public String getShiftByID(long doneBy, long shiftId) {
        String PERMISSION_REQUIRED = "GET_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get shift");
        }
        if (shiftId <= 0) {
            throw new IllegalArgumentException("Shift ID must be a positive number");
        }

        // Get the shift from the repository
        ShiftDTO shiftDTO = shiftRepository.getById(shiftId);

        if (shiftDTO == null) {
            throw new ShiftNotFoundException("Shift does not exist");
        }

        return shiftDTO.serialize();
    }

    /**
     * get all shifts from the system
     * @param doneBy employee who is requesting the shifts
     * @return all shifts
     */
    public String getAllShifts(long doneBy) {
        String PERMISSION_REQUIRED = "VIEW_SHIFT"; // TODO: check if this is the correct permission - super user / management user
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get all shifts");
        }

        // Get all shifts from the repository
        List<ShiftDTO> allShifts = shiftRepository.getAll();

        if (allShifts.isEmpty())
            return ""; // No shifts found

        // Convert DTOs to Shifts
        List<Shift> shiftsList = allShifts.stream()
            .map(dto -> new Shift(
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
                dto.getBranchId()))
            .collect(Collectors.toList());

        return serializeArrayShifts(shiftsList);
    }

    /**
     * get all shifts from the system for a specific branch
     * @param doneBy employee who is requesting the shifts
     * @param branch branch of the shifts (if null, returns shifts for all branches)
     * @return all shifts for the branch
     */
    public String getAllShiftsByBranch(long doneBy, long branch) {
        String PERMISSION_REQUIRED = "GET_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get all shifts");
        }

        // Get all shifts for the branch from the repository
        List<ShiftDTO> branchShifts = shiftRepository.getAllByBranchId(branch);

        if (branchShifts.isEmpty())
            throw new ShiftNotFoundException("No shifts were found for the branch: " + branch);

        // Convert DTOs to Shifts
        List<Shift> shiftsList = branchShifts.stream()
            .map(dto -> new Shift(
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
                dto.getBranchId()))
            .collect(Collectors.toList());

        return serializeArrayShifts(shiftsList);
    }

    /**
     * get all shifts for a specific date for the employee's branch
     * @param doneBy employee who is requesting the shifts
     * @param date date of the shifts
     * @return all shifts for the date
     */
    public String getAllShiftsByDate(long doneBy, LocalDate date) {
        long branchId = empCon.getEmployeeByIsraeliId(doneBy).getBranchId(); // Get the branch of the employee that is requesting the shifts
        return getAllShiftsByDateAndBranch(doneBy, date, branchId);
    }

    /**
     * get all shifts for a specific date and branch
     * @param doneBy employee who is requesting the shifts
     * @param date date of the shifts
     * @param branchId branch of the shifts (if null, returns shifts for all branches)
     * @return all shifts for the date and branch
     */
    public String getAllShiftsByDateAndBranch(long doneBy, LocalDate date, long branchId) {
        String PERMISSION_REQUIRED = "GET_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get shifts by date");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        // Get all shifts for the branch from the repository
        List<ShiftDTO> branchShifts = shiftRepository.getAllByBranchId(branchId);

        // Filter shifts by date
        List<ShiftDTO> filteredShifts = branchShifts.stream()
            .filter(dto -> dto.getShiftDate().equals(date))
            .collect(Collectors.toList());

        if (filteredShifts.isEmpty())
            throw new ShiftNotFoundException("No shifts found for the date and branch");

        // Convert DTOs to Shifts
        List<Shift> shiftsList = filteredShifts.stream()
            .map(dto -> new Shift(
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
                dto.getBranchId()))
            .collect(Collectors.toList());

        return serializeArrayShifts(shiftsList);
    }

    /**
     * get all shifts for a specific employee
     * @param doneBy employee who is requesting the shifts
     * @param employeeID employee to get shifts for
     * @return all shifts for the employee
     */
    public String getShiftsByEmployee(long doneBy, long employeeID) {
        long branchId = empCon.getEmployeeByIsraeliId(doneBy).getBranchId(); // Get the branch of the employee that is requesting the shifts
        return getShiftsByEmployeeAndBranch(doneBy, employeeID, branchId);
    }

    /**
     * get all shifts for a specific employee and branch
     * @param doneBy employee who is requesting the shifts
     * @param employeeID employee to get shifts for
     * @param branchId branch of the shifts (if null, returns shifts for all branches)
     * @return all shifts for the employee and branch
     */
    public String getShiftsByEmployeeAndBranch(long doneBy, long employeeID, long branchId) {
        String PERMISSION_REQUIRED = "GET_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get shifts by employee");
        }
        if (employeeID <= 0) {
            throw new IllegalArgumentException("Employee ID must be a positive number");
        }

        // Get all shifts for the branch from the repository
        List<ShiftDTO> branchShifts = shiftRepository.getAllByBranchId(branchId);

        // Filter shifts by employee
        List<ShiftDTO> filteredShifts = branchShifts.stream()
                .filter(dto -> dto.getAssignedEmployees().values().stream().anyMatch(set -> set.contains(employeeID)))
                .collect(Collectors.toList());

        if (filteredShifts.isEmpty())
            throw new ShiftNotFoundException("No shifts found for the employee in branch: " + branchId);

        // Convert DTOs to Shifts
        List<Shift> shiftsList = filteredShifts.stream()
            .map(dto -> new Shift(
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
                dto.getBranchId()))
            .collect(Collectors.toList());

        return serializeArrayShifts(shiftsList);
    }

    /**
     * get shift by date and shift type for the employee's branch
     * @param doneBy    employee who is requesting the shift
     * @param date      date of the shift
     * @param shiftType morning or evening
     * @return the shift if it exists, null otherwise
     */
    public String getshift(long doneBy, LocalDate date, ShiftType shiftType) {
        long branchId = empCon.getEmployeeByIsraeliId(doneBy).getBranchId(); // Get the branch of the employee that is requesting the shifts
        return getshiftByBranch(doneBy, date, shiftType, branchId);
    }

    /**
     * get shift by date, shift type, and branch
     * @param doneBy    employee who is requesting the shift
     * @param date      date of the shift
     * @param shiftType morning or evening
     * @param branchId    branch of the shift (if null, returns shift for any branch)
     * @return the shift if it exists, null otherwise
     */
    public String getshiftByBranch(long doneBy, LocalDate date, ShiftType shiftType, long branchId) {
        String PERMISSION_REQUIRED = "GET_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get shifts by date and type");
        }
        if (date == null || shiftType == null) {
            throw new IllegalArgumentException("Date and shift type cannot be null");
        }

        // Get all shifts for the branch from the repository
        List<ShiftDTO> branchShifts = shiftRepository.getAllByBranchId(branchId);

        // Find the shift with the specified date and type
        return branchShifts.stream()
                .filter(dto -> dto.getShiftDate().equals(date) && dto.getShiftType().equals(shiftType))
                .findFirst()
                .map(ShiftDTO::serialize)
                .orElseThrow(() -> {
                    return new ShiftNotFoundException("Shift does not exist for the specified date, type, and branch: " + branchId);
                });
    }

    /**
     * update the roles required for a shift
     * @param doneBy        employee who is updating the shift
     * @param shiftId       id of the shift to update
     * @param rolesRequired number of employees of each role required for the shift
     * @return true if the roles were updated successfully, false otherwise
     */
    public boolean updateRolesRequired(long doneBy, long shiftId, String role, Integer rolesRequired) {
        String PERMISSION_REQUIRED = "ROLE_REQUIRED";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to update shifts (roles required)");
        }
        // Check if its not removing the shift manager role
        if (role.equals(ShiftManagerStr) && rolesRequired <= 0) {
            throw new IllegalArgumentException("Shift manager role cannot be removed, at least 1 shift manager in the shift is required");
        }
        if (rolesRequired == null) {
            throw new IllegalArgumentException("Roles required cannot be null");
        }
        if (shiftId <= 0) {
            throw new IllegalArgumentException("Shift ID must be a positive number");
        }

        // Get the shift from the repository
        ShiftDTO shiftDTO = shiftRepository.getById(shiftId);
        if (shiftDTO == null) {
            throw new ShiftNotFoundException("Shift does not exist");
        }

        // Update the roles required
        Map<String, Integer> rolesRequiredMap = shiftDTO.getRolesRequired();
        rolesRequiredMap.put(role, rolesRequired);
        shiftDTO.setRolesRequired(rolesRequiredMap);
        shiftDTO.setUpdateDate(LocalDate.now());

        // Persist changes to database through repository
        return shiftRepository.update(shiftDTO);
    }

    /**
     * update the shift manager for a shift
     * @param doneBy                employee who is updating the shift
     * @param shiftId                id of the shift to update
     * @param isAssignedShiftManager true if the shift has a shift manager assigned, false otherwise
     * @return true if the shift manager was updated successfully, false otherwise
     */
    public boolean updateShiftManager(long doneBy, long shiftId, boolean isAssignedShiftManager) {
        String PERMISSION_REQUIRED = "UPDATE_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to update shifts");
        }
        if (shiftId <= 0) {
            throw new IllegalArgumentException("Shift ID must be a positive number");
        }

        // Get the shift from the repository
        ShiftDTO shiftDTO = shiftRepository.getById(shiftId);
        if (shiftDTO == null) {
            throw new ShiftNotFoundException("Shift does not exist");
        }

        // Update the shift manager status
        shiftDTO.setAssignedShiftManager(isAssignedShiftManager);
        shiftDTO.setUpdateDate(LocalDate.now());

        // Persist changes to database through repository
        return shiftRepository.update(shiftDTO);
    }

    /**
     * update the open status for a shift
     * @param doneBy employee who is updating the shift
     * @param shiftId id of the shift to update
     * @param isOpen  true if the place is open, false otherwise
     * @return true if the open status was updated successfully, false otherwise
     */
    public boolean updateOpenStatus(long doneBy, long shiftId, boolean isOpen) {
        String PERMISSION_REQUIRED = "UPDATE_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to update shifts");
        }
        if (shiftId <= 0) {
            throw new IllegalArgumentException("Shift ID must be a positive number");
        }

        // Get the shift from the repository
        ShiftDTO shiftDTO = shiftRepository.getById(shiftId);
        if (shiftDTO == null) {
            throw new ShiftNotFoundException("Shift does not exist");
        }

        // Update the open status
        shiftDTO.setOpen(isOpen);
        shiftDTO.setUpdateDate(LocalDate.now());

        // Persist changes to database through repository
        return shiftRepository.update(shiftDTO);
    }


    /**
     * Removes a role requirement from a shift.
     * @param doneBy   employee who is removing the shift
     * @param shiftId the ID of the shift
     * @param role    the role to remove
     * @return true if the role was removed, false if it was not present
     * @throws ShiftNotFoundException if the shift does not exist
     */
    public boolean removeRoleRequired(long doneBy, long shiftId, String role) {
        String PERMISSION_REQUIRED = "ROLE_REQUIRED";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to update Role required");
        }

        // Check if its not removing the shift manager role
        if (role.equals(ShiftManagerStr)) {
            throw new IllegalArgumentException("Shift manager role cannot be removed.");
        }
        if (role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        if (shiftId <= 0) {
            throw new IllegalArgumentException("Shift ID must be a positive number");
        }

        // Get the shift from the repository
        ShiftDTO shiftDTO = shiftRepository.getById(shiftId);
        if (shiftDTO == null) {
            throw new ShiftNotFoundException("Shift does not exist");
        }

        // Check if the role exists in the shift
        if (!shiftDTO.getRolesRequired().containsKey(role)) {
            throw new IllegalArgumentException("Role not found in the shift");
        }

        // Check if there are employees assigned to this role
        if (shiftDTO.getAssignedEmployees().containsKey(role) && !shiftDTO.getAssignedEmployees().get(role).isEmpty()) {
            throw new IllegalArgumentException("Cannot remove role that has assigned employees");
        }

        // Remove the role
        Map<String, Integer> requiredRoles = shiftDTO.getRolesRequired();
        boolean removed = requiredRoles.remove(role) != null;
        shiftDTO.setUpdateDate(LocalDate.now());

        if (removed) {
            // Persist changes to database through repository
            return shiftRepository.update(shiftDTO);
        }

        return false;
    }

    public String getRoles(long doneBy) {
        String PERMISSION_REQUIRED = "GET_ROLES";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get all roles");
        }
        if (doneBy <= 0) {
            throw new IllegalArgumentException("Employee ID must be a positive number");
        }

        return empCon.getAllRolesAsString();
    }


    public String getShiftsByWeek(long doneBy, Week week) {
        return getShiftsByWeekAndBranch(doneBy, week, null);
    }

    /**
     * Get shifts for a specific week and branch
     * @param doneBy employee who is requesting the shifts
     * @param week the week to get shifts for
     * @param branch branch of the shifts (if null, returns shifts for all branches)
     * @return serialized shifts for the week and branch
     */
    public String getShiftsByWeekAndBranch(long doneBy, Week week, String branch) {
        String PERMISSION_REQUIRED = "GET_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get shifts by week");
        }

        // Use the repository to get shifts by week and branch
        List<ShiftDTO> dtos;
        if (branch == null) {
            dtos = shiftRepository.getShiftsByWeek(week);
        } else {
            try {
                long branchId = Long.parseLong(branch);
                dtos = shiftRepository.getShiftsByWeekAndBranch(week, branchId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Branch ID must be a number");
            }
        }

        if (dtos == null || dtos.isEmpty()) {
            return null; // No shifts for this week and branch
        }

        // Convert DTOs to Shifts
        List<Shift> shifts = dtos.stream()
            .map(dto -> new Shift(
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
                dto.getBranchId()))
            .collect(Collectors.toList());

        // Sort shifts by date and shift type
        shifts.sort(Comparator.comparing(Shift::getShiftDate).thenComparing(Shift::getShiftType));

        return serializeArrayShifts(shifts);
    }


    // This method is no longer needed as the repository manages the collections
    // The repository's addShiftsToWeekly method handles this functionality

    // This method is no longer needed as the repository manages the collections
    // The repository's addShiftToWeekly method handles this functionality

    public Shift getShiftbyDateAndTime(long doneBy, LocalDate date, LocalTime hour, long branchId) {
        return getShiftbyDateTimeAndBranch( date, hour, branchId);
    }

    /**
     * Retrieves a shift for a given date, time, and branch ID.
     *
     * Filters the list of shifts to find the one that matches the specified date and branch ID,
     * and checks if the given time falls within the shift's start and end hours.
     *
     * @param date the date for which the shift is being retrieved; must not be null
     * @param hour the time of day for which the shift is being retrieved; must not be null
     * @param branchId the unique identifier of the branch for which the shift is being retrieved
     * @return the shift that matches the specified criteria, or null if no such shift exists
     * @throws IllegalArgumentException if date or hour is null
     */
    public Shift getShiftbyDateTimeAndBranch( LocalDate date, LocalTime hour, long branchId) {
        String PERMISSION_REQUIRED = "GET_SHIFT";
//        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
//            throw new UnauthorizedPermissionException("User does not have permission to get shifts by date and time");
//        }
        if (date == null || hour == null) {
            throw new IllegalArgumentException("Date and hour cannot be null");
        }

        // Get all shifts for the branch from the repository
        List<ShiftDTO> branchShifts = shiftRepository.getAllByBranchId(branchId);

        // Filter shifts by date and time
        return branchShifts.stream()
                .filter(dto -> dto.getShiftDate().equals(date) && 
                               dto.getStartHour().isBefore(hour) && 
                               dto.getEndHour().isAfter(hour))
                .map(dto -> new Shift(
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
                    dto.getBranchId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all shifts assigned to a specific employee.
     * The requesting employee must have the "GET_SHIFT" permission.
     * An exception is thrown if the permission is not granted or the provided employee ID is invalid.
     *
     * @param doneBy     the ID of the employee requesting the shifts
     * @param employeeId the ID of the employee whose shifts are being retrieved
     * @return a serialized string representation of all shifts assigned to the specified employee
     * @throws UnauthorizedPermissionException if the requesting employee does not have the required permission
     * @throws IllegalArgumentException        if the provided employee ID is not a positive number
     */
    public String getShiftByEmployee(long doneBy, long employeeId) {
        return getShiftByEmployeeAndBranch(doneBy, employeeId, null);
    }

    /**
     * Retrieves all shifts assigned to a specific employee for a specific branch.
     * The requesting employee must have the "GET_SHIFT" permission.
     * An exception is thrown if the permission is not granted or the provided employee ID is invalid.
     *
     * @param doneBy     the ID of the employee requesting the shifts
     * @param employeeId the ID of the employee whose shifts are being retrieved
     * @param branch     the branch to filter shifts by (if null, returns shifts for all branches)
     * @return a serialized string representation of all shifts assigned to the specified employee for the specified branch
     * @throws UnauthorizedPermissionException if the requesting employee does not have the required permission
     * @throws IllegalArgumentException        if the provided employee ID is not a positive number
     */
    public String getShiftByEmployeeAndBranch(long doneBy, long employeeId, String branch) {
        String PERMISSION_REQUIRED = "GET_SHIFT";
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get shifts by employee");
        }
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be a positive number");
        }

        // Get all shifts from the repository
        List<ShiftDTO> allShifts = shiftRepository.getAll();

        // Filter shifts by employee and branch
        List<ShiftDTO> filteredShifts = allShifts.stream()
                .filter(dto -> dto.getAssignedEmployees().values().stream().anyMatch(set -> set.contains(employeeId)))
                .filter(dto -> branch == null || branch.equals(String.valueOf(dto.getBranchId())))
                .collect(Collectors.toList());

        if (filteredShifts.isEmpty()) {
            return ""; // No shifts found
        }

        // Convert DTOs to Shifts for serialization
        List<Shift> employeeShifts = filteredShifts.stream()
            .map(dto -> new Shift(
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
                dto.getBranchId()))
            .collect(Collectors.toList());

        return serializeArrayShifts(employeeShifts);
    }

    public long getBranchIdByAddress(String address, int areaCode) {
        return empCon.getBranchIdByAddress(address, areaCode);
    }


    public boolean isDriverOnShiftAt(long driverId, LocalDate date, LocalTime time, String address, int areaCode) {
        boolean isOnShift = false;
        // Get the branch ID based on the address and area code
        long branchId = empCon.getBranchIdByAddress(address, areaCode);
        if (branchId <= 0) {
            throw new IllegalArgumentException("Invalid branch ID for the given address and area code");
        }
        // Retrieve all shifts for the branch on the specified date
        List<ShiftDTO> branchShifts = shiftRepository.getAllByBranchId(branchId);
        if (branchShifts == null || branchShifts.isEmpty()) {
            throw new ShiftNotFoundException("No shifts found for the branch on the specified date");
        }
        // Check if the driver is assigned to any shift on the specified date and time
        for (ShiftDTO shift : branchShifts) {
            if (shift.getShiftDate().equals(date) &&
                shift.getStartHour().isBefore(time) &&
                shift.getEndHour().isAfter(time)) {
                // Check if the driver is assigned to this shift
                if (shift.getAssignedEmployees().getOrDefault("Driver", new HashSet<>()).contains(driverId)) {
                    isOnShift = true;
                    break;
                }
            }
        }
        return isOnShift;
    }

    public String getCurrentShift(long doneBy) {
        String PERMISSION_REQUIRED = "VIEW_SHIFT"; // TODO: check if this is the correct permission for low-level access
        if (!empCon.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to get current shift");
        }
        long branchId = empCon.getEmployeeByIsraeliId(doneBy).getBranchId(); // Get the branch of the employee that is requesting the shifts
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Get all shifts for the branch from the repository
        List<ShiftDTO> branchShifts = shiftRepository.getAllByBranchId(branchId);

        // Find the current shift based on today's date and current time
        return branchShifts.stream()
                .filter(dto -> dto.getShiftDate().equals(today) &&
                               dto.getStartHour().isBefore(now) &&
                               dto.getEndHour().isAfter(now))
                .findFirst()
                .map(ShiftDTO::serialize)
                .orElseThrow(() -> new ShiftNotFoundException("No current shift found for today in branch: " + branchId));
    }

    public List<String> getAvailableBranches() {

        // Get all branches from the employee controller
        return empCon.getAllBranches();
    }
}
