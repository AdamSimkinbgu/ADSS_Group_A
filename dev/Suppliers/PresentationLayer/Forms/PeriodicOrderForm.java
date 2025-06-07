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
        DayOfWeek deliveryDay = askDayOfWeek("Enter delivery day (MONDAY, TUESDAY, ...):");

        // 2) productsInOrder (Map<productId, quantity>)
        HashMap<Integer, Integer> productsInOrder = askForPeriodicOrderProducts();
        // 3) isActive (boolean)

        // ID will be assigned by DB → pass -1
        return new PeriodicOrderDTO(-1, deliveryDay, productsInOrder, false);
    }

    @Override
    protected PeriodicOrderDTO update(PeriodicOrderDTO dto) throws Cancelled {
        view.showMessage("Updating Periodic Order... (enter 'cancel' to cancel)");
        view.showMessage("Current details: \n" + dto);

        while (true) {
            String input = askNonEmpty("What would you like to update? (deliveryDay/productsInOrder/done)")
                    .toLowerCase();
            switch (input.toLowerCase()) {
                case "deliveryday":
                    dto.setDeliveryDay(askDayOfWeek("Enter new delivery day (MONDAY, TUESDAY, ...):"));
                case "productsinorder":
                    dto.setProductsInOrder(askForPeriodicOrderProducts());
                case "done":
                    view.showMessage("Periodic Order updated successfully: \n" + dto);
                    return dto; // Return the updated DTO
                default:
                    view.showError("Invalid option. Please choose 'deliveryDay', 'productsInOrder' or 'cancel'.");
                    continue;
            }

        }
    }
}
