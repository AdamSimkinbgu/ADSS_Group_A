package DTOs;

import DTOs.Enums.OrderStatus;
import DomainLayer.Classes.Address;
import DomainLayer.Classes.OrderItemLine;

import java.time.LocalDate;
import java.util.List;

public record OrderDTO(
        int supplierId,
        String supplierName,
        LocalDate orderDate,
        Address address ,
        String contactPhoneNumber
        //,List<OrderItemLineDTO>items
       )
    {}
