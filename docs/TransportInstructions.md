# User Instructions for Transport Module

### Quick Start
The module's entry point is from the "Transport Module Main Menu" screen where users can navigate to desired features or operations. The system manages transportation tasks, including scheduling, route planning, delivery management, inventory-related logistics, and driver/truck assignments.

---

## Overview
The Transport Module handles all transportation logistics within the system and supports the following features:

- **Transport Management**
- **Truck and Driver Assignment**
- **Schedule Management**
- **Shipping Area and Site Management**
- **Delivery Creation and Monitoring**
- **Route Planning**

The features listed below can vary depending on permissions and roles:

---

## Getting Started

1. From the **Transport System Main Menu**, you will see options based on your role permissions:
    - **Enter my relevant menu** - Access appropriate options based on your role (e.g., Transport Manager, Admin, or Driver menus).
    - **Exit the Transport System** - Return to the main program menu.

2. If you are an Admin or Transport Manager, the following options will appear:
    - **(1)** **Transports Options Menu** - Manage all aspects of transports, including creation, editing, and monitoring.
    - **(2)** **Shipping Areas Options Menu** - Manage shipping areas.
    - **(3)** **Sites Options Menu** - Handle all site-related configurations.
    - **(4)** **Trucks Options Menu** - Manage trucks in the system.
    - **(5)** **View All Drivers** - Display details of all drivers.
    - **(6)** **Go back to Welcoming Transport System Screen** - Return to the module's welcome screen.

3. If you are a Driver, you will see options tailored to delivery and route assignments.

4. Use the corresponding menu numbers to access a specific feature and follow prompts to complete your intended action.

---

## Transport Management Features

### **1. Transports Options Menu**
Manage all transport-related operations:
- **Create Transport:** Initiate a new delivery request with routes, assigned drivers, and vehicles.
- **View All Transports:** Display all pending and completed transports.
- **Edit Transport:** Edit delivery details, such as status, routes, or items.
- **Delete Transport:** Delete a transport if no longer needed.
- **Monitor Transport Issues:** Manage pairing or weight-related problems for queued transports.

---

### **2. Shipping Areas Options Menu**
Configure and manage shipping areas:
- **Add Shipping Area:** Define a new shipping area.
- **View All Shipping Areas:** Review details of existing shipping areas.
- **Edit Shipping Area:** Update details of a shipping area.
- **Delete Shipping Area:** Remove a shipping area from the system.

---

### **3. Sites Options Menu**
Oversee site-related configurations:
- **Add Site:** Create a new location in the database.
- **View All Sites:** Review details of existing sites.
- **Edit Site Details:** Modify attributes of a specific site.
- **Delete Site:** Remove a site from the system.

---

### **4. Trucks Options Menu**
Manage fleet vehicles:
- **View All Trucks:** Display details for all trucks.
- **Add Truck:** Register a new truck for operations.
- **Delete Truck:** Remove an existing truck from the system if no longer operational.

---

### **5. Driver Management**
- **View All Drivers:** Display all drivers currently registered in the system. Includes details such as IDs, availability, and assigned tasks.

---

## General Usage Instructions

### Permissions
Access to features depends on user roles:
- Transport Managers and Admins have full permissions and access to the entire system.
- Drivers will see restricted options related to routes, schedules, and assigned deliveries.

### Menu Navigation
- Use numerical keys to navigate within menus.
- Enter the corresponding number associated with any given feature or submenu to proceed.
- Use `0` to return to the previous menu or `9` if prompted to exit entirely.

### Input Validation
- The system requires accurate inputs, such as driver IDs, truck fleet numbers, site names, or transport weights.
- If invalid information is entered, the system will prompt for corrections.

---

## Error and Warning Messages

The system provides meaningful messages for troubleshooting:
- **Permission Errors:** "You lack the permissions for this operation."
- **Invalid Input:** "The entered details are incorrect; please verify and try again."
- **Transport Overload:** "Warning: Specified load exceeds truck capacity; re-assign weight or change vehicle."

---

## Permissions in Transport Management

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

## Final Notes

- Contact your administrator to resolve any access or technical issues.
- Log out after using the module to secure system access.