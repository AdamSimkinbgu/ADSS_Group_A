package Suppliers.PresentationLayer.CLIs;

import java.util.HashMap;
import java.util.Map;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;

public class OrderCLI {
    private final View view;
    private final Map<String, CommandInterface> orderCommands;

    /**
     * @param view     – your View implementation for user I/O (showMessage, readLine, showError, etc.)
     * @param commands – a Map containing the following keys:
     *                 "CreateRegularOrderCMD", "CreatePeriodicOrderCMD",
     *                 "UpdateRegularOrderCMD", "UpdatePeriodicOrderCMD",
     *                 "RemoveRegularOrderCMD", "RemovePeriodicOrderCMD",
     *                 "ViewAllRegularOrdersCMD", "ViewAllPeriodicOrdersCMD"
     */
    public OrderCLI(View view, Map<String, CommandInterface> commands) {
        this.view = view;
        this.orderCommands = new HashMap<>();

        // Creation commands
        orderCommands.put("CreateRegularOrderCMD", commands.get("CreateRegularOrderCMD"));
        orderCommands.put("CreatePeriodicOrderCMD", commands.get("CreatePeriodicOrderCMD"));

        // Update commands
        orderCommands.put("UpdateRegularOrderCMD", commands.get("UpdateRegularOrderCMD"));
        orderCommands.put("UpdatePeriodicOrderCMD", commands.get("UpdatePeriodicOrderCMD"));

        // Removal commands
        orderCommands.put("RemoveRegularOrderCMD", commands.get("RemoveRegularOrderCMD"));
        orderCommands.put("RemovePeriodicOrderCMD", commands.get("RemovePeriodicOrderCMD"));

        // “View All” commands
        orderCommands.put("ViewAllRegularOrdersCMD", commands.get("ViewAllRegularOrdersCMD"));
        orderCommands.put("ViewAllPeriodicOrdersCMD", commands.get("ViewAllPeriodicOrdersCMD"));
    }

    /**
     * Starts the top‐level Order menu loop. Typing "return" here goes back to the caller (e.g. main menu).
     */
    public void start() {
        while (true) {
            view.showMessage("\n=== Order Management Menu ===");
            view.showMessage("1. Create Order");
            view.showMessage("2. Update Order");
            view.showMessage("3. Remove Order");
            view.showMessage("4. List Orders");
            view.showMessage("Type 'return' to go back to the previous menu.");

            String choice = view.readLine("Choose an option: ").trim().toLowerCase();
            if (choice.equals("return")) {
                return;
            }

            switch (choice) {
                case "1":
                    showCreateSubmenu();
                    break;
                case "2":
                    showUpdateSubmenu();
                    break;
                case "3":
                    showRemoveSubmenu();
                    break;
                case "4":
                    showListSubmenu();
                    break;
                default:
                    view.showError("Invalid option. Please choose 1–4 or type 'return'.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // SUBMENU: Create Order (Regular vs. Periodic)
    // ─────────────────────────────────────────────────────────────────────────────
    private void showCreateSubmenu() {
        while (true) {
            view.showMessage("\n--- Create Order ---");
            view.showMessage("1. Create Regular Order");
            view.showMessage("2. Create Periodic Order");
            view.showMessage("Type 'back' to go back.");

            String choice = view.readLine("Choose an option: ").trim().toLowerCase();
            if (choice.equals("back")) {
                return; // go up one level
            }

            switch (choice) {
                case "1":
                    runCommand("CreateRegularOrderCMD");
                    break;
                case "2":
                    runCommand("CreatePeriodicOrderCMD");
                    break;
                default:
                    view.showError("Invalid option. Please choose 1–2 or 'back'.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // SUBMENU: Update Order (Regular vs. Periodic)
    // ─────────────────────────────────────────────────────────────────────────────
    private void showUpdateSubmenu() {
        while (true) {
            view.showMessage("\n--- Update Order ---");
            view.showMessage("1. Update Regular Order");
            view.showMessage("2. Update Periodic Order");
            view.showMessage("Type 'back' to go back.");

            String choice = view.readLine("Choose an option: ").trim().toLowerCase();
            if (choice.equals("back")) {
                return; // go up one level
            }

            switch (choice) {
                case "1":
                    runCommand("UpdateRegularOrderCMD");
                    break;
                case "2":
                    runCommand("UpdatePeriodicOrderCMD");
                    break;
                default:
                    view.showError("Invalid option. Please choose 1–2 or 'back'.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // SUBMENU: Remove Order (Regular vs. Periodic)
    // ─────────────────────────────────────────────────────────────────────────────
    private void showRemoveSubmenu() {
        while (true) {
            view.showMessage("\n--- Remove Order ---");
            view.showMessage("1. Remove Regular Order");
            view.showMessage("2. Remove Periodic Order");
            view.showMessage("Type 'back' to go back.");

            String choice = view.readLine("Choose an option: ").trim().toLowerCase();
            if (choice.equals("back")) {
                return; // go up one level
            }

            switch (choice) {
                case "1":
                    runCommand("RemoveRegularOrderCMD");
                    break;
                case "2":
                    runCommand("RemovePeriodicOrderCMD");
                    break;
                default:
                    view.showError("Invalid option. Please choose 1–2 or 'back'.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // SUBMENU: List Orders (Regular vs. Periodic)
    // ─────────────────────────────────────────────────────────────────────────────
    private void showListSubmenu() {
        while (true) {
            view.showMessage("\n--- List Orders ---");
            view.showMessage("1. List All Regular Orders");
            view.showMessage("2. List All Periodic Orders");
            view.showMessage("Type 'back' to go back.");

            String choice = view.readLine("Choose an option: ").trim().toLowerCase();
            if (choice.equals("back")) {
                return; // go up one level
            }

            switch (choice) {
                case "1":
                    runCommand("ViewAllRegularOrdersCMD");
                    break;
                case "2":
                    runCommand("ViewAllPeriodicOrdersCMD");
                    break;
                default:
                    view.showError("Invalid option. Please choose 1–2 or 'back'.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Helper to invoke a command by key, if it exists in the map
    // ─────────────────────────────────────────────────────────────────────────────
    private void runCommand(String commandKey) {
        CommandInterface cmd = orderCommands.get(commandKey);
        if (cmd == null) {
            view.showError("Command not found for key: " + commandKey);
            return;
        }
        try {
            cmd.execute();
        } catch (Exception e) {
            view.showError("An error occurred while executing " + commandKey + ": " + e.getMessage());
        }
    }
}
