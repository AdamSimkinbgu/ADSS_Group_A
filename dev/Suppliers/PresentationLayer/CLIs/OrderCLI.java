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
        orderCommands.put("ViewAllPeriodicOrdersForTodayCMD", commands.get("ViewAllPeriodicOrdersForTodayCMD"));

        // Additional command for delivering orders to inventory
        orderCommands.put("DeliverOrderToInventoryCMD", commands.get("DeliverOrderToInventoryCMD"));
        orderCommands.put("AdvanceOrderStatusCMD", commands.get("AdvanceOrderStatusCMD"));
        // Command to execute periodic orders for the current week
        orderCommands.put("ExecutePeriodicOrdersForThisWeekCMD", commands.get("ExecutePeriodicOrdersForThisWeekCMD"));
    }

    public void start() {
        while (true) {
            view.showMessage("\n === Order Management Menu ===");
            view.showMessage("1. Create Order");
            view.showMessage("2. Update Order");
            view.showMessage("3. Remove Order");
            view.showMessage("4. List Orders");
            view.showMessage("5. Create Periodic Order");
            view.showMessage("6. Update Periodic Order");
            view.showMessage("7. Remove Periodic Order");
            view.showMessage("8. List Periodic Orders");
            view.showMessage("9. View All Periodic Orders for Today");
            view.showMessage("10. Deliver Order to Inventory");
            view.showMessage("11. Advance Order Status");
            view.showMessage("12. Execute Periodic Orders for This Week");
            view.showMessage("Type 'back' or '0' to go back.");

            String choice = view.readLine("Choose an option: ").toLowerCase();
            if (choice.equals("back") || choice.equals("0")) {
                view.showMessage(" === Returning to the Suppliers main menu ===");
                break;
            }

            switch (choice) {
                case "1":
                    runCommand("CreateOrderCMD");
                    break;
                case "2":
                    runCommand("UpdateOrderCMD");
                    break;
                case "3":
                    runCommand("RemoveOrderCMD");
                    break;
                case "4":
                    runCommand("ViewAllOrdersCMD");
                    break;
                case "5":
                    runCommand("CreatePeriodicOrderCMD");
                    break;
                case "6":
                    runCommand("UpdatePeriodicOrderCMD");
                    break;
                case "7":
                    runCommand("RemovePeriodicOrderCMD");
                    break;
                case "8":
                    runCommand("ViewAllPeriodicOrdersCMD");
                    break;
                case "9":
                    runCommand("ViewAllPeriodicOrdersForTodayCMD");
                    break;
                case "10":
                    runCommand("DeliverOrderToInventoryCMD");
                    break;
                case "11":
                    runCommand("AdvanceOrderStatusCMD");
                    break;
                case "12":
                    runCommand("ExecutePeriodicOrdersForThisWeekCMD");
                    break;
                default:
                    view.showError("Invalid option. Please try again.");
                    break;
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
