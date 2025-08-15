# User Instructions for Employee Module

### Quick Start
The module entry point is from the "Employee Module Main Menu" screen where the user can navigate to the desired part.
The system is preloaded with an Admin user with all the system permissions, the user id is 123456789.
**NEED TO ADD MORE ONCE WE WILL UNITE ALL PARTS TO ONE CLI**

### Overview
The employee module is responsible for managing the employees, shifts, and system permissions.
 - The employee management supports viewing, editing, and creating employees.
 - The Shift management supports viewing, editing, and creating shifts.
 - The Assignment management supports assigning and remove employees to shifts.
 - The Availability management supports mark employees as available or unavailable for shifts.

---

## Employee Management Instructions

### Overview
The Employee Management allows you to manage employees, roles, and permissions within the system. Different options are available based on user permissions.

### Getting Started
1. When you start the Employee Management, you'll see a main menu with numbered options.
2. Enter the number corresponding to your desired action and press Enter.

### Features

#### Employee Management
- **View All Employees**: See a list of all employees in the system
- **View Employee Details**: Enter an employee's Israeli ID to see their complete information
- **Create Employee**: Add a new employee to the system (requires CREATE_EMPLOYEE permission)
- **Update Employee**: Modify an existing employee's information (requires UPDATE_EMPLOYEE permission)
- **Deactivate Employee**: Deactivate an employee in the system (requires DEACTIVATE_EMPLOYEE permission)

#### Role Management
- **View All Roles**: See a list of all roles in the system
- **View Role Details**: Enter a role name to see its complete information
- **Create Role**: Create a new role in the system (requires CREATE_ROLE permission)
- **Clone Role**: Create a new role based on an existing one (requires CREATE_ROLE permission)
- **Add Role to Employee**: Assign a role to an employee (requires ROLE_PERMISSION)
- **Remove Role from Employee**: Remove a role from an employee (requires ROLE_PERMISSION permission)

#### Permission Management
- **View All Permissions**: See a list of all permissions in the system
- **Create Permission**: Add a new permission to the system (requires CREATE_PERMISSION permission)
- **Add Permission to Role**: Assign a permission to a role (requires ADD_PERMISSION_TO_ROLE permission)
- **Remove Permission from Role**: Remove a permission from a role (requires REMOVE_PERMISSION_FROM_ROLE permission)

---

## Shift Management Instructions

### Overview
The Shift Management System allows you to create, update, and manage shifts for employees. Different actions are available depending on your permissions.

### Getting Started
1. From the main menu, select the **Shifts** option by entering the number `2` and pressing Enter.
2. You will enter the Shift Management System and see a list of shift-related actions.

### Features

#### Shift Management
- **View All Shifts**: Display a list of all scheduled shifts in the system.
- **View Shift Details**: Enter a shift ID to view the full details of a specific shift.
- **Add Shift**: Manually create a new shift (requires CREATE_SHIFT permission).
- **Add Weekly Shifts**: Generate a full week's schedule of shifts automatically (requires CREATE_SHIFT permission).
- **Edit Shifts**: Modify the details of an existing shift (requires UPDATE_SHIFT permission).
- **Delete Shifts**: Remove an existing shift from the schedule (requires DELETE_SHIFT permission).
- **Back to Main Menu**: Return to the Employee Module main menu.

---

## Assignment Management Instructions

### Overview
The Assignment Management System allows you to assign employees to specific shifts and manage their participation. Different actions are available depending on your permissions.

### Getting Started
1. From the main menu, select the **Assignment Board** option by entering the number `3` and pressing Enter.
2. You will enter the Assignment Management System and see a list of assignment-related actions.

### Features

#### Assignment Management
- **Assign Employees to a Shift**: Select a shift and assign employees based on their roles and availability (requires ASSIGN_EMPLOYEE permission).
- **Remove Employee from Shift**: Remove an employee who has already been assigned to a shift (requires REMOVE_EMPLOYEE_FROM_SHIFT permission).
- **Back to Main Menu**: Return to the Employee Module main menu.

---

## Availability Management Instructions

### Overview
The Availability Management System allows employees to mark their availability for upcoming shifts. This helps shift managers assign employees according to their availability status.

### Getting Started
1. From the main menu, select the **Availability Board** option by entering the number `4` and pressing Enter.
2. You will enter the Availability Management System and see a table listing the shifts for the week.
3. You can update your availability by selecting a shift number and marking yourself as available or not available.

### Features

#### Availability Management
- **View Weekly Availability**: See a list of all your upcoming shifts and current availability status.
- **Update Availability**: Select a shift by its number and mark yourself as Available (`Y`) or Not Available (`N`).
- **Save Changes**: After updating, availability will be saved automatically.
- **Back to Main Menu**: Return to the Employee Module main menu.

---

## General 

### Starting the System

1. The system will display: "Initializing Employee Module System..."

2. You will be prompted with: "Do you want to load data? (y/n) ==>"
   - Type `y` and press Enter to load data from files (recommended for normal operation)
   - Type `n` and press Enter to start in minimal mode with only the Admin user
3. System Initialization:
   - If you chose to load data, the system will load from data files in the "./data" directory
   - If you chose minimal mode, only the Admin user will be available
   - The system will display a confirmation message upon successful initialization
4. User Selection:
   - In minimal mode, you will automatically log in as the Admin user (ID: 123456789)
   - In normal mode, you will be prompted to select a user:
        ```
        Pick an employee to start the CLI with:
        0. Admin
        1. Shira Steinbuch
        2. Ramzi Abd Rabo
        3. Kochava Shavit
        Enter your choice:
        ```
   - Enter the number (0-3) corresponding to your choice and press Enter
4. The Main Menu will appear, displaying a welcome banner with the current date and logged-in user information.

### Using the Main Menu

The Main Menu displays options based on your user permissions:

1. **Employees** - Manage employee information (requires VIEW_EMPLOYEE or EDIT_EMPLOYEE permission)
2. **Shifts** - Manage shifts (requires VIEW_SHIFT or EDIT_SHIFT permission)
3. **Assignment Board** - Manage employee assignments to shifts (requires ASSIGN_EMPLOYEE permission)
4. **Availability Board** - Manage employee availability (requires UPDATE_AVAILABLE permission)
0. **Exit** - Exit the system

Enter the number corresponding to your desired action and press Enter.

### Errors

- **Error initializing system**: Check that the data directory exists and contains valid data files
- **Invalid choice**: Make sure you enter a valid option number from the displayed menu
- **Permission denied**: The current user does not have the required permissions for the selected action

### Data and Massages info
- Required fields are marked with an asterisk (*) or highlighted
- Error messages appear in red text
- Success messages appear in green text
- Warning messages appear in yellow text
- Press Enter when prompted to continue to the next screen
- The System date format is dd-MM-yyyy (e.g 02-05-2025)

### System Permission List

| Permission | Category | Description |
|------------|----------|-------------|
| `CREATE_EMPLOYEE` | Employee Management | Allows creating new employees in the system with full details. |
| `UPDATE_EMPLOYEE` | Employee Management | Allows modifying existing employee information such as name, salary, and employment terms. |
| `EDIT_EMPLOYEE` | Employee Management | Allows editing employee information (alias for updating fields). |
| `VIEW_EMPLOYEE` | Employee Management | Allows viewing employee information without editing rights. |
| `DEACTIVATE_EMPLOYEE` | Employee Management | Allows marking an employee as inactive in the system. |
| `DELETE_EMPLOYEE` | Employee Management | Allows permanent removal of an employee record from the system. |
| `MANAGE_HR` | Employee Management | Provides general human resources management capabilities. |
| `CREATE_ROLE` | Role Management | Allows creating new roles in the system and cloning existing ones. |
| `EDIT_ROLE` | Role Management | Allows editing existing role details. |
| `ROLE_PERMISSION` | Role Management | Allows assigning and removing roles from employees. |
| `ADD_PERMISSION_TO_ROLE` | Role Management | Allows adding specific permissions to a role. |
| `REMOVE_PERMISSION_FROM_ROLE` | Role Management | Allows removing specific permissions from a role. |
| `CREATE_PERMISSION` | Role Management | Allows creating new permission types in the system. |
| `EDIT_PERMISSION` | Role Management | Allows editing the properties of a permission. |
| `GET_ROLES` | Role Management | Allows viewing all existing roles in the system. |
| `ROLE_REQUIRED` | Role Management | Indicates a role requirement for a certain operation (internal system use). |
| `CREATE_SHIFT` | Shift Management | Allows creating new shifts individually or by week. |
| `UPDATE_SHIFT` | Shift Management | Allows modifying existing shift information. |
| `EDIT_SHIFT` | Shift Management | Allows editing shift details (alias for updating fields). |
| `REMOVE_SHIFT` | Shift Management | Allows deleting shifts from the system. |
| `GET_SHIFT` | Shift Management | Allows viewing shift information and shift lists. |
| `MANAGE_SHIFT` | Shift Management | Provides general shift management capabilities. |
| `ASSIGN_EMPLOYEE` | Assignment Management | Allows assigning employees to shifts. |
| `ASSIGN_EMPLOYEE_TO_SHIFT` | Assignment Management | Allows assigning employees to shifts based on roles and availability. |
| `UPDATE_AVAILABLE` | Availability Management | Allows marking availability for shifts. |
| `MANAGE_INVENTORY` | Operational | Allows managing inventory stock and operations. |
| `HANDLE_CASH` | Operational | Allows handling cash operations (e.g., cashier duties). |
| `DRIVE_VEHICLE` | Operational | Allows operating company vehicles. |
| `CLEAN_FACILITY` | Operational | Allows cleaning and maintaining company facilities. |
| `STOCK_SHELVES` | Operational | Allows stocking merchandise and organizing inventory shelves. |
---