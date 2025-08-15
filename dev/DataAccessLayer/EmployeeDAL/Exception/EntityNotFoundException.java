package DataAccessLayer.EmployeeDAL.Exception;

import java.sql.SQLException;

public class EntityNotFoundException extends SQLException {
    public EntityNotFoundException(String message) {
        super(message);
    }

}
