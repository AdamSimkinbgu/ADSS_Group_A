package DTOs.SuppliersModuleDTOs;

public class ProductOrderSuccessDTO {
    private int productId;
    private String productName;
    private int quantity;
    private int supplierId;
    private String supplierName;

    public ProductOrderSuccessDTO(int productId, String productName, int quantity,
            int supplierId, String supplierName) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
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

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Override
    public String toString() {
        return String.format(
                "✓ [%d] %s  x%d  @ Supplier[%d:%s]",
                productId,
                productName,
                quantity,
                supplierId,
                supplierName);
    }

}