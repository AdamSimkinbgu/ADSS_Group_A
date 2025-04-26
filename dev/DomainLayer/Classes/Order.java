package DomainLayer.Classes;

import DomainLayer.Enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Order {

    private UUID orderId;
    private String supplierAddress;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;


}
