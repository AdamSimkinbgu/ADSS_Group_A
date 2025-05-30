package DTO;

import Domain.DiscountDomain;

import java.time.LocalDate;

public class DiscountDTO {

    private float percent;
    private LocalDate discountEnd;
    private LocalDate discountStart;
    private int pId;
    private String catName;


    //Getters
    public float getPercent() {
        return percent;
    }
    public int getpId() {
        return pId;
    }
    public String getCatName() {
        return catName;
    }
    public LocalDate getDiscountEnd() {
        return discountEnd;
    }
    public LocalDate getDiscountStart() {
        return discountStart;
    }
    //Setters
    public void setCatName(String catName) {
        this.catName = catName;
    }
    public void setDiscountEnd(LocalDate discountEnd) {
        this.discountEnd = discountEnd;
    }
    public void setDiscountStart(LocalDate discountStart) {
        this.discountStart = discountStart;
    }
    public void setPercent(float percent) {
        this.percent = percent;
    }
    public void setpId(int pId) {
        this.pId = pId;
    }

    public DiscountDTO(){}

    public DiscountDTO(float percent,LocalDate end,int pId){
        this.percent = percent;
        this.discountStart = LocalDate.now();
        discountEnd = end;
        this.pId = pId;
        this.catName = "";
    }

    public DiscountDTO(float percent,LocalDate end,String cat) {
        this.percent = percent;
        this.discountStart = LocalDate.now();
        discountEnd = end;
        this.pId = -1;
        this.catName = cat;
    }

    public DiscountDTO(DiscountDomain d,int pId){
        this.pId = pId;
        this.catName = "";
        this.discountStart = d.getdiscountStart();
        this.discountEnd = d.getdiscountEnd();
        this.percent = d.getpercent();
    }

    public DiscountDTO(DiscountDomain d,String cat){
        this.pId = -1;
        this.catName = cat;
        this.discountStart = d.getdiscountStart();
        this.discountEnd = d.getdiscountEnd();
        this.percent = d.getpercent();
    }
}
