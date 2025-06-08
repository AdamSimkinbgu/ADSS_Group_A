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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OrderResult:\n");
        sb.append("  Successful Products (")
                .append(successfulProducts.size())
                .append("):\n");
        for (ProductOrderSuccessDTO p : successfulProducts) {
            sb.append("    • ").append(p).append("\n");
        }
        sb.append("  Failed Products (")
                .append(failedProducts.size())
                .append("):\n");
        for (ProductOrderFailureDTO p : failedProducts) {
            sb.append("    • ").append(p).append("\n");
        }
        return sb.toString();
    }
}