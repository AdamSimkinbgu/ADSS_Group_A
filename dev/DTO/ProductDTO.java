package DTO;

import Domain.ProductDomain;

public class ProductDTO {

    int productId;
    String productName;
    String manufacturerName;
    float productPrice;


    public int getproductId() { return productId;}
    public String getproductName() { return productName;}
    public String getmanufacturerName() { return manufacturerName;}
    public float getproductPrice() { return productPrice;}

    public ProductDTO(){}

    public ProductDTO(String pName,String MfName, float PPrice){
        productId = 0;
        productName = pName;
        manufacturerName = MfName;
        productPrice = PPrice;
        }
    public ProductDTO(ProductDomain other){
        productId = other.getproductID();
        productName = other.getproductName();
        productPrice = other.getproductPrice();
        manufacturerName = other.getmanufactuerName();
        }

}
