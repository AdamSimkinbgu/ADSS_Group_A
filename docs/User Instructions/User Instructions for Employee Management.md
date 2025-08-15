# User Instructions for Employee Management

## Introduction to the Employee Management System

This document provides comprehensive instructions for using the supermarket's HR system. 

## System Access

The Employee Management system is accessed through the main menu. 
Appropriate permissions are required to access different features. 
The system comes preloaded with an Admin user (ID: 123456789) who has full system permissions.

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

1. Upon accessing the main menu, the following options will be displayed based on user permissions:
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
