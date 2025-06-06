package Suppliers.PresentationLayer.Commands.OrderCommands;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.Forms.PeriodicOrderForm;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class CreatePeriodicOrderCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;
    private final PeriodicOrderForm form;

    public CreatePeriodicOrderCMD(View view, OrderService orderService) {
        this.view = view;
        this.orderService = orderService;
        this.form = new PeriodicOrderForm(view);
    }

    @Override
    public void execute() {
        // form.fillBuild().ifPresent(periodicOrderDTO -> {
        // try {
        // ServiceResponse<?> res = orderService.createPeriodicOrder(periodicOrderDTO);
        // if (res.isSuccess()) {
        // view.showMessage("-- Periodic order created successfully --\n" +
        // periodicOrderDTO);
        // } else {
        // view.showError("-- Failed to create periodic order --");
        // AtomicInteger counter = new AtomicInteger(1);
        // res.getErrors().forEach(error ->
        // view.showError(counter.getAndIncrement() + ". " + error)
        // );
        // }
        // } catch (Exception e) {
        // view.showError("Error creating periodic order: " + e.getMessage());
        // }
        // });
        HashMap<Integer, Integer> productsAndAmount = new HashMap<>();
        // Example products and amounts
        productsAndAmount.put(1, 2364);
        productsAndAmount.put(2, 3653);
        PeriodicOrderDTO periodicOrderDTO = new PeriodicOrderDTO(-1, DayOfWeek.MONDAY, productsAndAmount, true);
        try {
            ServiceResponse<?> res = orderService.createPeriodicOrder(periodicOrderDTO);
            if (res.isSuccess()) {
                view.showMessage("-- Periodic order created successfully --\n" + res.getValue());
            } else {
                view.showError("-- Failed to create periodic order --");
                AtomicInteger counter = new AtomicInteger(1);
                res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
            }
        } catch (Exception e) {
            view.showError("Error creating periodic order: " + e.getMessage());
        }
    }
}
