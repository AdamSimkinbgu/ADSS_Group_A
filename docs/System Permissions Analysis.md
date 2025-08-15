
# System Permissions Analysis

Based on the code review, I've compiled a comprehensive analysis of all system permissions in the Supermarket Management System, including where they are used and their purposes. For each permission, I've identified the specific domain layer functions that use it.

## Employee Management Permissions

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `CREATE_EMPLOYEE` | Allows creating new employees | EmployeeController, EmployeeCLI | `EmployeeController.createEmployee()`: Creates a new employee record with specified details |
| `UPDATE_EMPLOYEE` | Allows modifying existing employee information | EmployeeController, EmployeeCLI | `EmployeeController.updateEmployee()`: Updates an existing employee's information |
| `EDIT_EMPLOYEE` | Alias for updating employee fields | EmployeeController, EmployeeCLI | `EmployeeController.deactivateEmployee()`: Marks an employee as inactive |
| `VIEW_EMPLOYEE` | Allows viewing employee information | EmployeeController, EmployeeCLI, MainCLI | `EmployeeController.getEmployeeById()`, `EmployeeController.getAllEmployees()`: Retrieves employee information |
| `DEACTIVATE_EMPLOYEE` | Allows marking an employee as inactive | EmployeeController, EmployeeCLI | `EmployeeController.deactivateEmployee()`: Changes an employee's status to inactive |
| `DELETE_EMPLOYEE` | Allows permanent removal of employee records | EmployeeController, EmployeeCLI | `EmployeeController.deleteEmployee()`: Permanently removes an employee from the system |
| `MANAGE_HR` | Provides general HR management capabilities | EmployeeController, EmployeeCLI | Various HR management functions across the EmployeeController |

## Role and Permission Management

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `CREATE_ROLE` | Allows creating new roles | AuthorisationController, EmployeeCLI | `AuthorisationController.createRole()`: Creates a new role in the system |
| `EDIT_ROLE` | Allows modifying existing roles | AuthorisationController, EmployeeCLI | Various role editing functions in AuthorisationController |
| `ROLE_PERMISSION` | Allows assigning/removing roles from employees | EmployeeController, EmployeeCLI | `EmployeeController.addRoleToEmployee()`: Assigns a role to an employee<br>`EmployeeController.removeRoleFromEmployee()`: Removes a role from an employee |
| `ADD_PERMISSION_TO_ROLE` | Allows adding permissions to roles | AuthorisationController, EmployeeCLI | `AuthorisationController.addPermissionToRole()`: Adds a permission to a role |
| `REMOVE_PERMISSION_FROM_ROLE` | Allows removing permissions from roles | AuthorisationController, EmployeeCLI | `AuthorisationController.removePermissionFromRole()`: Removes a permission from a role |
| `CREATE_PERMISSION` | Allows creating new permission types | AuthorisationController, EmployeeCLI | `AuthorisationController.createPermission()`: Creates a new permission type |
| `EDIT_PERMISSION` | Allows modifying permission properties | AuthorisationController, EmployeeCLI | Not directly implemented in current code |
| `GET_ROLES` | Allows viewing all roles | AuthorisationController, EmployeeCLI, ShiftController | `AuthorisationController.getAllRoles()`: Gets all roles in the system<br>`ShiftController.getRolesForShift()`: Gets roles assigned to a shift |
| `ROLE_REQUIRED` | Indicates a role requirement | ShiftController | `ShiftController.addRoleToShift()`: Adds a role requirement to a shift<br>`ShiftController.removeRoleFromShift()`: Removes a role requirement from a shift |

## Shift Management Permissions

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `CREATE_SHIFT` | Allows creating new shifts | ShiftController, ShiftCLI | `ShiftController.createShift()`: Creates a new shift<br>`ShiftController.createWeeklyShifts()`: Creates shifts for an entire week |
| `UPDATE_SHIFT` | Allows modifying shift information | ShiftController, ShiftCLI | `ShiftController.updateShift()`: Updates shift details<br>`ShiftController.updateShiftEndTime()`: Updates a shift's end time<br>`ShiftController.updateShiftStartTime()`: Updates a shift's start time |
| `EDIT_SHIFT` | Alias for updating shift fields | ShiftController, ShiftCLI | Various shift editing functions in ShiftController |
| `REMOVE_SHIFT` | Allows deleting shifts | ShiftController, ShiftCLI | `ShiftController.removeShift()`: Removes a specific shift<br>`ShiftController.removeShiftsByDate()`: Removes all shifts on a specific date |
| `GET_SHIFT` | Allows viewing shift information | ShiftController, ShiftCLI | `ShiftController.getShift()`: Gets a specific shift<br>`ShiftController.getShiftsByDate()`: Gets shifts on a specific date<br>`ShiftController.getShiftsByDateRange()`: Gets shifts within a date range<br>`ShiftController.getShiftsByBranch()`: Gets shifts for a specific branch<br>`ShiftController.getShiftsByBranchAndDate()`: Gets shifts for a branch on a date<br>`ShiftController.getShiftsByBranchAndDateRange()`: Gets shifts for a branch in a date range<br>`ShiftController.getShiftsByEmployee()`: Gets shifts for a specific employee<br>`ShiftController.getShiftsByEmployeeAndDate()`: Gets shifts for an employee on a date<br>`ShiftController.getShiftsByEmployeeAndDateRange()`: Gets shifts for an employee in a date range |
| `MANAGE_SHIFT` | Provides general shift management | ShiftController, ShiftCLI | Various shift management functions across ShiftController |
| `VIEW_SHIFT` | Allows viewing shift details | ShiftController, ShiftCLI, MainCLI | `ShiftController.getAllShifts()`: Gets all shifts in the system |

## Assignment Management Permissions

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `ASSIGN_EMPLOYEE` | Allows assigning employees to shifts | AssignmentController, AssignmentCLI, MainCLI | `AssignmentController.assignEmployeeToShift()`: Assigns an employee to a shift<br>`AssignmentController.removeEmployeeFromShift()`: Removes an employee from a shift<br>`AssignmentController.canEmployeeBeAssignedToShift()`: Checks if an employee can be assigned<br>`AssignmentController.isEmployeeAssignedToShift()`: Checks if an employee is assigned<br>`AssignmentController.getAssignedEmployeesForShift()`: Gets employees assigned to a shift<br>`AssignmentController.getAssignedShiftsForEmployee()`: Gets shifts assigned to an employee |
| `GET_ASSIGN` | Allows viewing assignment information | AssignmentController | `AssignmentController.getAssignedRolesForShift()`: Gets roles assigned to a shift |

## Availability Management Permissions

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `UPDATE_AVAILABLE` | Allows marking availability for shifts | AvailabilityController, AvailabilityCLI, MainCLI | `AvailabilityController.markEmployeeAvailable()`: Marks an employee as available for a shift<br>`AvailabilityController.removeEmployeeAvailability()`: Removes an employee's availability<br>`AvailabilityController.isEmployeeAvailable()`: Checks if an employee is available<br>`AvailabilityController.getAvailableEmployeesForShift()`: Gets employees available for a shift |

## Transport Management Permissions

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `CREATE_TRANSPORT` | Allows creating transport documents | TransportService, TransportController | `TransportController.createTransport()`: Creates a new transport document |
| `EDIT_TRANSPORT` | Allows modifying transport details | TransportService, TransportController | `TransportController.setTransportStatus()`: Updates transport status<br>`TransportController.setTransportTruck()`: Changes the truck assigned to a transport<br>`TransportController.setTransportDriver()`: Changes the driver assigned to a transport<br>`TransportController.addTransportProblem()`: Adds a problem to a transport<br>`TransportController.removeTransportProblem()`: Removes a problem from a transport<br>`TransportController.addDestSiteToTransport()`: Adds a destination site to a transport<br>`TransportController.removeDestSiteFromTransport()`: Removes a destination site<br>`TransportController.setSiteArrivalIndexInTransport()`: Sets the arrival index for a site<br>`TransportController.changeAnItemsDocNum()`: Changes an item's document number |
| `DELETE_TRANSPORT` | Allows removing transport records | TransportService, TransportController | `TransportController.deleteTransport()`: Deletes a transport document |
| `VIEW_TRANSPORT` | Allows viewing transport information | TransportService, TransportController | `TransportController.showAllQueuedTransports()`: Shows all queued transports<br>`TransportController.showAllTransports()`: Shows all transports |
| `VIEW_RELEVANT_TRANSPORTS` | Allows viewing assigned transports | TransportService, TransportController | `TransportController.showTransportsOfDriver()`: Shows transports assigned to a specific driver |
| `ADD_ITEM_TO_TRANSPORT` | Allows adding items to transport | TransportService, TransportController | `TransportController.addItem()`: Adds an item to a transport |
| `DELETE_ITEM_FROM_TRANSPORT` | Allows removing items from transport | TransportService, TransportController | `TransportController.removeItem()`: Removes an item from a transport |
| `EDIT_TRANSPORT_ITEM_CONDITION` | Allows updating item condition | TransportService, TransportController | `TransportController.setItemCond()`: Updates the condition of an item in a transport |

## Site Management Permissions

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `ADD_SITE` | Allows adding new sites | SiteService, SiteFacade | `SiteFacade.addSiteTOArea()`: Adds a new site to a shipping area |
| `EDIT_SITE` | Allows modifying site information | SiteService, SiteFacade | `SiteFacade.setSiteAddress()`: Updates a site's address<br>`SiteFacade.setSiteAreaNum()`: Changes a site's area number<br>`SiteFacade.setSiteContName()`: Updates a site's contact name<br>`SiteFacade.setSiteContNum()`: Updates a site's contact number |
| `DELETE_SITE` | Allows removing sites | SiteService, SiteFacade | `SiteFacade.deleteSiteFromArea()`: Removes a site from a shipping area |
| `SHOW_SITES` | Allows viewing site information | SiteService, SiteFacade | `SiteFacade.showAllSites()`: Shows all sites in the system |

## Shipping Area Permissions

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `ADD_SHIPPING_AREA` | Allows creating shipping areas | SiteService, SiteFacade | `SiteFacade.addShippingArea()`: Creates a new shipping area |
| `EDIT_SHIPPING_AREA` | Allows modifying shipping areas | SiteService, SiteFacade | `SiteFacade.setShippingAreaNum()`: Changes a shipping area's number<br>`SiteFacade.setShippingAreaName()`: Updates a shipping area's name |
| `DELETE_SHIPPING_AREA` | Allows removing shipping areas | SiteService, SiteFacade | `SiteFacade.deleteShippingArea()`: Deletes a shipping area |
| `SHOW_SHIPPING_AREAS` | Allows viewing shipping areas | SiteService, SiteFacade | `SiteFacade.showAllShippingAreas()`: Shows all shipping areas |

## Truck Management Permissions

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `ADD_TRUCK` | Allows adding new trucks | TruckService, TruckFacade | `TruckFacade.addTruck()`: Adds a new truck to the system |
| `DELETE_TRUCK` | Allows removing trucks | TruckService, TruckFacade | `TruckFacade.removeTruck()`: Removes a truck from the system |
| `SHOW_TRUCKS` | Allows viewing truck information | TruckService, TruckFacade | `TruckFacade.showAllTrucks()`: Shows all trucks in the system |

## Operational Permissions

| Permission | Purpose | Where Used | Domain Functions |
|------------|---------|------------|------------------|
| `MANAGE_INVENTORY` | Allows inventory management | Assigned to roles (Stocker, Admin) | Not directly implemented in current code |
| `HANDLE_CASH` | Allows cash handling | Assigned to roles (Cashier, Admin) | Not directly implemented in current code |
| `DRIVE_VEHICLE` | Allows vehicle operation | Assigned to roles (Driver roles, Admin) | Not directly implemented in current code |
| `CLEAN_FACILITY` | Allows facility maintenance | Assigned to roles (Cleaner, Admin) | Not directly implemented in current code |
| `STOCK_SHELVES` | Allows shelf stocking | Assigned to roles (Stocker, Admin) | Not directly implemented in current code |

## Permission Implementation Details

The permission system is implemented through several layers:

1. **Domain Layer**:
    - `AuthorisationController` contains the core logic for checking permissions
    - `EmployeeController.isEmployeeAuthorised()` verifies if an employee has a specific permission
    - Each controller checks permissions before executing operations using the `isEmployeeAuthorised()` method
    - The permission check flows through the domain layer to ensure security at the core business logic level

2. **Service Layer**:
    - `EmployeeService.isEmployeeAuthorised()` wraps the domain layer functionality
    - `EmployeeIntegrationService.isEmployeeAuthorised()` provides a simplified interface for other modules
    - Service classes like `TransportService`, `SiteService`, and `TruckService` check permissions before calling domain methods

3. **Presentation Layer**:
    - CLI classes use the permission system to determine which menu options to display
    - Permission checks are performed before executing operations to ensure proper authorization

4. **Database**:
    - Permissions are stored in the `Permissions` table
    - Roles are stored in the `Roles` table
    - Role-permission associations are stored in the `RolePermissions` table
    - Employee-role associations are stored in the `EmployeeRoles` table

The system follows the principle of role-based access control, where:
- Permissions represent granular actions
- Roles are collections of permissions
- Employees are assigned roles
- An employee has a permission if any of their roles includes that permission

This design allows for flexible permission management while maintaining security throughout the application.
