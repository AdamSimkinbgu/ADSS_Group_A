package DTO;

import Domain.SaleDomain;

import java.util.HashMap;

public class SaleDTO {

    float salePrice;
    HashMap<Integer,Integer> products; //// hashmap(ID,saleQuantity) ////


    public float getSalePrice() {
        return salePrice;
    }
    public HashMap<Integer, Integer> getProducts() {
        return products;
    }

    public SaleDTO(){}

    public SaleDTO(HashMap<Integer,Integer> prod){

        salePrice = 0;
        products = new HashMap<>(prod);
    }

    public SaleDTO(SaleDomain other){
        salePrice = other.getPrice();
        products = new HashMap<>();

        for(Integer i : other.getItemLs().keySet())products.put(i,other.getItemLs().get(i));
    }
}
