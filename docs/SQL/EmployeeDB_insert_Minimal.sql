-- Script for inserts basic data into the EmployeeDB database.

-- Branches
INSERT INTO Branches (branchId, branchName, areaCode, branchAddress, managerID) VALUES (1, 'Ben Gurion Uni', 1, 'Beer Sheva', 111111111);

-- Permissions

-- Employee Management Permissions
INSERT INTO Permissions (permissionName) VALUES ('CREATE_EMPLOYEE');
INSERT INTO Permissions (permissionName) VALUES ('UPDATE_EMPLOYEE');
INSERT INTO Permissions (permissionName) VALUES ('EDIT_EMPLOYEE');
INSERT INTO Permissions (permissionName) VALUES ('VIEW_EMPLOYEE');
INSERT INTO Permissions (permissionName) VALUES ('DEACTIVATE_EMPLOYEE');
INSERT INTO Permissions (permissionName) VALUES ('DELETE_EMPLOYEE');

-- Role and Permission Management
INSERT INTO Permissions (permissionName) VALUES ('CREATE_ROLE');
INSERT INTO Permissions (permissionName) VALUES ('EDIT_ROLE');
INSERT INTO Permissions (permissionName) VALUES ('ROLE_PERMISSION');
INSERT INTO Permissions (permissionName) VALUES ('ADD_PERMISSION_TO_ROLE');
INSERT INTO Permissions (permissionName) VALUES ('REMOVE_PERMISSION_FROM_ROLE');
INSERT INTO Permissions (permissionName) VALUES ('CREATE_PERMISSION');
INSERT INTO Permissions (permissionName) VALUES ('EDIT_PERMISSION');
INSERT INTO Permissions (permissionName) VALUES ('GET_ROLES');
INSERT INTO Permissions (permissionName) VALUES ('ROLE_REQUIRED');

-- Shift Management Permissions
INSERT INTO Permissions (permissionName) VALUES ('CREATE_SHIFT');
INSERT INTO Permissions (permissionName) VALUES ('UPDATE_SHIFT');
INSERT INTO Permissions (permissionName) VALUES ('EDIT_SHIFT');
INSERT INTO Permissions (permissionName) VALUES ('REMOVE_SHIFT');
INSERT INTO Permissions (permissionName) VALUES ('GET_SHIFT');
INSERT INTO Permissions (permissionName) VALUES ('MANAGE_SHIFT');
INSERT INTO Permissions (permissionName) VALUES ('VIEW_SHIFT');

-- Assignment Management Permissions
INSERT INTO Permissions (permissionName) VALUES ('ASSIGN_EMPLOYEE');

-- Availability Management Permissions
INSERT INTO Permissions (permissionName) VALUES ('UPDATE_AVAILABLE');

-- Transport Management Permissions
INSERT INTO Permissions (permissionName) VALUES ('CREATE_TRANSPORT');
INSERT INTO Permissions (permissionName) VALUES ('EDIT_TRANSPORT');
INSERT INTO Permissions (permissionName) VALUES ('DELETE_TRANSPORT');
INSERT INTO Permissions (permissionName) VALUES ('VIEW_TRANSPORT');
INSERT INTO Permissions (permissionName) VALUES ('VIEW_RELEVANT_TRANSPORTS');
INSERT INTO Permissions (permissionName) VALUES ('ADD_ITEM_TO_TRANSPORT');
INSERT INTO Permissions (permissionName) VALUES ('EDIT_ITEM_IN_TRANSPORT');
INSERT INTO Permissions (permissionName) VALUES ('DELETE_ITEM_FROM_TRANSPORT');
INSERT INTO Permissions (permissionName) VALUES ('EDIT_TRANSPORT_ITEM_CONDITION');

-- Site Management Permissions
INSERT INTO Permissions (permissionName) VALUES ('ADD_SITE');
INSERT INTO Permissions (permissionName) VALUES ('EDIT_SITE');
INSERT INTO Permissions (permissionName) VALUES ('DELETE_SITE');
INSERT INTO Permissions (permissionName) VALUES ('SHOW_SITES');

-- Shipping Area Permissions
INSERT INTO Permissions (permissionName) VALUES ('ADD_SHIPPING_AREA');
INSERT INTO Permissions (permissionName) VALUES ('EDIT_SHIPPING_AREA');
INSERT INTO Permissions (permissionName) VALUES ('DELETE_SHIPPING_AREA');
INSERT INTO Permissions (permissionName) VALUES ('SHOW_SHIPPING_AREAS');

-- Truck Management Permissions
INSERT INTO Permissions (permissionName) VALUES ('ADD_TRUCK');
INSERT INTO Permissions (permissionName) VALUES ('DELETE_TRUCK');
INSERT INTO Permissions (permissionName) VALUES ('SHOW_TRUCKS');

-- Operational Permissions
INSERT INTO Permissions (permissionName) VALUES ('MANAGE_HR');
INSERT INTO Permissions (permissionName) VALUES ('MANAGE_INVENTORY');
INSERT INTO Permissions (permissionName) VALUES ('HANDLE_CASH');
INSERT INTO Permissions (permissionName) VALUES ('DRIVE_VEHICLE');
INSERT INTO Permissions (permissionName) VALUES ('STOCK_SHELVES');

-- Roles
INSERT INTO Roles (roleName) VALUES ('Admin');
INSERT INTO Roles (roleName) VALUES ('HR manager');
INSERT INTO Roles (roleName) VALUES ('Transport Manager');
INSERT INTO Roles (roleName) VALUES ('Shift Manager');
INSERT INTO Roles (roleName) VALUES ('Cashier');
INSERT INTO Roles (roleName) VALUES ('Stocker');
INSERT INTO Roles (roleName) VALUES ('Cleaner');
-- for transport module
INSERT INTO Roles (roleName) VALUES ('DriverA');
INSERT INTO Roles (roleName) VALUES ('DriverB');
INSERT INTO Roles (roleName) VALUES ('DriverC');
INSERT INTO Roles (roleName) VALUES ('DriverD');
INSERT INTO Roles (roleName) VALUES ('DriverE');
INSERT INTO Roles (roleName) VALUES ('WarehouseMan');


-- RolePermissions
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'UPDATE_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'DELETE_SITE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'EDIT_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'EDIT_ITEM_IN_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'ADD_ITEM_TO_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'EDIT_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'GET_ROLES');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'EDIT_SHIPPING_AREA');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'CREATE_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'SHOW_TRUCKS');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'MANAGE_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'SHOW_SITES');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'EDIT_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'CREATE_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'ADD_PERMISSION_TO_ROLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'MANAGE_HR');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'DELETE_SHIPPING_AREA');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'CREATE_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'CREATE_ROLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'ROLE_REQUIRED');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'VIEW_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'MANAGE_INVENTORY');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'ADD_SHIPPING_AREA');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'EDIT_ROLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'ASSIGN_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'DELETE_TRUCK');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'EDIT_SITE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'UPDATE_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'CREATE_PERMISSION');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'SHOW_SHIPPING_AREAS');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'EDIT_PERMISSION');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'STOCK_SHELVES');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'ROLE_PERMISSION');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'ADD_SITE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'DEACTIVATE_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'REMOVE_PERMISSION_FROM_ROLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'DELETE_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'DELETE_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'REMOVE_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'HANDLE_CASH');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'VIEW_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'DELETE_ITEM_FROM_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'ADD_TRUCK');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'DRIVE_VEHICLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Admin', 'EDIT_TRANSPORT_ITEM_CONDITION');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'UPDATE_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'CREATE_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'ROLE_REQUIRED');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'VIEW_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'DEACTIVATE_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'ASSIGN_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'EDIT_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'REMOVE_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'EDIT_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'UPDATE_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'GET_ROLES');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'CREATE_EMPLOYEE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('HR manager', 'MANAGE_HR');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'SHOW_SITES');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'ADD_SHIPPING_AREA');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'VIEW_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'EDIT_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'DELETE_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'DELETE_SITE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'DELETE_ITEM_FROM_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'EDIT_SITE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'ADD_ITEM_TO_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'EDIT_ITEM_IN_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'DELETE_TRUCK');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'EDIT_SHIPPING_AREA');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'SHOW_TRUCKS');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'SHOW_SHIPPING_AREAS');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'ADD_TRUCK');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'CREATE_TRANSPORT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'ADD_SITE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'DELETE_SHIPPING_AREA');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Transport Manager', 'EDIT_TRANSPORT_ITEM_CONDITION');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Shift Manager', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Shift Manager', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Shift Manager', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Shift Manager', 'MANAGE_SHIFT');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Cashier', 'HANDLE_CASH');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Cashier', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Cashier', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Cashier', 'GET_SHIFT');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Stocker', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Stocker', 'MANAGE_INVENTORY');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Stocker', 'STOCK_SHELVES');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Stocker', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Stocker', 'UPDATE_AVAILABLE');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Cleaner', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Cleaner', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Cleaner', 'GET_SHIFT');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverA', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverA', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverA', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverA', 'VIEW_RELEVANT_TRANSPORTS');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverA', 'EDIT_TRANSPORT_ITEM_CONDITION');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverB', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverB', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverB', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverB', 'VIEW_RELEVANT_TRANSPORTS');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverB', 'EDIT_TRANSPORT_ITEM_CONDITION');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverC', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverC', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverC', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverC', 'VIEW_RELEVANT_TRANSPORTS');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverC', 'EDIT_TRANSPORT_ITEM_CONDITION');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverD', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverD', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverD', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverD', 'VIEW_RELEVANT_TRANSPORTS');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverD', 'EDIT_TRANSPORT_ITEM_CONDITION');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverE', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverE', 'UPDATE_AVAILABLE');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverE', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverE', 'VIEW_RELEVANT_TRANSPORTS');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverE', 'EDIT_TRANSPORT_ITEM_CONDITION');

INSERT INTO RolePermissions (roleName, permissionName) VALUES ('WarehouseMan', 'VIEW_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('WarehouseMan', 'MANAGE_INVENTORY');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('WarehouseMan', 'STOCK_SHELVES');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('WarehouseMan', 'GET_SHIFT');
INSERT INTO RolePermissions (roleName, permissionName) VALUES ('WarehouseMan', 'UPDATE_AVAILABLE');

-- Employees
INSERT INTO Employees (israeliId, firstName, lastName, salary, startOfEmployment, isActive, creationDate, updateDate, branchId)
VALUES (123456789, 'Admin', 'User', 20000, '01-01-2020', TRUE, '01-01-2023', '01-01-2023', 1);

-- EmployeeRoles
INSERT INTO EmployeeRoles (israeliId, role) VALUES (123456789, 'Admin');

-- EmployeeTerms
INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (123456789, 'WorkingHours', '9:00-17:00');
INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (123456789, 'WorkingDays', 'Sunday-Thursday');
INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (123456789, 'VacationDays', '22');
INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (123456789, 'SickDays', '18');
INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (123456789, 'PensionFund', 'Menora Mivtachim');
INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (123456789, 'PensionRate', '6.5%');
INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (123456789, 'HealthInsurance', 'Maccabi');
INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (123456789, 'Position', 'System Administrator');
INSERT INTO EmployeeTerms (israeliId, termKey, termValue) VALUES (123456789, 'Department', 'IT');

-- BankAccounts
INSERT INTO BankAccounts (employeeId, bankNumber, bankBranchNumber, bankAccountNumber) VALUES (123456789, 1, 123, 1234567890);

-- Insert data into the ShippingAreas table
INSERT INTO ShippingAreas (areaNumber, areaName) VALUES
                                                     (0, 'Central District'),
                                                     (1, 'South District'),
                                                     (2, 'East District');

-- Insert data into the Sites table
INSERT INTO Sites (areaNum, addressStr, contName, contNumber) VALUES
                                                                  (0, 'Tel Aviv', 'Yossi Oren', 0542315421),
                                                                  (1, 'Ben Gurion Uni', 'Meni Adler', 0526451234),
                                                                  (2, 'Afula', 'Dani Hendler', 0535471594);

-- Insert data into the Counters table
INSERT INTO Counters (CounterName, CounterValue) VALUES
    (  'transportIDCounter', 0);