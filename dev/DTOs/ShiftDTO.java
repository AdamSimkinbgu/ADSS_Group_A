package DTOs;

import DomainLayer.enums.ShiftType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Data Transfer Object for Shift entities.
 * This class is used to transfer shift data between layers.
 * It uses Jackson for JSON serialization/deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftDTO {
    // Static ObjectMapper configured for all serialization/deserialization
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private long id;
    private ShiftType shiftType;
    private LocalDate shiftDate;
    private Map<String, Integer> rolesRequired;   // e.g., {"Cashier": 2, "Security": 1}
    private Map<String, Set<Long>> assignedEmployees;  // e.g., {"Cashier": [123456789, 987654321], "Security": [112233445]}

    // This field maps to AvailableEmployees in the Shift class (note the case difference)
    @JsonProperty("AvailableEmployees")
    private Set<Long> availableEmployees;  // e.g., [123456789, 555444333]

    // This field maps to isAssignedShitManager in the Shift class (note the typo in the domain class)
    @JsonProperty("isAssignedShitManager")
    private boolean isAssignedShiftManager;

    private boolean isOpen;
    private LocalTime startHour;
    private LocalTime endHour;
    private LocalDate createDate;
    private LocalDate updateDate;
    private long branchId; // Branch that this shift belongs to

    /**
     * Default constructor for serialization
     */
    public ShiftDTO() {
        this.rolesRequired = new HashMap<>();
        this.assignedEmployees = new HashMap<>();
        this.availableEmployees = new HashSet<>();
    }

    /**
     * Full constructor for creating a ShiftDTO with all fields including branch
     */
    public ShiftDTO(long id, ShiftType shiftType, LocalDate shiftDate, 
                   Map<String, Integer> rolesRequired, Map<String, Set<Long>> assignedEmployees, 
                   Set<Long> availableEmployees, boolean isAssignedShiftManager, 
                   boolean isOpen,LocalTime startHour , LocalTime endHour, LocalDate createDate, LocalDate updateDate, long branchId) {
        this.id = id;
        this.shiftType = shiftType;
        this.shiftDate = shiftDate;
        this.rolesRequired = rolesRequired != null ? new HashMap<>(rolesRequired) : new HashMap<>();
        this.assignedEmployees = assignedEmployees != null ? new HashMap<>(assignedEmployees) : new HashMap<>();
        this.availableEmployees = availableEmployees != null ? new HashSet<>(availableEmployees) : new HashSet<>();
        this.isAssignedShiftManager = isAssignedShiftManager;
        this.isOpen = isOpen;
        this.startHour = startHour;
        this.endHour = endHour;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.branchId = branchId;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public Map<String, Integer> getRolesRequired() {
        return new HashMap<>(rolesRequired);
    }

    public void setRolesRequired(Map<String, Integer> rolesRequired) {
        this.rolesRequired = rolesRequired != null ? new HashMap<>(rolesRequired) : new HashMap<>();
    }

    public Map<String, Set<Long>> getAssignedEmployees() {
        return new HashMap<>(assignedEmployees);
    }

    public void setAssignedEmployees(Map<String, Set<Long>> assignedEmployees) {
        this.assignedEmployees = assignedEmployees != null ? new HashMap<>(assignedEmployees) : new HashMap<>();
    }

    public Set<Long> getAvailableEmployees() {
        return new HashSet<>(availableEmployees);
    }

    public void setAvailableEmployees(Set<Long> availableEmployees) {
        this.availableEmployees = availableEmployees != null ? new HashSet<>(availableEmployees) : new HashSet<>();
    }

    public boolean isAssignedShiftManager() {
        return isAssignedShiftManager;
    }

    public void setAssignedShiftManager(boolean assignedShiftManager) {
        this.isAssignedShiftManager = assignedShiftManager;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {this.startHour = startHour;}

    public LocalTime getEndHour() {return endHour;}

    public void setEndHour(LocalTime endHour) {this.endHour = endHour;}

    public String getHours() {return startHour + " - " + endHour;}

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Gets the branch that this shift belongs to.
     * 
     * @return The branch name
     */
    public long getBranchId() {
        return branchId;
    }

    /**
     * Sets the branch that this shift belongs to.
     * 
     * @param branchId The branch name
     */
    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    /**
     * Serializes this ShiftDTO object to a JSON string
     * 
     * @return JSON string representation of this object
     * @throws SerializeException if serialization fails
     */
    public String serialize() {
        try {
            // Ensure collections are initialized before serialization
            if (rolesRequired == null) rolesRequired = new HashMap<>();
            if (assignedEmployees == null) assignedEmployees = new HashMap<>();
            if (availableEmployees == null) availableEmployees = new HashSet<>();

            return objectMapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new SerializeException("Failed to serialize ShiftDTO to JSON", e);
        }
    }

    /**
     * Deserializes a ShiftDTO object from a JSON string
     * 
     * @param serialized JSON string representation of a ShiftDTO object
     * @return The deserialized ShiftDTO object
     * @throws SerializeException if deserialization fails
     */
    public static ShiftDTO deserialize(String serialized) {
        if (serialized == null || serialized.trim().isEmpty()) {
            throw new SerializeException("Cannot deserialize null or empty string");
        }

        try {
            ShiftDTO dto = objectMapper.readValue(serialized, ShiftDTO.class);

            // Ensure collections are initialized after deserialization
            if (dto.rolesRequired == null) dto.rolesRequired = new HashMap<>();
            if (dto.assignedEmployees == null) dto.assignedEmployees = new HashMap<>();
            if (dto.availableEmployees == null) dto.availableEmployees = new HashSet<>();

            return dto;
        } catch (IOException e) {
            throw new SerializeException("Failed to deserialize ShiftDTO from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializes a Set of ShiftDTO objects from a JSON string
     * 
     * @param serialized JSON string representation of a Set of ShiftDTO objects
     * @return The deserialized Set of ShiftDTO objects
     * @throws SerializeException if deserialization fails
     */
    public static Set<ShiftDTO> deserializeSet(String serialized) {
        if (serialized == null || serialized.trim().isEmpty()) {
            return new HashSet<>();
        }

        try {
            Set<ShiftDTO> dtos = objectMapper.readValue(serialized, 
                objectMapper.getTypeFactory().constructCollectionType(Set.class, ShiftDTO.class));

            // Ensure collections are initialized for each DTO
            for (ShiftDTO dto : dtos) {
                if (dto.rolesRequired == null) dto.rolesRequired = new HashMap<>();
                if (dto.assignedEmployees == null) dto.assignedEmployees = new HashMap<>();
                if (dto.availableEmployees == null) dto.availableEmployees = new HashSet<>();
            }

            return dtos;
        } catch (IOException e) {
            throw new SerializeException("Failed to deserialize Set<ShiftDTO> from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializes a List of ShiftDTO objects from a JSON string
     * 
     * @param serialized JSON string representation of a List of ShiftDTO objects
     *                  or multiple JSON objects separated by newlines
     * @return The deserialized List of ShiftDTO objects
     * @throws SerializeException if deserialization fails
     */
    public static List<ShiftDTO> deserializeList(String serialized) {
        if (serialized == null || serialized.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            List<ShiftDTO> dtos;

            // First try to parse as a JSON array
            try {
                dtos = objectMapper.readValue(serialized, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ShiftDTO.class));
            } catch (Exception e) {
                // If that fails, try to parse as multiple JSON objects separated by newlines
                dtos = new ArrayList<>();
                String[] lines = serialized.split("\n");
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        ShiftDTO dto = deserialize(line);
                        dtos.add(dto);
                    }
                }
            }

            // Ensure collections are initialized for each DTO
            for (ShiftDTO dto : dtos) {
                if (dto.rolesRequired == null) dto.rolesRequired = new HashMap<>();
                if (dto.assignedEmployees == null) dto.assignedEmployees = new HashMap<>();
                if (dto.availableEmployees == null) dto.availableEmployees = new HashSet<>();
            }

            return dtos;
        } catch (Exception e) {
            throw new SerializeException("Failed to deserialize List<ShiftDTO> from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializes an array of ShiftDTO objects from a JSON string
     * 
     * @param serialized JSON string representation of an array of ShiftDTO objects
     * @return The deserialized array of ShiftDTO objects
     * @throws SerializeException if deserialization fails
     */
    public static ShiftDTO[] deserializeArray(String serialized) {
        if (serialized == null || serialized.trim().isEmpty()) {
            return new ShiftDTO[0];
        }

        try {
            ShiftDTO[] dtos = objectMapper.readValue(serialized, ShiftDTO[].class);

            // Ensure collections are initialized for each DTO
            for (ShiftDTO dto : dtos) {
                if (dto.rolesRequired == null) dto.rolesRequired = new HashMap<>();
                if (dto.assignedEmployees == null) dto.assignedEmployees = new HashMap<>();
                if (dto.availableEmployees == null) dto.availableEmployees = new HashSet<>();
            }

            return dtos;
        } catch (IOException e ) {
            throw new SerializeException("Failed to deserialize ShiftDTO[] from JSON: " + e.getMessage(), e);
        }
    }


    @Override
    public String toString() {
        return "ShiftDTO{" +
                "id=" + id +
                ", shiftType=" + shiftType +
                ", shiftDate=" + shiftDate +
                ", rolesRequired=" + (rolesRequired != null ? rolesRequired.size() : "null") +
                ", assignedEmployees=" + (assignedEmployees != null ? assignedEmployees.size() : "null") +
                ", availableEmployees=" + (availableEmployees != null ? availableEmployees.size() : "null") +
                ", isOpen=" + isOpen +
                ", isAssignedShiftManager=" + isAssignedShiftManager +
                ", startHour=" + startHour +
                ", endHour=" + endHour +
                ", branch='" + branchId + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
