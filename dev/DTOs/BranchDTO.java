package DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchDTO {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private long branchId;
    private String branchName;
    private int areaCode;
    private String branchAddress;
    private String managerID;

    public BranchDTO() {
    }

    public BranchDTO(long branchId, String branchName, int areaCode, String branchAddress, String managerID) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.areaCode = areaCode;
        this.branchAddress = branchAddress;
        this.managerID = managerID;
    }

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public int getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }

    public String serialize() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new SerializeException("Failed to serialize BranchDTO to JSON", e);
        }
    }

    public static BranchDTO deserialize(String serialized) {
        try {
            return objectMapper.readValue(serialized, BranchDTO.class);
        } catch (IOException e) {
            throw new SerializeException("Failed to deserialize BranchDTO from JSON", e);
        }
    }

    @Override
    public String toString() {
        return "BranchDTO{" +
                "branchId=" + branchId +
                ", branchName='" + branchName + '\'' +
                ", areaCode=" + areaCode +
                ", branchAddress='" + branchAddress + '\'' +
                ", managerID='" + managerID + '\'' +
                '}';
    }

    public String getAddress() {
        return branchAddress;
    }
}
