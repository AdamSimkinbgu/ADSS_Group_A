package Suppliers.PresentationLayer.Forms;

import java.time.DayOfWeek;
import java.util.HashMap;

import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.PresentationLayer.InteractiveForm;
import Suppliers.PresentationLayer.View;

public class PeriodicOrderForm extends InteractiveForm<PeriodicOrderDTO> {

    public PeriodicOrderForm(View view) {
        super(view);
    }

    @Override
    protected PeriodicOrderDTO build() throws Cancelled {
        view.showMessage("Creating a new Periodic Order... (enter 'cancel' to cancel)");
        view.showMessage("Please fill in the following details:");

        // 1) deliveryDay (DayOfWeek)
        DayOfWeek deliveryDay;
        while (true) {
            String dayStr = askNonEmpty("Enter delivery day (MONDAY, TUESDAY, ...):")
                    .trim().toUpperCase();
            try {
                deliveryDay = DayOfWeek.valueOf(dayStr);
                break;
            } catch (IllegalArgumentException e) {
                view.showError("Invalid day. Use MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY.");
            }
        }

        // 2) productsInOrder (Map<productId, quantity>)
        HashMap<Integer, Integer> productsInOrder = new HashMap<>();
        while (true) {
            String more = askNonEmpty("Add a product to this periodic order? (y/n):")
                    .trim().toLowerCase();
            if (more.startsWith("n")) {
                break;
            }
            int productId = askInt("Enter product ID:");
            int quantity  = askInt("Enter quantity for that product:");
            productsInOrder.put(productId, quantity);
        }

        // 3) isActive (boolean)
        boolean isActive;
        while (true) {
            String activeStr = askNonEmpty("Is this periodic order active? (y/n):")
                    .trim().toLowerCase();
            if (activeStr.startsWith("y")) {
                isActive = true;
                break;
            } else if (activeStr.startsWith("n")) {
                isActive = false;
                break;
            } else {
                view.showError("Please enter 'y' or 'n'.");
            }
        }

        // ID will be assigned by DB → pass -1
        return new PeriodicOrderDTO(-1, deliveryDay, productsInOrder, isActive);
    }

    @Override
    protected PeriodicOrderDTO update(PeriodicOrderDTO dto) throws Cancelled {
        view.showMessage("Updating Periodic Order... (enter 'cancel' to cancel)");
        view.showMessage("Current details: " + dto);

        String fieldToUpdate = askNonEmpty(
                "What do you want to change? (deliveryDay, productsInOrder, isActive):"
        ).trim().toLowerCase();

        switch (fieldToUpdate) {
            case "deliveryday":
                DayOfWeek newDay;
                while (true) {
                    String dayStr = askNonEmpty("Enter new delivery day (MONDAY, TUESDAY, ...):")
                            .trim().toUpperCase();
                    try {
                        newDay = DayOfWeek.valueOf(dayStr);
                        break;
                    } catch (IllegalArgumentException e) {
                        view.showError("Invalid day. Use MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY.");
                    }
                }
                dto.setDeliveryDay(newDay);
                break;

            case "productsinorder":
                view.showMessage("Current productsInOrder: " + dto.getProductsInOrder());
                while (true) {
                    String action = askNonEmpty("Action (add/edit/remove/done):")
                            .trim().toLowerCase();
                    if (action.equals("done")) {
                        break;
                    }
                    switch (action) {
                        case "add":
                        case "edit":
                            int prodId = askInt("Enter product ID to add/edit:");
                            int qty    = askInt("Enter new quantity:");
                            dto.getProductsInOrder().put(prodId, qty);
                            break;
                        case "remove":
                            int prodToRemove = askInt("Enter product ID to remove:");
                            dto.getProductsInOrder().remove(prodToRemove);
                            break;
                        default:
                            view.showError("Invalid action. Use add, edit, remove, or done.");
                    }
                    view.showMessage("Updated map: " + dto.getProductsInOrder());
                }
                break;

            case "isactive":
                boolean newActive;
                while (true) {
                    String activeStr = askNonEmpty("Is this periodic order active? (y/n):")
                            .trim().toLowerCase();
                    if (activeStr.startsWith("y")) {
                        newActive = true;
                        break;
                    } else if (activeStr.startsWith("n")) {
                        newActive = false;
                        break;
                    } else {
                        view.showError("Please enter 'y' or 'n'.");
                    }
                }
                dto.setActive(newActive);
                break;

            default:
                view.showError("Invalid field. Choose one of: deliveryDay, productsInOrder, isActive.");
                break;
        }
        return dto;
    }
}
