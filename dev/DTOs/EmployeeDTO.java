package DTOs;

import DomainLayer.EmployeeSubModule.Branch;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.IOException;

/**
 * Data Transfer Object for Employee entities.
 * This class is used to transfer employee data between layers.
 * It uses Jackson for JSON serialization/deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeDTO {
    // Static ObjectMapper configured for all serialization/deserialization
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private long israeliId;
    private String firstName;
    private String lastName;
    private long salary;
    private Map<String, Object> termsOfEmployment;
    private Set<String> roles;
    private LocalDate startOfEmployment;
    private boolean isActive;
    private LocalDate creationDate;
    private LocalDate updateDate;
    private long branchId; // Branch that the employee is assigned to
    private BankAccountDTO bankAccount; // Bank account information for the employee

    /**
     * Default constructor for serialization
     */
    public EmployeeDTO() {
        this.roles = new HashSet<>();
        this.termsOfEmployment = new HashMap<>();
    }


    /**
     * Full constructor for creating an EmployeeDTO with all fields including branch
     */
    public EmployeeDTO(long israeliId, String firstName, String lastName, long salary,
                      Map<String, Object> termsOfEmployment, Set<String> roles,
                      LocalDate startOfEmployment, boolean isActive,
                      LocalDate creationDate, LocalDate updateDate, Long branchId) {
        this(israeliId, firstName, lastName, salary, termsOfEmployment, roles, 
             startOfEmployment, isActive, creationDate, updateDate, branchId, null);
    }

    /**
     * Full constructor for creating an EmployeeDTO with all fields including branch and bank account
     */
    public EmployeeDTO(long israeliId, String firstName, String lastName, long salary,
                      Map<String, Object> termsOfEmployment, Set<String> roles,
                      LocalDate startOfEmployment, boolean isActive,
                      LocalDate creationDate, LocalDate updateDate, Long branchId,
                      BankAccountDTO bankAccount) {
        this.israeliId = israeliId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.termsOfEmployment = termsOfEmployment != null ? new HashMap<>(termsOfEmployment) : new HashMap<>();
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.startOfEmployment = startOfEmployment;
        this.isActive = isActive;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.branchId = branchId;
        this.bankAccount = bankAccount;
    }

    // Getters and setters
    public long getIsraeliId() {
        return israeliId;
    }

    public void setIsraeliId(long israeliId) {
        this.israeliId = israeliId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public Map<String, Object> getTermsOfEmployment() {
        return new HashMap<>(termsOfEmployment);
    }

    public void setTermsOfEmployment(Map<String, Object> termsOfEmployment) {
        this.termsOfEmployment = termsOfEmployment != null ? new HashMap<>(termsOfEmployment) : new HashMap<>();
    }

    public Set<String> getRoles() {
        return roles != null ? new HashSet<>(roles) : new HashSet<>();
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
    }


    public LocalDate getStartOfEmployment() {
        return startOfEmployment;
    }

    public void setStartOfEmployment(LocalDate startOfEmployment) {
        this.startOfEmployment = startOfEmployment;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Gets the branch that the employee is assigned to.
     * 
     * @return The branch name
     */
    public long getBranchId() {
        return branchId;
    }

    /**
     * Sets the branch that the employee is assigned to.
     * 
     * @param branchId The branch name
     */
    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    /**
     * Serializes this EmployeeDTO object to a JSON string
     * 
     * @return JSON string representation of this object
     * @throws RuntimeException if serialization fails
     */
    public String serialize() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new SerializeException("Failed to serialize EmployeeDTO to JSON", e);
        }
    }

    /**
     * Deserializes an EmployeeDTO object from a JSON string
     * 
     * @param serialized JSON string representation of an EmployeeDTO object
     * @return The deserialized EmployeeDTO object
     * @throws RuntimeException if deserialization fails
     */
    public static EmployeeDTO deserialize(String serialized) {
        try {
            return objectMapper.readValue(serialized, EmployeeDTO.class);
        } catch (IOException e) {
            throw new SerializeException("Failed to deserialize EmployeeDTO from JSON", e);
        }
    }

    /**
     * Gets the bank account information for the employee.
     * 
     * @return The bank account information
     */
    public BankAccountDTO getBankAccount() {
        return bankAccount;
    }

    /**
     * Sets the bank account information for the employee.
     * 
     * @param bankAccount The bank account information
     */
    public void setBankAccount(BankAccountDTO bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "israeliId=" + israeliId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", salary=" + salary +
                ", isActive=" + isActive +
                ", branch='" + branchId + '\'' +
                ", bankAccount=" + (bankAccount != null ? bankAccount.toString() : "null") +
                '}';
    }
}
