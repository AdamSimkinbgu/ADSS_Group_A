package Suppliers.DTOs;

public class ProductOrderFailureDTO {
    private int productId;
    private String productName;
    private int quantity;

    public ProductOrderFailureDTO(int productId, String productName, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format(
                "✗ [%d] %s  x%d",
                productId,
                productName,
                quantity);
    }
}