package Suppliers.PresentationLayer.CLIs;

import java.util.HashMap;
import java.util.Map;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;

public class OrderCLI {
    private final View view;
    private final Map<String, CommandInterface> orderCommands;

    public OrderCLI(View view, Map<String, CommandInterface> commands) {
        this.view = view;
        this.orderCommands = new HashMap<>();

        // Generic order commands (formerly “RegularOrderCMD”)
        orderCommands.put("CreateOrderCMD", commands.get("CreateOrderCMD"));
        orderCommands.put("UpdateOrderCMD", commands.get("UpdateOrderCMD"));
        orderCommands.put("RemoveOrderCMD", commands.get("RemoveOrderCMD"));
        orderCommands.put("ViewAllOrdersCMD", commands.get("ViewAllOrdersCMD"));

        // Periodic order commands remain unchanged
        orderCommands.put("CreatePeriodicOrderCMD", commands.get("CreatePeriodicOrderCMD"));
        orderCommands.put("UpdatePeriodicOrderCMD", commands.get("UpdatePeriodicOrderCMD"));
        orderCommands.put("RemovePeriodicOrderCMD", commands.get("RemovePeriodicOrderCMD"));
        orderCommands.put("ViewAllPeriodicOrdersCMD", commands.get("ViewAllPeriodicOrdersCMD"));
    }

    public void start() {
        while (true) {
            view.showMessage("\n=== Order Management Menu ===");
            view.showMessage("1. Create Order");
            view.showMessage("2. Update Order");
            view.showMessage("3. Remove Order");
            view.showMessage("4. List Orders");
            view.showMessage("Type 'back' or '0' to go back.");

            String choice = view.readLine("Choose an option: ").toLowerCase();
            if (choice.equals("back") || choice.equals("0")) {
                view.showMessage(" === Returning to the Suppliers main menu ===");
                break;
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
                    view.showError("Invalid option. Choose 1–4 or 'return'.");
            }
        }
    }

    private void showCreateSubmenu() {
        while (true) {
            view.showMessage("\n--- Create Order ---");
            view.showMessage("1. Create Order");
            view.showMessage("2. Create Periodic Order");
            view.showMessage("Type 'back' or '0' to go back.");

            String choice = view.readLine("Choose an option: ").toLowerCase();
            if (choice.equals("back") || choice.equals("0")) {
                view.showMessage(" === Returning to the Orders menu ===");
                return;
            }
            switch (choice) {
                case "1":
                    runCommand("CreateOrderCMD");
                    break;
                case "2":
                    runCommand("CreatePeriodicOrderCMD");
                    break;
                default:
                    view.showError("Invalid option. Choose 1–2 or 'back'.");
            }
        }
    }

    private void showUpdateSubmenu() {
        while (true) {
            view.showMessage("\n--- Update Order ---");
            view.showMessage("1. Update Order");
            view.showMessage("2. Update Periodic Order");
            view.showMessage("Type 'back' or '0' to go back.");

            String choice = view.readLine("Choose an option: ").toLowerCase();
            if (choice.equals("back") || choice.equals("0")) {
                view.showMessage(" === Returning to the Orders menu ===");
                break;
            }
            switch (choice) {
                case "1":
                    runCommand("UpdateOrderCMD");
                    break;
                case "2":
                    runCommand("UpdatePeriodicOrderCMD");
                    break;
                default:
                    view.showError("Invalid option. Choose 1–2 or 'back'.");
            }
        }
    }

    private void showRemoveSubmenu() {
        while (true) {
            view.showMessage("\n--- Remove Order ---");
            view.showMessage("1. Remove Order");
            view.showMessage("2. Remove Periodic Order");
            view.showMessage("Type 'back' or '0' to go back.");

            String choice = view.readLine("Choose an option: ").toLowerCase();
            if (choice.equals("back") || choice.equals("0")) {
                view.showMessage(" === Returning to the Orders menu ===");
                break;
            }
            switch (choice) {
                case "1":
                    runCommand("RemoveOrderCMD");
                    break;
                case "2":
                    runCommand("RemovePeriodicOrderCMD");
                    break;
                default:
                    view.showError("Invalid option. Choose 1–2 or 'back'.");
            }
        }
    }

    private void showListSubmenu() {
        while (true) {
            view.showMessage("\n--- List Orders ---");
            view.showMessage("1. View All Orders");
            view.showMessage("2. View All Periodic Orders");
            view.showMessage("Type 'back' or '0' to go back.");

            String choice = view.readLine("Choose an option: ").toLowerCase();
            if (choice.equals("back") || choice.equals("0")) {
                view.showMessage(" === Returning to the Orders menu ===");
                break;
            }
            switch (choice) {
                case "1":
                    runCommand("ViewAllOrdersCMD");
                    break;
                case "2":
                    runCommand("ViewAllPeriodicOrdersCMD");
                    break;
                default:
                    view.showError("Invalid option. Choose 1–2 or 'back'.");
            }
        }
    }

    private void runCommand(String commandKey) {
        CommandInterface cmd = orderCommands.get(commandKey);
        if (cmd == null) {
            view.showError("No command found for key: " + commandKey);
            return;
        }
        try {
            cmd.execute();
        } catch (Exception e) {
            view.showError("Error executing " + commandKey + ": " + e.getMessage());
        }
    }
}
