package PresentationLayer.EmployeeSubModule.models;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import DTOs.EmployeeDTO;

/**
 * UI model class for Employee data.
 * This class uses JavaFX properties to enable data binding with UI controls.
 */
public class EmployeeUIModel {
    private final LongProperty israeliId = new SimpleLongProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty roles = new SimpleStringProperty();
    private final LongProperty branchId = new SimpleLongProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final BooleanProperty active = new SimpleBooleanProperty();

    /**
     * Default constructor.
     */
    public EmployeeUIModel() {
    }

    /**
     * Constructor with all properties.
     * 
     * @param israeliId The Israeli ID of the employee
     * @param firstName The first name of the employee
     * @param lastName The last name of the employee
     * @param roles The roles of the employee (comma-separated)
     * @param branchId The ID of the branch where the employee works
     * @param active Whether the employee is active
     */
    public EmployeeUIModel(long israeliId, String firstName, String lastName, 
                          String roles, long branchId, boolean active) {
        this.israeliId.set(israeliId);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.roles.set(roles);
        this.branchId.set(branchId);
        this.active.set(active);
        this.status.set(active ? "Active" : "Inactive");
    }

    /**
     * Creates an EmployeeUIModel from an EmployeeDTO.
     * 
     * @param dto The EmployeeDTO to convert
     * @return A new EmployeeUIModel
     */
    public static EmployeeUIModel fromDTO(EmployeeDTO dto) {
        return new EmployeeUIModel(
            dto.getIsraeliId(),
            dto.getFirstName(),
            dto.getLastName(),
            String.join(", ", dto.getRoles()),
            dto.getBranchId(),
            dto.isActive()
        );
    }

    // Getters and setters for JavaFX properties

    public long getIsraeliId() { return israeliId.get(); }
    public void setIsraeliId(long id) { israeliId.set(id); }
    public LongProperty israeliIdProperty() { return israeliId; }

    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String name) { firstName.set(name); }
    public StringProperty firstNameProperty() { return firstName; }

    public String getLastName() { return lastName.get(); }
    public void setLastName(String name) { lastName.set(name); }
    public StringProperty lastNameProperty() { return lastName; }

    public String getRoles() { return roles.get(); }
    public void setRoles(String roleList) { roles.set(roleList); }
    public StringProperty rolesProperty() { return roles; }

    public long getBranchId() { return branchId.get(); }
    public void setBranchId(long branch) { branchId.set(branch); }
    public LongProperty branchIdProperty() { return branchId; }

    public String getStatus() { return status.get(); }
    public void setStatus(String statusValue) { status.set(statusValue); }
    public StringProperty statusProperty() { return status; }

    public boolean isActive() { return active.get(); }
    public void setActive(boolean activeValue) { 
        active.set(activeValue);
        status.set(activeValue ? "Active" : "Inactive");
    }
    public BooleanProperty activeProperty() { return active; }

    @Override
    public String toString() {
        return firstName.get() + " " + lastName.get() + " (" + israeliId.get() + ")";
    }
}
