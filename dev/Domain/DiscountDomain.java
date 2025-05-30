package Domain;

import DTO.DiscountDTO;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Date;

public class DiscountDomain {
    private float percent;
    private LocalDate discountEnd;
    private LocalDate discountStart;



    public float getpercent() { return percent;}
    public LocalDate getdiscountStart() { return discountStart;}
    public LocalDate getdiscountEnd() { return discountEnd;}


    public DiscountDomain(float disPercent, LocalDate eDate){
        percent = disPercent;
        discountStart = LocalDate.now();
        discountEnd = eDate;
    }

    public DiscountDomain(DiscountDTO d){
        percent = d.getPercent();
        discountStart = d.getDiscountStart();
        discountEnd = d.getDiscountEnd();
    }

    public DiscountDomain(DiscountDTO other){
        percent = other.getPercent();
        discountStart = other.getDiscountStart();
        discountEnd = other.getDiscountEnd();
    }

}
