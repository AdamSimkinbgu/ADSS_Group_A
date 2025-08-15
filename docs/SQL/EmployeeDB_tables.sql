PRAGMA foreign_keys = ON;

-- Branches must exist before Employees references it
CREATE TABLE IF NOT EXISTS Branches (
                                        branchId        INTEGER PRIMARY KEY,
                                        branchName      TEXT    NOT NULL,
                                        areaCode        INTEGER NOT NULL,
                                        branchAddress   TEXT    NOT NULL,
                                        managerID       TEXT
);

-- Roles and Permissions before their many-to-many join tables
CREATE TABLE IF NOT EXISTS Roles (
                                     roleName TEXT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS Permissions (
                                           permissionName TEXT PRIMARY KEY
);

-- Junction table between Roles and Permissions
CREATE TABLE IF NOT EXISTS RolePermissions (
                                               roleName        TEXT NOT NULL,
                                               permissionName  TEXT NOT NULL,
                                               PRIMARY KEY (roleName, permissionName),
    FOREIGN KEY (roleName)       REFERENCES Roles(roleName),
    FOREIGN KEY (permissionName) REFERENCES Permissions(permissionName)
    );

-- Employees come after Branches
CREATE TABLE IF NOT EXISTS Employees (
                                         israeliId          INTEGER PRIMARY KEY,
                                         firstName          TEXT    NOT NULL,
                                         lastName           TEXT    NOT NULL,
                                         salary             INTEGER NOT NULL,
                                         startOfEmployment  TEXT    NOT NULL,
                                         isActive           BOOLEAN NOT NULL,
                                         creationDate       TEXT    NOT NULL,
                                         updateDate         TEXT    NOT NULL,
                                         branchId           INTEGER,
                                         FOREIGN KEY (branchId) REFERENCES Branches(branchId)
    );

-- Roles assigned to Employees
CREATE TABLE IF NOT EXISTS EmployeeRoles (
                                             israeliId  INTEGER NOT NULL,
                                             role       TEXT    NOT NULL,
                                             PRIMARY KEY (israeliId, role),
    FOREIGN KEY (israeliId) REFERENCES Employees(israeliId),
    FOREIGN KEY (role)      REFERENCES Roles(roleName)
    );

-- Extra key/value terms per Employee
CREATE TABLE IF NOT EXISTS EmployeeTerms (
                                             israeliId  INTEGER NOT NULL,
                                             termKey    TEXT    NOT NULL,
                                             termValue  TEXT    NOT NULL,
                                             PRIMARY KEY (israeliId, termKey),
    FOREIGN KEY (israeliId) REFERENCES Employees(israeliId)
    );
