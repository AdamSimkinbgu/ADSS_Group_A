# System Permissions

## Employee Management Permissions

| Permission | Purpose | Usage |
|------------|---------|-------|
| `CREATE_EMPLOYEE` | Allows creating new employees in the system | Used in employee creation workflows to add new staff members with complete details |
| `UPDATE_EMPLOYEE` | Allows modifying existing employee information | Used when updating employee details such as name, salary, and employment terms |
| `EDIT_EMPLOYEE` | Alias for updating employee fields | Alternative permission for employee information editing |
| `VIEW_EMPLOYEE` | Allows viewing employee information | Used for read-only access to employee records |
| `DEACTIVATE_EMPLOYEE` | Allows marking an employee as inactive | Used when an employee leaves or is temporarily suspended |
| `DELETE_EMPLOYEE` | Allows permanent removal of employee records | Used for completely removing an employee from the system |
| `MANAGE_HR` | Provides general HR management capabilities | Umbrella permission for human resources management functions |

## Role and Permission Management

| Permission | Purpose | Usage |
|------------|---------|-------|
| `CREATE_ROLE` | Allows creating new roles | Used when defining new job roles in the system |
| `EDIT_ROLE` | Allows modifying existing roles | Used when changing role definitions |
| `ROLE_PERMISSION` | Allows assigning/removing roles from employees | Used when managing which roles an employee has |
| `ADD_PERMISSION_TO_ROLE` | Allows adding permissions to roles | Used when extending a role's capabilities |
| `REMOVE_PERMISSION_FROM_ROLE` | Allows removing permissions from roles | Used when restricting a role's capabilities |
| `CREATE_PERMISSION` | Allows creating new permission types | Used when defining new system capabilities |
| `EDIT_PERMISSION` | Allows modifying permission properties | Used when changing permission definitions |
| `GET_ROLES` | Allows viewing all roles | Used for accessing role information |
| `ROLE_REQUIRED` | Indicates a role requirement | Internal system use for role-based access control |

## Shift Management Permissions

| Permission | Purpose | Usage |
|------------|---------|-------|
| `CREATE_SHIFT` | Allows creating new shifts | Used when scheduling new work shifts |
| `UPDATE_SHIFT` | Allows modifying shift information | Used when changing shift details |
| `EDIT_SHIFT` | Alias for updating shift fields | Alternative permission for shift editing |
| `REMOVE_SHIFT` | Allows deleting shifts | Used when canceling scheduled shifts |
| `GET_SHIFT` | Allows viewing shift information | Used for accessing shift schedules |
| `MANAGE_SHIFT` | Provides general shift management | Umbrella permission for shift-related functions |
| `VIEW_SHIFT` | Allows viewing shift details | Used for read-only access to shift information |

## Assignment Management Permissions

| Permission | Purpose | Usage |
|------------|---------|-------|
| `ASSIGN_EMPLOYEE` | Allows assigning employees to tasks | General permission for employee assignment |

## Availability Management Permissions

| Permission | Purpose | Usage |
|------------|---------|-------|
| `UPDATE_AVAILABLE` | Allows marking availability for shifts | Used by employees to indicate their availability |

## Transport Management Permissions

| Permission | Purpose | Usage |
|------------|---------|-------|
| `CREATE_TRANSPORT` | Allows creating transport documents | Used when initiating new transport operations |
| `EDIT_TRANSPORT` | Allows modifying transport details | Used when updating transport information |
| `DELETE_TRANSPORT` | Allows removing transport records | Used when canceling transport operations |
| `VIEW_TRANSPORT` | Allows viewing transport information | Used for read-only access to transport details |
| `VIEW_RELEVANT_TRANSPORTS` | Allows viewing assigned transports | Used by drivers to see their assigned deliveries |
| `ADD_ITEM_TO_TRANSPORT` | Allows adding items to transport | Used when loading items for delivery |
| `EDIT_ITEM_IN_TRANSPORT` | Allows modifying transport items | Used when updating item details in transport |
| `DELETE_ITEM_FROM_TRANSPORT` | Allows removing items from transport | Used when unloading items from transport |
| `EDIT_TRANSPORT_ITEM_CONDITION` | Allows updating item condition | Used for marking damaged or special condition items |

## Site Management Permissions

| Permission | Purpose | Usage |
|------------|---------|-------|
| `ADD_SITE` | Allows adding new sites | Used when creating new store locations |
| `EDIT_SITE` | Allows modifying site information | Used when updating site details |
| `DELETE_SITE` | Allows removing sites | Used when closing locations |
| `SHOW_SITES` | Allows viewing site information | Used for accessing site listings |

## Shipping Area Permissions

| Permission | Purpose | Usage |
|------------|---------|-------|
| `ADD_SHIPPING_AREA` | Allows creating shipping areas | Used when defining new delivery zones |
| `EDIT_SHIPPING_AREA` | Allows modifying shipping areas | Used when updating delivery zone information |
| `DELETE_SHIPPING_AREA` | Allows removing shipping areas | Used when discontinuing delivery to certain areas |
| `SHOW_SHIPPING_AREAS` | Allows viewing shipping areas | Used for accessing delivery zone information |

## Truck Management Permissions

| Permission | Purpose | Usage |
|------------|---------|-------|
| `ADD_TRUCK` | Allows adding new trucks | Used when acquiring new delivery vehicles |
| `DELETE_TRUCK` | Allows removing trucks | Used when retiring vehicles from service |
| `SHOW_TRUCKS` | Allows viewing truck information | Used for accessing vehicle listings |

## Operational Permissions

All the permissions listed below are used for operational tasks and are not directly related to employee management or system administration.
Here for reference only.

| Permission | Purpose | Usage |
|------------|---------|-------|
| `MANAGE_INVENTORY` | Allows inventory management | Used for stock control and inventory operations |
| `HANDLE_CASH` | Allows cash handling | Used by cashiers for payment processing |
| `DRIVE_VEHICLE` | Allows vehicle operation | Used by delivery personnel |
| `CLEAN_FACILITY` | Allows facility maintenance | Used by cleaning staff |
| `STOCK_SHELVES` | Allows shelf stocking | Used by stockers for merchandise organization |
