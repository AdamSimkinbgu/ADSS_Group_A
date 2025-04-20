package Domain;

import java.util.Date;

public class DiscountDomain {
    private int percent;
    private Date discountEnd;
    private Date discountStart;


    public int getpercent() { return percent;}
    public Date getdiscountStart() { return discountStart;}
    public Date getdiscountEnd() { return discountEnd;}

    public DiscountDomain(int disPercent, Date sDate, Date eDate){
        percent = disPercent;
        discountStart = sDate;
        discountEnd = eDate;
    }

}
