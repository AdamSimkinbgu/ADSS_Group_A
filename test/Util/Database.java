package Util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    // SQLite database properties
    public static String DB_URL = "jdbc:sqlite:superLee_test.db";
    //    private static String FULL_DB_URL = "jdbc:sqlite:superLee.db";
//    private static String MINIMAL_DB_URL = "jdbc:sqlite:superLee_minimal.db";
    private static Connection conn;

    // Tables creation
    private static final String EmployeesTable =
            "CREATE TABLE IF NOT EXISTS Employees (" +
                    "israeliId BIGINT PRIMARY KEY, " +
                    "firstName TEXT NOT NULL, " +
                    "lastName TEXT NOT NULL, " +
                    "salary BIGINT NOT NULL, " +
                    "startOfEmployment DATE NOT NULL, " +
                    "isActive BOOLEAN NOT NULL, " +
                    "creationDate DATE NOT NULL, " +
                    "updateDate DATE NOT NULL, " +
                    "branchId BIGINT, " +
                    "FOREIGN KEY (branchId) REFERENCES Branches(branchId)" +
                    ")";

    private static final String EmployeeRolesTable =
            "CREATE TABLE IF NOT EXISTS EmployeeRoles (" +
                    "israeliId BIGINT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "PRIMARY KEY (israeliId, role), " +
                    "FOREIGN KEY (israeliId) REFERENCES Employees(israeliId), " +
                    "FOREIGN KEY (role) REFERENCES Roles(roleName)" +
                    ")";

    private static final String EmployeeTermsTable =
            "CREATE TABLE IF NOT EXISTS EmployeeTerms (" +
                    "israeliId BIGINT NOT NULL, " +
                    "termKey TEXT NOT NULL, " +
                    "termValue TEXT NOT NULL, " +
                    "PRIMARY KEY (israeliId, termKey), " +
                    "FOREIGN KEY (israeliId) REFERENCES Employees(israeliId)" +
                    ")";

    private static final String BranchesTable =
            "CREATE TABLE IF NOT EXISTS Branches (" +
                    "branchId BIGINT PRIMARY KEY, " +
                    "branchName TEXT NOT NULL, " +
                    "areaCode INTEGER NOT NULL, " +
                    "branchAddress TEXT NOT NULL, " +
                    "managerID TEXT" +
                    ")";

    private static final String RolesTable =
            "CREATE TABLE IF NOT EXISTS Roles (" +
                    "roleName TEXT PRIMARY KEY" +
                    ")";

    private static final String PermissionsTable =
            "CREATE TABLE IF NOT EXISTS Permissions (" +
                    "permissionName TEXT PRIMARY KEY" +
                    ")";

    private static final String RolePermissionsTable =
            "CREATE TABLE IF NOT EXISTS RolePermissions (" +
                    "roleName TEXT NOT NULL, " +
                    "permissionName TEXT NOT NULL, " +
                    "PRIMARY KEY (roleName, permissionName), " +
                    "FOREIGN KEY (roleName) REFERENCES Roles(roleName), " +
                    "FOREIGN KEY (permissionName) REFERENCES Permissions(permissionName)" +
                    ")";

    private static final String ShiftType =
            "CREATE TABLE IF NOT EXISTS ShiftType (" +
                    "type TEXT PRIMARY KEY" +
                    ")";

    private static final String ShiftsTable =
            "CREATE TABLE IF NOT EXISTS Shifts (" +
                    "id BIGINT PRIMARY KEY, " +
                    "shiftType TEXT NOT NULL, " +
                    "shiftDate DATE NOT NULL, " +
                    "isAssignedShiftManager BOOLEAN NOT NULL, " +
                    "isOpen BOOLEAN NOT NULL, " +
                    "startHour TIME NOT NULL, " +
                    "endHour TIME NOT NULL, " +
                    "creationDate DATE NOT NULL, " +
                    "updateDate DATE NOT NULL, " +
                    "branchId BIGINT NOT NULL, " +
                    "FOREIGN KEY (branchId) REFERENCES Branches(branchId)" +
                    ")";

    private static final String RoleRequiredTable =
            "CREATE TABLE IF NOT EXISTS RoleRequired (" +
                    "shiftId BIGINT NOT NULL, " +
                    "roleName TEXT NOT NULL, " +
                    "requiredCount INTEGER NOT NULL, " +
                    "PRIMARY KEY (shiftId, roleName), " +
                    "FOREIGN KEY (shiftId) REFERENCES Shifts(id), " +
                    "FOREIGN KEY (roleName) REFERENCES Roles(roleName)" +
                    ")";

    private static final String AssignedEmployeesTable =
            "CREATE TABLE IF NOT EXISTS AssignedEmployees (" +
                    "shiftId BIGINT NOT NULL, " +
                    "roleName TEXT NOT NULL, " +
                    "employeeId BIGINT NOT NULL, " +
                    "PRIMARY KEY (shiftId, roleName, employeeId), " +
                    "FOREIGN KEY (shiftId) REFERENCES Shifts(id), " +
                    "FOREIGN KEY (roleName) REFERENCES Roles(roleName), " +
                    "FOREIGN KEY (employeeId) REFERENCES Employees(israeliId)" +
                    ")";

    private static final String AvailableEmployeesTable =
            "CREATE TABLE IF NOT EXISTS AvailableEmployees (" +
                    "shiftId BIGINT NOT NULL, " +
                    "employeeId BIGINT NOT NULL, " +
                    "PRIMARY KEY (shiftId, employeeId), " +
                    "FOREIGN KEY (shiftId) REFERENCES Shifts(id), " +
                    "FOREIGN KEY (employeeId) REFERENCES Employees(israeliId)" +
                    ")";

    private static final String BankAccountsTable =
            "CREATE TABLE IF NOT EXISTS BankAccounts (" +
                    "employeeId INTEGER PRIMARY KEY, " +
                    "bankNumber INTEGER NOT NULL, " +
                    "bankBranchNumber INTEGER NOT NULL, " +
                    "bankAccountNumber INTEGER NOT NULL, " +
                    "FOREIGN KEY (employeeId) REFERENCES Employees(israeliId)" +
                    ")";


    /// Transport related tables creation             <<--------------

    private static final String ShippingAreasTable =
            "CREATE TABLE IF NOT EXISTS ShippingAreas (" +
                    "areaNumber BIGINT PRIMARY KEY, " +
                    "areaName TEXT NOT NULL" +
                    ")";

    private static final String SitesTable =
            "CREATE TABLE IF NOT EXISTS Sites (" +
                    "areaNum BIGINT NOT NULL, " +
                    "addressStr TEXT NOT NULL, " +
                    "contName TEXT NOT NULL, " +
                    "contNumber BIGINT NOT NULL, " +
                    "PRIMARY KEY (areaNum, addressStr), " +
                    "FOREIGN KEY (areaNum) REFERENCES ShippingAreas(areaNumber) ON UPDATE CASCADE" +
                    ")";

    private static final String TrucksTable =
            "CREATE TABLE IF NOT EXISTS Trucks (" +
                    "truckNum BIGINT PRIMARY KEY, " +
                    "model TEXT NOT NULL, " +
                    "netWeight DOUBLE NOT NULL, " +
                    "maxCarryWeight DOUBLE NOT NULL, " +
                    "validLicense TEXT NOT NULL, " +
                    "inTransportID BIGINT NOT NULL" +
                    ")";

    private static final String TransportsTable =
            "CREATE TABLE IF NOT EXISTS Transports (" +
                    "tranDocId BIGINT PRIMARY KEY, " +
                    "status TEXT NOT NULL, " +
                    "departure_dt TIMESTAMP NOT NULL, " +
                    "transportTruckNumber BIGINT NOT NULL, " +
                    "transportDriverId BIGINT NOT NULL, " +
                    "truck_Depart_Weight DOUBLE NOT NULL, " +
                    "srcSiteArea BIGINT NOT NULL, " +
                    "srcSiteString TEXT NOT NULL, " +
                    "isQueued BOOLEAN NOT NULL, " +
                    "FOREIGN KEY (transportTruckNumber) REFERENCES Trucks(truckNum) ON UPDATE CASCADE, " +
                    "FOREIGN KEY (transportDriverId) REFERENCES Employees(israeliId) ON UPDATE CASCADE, " +
                    "FOREIGN KEY (srcSiteArea, srcSiteString) REFERENCES Sites(areaNum, addressStr) ON UPDATE CASCADE" +
                    ")";


    ///  maybe the enums should have a VARCHAR column, instead of String column.

    private static final String CountersTable =
            "CREATE TABLE IF NOT EXISTS Counters (" +
                    "CounterName TEXT PRIMARY KEY, " +
                    "CounterValue BIGINT NOT NULL" +
                    ")";


    private static final String DriverIdToInTransportIDTable =
            "CREATE TABLE IF NOT EXISTS DriverIdToInTransportID (" +
                    "transportDriverId BIGINT PRIMARY KEY, " +
                    "transportId BIGINT NOT NULL, " +
                    "FOREIGN KEY (transportDriverId) REFERENCES Employees(israeliId) ON UPDATE CASCADE" +
                    ")";


    private static final String TransportsProblemsTable =
            "CREATE TABLE IF NOT EXISTS TransportsProblems (" +
                    "problemOfTranDocId BIGINT NOT NULL, " +
                    "problem TEXT NOT NULL, " +
                    "PRIMARY KEY (problemOfTranDocId, problem), " +
                    "FOREIGN KEY (problemOfTranDocId) REFERENCES Transports(tranDocId) ON UPDATE CASCADE" +
                    ")";

    private static final String ItemsDocsTable =
            "CREATE TABLE IF NOT EXISTS ItemsDocs (" +
                    "itemsDocNum BIGINT PRIMARY KEY, " +
                    "ItemsDocInTransportID BIGINT NOT NULL, " +
                    "srcSiteArea BIGINT NOT NULL, " +
                    "srcSiteString TEXT NOT NULL, " +
                    "destSiteArea BIGINT NOT NULL, " +
                    "destSiteString TEXT NOT NULL, " +
                    "estimatedArrivalTime TIMESTAMP NOT NULL, " +
                    "FOREIGN KEY (ItemsDocInTransportID) REFERENCES Transports(tranDocId) ON UPDATE CASCADE, " +
                    "FOREIGN KEY (srcSiteArea, srcSiteString) REFERENCES Sites(areaNum, addressStr) ON UPDATE CASCADE, " +
                    "FOREIGN KEY (destSiteArea, destSiteString) REFERENCES Sites(areaNum, addressStr) ON UPDATE CASCADE" +
                    ")";   //  if needed, maybe make a new field named arrivalIndex that we can update and when we get all the ItemsDocs, we ORDER BY arrivalIndex.

    private static final String ItemsQTable =
            "CREATE TABLE IF NOT EXISTS ItemsQ (" +
                    "itemInItemsDocId BIGINT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "weight DOUBLE NOT NULL, " +
                    "condition BOOLEAN NOT NULL, " +
                    "amount BIGINT NOT NULL, " +
                    "PRIMARY KEY (itemInItemsDocId, name, weight, condition), " +
                    "FOREIGN KEY (itemInItemsDocId) REFERENCES ItemsDocs(itemsDocNum) ON UPDATE CASCADE" +
                    ")";


    public static void init(boolean minimalMode) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);

            // Enable foreign key constraints
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
            }

            try (Statement st = conn.createStatement()) {
                // Create tables in the correct order to avoid foreign key constraint issues

                // First, create tables with no foreign key dependencies
                st.executeUpdate(BranchesTable);
                st.executeUpdate(RolesTable);
                st.executeUpdate(PermissionsTable);
                st.executeUpdate(ShiftType);
                st.executeUpdate(ShippingAreasTable);

                // Then create tables that depend on the above tables
                st.executeUpdate(EmployeesTable);
                st.executeUpdate(ShiftsTable);
                st.executeUpdate(SitesTable);
                st.executeUpdate(TransportsTable);
                st.executeUpdate(ItemsDocsTable);
                st.executeUpdate(BankAccountsTable);

                // Finally, create tables that depend on the second level tables
                st.executeUpdate(EmployeeRolesTable);
                st.executeUpdate(EmployeeTermsTable);
                st.executeUpdate(RolePermissionsTable);
                st.executeUpdate(RoleRequiredTable);
                st.executeUpdate(AssignedEmployeesTable);
                st.executeUpdate(AvailableEmployeesTable);
                st.executeUpdate(TrucksTable);
                st.executeUpdate(ItemsQTable);
                st.executeUpdate(TransportsProblemsTable);
                st.executeUpdate(DriverIdToInTransportIDTable);
                st.executeUpdate(CountersTable);

                // ***ADD YOUR TABLES HERE***


            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }



    private Database() {}

    public static Connection getConnection() throws SQLException {
        return conn;
    }
}
