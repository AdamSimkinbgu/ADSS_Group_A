package Service;

import java.util.Date;
import java.util.List;

public class DiscountService {
    int items; /// list of items for the discount ///
    int percent;
    Date startDate;
    Date endDate;


    public int getitems() { return items;}
    public int getpercent() { return percent;}
    public Date getstartDate() { return startDate;}
    public Date getendDate() { return endDate;}

    public DiscountService(int disItems, int disPercent, Date sDate, Date eDate){
        items = disItems;
        percent = disPercent;
        startDate = sDate;
        endDate = eDate;
    }

    
}
