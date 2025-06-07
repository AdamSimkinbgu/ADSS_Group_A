package Suppliers.DTOs;

import Suppliers.DTOs.ProductOrderFailureDTO;
import Suppliers.DTOs.ProductOrderSuccessDTO;

import java.util.List;


public class OrderResultDTO {

    private List<ProductOrderSuccessDTO> successfulProducts;
    private List<ProductOrderFailureDTO> failedProducts;

    public OrderResultDTO(List<ProductOrderSuccessDTO> successfulProducts,
                          List<ProductOrderFailureDTO> failedProducts) {
        this.successfulProducts = successfulProducts;
        this.failedProducts = failedProducts;
    }

    public List<ProductOrderSuccessDTO> getSuccessfulProducts() {
        return successfulProducts;
    }

    public void setSuccessfulProducts(List<ProductOrderSuccessDTO> successfulProducts) {
        this.successfulProducts = successfulProducts;
    }

    public List<ProductOrderFailureDTO> getFailedProducts() {
        return failedProducts;
    }

    public void setFailedProducts(List<ProductOrderFailureDTO> failedProducts) {
        this.failedProducts = failedProducts;
    }
}