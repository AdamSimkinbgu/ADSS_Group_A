package DomainLayer;

import java.util.HashSet;
import java.util.Set;

public class Role {
    private long id;
    private String name;
    private Set<String> permissions; // maybe Set will be a better option..

    public Role(long id, String name, Set<String> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = new HashSet<>(permissions);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
