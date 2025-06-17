package Inventory.DTO;

import Inventory.Domain.SupplyDomain;
import Suppliers.DTOs.OrderItemLineDTO;

import java.time.LocalDate;
import java.util.List;

public class SupplyDTO {
    int sId;
    int productID;
    LocalDate expireDate;
    int quantityWH;
    int quantityS;
    int quantityB;

    // getters

    public int getProductID() {
        return productID;
    }

    public int getQuantityWH() {
        return quantityWH;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public int getsId() {
        return sId;
    }

    public int getquantityS() {
        return quantityS;
    }

    public int getquantityB() {
        return quantityB;
    }

    // setters

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setQuantityWH(int quantityWH) {
        this.quantityWH = quantityWH;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public void setSId(int sId) {
        this.sId = sId;
    }

    public void setQuantityS(int quantityS) {
        this.quantityS = quantityS;
    }

    public void setQuantityB(int quantityB) {
        this.quantityB = quantityB;
    }

    public SupplyDTO() {
    }

    public SupplyDTO(int pid, int quantity, LocalDate ex) {
        productID = pid;
        quantityWH = quantity;
        expireDate = ex;
        quantityS = 0;
        sId = -1;
        quantityB = 0;
    }

    public SupplyDTO(SupplyDomain other, int otherId) {
        sId = other.getId();
        productID = otherId;
        quantityB = other.getQuantityBad();
        quantityS = other.getQuantityStore();
        quantityWH = other.getQuantityWarehouse();
        expireDate = other.getExpierDate();
    }

    public SupplyDTO(OrderItemLineDTO item) {
        this.sId = item.getOrderID();
        this.productID = item.getProductId();
        this.quantityWH = item.getQuantity();
        this.quantityS = 0;
        this.quantityB = 0;
        this.expireDate = null;
    }

}
