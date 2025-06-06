package Suppliers.DataLayer.DAOs;

import java.sql.SQLException;

public abstract class BaseDAO {
   public void handleSQLException(SQLException e) throws Exception {
      int code = e.getErrorCode();
      String msg = e.getMessage();
      if (code == 1) {
         throw new Exception("Database is read-only.");
      } else if (code == 8) {
         throw new Exception("Database is locked.");
      } else if (code == 14) {
         throw new Exception("Database file not found or inaccessible.");
      } else if (code == 19 && msg.contains("UNIQUE constraint failed")) {
         throw new Exception("Unique constraint violated: " + msg);
      } else if (code == 21) {
         throw new Exception("Database is full.");
      } else if (code == 26) {
         throw new Exception("Table not found: " + msg);
      } else if (code == 27) {
         throw new Exception("Column not found: " + msg);
      } else if (code == 30) {
         throw new Exception("Disk I/O error: " + msg);
      } else if (code == 34) {
         throw new Exception("Invalid database schema: " + msg);
      } else {
         throw new Exception("An unexpected SQL error occurred: " + msg);
      }
   }
}
