package DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;

/**
 * Data Transfer Object for BankAccount entities.
 * This class is used to transfer bank account data between layers.
 * It uses Jackson for JSON serialization/deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccountDTO {
    // Static ObjectMapper configured for all serialization/deserialization
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private long employeeId;
    private long bankNumber;
    private long bankBranchNumber;
    private long bankAccountNumber;

    /**
     * Default constructor for serialization
     */
    public BankAccountDTO() {
    }

    /**
     * Full constructor for creating a BankAccountDTO with all fields
     */
    public BankAccountDTO(long employeeId, long bankNumber, long bankBranchNumber, long bankAccountNumber) {
        this.employeeId = employeeId;
        this.bankNumber = bankNumber;
        this.bankBranchNumber = bankBranchNumber;
        this.bankAccountNumber = bankAccountNumber;
    }

    // Getters and setters
    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(long bankNumber) {
        this.bankNumber = bankNumber;
    }

    public long getBankBranchNumber() {
        return bankBranchNumber;
    }

    public void setBankBranchNumber(long bankBranchNumber) {
        this.bankBranchNumber = bankBranchNumber;
    }

    public long getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(long bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    /**
     * Serializes this BankAccountDTO object to a JSON string
     * 
     * @return JSON string representation of this object
     * @throws RuntimeException if serialization fails
     */
    public String serialize() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new SerializeException("Failed to serialize BankAccountDTO to JSON", e);
        }
    }

    /**
     * Deserializes a BankAccountDTO object from a JSON string
     * 
     * @param serialized JSON string representation of a BankAccountDTO object
     * @return The deserialized BankAccountDTO object
     * @throws RuntimeException if deserialization fails
     */
    public static BankAccountDTO deserialize(String serialized) {
        try {
            return objectMapper.readValue(serialized, BankAccountDTO.class);
        } catch (IOException e) {
            throw new SerializeException("Failed to deserialize BankAccountDTO from JSON", e);
        }
    }

    @Override
    public String toString() {
        return "BankAccountDTO{" +
                "employeeId=" + employeeId +
                ", bankNumber=" + bankNumber +
                ", bankBranchNumber=" + bankBranchNumber +
                ", bankAccountNumber=" + bankAccountNumber +
                '}';
    }
}