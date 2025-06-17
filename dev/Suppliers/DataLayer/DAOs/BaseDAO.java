package Suppliers.DataLayer.DAOs;

import java.sql.SQLException;

public abstract class BaseDAO {
   public void handleSQLException(SQLException e) throws DataAccessException {
      int code = e.getErrorCode();
      String msg = e.getMessage();
      if (code == 1) {
         throw new DataAccessException("Database is read-only.", e);
      } else if (code == 8) {
         throw new DataAccessException("Database is locked.", e);
      } else if (code == 14) {
         throw new DataAccessException("Database file not found or inaccessible.", e);
      } else if (code == 19 && msg.contains("UNIQUE constraint failed")) {
         throw new DataAccessException("Unique constraint violated: " + msg, e);
      } else if (code == 21) {
         throw new DataAccessException("Database is full.", e);
      } else if (code == 26) {
         throw new DataAccessException("Table not found: " + msg, e);
      } else if (code == 27) {
         throw new DataAccessException("Column not found: " + msg, e);
      } else if (code == 30) {
         throw new DataAccessException("Disk I/O error: " + msg, e);
      } else if (code == 34) {
         throw new DataAccessException("Invalid database schema: " + msg, e);
      } else {
         throw new DataAccessException("An unexpected SQL error occurred: " + msg, e);
      }
   }
}
