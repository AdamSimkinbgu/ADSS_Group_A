package Suppliers.DTOs.Enums;

public enum OrderStatus {
   PENDING, // Order has been created because of inventory request
   SENT, // Order has been sent (issued) to supplier
   ON_DELIVERY, // Order is currently being delivered (a day before DELIVERY)
   DELIVERED, // Order has been delivered to the warehouse
   COMPLETED, // Order has been completed (all items received by the warehouse staff)
   CANCELLED // Order has been cancelled
}