package Suppliers.PresentationLayer.Commands.OrderCommands;


import Suppliers.PresentationLayer.Forms.OrderForm;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;

public class CreateOrderCMD {
    private final View view;
    private final OrderService orderService;
    // private final SupplierService supplierService;
    private final OrderForm form;

    public CreateOrderCMD(View view, OrderService orderService, OrderForm form) {
        this.view = view;
        this.orderService = orderService;
        this.form = form;
    }


}
