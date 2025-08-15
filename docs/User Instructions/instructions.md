# 📘 System Instructions Manual

This guide includes all instructions required to use the full system, which includes:

- 🧑‍💼 **Employee Management Module**
- 🚚 **Transport Management Module**

---

## 📑 Table of Contents

1. [Getting Started](#getting-started)
2. [Employee Management Module](#employee-management-module)
3. [Transport Management Module](#transport-management-module)
4. [Common System Permissions](#common-system-permissions)
5. [Troubleshooting & Error Messages](#troubleshooting--error-messages)
6. [Contact & Support](#contact--support)

---

## 🧭 Getting Started

Upon successful run, you'll see:

```
╔══════════════════════════════════════════════════╗
║                                                  ║
║     Welcome to SuperLee System Assignment 2      ║
║                                                  ║
╠══════════════════════════════════════════════════╣
║                     Welcome!                     ║
╚══════════════════════════════════════════════════╝
Current date: 2025-06-04
Logged in as: Not Logged In
┌─────────────────────────────────────────────┐
│ LOGIN                          │
└─────────────────────────────────────────────┘
💡 TIP: Enter 0 to exit the program.
Please enter your ID: 123456789
```
Appropriate permissions are required to access different features.
The system comes preloaded with an Admin user (ID: 123456789) who has full system permissions.

System options:

```
⚙️ SuperLee System OPTIONS:
1. Employee Module
2. Transport Module
3. Exit
```
**The Transport Module will be only available to users with proper permissions*

### Navigating the Modules:

- Choosing `1` starts the **Employee Module**:
    - Manages employees, shifts, assignments, and availability.

- Choosing `2` starts the **Transport Module**:
    - Manages Transports, Shipping Areas, Sites, and Trucks.

Each module includes its own menu system and ends with a return to the main system menu.

---

## 👥 Employee Management Module

# User Instructions for Employee Management

## Introduction to the Employee Management System

This document provides comprehensive instructions for using the supermarket's HR system.

## System Capabilities

The Employee Management system provides the following functionalities:
- Management of employee personal and employment information
- Configuration of roles and associated permissions
- Modification of employee data as required
- Administration of branch assignments
- Creation and management of shift schedules
- Assignment of personnel to appropriate shifts
- Recording of employee availability for scheduling purposes

## Navigation Instructions

1. Upon accessing the Employee Management menu, the following options will be displayed based on user permissions:
    - **Employees** - Personnel information management module
    - **Shifts** - Shift creation and management module
    - **Assignment Board** - Employee-to-shift assignment module
    - **Availability Board** - Employee availability management module

2. Enter the corresponding numerical value and press Enter to select an option.
3. A submenu with additional options will be displayed.
4. Enter the corresponding numerical value and press Enter to select the desired function.

## Employee Management

### Employee Information Management

- **View All Employees**: Displays a paginated list of all employees with basic information.
- **View Employee Details**: Retrieve comprehensive information for a specific employee by entering their ID number.
- **Create Employee**: Add a new employee record with the following required information:
    - Israeli ID
    - First and Last Name
    - Salary
    - Employment Terms (if applicable)
    - Start Date (DD-MM-YYYY format)
    - Branch Assignment

  *Requires CREATE_EMPLOYEE permission*

- **Update Employee Information**: Modify existing employee data including name, salary, or employment terms.

  *Requires UPDATE_EMPLOYEE permission*

- **Deactivate Employee**: Change an employee's status to inactive, preventing shift assignments.

  *Requires DEACTIVATE_EMPLOYEE permission*

### Role Management

- **View All Roles**: Displays a list of all roles defined in the system.
- **View Role Details**: Provides detailed information about a selected role, including associated permissions.
- **Assign Role to Employee**: Associates a role with an employee, granting all permissions of that role.

  *Requires ROLE_PERMISSION permission*

- **Remove Role from Employee**: Disassociates a role from an employee, revoking associated permissions.

  *Requires ROLE_PERMISSION permission*

- **Create Role**: Establishes a new role with a defined set of permissions.

  *Requires CREATE_ROLE permission*

- **Clone Role**: Creates a new role by duplicating an existing role's permissions.

  *Requires CREATE_ROLE permission*

### Permission Management

- **View All Permissions**: Displays a list of all permission types defined in the system.
- **Add Permission to Role**: Assigns a specific permission to a role.

  *Requires ADD_PERMISSION_TO_ROLE permission*

- **Remove Permission from Role**: Removes a specific permission from a role.

  *Requires REMOVE_PERMISSION_FROM_ROLE permission*

- **Create Permission**: Establishes a new permission type in the system.

  *Requires CREATE_PERMISSION permission*

## Shift Management

### Shift Information Management

- **View All Shifts**: Displays a list of all upcoming and past shifts.
- **View Shift Details**: Provides detailed information about a selected shift, including assigned employees and required roles.
- **View Personal Schedule**: Displays shifts assigned to the current user.
- **Create Shift**: Establishes a new shift with the following parameters:
    - Date
    - Shift Type (Morning or Evening)
    - Start and End Times
    - Required Personnel per Role
    - Assignment Status (Open/Closed)

  *Requires CREATE_SHIFT permission*

- **Create Weekly Shifts**: Generates morning and evening shifts for an entire week.

  *Requires CREATE_SHIFT permission*

- **Edit Shift**: Modifies existing shift parameters including times, required roles, and other information.

  *Requires EDIT_SHIFT permission*

- **Delete Shift**: Removes a shift from the system.

  *Requires REMOVE_SHIFT permission*

## Assignment Management

### Assignment Board Functionality

- **Upcoming Week Assignments**: View and manage employee assignments for the upcoming week.
- **Date-Specific Assignments**: Access and modify assignments for a specific date.
- **Historical Assignment Review**: View and modify assignments from previous periods.
- **Shift Management**: Access shift creation, viewing, and organization functions.
- **Assign Employee to Shift**: Associate an employee with a specific role in a shift.
- **Remove Employee from Shift**: Disassociate an employee from a shift assignment.
- **Modify Required Roles**: Adjust the quantity of personnel required for each role.
- **View Employee Assignments**: Display all shifts assigned to a specific employee.

## Availability Management

### Availability Board Functionality

- **Record Availability**: Register availability status for specific shifts.
- **Batch Availability Update**: Modify availability status for multiple shifts simultaneously.

## Procedural Instructions

### Employee Creation Procedure

1. Navigate to the main menu and select **Employees**.
2. Select **Create Employee** from the submenu.
3. Enter the required information when prompted:
    - Israeli ID (must be a valid identification number)
    - First and Last Name
    - Salary
    - Start Date (DD-MM-YYYY format)
    - Branch ID
4. Enter employment terms if applicable, or enter 'done' to proceed without terms.
5. Verify the entered information for accuracy.
6. System will display confirmation upon successful employee creation.

### Role Assignment Procedure

1. Navigate to the main menu and select **Employees**.
2. Select **Add Role to Employee** from the submenu.
3. Enter the employee's Israeli ID.
4. Select the appropriate role from the displayed list.
5. Confirm the selection when prompted.
6. System will display confirmation upon successful role assignment.

### Shift Creation Procedure

1. Navigate to the main menu and select **Shifts**.
2. Select **Add Shift** from the submenu.
3. Enter the required information when prompted:
    - Date
    - Shift Type (Morning/Evening)
    - Start and End Times
    - Required Personnel per Role
    - Assignment Status (Open/Closed)
4. Verify the entered information for accuracy.
5. System will display confirmation upon successful shift creation.

### Employee-to-Shift Assignment Procedure

1. Navigate to the main menu and select **Assignment Board**.
2. Select the desired view option (upcoming week, specific date, or historical).
3. Select the target shift.
4. Select **Assign Employee to Shift**.
5. Select the role to be filled.
6. Select an employee from the list of eligible personnel.
7. Confirm the assignment when prompted.
8. System will display confirmation upon successful assignment.

### Availability Registration Procedure

1. Navigate to the main menu and select **Availability Board**.
2. Review the shifts displayed for the upcoming period.
3. Enter the shift identifiers for which availability status will be updated (comma-separated values or 'all' for batch update).
4. Enter 'Y' to indicate availability or 'N' to indicate unavailability.
5. System will update the availability status immediately.

## System Permissions Reference Table

The following table provides a comprehensive reference of system permissions and their associated functionalities:

| Permission | Description |
|------------|-------------|
| `CREATE_EMPLOYEE` | Allows creation of new employee records in the system |
| `UPDATE_EMPLOYEE` | Allows modification of existing employee information |
| `EDIT_EMPLOYEE` | Alternative designation for employee information modification permission |
| `VIEW_EMPLOYEE` | Allows viewing employee information without modification rights |
| `DEACTIVATE_EMPLOYEE` | Allows changing an employee's status to inactive |
| `DELETE_EMPLOYEE` | Allows permanent removal of employee records from the system |
| `MANAGE_HR` | Provides general human resources management capabilities |
| `CREATE_ROLE` | Allows creation of new roles and role duplication |
| `EDIT_ROLE` | Allows modification of role parameters |
| `ROLE_PERMISSION` | Allows assignment and removal of roles from employees |
| `ADD_PERMISSION_TO_ROLE` | Allows addition of permissions to roles |
| `REMOVE_PERMISSION_FROM_ROLE` | Allows removal of permissions from roles |
| `CREATE_PERMISSION` | Allows creation of new permission types |
| `EDIT_PERMISSION` | Allows modification of permission parameters |
| `GET_ROLES` | Allows viewing of all roles in the system |
| `CREATE_SHIFT` | Allows creation of new shifts |
| `UPDATE_SHIFT` | Allows modification of shift information |
| `EDIT_SHIFT` | Alternative designation for shift information modification permission |
| `REMOVE_SHIFT` | Allows deletion of shifts from the system |
| `GET_SHIFT` | Allows viewing of shift information |
| `MANAGE_SHIFT` | Provides general shift management capabilities |
| `VIEW_SHIFT` | Allows viewing shift information without modification rights |
| `ASSIGN_EMPLOYEE` | Allows assignment of employees to shifts |
| `UPDATE_AVAILABLE` | Allows modification of employee availability status |


---

## 🚚 Transport Management Module

# User Instructions for Transport Module

### Quick Start
The module entry point is from the "Transport Module Main Menu" screen where the user can navigate to the desired feature or operation.
The system manages transport tasks, including transports scheduling, assigning drivers and trucks to transports, delivery management, and inventory-related transportation.

---

## Overview
The Transport Module handles all transportation logistics within the system and supports features such as:

- Transports and fleet management
- Trucks and drivers assignment
- Schedule management for transport tasks
- Driver assignment and tracking
- Transports Routes and logistics tracking

Below are detailed instructions for navigating and using the Transport Module.

---

## Features Overview

### 1. Vehicle Management
Efficiently manage and maintain the company's fleet.
- **View Vehicle List:** View all vehicles registered within the system.
- **Add Vehicle:** Add a new vehicle to the fleet.
- **Edit Vehicle Details:** Update attributes of a vehicle (e.g., operational status, capacity).
- **Remove Vehicle:** Remove a vehicle from the system if it is no longer operational.

---

### 2. Driver Management
Manage the drivers responsible for handling transportation.
- **View Drivers:** See a list of registered drivers and their statuses.
- **Add Driver:** Add a new driver to the system.
- **Edit Driver Details:** Update driver information (e.g., license details, availability).
- **Remove Driver:** Remove a driver profile from the system.

---

### 3. Route Planning
Optimize and plan transport routes to ensure efficient delivery.
- **View Routes:** List all available routes used for transportation.
- **Plan New Route:** Create a new route based on pickup and delivery points.
- **Edit Route Details:** Modify existing route information.
- **Delete Route:** Remove routes if they are no longer necessary.

---

### 4. Delivery Management
Oversee the complete delivery lifecycle.
- **Create New Delivery Order:** Initiate a new delivery order with assigned routes, drivers, and vehicles.
- **Check Delivery Status:** Track the status of ongoing delivery orders.
- **Edit Delivery Orders:** Update existing delivery order details.
- **Cancel Delivery Orders:** Cancel a delivery order if it is no longer valid.
- **Delivery History:** View past delivery records and reports.

---

### 5. Schedule Management
Streamline scheduling for drivers and vehicles.
- **View Transport Schedule:** View the current day's or week's transport schedules.
- **Add Schedule:** Create a new schedule for a vehicle or driver.
- **Edit Schedule:** Modify existing transport schedules as required.
- **Delete Schedule:** Remove a schedule that is no longer valid.

---

### 6. Reports and Logs
Track transport and driver performance through reports.
- **View Vehicle Logs:** Access maintenance and operational logs for vehicles.
- **Driver Performance Reports:** Evaluate driver performance metrics.
- **Delivery Success Reports:** View statistics on completed and delayed deliveries.

---

## General Usage Instructions

### Starting the Transport Module
1. Launch the system and select the Transport Module from the main menu.
2. Review your permissions to ensure access to required features.

### Navigation
- Use the numbered options on each menu to navigate to specific features.
- Submenus will guide you to finer-grain options for managing vehicles, routes, schedules, or drivers.

### Permissions
Access to specific features depends on your role and permissions. For example:
- **VIEW_VEHICLE**: For accessing vehicle information.
- **CREATE_DELIVERY_ORDER**: For creating delivery tasks.
- **EDIT_SCHEDULE**: For modifying schedules.

Ensure you have the appropriate role or notify your system administrator.

---

## Common Commands and Actions

- **Navigating Menus:** Use numeric keys to enter a menu. Enter "0" to go back to the previous menu.
- **Input Validation:** Provide correct details for inputs like vehicle registration numbers, driver IDs, or route names.
- **Error Messages:** Pay attention to error messages that may indicate invalid commands or permissions.
- **Exit Options:** Use the "Exit" option in any menu to return to the Main Menu or end the session.

---

## Error and Warning Messages

- **Missing Permissions:** "Permission Denied - You lack the required permission for this operation."
- **Invalid Inputs:** "Error - Input does not match the required format, please try again."
- **Schedule Conflict:** "Warning - Schedule conflict detected. Please review and adjust timing."

---

## Permissions Specific to Transport Management

| **Permission**                  | **Description**                                              |
|---------------------------------|--------------------------------------------------------------|
| `CREATE_TRANSPORT`              | Allows to Create delivery transport.                         |
| `DELETE_TRANSPORT`              | Allows to Delete transport.                                  |
| `EDIT_TRANSPORT`                | Allows to Edit transport details.                            |
| `VIEW_TRANSPORT`                | Allows to View transport\s.                                  |
| `VIEW_RELEVANT_TRANSPORTS`      | Allows to View relevant transports.(Specifically for Driver) |
| `ADD_SHIPPING_AREA`             | Allows to Add a shipping area.                               |
| `DELETE_SHIPPING_AREA`          | Allows to Delete a shipping area.                            |
| `EDIT_SHIPPING_AREA`            | Allows to Edit a shipping area's details.                    |
| `SHOW_SHIPPING_AREAS`           | Allows to Show shipping area's details.                      |
| `ADD_SITE`                      | Allows to Add a site.                                        |
| `DELETE_SITE`                   | Allows to Delete a site.                                     |
| `EDIT_SITE`                     | Allows to Edit a site's details.                             |
| `SHOW_SITES`                    | Allows to View site's details.                               |
| `ADD_ITEM_TO_TRANSPORT`         | Allows to add an item to a transport.                        |
| `DELETE_ITEM_FROM_TRANSPORT`    | Allows to delete an item from a transport.                   |
| `EDIT_ITEM_IN_TRANSPORT`        | Allows to edit an item that is in a transport.               |
| `EDIT_TRANSPORT_ITEM_CONDITION` | Allows to edit the condition of an item in a transport.      |
| `ADD_TRUCK`                     | Allows to add a truck to the truck fleet.                    |
| `DELETE_TRUCK`                  | Allows to delete a truck from the truck fleet.               |
| `SHOW_TRUCKS`                   | Allows to view truck's details.                              |

---

# 🧩 System Database Initialization Overview

This document outlines the data initialized by the system in two configurations:
- **Full Database**: Full dataset for operational use.
- **Minimal Database**: Lightweight setup for development or testing.

---

## 🔹 Full Database

The Full Database includes comprehensive data for all modules, ensuring the system is fully functional with all features available.

### 🔧 Employee & HR Module

- **Branches**: Includes `Headquarters` and `Beer Shave` branches.
- **Permissions**: Comprehensive permission set across employees, roles, shifts, transports, and operational tasks.
- **Roles**: Includes roles like `Admin`, `HR manager`, `Transport Manager`, `Shift Manager`, `Stocker`, `Cashier`, `DriverA`, `DriverB`, `DriverC`, `DriverD`, `DriverE`, `Cleaner`, and `Warehouse Manager`.
- **Employees**: A diverse team of employees is initialized, each with unique roles, salaries, and terms:
    - `Admin User` (ID: 123456789): System administrator with full access.
    - `Shira Steinbuch` (ID: 111111111): HR manager.
    - `Ramzi Abd Rabo` (ID: 222222222): Stocker.
    - `Kochava Shavit` (ID: 333333333): Cashier.
    - `Moshe Cohen` (ID: 444444444): Transport Manager.
    - `Yael Levy` (ID: 555555555): Driver with license E.
    - `David Mizrahi` (ID: 666666666): Cleaner.
    - `Emmanuel Macroni` (ID: 777777777): Driver with license C.
    - `Doron Yakov` (ID: 888888888): Warehouse manager.
- **EmployeeTerms**: Full employment terms, pension, insurance, schedules, and departments.

### 🚛 Transport Module

- **Shipping Areas**: Central(0), South(1), and East(2) Districts.
- **Sites**: Locations in Tel Aviv(in Area), Ben Gurion Uni, and Afula.
- **Trucks**: Three trucks are initialized in the system:
    - `Truck 401` (Model: Truck Model A): Light-duty truck with a net weight of 1,000 kg and max carry weight of 20 tons. Requires license type A.
    - `Truck 402` (Model: Truck Model B): Heavy-duty truck with a net weight of 15,000 kg and max carry weight of 120 tons. Requires license type E.
    - `Truck 403` (Model: Truck Model C): Medium truck with a net weight of 12,000 kg and max carry weight of 100 tons. Requires license type C.
  All trucks are not assigned to any transport task at system startup.

---

## 🔸 Minimal Database

The minimal database includes essential setup for the system to run with core capabilities.

### 🔧 Employee & HR Module

- **Branches**: One branch - Ben Gurion Uni (Beer Sheva).
- **Permissions**: Full permissions setup as in the basic version.
- **Roles**: Same role definitions as the full setup.
- **Employees**: One Admin employee (`123456789`) with full credentials.
- **EmployeeRoles**: Admin linked to full-access `Admin` role.
- **EmployeeTerms**: Full set of employment terms for the Admin.
- **BankAccounts**: Includes a sample bank account for the Admin.

### 🚛 Transport Module

- **Shipping Areas**: Central, South, and East Districts.
- **Sites**: Includes Tel Aviv, Ben Gurion Uni, and Afula.

---

## Final Notes

- Contact an administrator if permission issues block you from accessing required features.
- Always log out of the module after completing work to secure access.

---
