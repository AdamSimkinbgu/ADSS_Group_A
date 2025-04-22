package Domain;

import java.util.Date;

public class DiscountDomain {
    private float percent;
    private Date discountEnd;
    private Date discountStart;
    private Integer pId;
    private String saleName;


    public float getpercent() { return percent;}
    public Date getdiscountStart() { return discountStart;}
    public Date getdiscountEnd() { return discountEnd;}
    public Integer getpId() {
        return pId;
    }
    public String getSaleName() {
        return saleName;
    }

    public DiscountDomain(float disPercent, Date sDate, Date eDate){
        percent = disPercent;
        discountStart = sDate;
        discountEnd = eDate;
        pId = 0;
        saleName = "";
    }


}
