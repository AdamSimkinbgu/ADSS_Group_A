-- Enable foreign key support (for SQLite)
PRAGMA foreign_keys = ON;

-- ======================
-- === Branches Table ===
-- ======================

-- Get all branches
SELECT * FROM Branches;

-- Get branch by ID
SELECT * FROM Branches WHERE branchId = ?;

-- Get branches by area code
SELECT * FROM Branches WHERE areaCode = ?;


-- =======================
-- === Employees Table ===
-- =======================

-- Get all employees
SELECT * FROM Employees;

-- Get employee by Israeli ID
SELECT * FROM Employees WHERE israeliId = ?;

-- Get only active employees
SELECT * FROM Employees WHERE isActive = 1;

-- Get employees from a specific branch
SELECT * FROM Employees WHERE branchId = ?;

-- Get employees with salary in a specific range
SELECT * FROM Employees WHERE salary BETWEEN ? AND ?;


-- ==========================
-- === EmployeeRoles Table ===
-- ==========================

-- Get all employee roles
SELECT * FROM EmployeeRoles;

-- Get all roles for a specific employee
SELECT * FROM EmployeeRoles WHERE israeliId = ?;

-- Get all employees with a specific role
SELECT israeliId FROM EmployeeRoles WHERE role = ?;


-- ====================
-- === Roles Table ===
-- ====================

-- Get all defined roles
SELECT * FROM Roles;

-- Check if a specific role exists
SELECT * FROM Roles WHERE roleName = ?;


-- =========================
-- === Permissions Table ===
-- =========================

-- Get all permissions
SELECT * FROM Permissions;

-- Check if a specific permission exists
SELECT * FROM Permissions WHERE permissionName = ?;


-- ================================
-- === RolePermissions Table ===
-- ================================

-- Get all role-permission mappings
SELECT * FROM RolePermissions;

-- Get all permissions for a specific role
SELECT permissionName FROM RolePermissions WHERE roleName = ?;

-- Get all roles that have a specific permission
SELECT roleName FROM RolePermissions WHERE permissionName = ?;


-- ==============================
-- === EmployeeTerms Table ===
-- ==============================

-- Get all terms for all employees
SELECT * FROM EmployeeTerms;

-- Get all terms for a specific employee
SELECT * FROM EmployeeTerms WHERE israeliId = ?;

-- Get specific term value for an employee
SELECT termValue FROM EmployeeTerms WHERE israeliId = ? AND termKey = ?;


-- ====================================
-- === Debug Queries and Integrity ===
-- ====================================

-- Find employees without assigned roles
SELECT e.*
FROM Employees e
         LEFT JOIN EmployeeRoles r ON e.israeliId = r.israeliId
WHERE r.role IS NULL;

-- Find roles that have no permissions assigned
SELECT r.roleName
FROM Roles r
         LEFT JOIN RolePermissions p ON r.roleName = p.roleName
WHERE p.permissionName IS NULL;

-- Find employee roles referring to undefined roles
SELECT *
FROM EmployeeRoles er
         LEFT JOIN Roles r ON er.role = r.roleName
WHERE r.roleName IS NULL;

-- Find employees referencing branches that don't exist
SELECT *
FROM Employees e
         LEFT JOIN Branches b ON e.branchId = b.branchId
WHERE e.branchId IS NOT NULL AND b.branchId IS NULL;
