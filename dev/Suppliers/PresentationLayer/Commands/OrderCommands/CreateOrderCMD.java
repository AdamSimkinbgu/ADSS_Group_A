package Suppliers.PresentationLayer.Commands.OrderCommands;

import java.util.concurrent.atomic.AtomicInteger;

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
        form.fillBuild().ifPresent(orderDTO -> {
            try {
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
        });
    }
}
