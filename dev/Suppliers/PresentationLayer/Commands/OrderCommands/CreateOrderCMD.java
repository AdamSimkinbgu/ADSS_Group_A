package Suppliers.PresentationLayer.Commands.OrderCommands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import Suppliers.DTOs.AddressDTO;
import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.Forms.OrderForm;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class CreateOrderCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;
    private final OrderForm form;

    public CreateOrderCMD(View view, OrderService orderService) {
        this.view = view;
        this.orderService = orderService;
        this.form = new OrderForm(view);
    }

    @Override
    public void execute() {
        // form.fillBuild().ifPresent(orderDTO -> {
        // try {
        // ServiceResponse<?> res = orderService.createOrder(orderDTO);
        // if (res.isSuccess()) {
        // view.showMessage("-- Order created successfully --\n" + orderDTO);
        // } else {
        // view.showError("-- Failed to create order --");
        // AtomicInteger counter = new AtomicInteger(1);
        // res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() +
        // ". " + error));
        // }
        // } catch (Exception e) {
        // view.showError("Error creating order: " + e.getMessage());
        // }
        // });
        try {
            ArrayList<OrderItemLineDTO> items = new ArrayList<>();
            items.add(new OrderItemLineDTO(1, 105));
            items.add(new OrderItemLineDTO(5, 200));
            OrderDTO orderDTO = new OrderDTO(
                    1,
                    LocalDate.now(),
                    LocalDate.now(),
                    new AddressDTO("123 Main St", "City", "156423"),
                    "054-570-6274",
                    items,
                    Suppliers.DTOs.Enums.OrderStatus.PENDING);
            ServiceResponse<?> res = orderService.createOrder(orderDTO);
            if (res.isSuccess()) {
                view.showMessage("-- Order created successfully --\n" + orderDTO);
            } else {
                view.showError("-- Failed to create order --");
                AtomicInteger counter = new AtomicInteger(1);
                res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
            }
        } catch (Exception e) {
            view.showError("Error creating order: " + e.getMessage());
        }
    }
}
