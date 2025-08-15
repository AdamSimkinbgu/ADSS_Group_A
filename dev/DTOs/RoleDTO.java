package DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for Role entities.
 * This class is used to transfer role data between layers.
 * It uses Jackson for JSON serialization/deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDTO {
    // Static ObjectMapper for all serialization/deserialization
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String name;
    private Set<String> permissions;

    /**
     * Default constructor for serialization
     */
    public RoleDTO() {
        this.permissions = new HashSet<>();
    }

    /**
     * Constructor for creating a RoleDTO with name and permissions
     * 
     * @param name The name of the role
     * @param permissions The set of permissions associated with the role
     */
    public RoleDTO(String name, Set<String> permissions) {
        this.name = name;
        this.permissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPermissions() {
        return new HashSet<>(permissions);
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();
    }

    /**
     * Checks if the role has a specific permission
     * 
     * @param permission The permission to check
     * @return true if the role has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    /**
     * Adds a permission to the role
     * 
     * @param permission The permission to add
     */
    public void addPermission(String permission) {
        if (permission != null) {
            this.permissions.add(permission);
        }
    }

    /**
     * Removes a permission from the role
     * 
     * @param permission The permission to remove
     * @return true if the permission was removed, false if it wasn't present
     */
    public boolean removePermission(String permission) {
        return this.permissions.remove(permission);
    }

    /**
     * Serializes this RoleDTO object to a JSON string
     * 
     * @return JSON string representation of this object
     * @throws RuntimeException if serialization fails
     */
    public String serialize() {
        try { // TODO: update it to return it not as a JSON string
            return objectMapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize RoleDTO to JSON", e);
        }
    }

    /**
     * Deserializes a RoleDTO object from a JSON string
     * 
     * @param serialized JSON string representation of a RoleDTO object
     * @return The deserialized RoleDTO object
     * @throws RuntimeException if deserialization fails
     */
    public static RoleDTO deserialize(String serialized) {
        try {
            return objectMapper.readValue(serialized, RoleDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize RoleDTO from JSON", e);
        }
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "name='" + name + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
