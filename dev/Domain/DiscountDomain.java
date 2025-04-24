package Domain;

import java.util.Date;

public class DiscountDomain {
    private float percent;
    private Date discountEnd;
    private Date discountStart;



    public float getpercent() { return percent;}
    public Date getdiscountStart() { return discountStart;}
    public Date getdiscountEnd() { return discountEnd;}


    public DiscountDomain(float disPercent, Date sDate, Date eDate){
        percent = disPercent;
        discountStart = sDate;
        discountEnd = eDate;
    }


}
