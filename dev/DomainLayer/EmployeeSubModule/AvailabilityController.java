package DomainLayer.EmployeeSubModule;

import DTOs.ShiftDTO;
import DomainLayer.EmployeeSubModule.Repository.interfaces.ShiftReposetory;
import DomainLayer.exception.UnauthorizedPermissionException;
import Util.Week;
import Util.config;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;

public class AvailabilityController {
    private final EmployeeController employeeController;
    private final ShiftController shiftController;
    private final ShiftReposetory shiftReposetory;

    public AvailabilityController(EmployeeController employeeController, ShiftController shiftController, ShiftReposetory shiftReposetory) {
        this.employeeController = employeeController;
        this.shiftController = shiftController;
        this.shiftReposetory = shiftReposetory;
    }

    public boolean markAvailable(long shiftId, long doneBy) {
        String PERMISSION = "UPDATE_AVAILABLE";
        if (!employeeController.isEmployeeAuthorised(doneBy, PERMISSION)) {
            throw new UnauthorizedPermissionException("User does not have permission to update availability");
        }
        Shift shift = shiftController.getShiftByIdAsShift(doneBy, shiftId);
        if (shift == null){
            throw new IllegalArgumentException("Shift cannot be null");
        }
        if (doneBy <= 0){
            throw new IllegalArgumentException("employeeID cannot be less than 0");
        }
        if (shift.getAvailableEmployees() != null && shift.getAvailableEmployees().contains(doneBy)) {
            throw new IllegalArgumentException("Employee already exists in available employees");
        }
        Set<Long> availableEmployees = shift.getAvailableEmployees();
        if (availableEmployees == null) {
            availableEmployees = new HashSet<>();
        }
        boolean added = availableEmployees.add(doneBy);
        shift.setAvailableEmployees(availableEmployees);
        if (added) {
            return shiftReposetory.update(convertShiftToDTO(shift));
        }
        return false;
    }

    public boolean removeAvailability(long shiftId, long doneBy) {
        String PERMISSION = "UPDATE_AVAILABLE";
        if(!employeeController.isEmployeeAuthorised(doneBy, PERMISSION)) {
            throw new UnauthorizedPermissionException("User does not have permission to update availability");
        }
        Shift shift = shiftController.getShiftByIdAsShift(doneBy, shiftId);
        if(shift == null){
            throw new IllegalArgumentException("Shift cannot be null");
        }
        if(doneBy <= 0){
            throw new IllegalArgumentException("employeeID cannot be less than 0");
        }
        Set<Long> availableEmployees = shift.getAvailableEmployees();
        if (!availableEmployees.contains(doneBy)) {
            throw new IllegalArgumentException("Employee not found in available employees");
        }
        boolean removed = availableEmployees.remove(doneBy);
        shift.setAvailableEmployees(availableEmployees);
        if (removed) {
            return shiftReposetory.update(convertShiftToDTO(shift));
        }
        return false;
    }

    public boolean isAvailable(long shiftId, long doneBy) {
        String PERMISSION = "UPDATE_AVAILABLE";
        if (!employeeController.isEmployeeAuthorised(doneBy, PERMISSION)) {
            throw new UnauthorizedPermissionException("User does not have permission to check availability");
        }
        Shift shift = shiftController.getShiftByIdAsShift(doneBy, shiftId);
        Set<Long> availableEmployees = shift.getAvailableEmployees();
        return availableEmployees.contains(doneBy);
    }

    public Map<LocalDate, Map<String, Boolean>> getWeeklyAvailability(Week week, long doneBy) {
        String PERMISSION = "UPDATE_AVAILABLE";
        if (isWeekendBlocked()) {
            throw new IllegalArgumentException("Availability update is blocked on weekends.");
        }
        if (!employeeController.isEmployeeAuthorised(doneBy, PERMISSION)) {
            throw new UnauthorizedPermissionException("User does not have permission to check availability");
        }
        String str = shiftController.getShiftsByWeek(doneBy,week);
        List<Shift> weekShifts = shiftController.deserializeArrayShifts(str);
        Map<LocalDate, Map<String, Boolean>> availability = new TreeMap<>();
        for (Shift shift : weekShifts) {
            availability
                    .computeIfAbsent(shift.getShiftDate(), k -> new HashMap<>())
                    .put(shift.getShiftType().toString(), isAvailable(shift.getId(), doneBy));
        }
        return availability;
    }

    public boolean isWeekendBlocked() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        DayOfWeek day = now.getDayOfWeek();
        LocalTime time = now.toLocalTime();

        return (day == config.BLOCK_AVAILABILITY_START_DAY && time.isAfter(config.BLOCK_AVAILABILITY_START_HOUR)) // TODO: add config for day as well
                || day == DayOfWeek.FRIDAY
                || day == DayOfWeek.SATURDAY;
    }

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

}
