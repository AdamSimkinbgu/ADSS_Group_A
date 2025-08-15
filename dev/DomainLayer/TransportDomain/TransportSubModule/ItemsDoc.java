package DomainLayer.TransportDomain.TransportSubModule;

import DomainLayer.TransportDomain.SiteSubModule.Site;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ItemsDoc {
    private int ItemsDocInTransportID;
    private int itemsDoc_num;
    private Site src_site;
    private Site dest_site;
    private LocalDateTime estimatedArrivalTime;
    private HashMap<Item, Integer> goodItems;   //  separate the good items and the bad items
    private HashMap<Item, Integer> badItems;

    public ItemsDoc(int itemDoc_num, Site src_site, Site dest_site, int itemsDocInTransportID) {
        this.itemsDoc_num = itemDoc_num;
        this.ItemsDocInTransportID = itemsDocInTransportID;
        this.src_site = src_site;
        this.dest_site = dest_site;
        this.estimatedArrivalTime = null;   ///   Is set a bit later, when a TransportDoc calculates it with it's calculation function.
        this.goodItems = new HashMap<Item, Integer>();
        this.badItems = new HashMap<Item, Integer>();
    }

    public int getItemsDocInTransportID() {return ItemsDocInTransportID;}
    public void setItemsDocInTransportID(int itemsDocInTransportID) {ItemsDocInTransportID = itemsDocInTransportID;}
    public int getItemDoc_num() {return itemsDoc_num;}
    public void setItemDoc_num(int itemDoc_num) {this.itemsDoc_num = itemDoc_num;}
    public Site getSrc_site() {return src_site;}
    public void setSrc_site(Site src_site) {this.src_site = src_site;}
    public Site getDest_site() {return dest_site;}
    public void setDest_site(Site dest_site) {this.dest_site = dest_site;}
    public LocalDateTime getEstimatedArrivalTime() {return estimatedArrivalTime;}
    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) {this.estimatedArrivalTime = estimatedArrivalTime;}
    public HashMap<Item, Integer> getGoodItems() {return goodItems;}
    public void setGoodItems(HashMap<Item, Integer> goodItems) {this.goodItems = goodItems;}
    public HashMap<Item, Integer> getBadItems() {return badItems;}
    public void setBadItems(HashMap<Item, Integer> badItems) {this.badItems = badItems;}

    public int addItem(String itemName, double itemWeight, boolean cond, int amount) {
        HashMap<Item, Integer> items = cond ? goodItems : badItems;
        for (Item item : items.keySet()) {
            if (item.getName().equals(itemName) && item.getWeight() == itemWeight) {
                items.put(item, amount + items.get(item));  // if item already exists, add amounts
                return 0;  // all good
            }
        }
        items.put(new Item(itemName, itemWeight, cond, this.itemsDoc_num), amount);
        return 0;  // all good
    }

    public int removeItem(String itemName, double itemWeight, boolean cond, int amount) {
        HashMap<Item, Integer> items = cond ? goodItems : badItems;
        for (Item item : items.keySet()) {
            if (item.getName().equals(itemName) && item.getWeight() == itemWeight) {
                if(amount >= items.get(item)){
                    int amount_removed = items.get(item);
                    items.remove(item);
                    return amount_removed; // return how many items removed
                }
                items.put(item, items.get(item) - amount);
                return amount; // return how many items removed
            }
        }
        return -1;  // item to remove not found
    }

    public int setItemCond(String itemName, double itemWeight, int amount, boolean newCond){
        HashMap<Item, Integer> itemsFrom = newCond ? badItems : goodItems;
        for (Item item : itemsFrom.keySet()) {
            if (item.getName().equals(itemName) && item.getWeight() == itemWeight) {
                int amount_removed = removeItem(itemName, itemWeight, !newCond, amount);   //  maybe save and remove out of the loop
                addItem(itemName, itemWeight, newCond, amount_removed);  //  only set the condition to the existing amount of items of that kind if requested to change more amount of items
                return 0;
            }
        }
        return -1;  //  item to change cond to was not found
    }

    public double calculateItemsWeight(){
        double sum = 0;
        for (Item item : goodItems.keySet()) {
            sum += item.getWeight()*goodItems.get(item);
        }
        for (Item item : badItems.keySet()) {
            sum += item.getWeight()*badItems.get(item);
        }
        return sum;
    }

    @Override
    public String toString() {
        String res = "-- Items Document #" + itemsDoc_num + " Details: from Source Site: " + src_site + " to Destination Site: " + dest_site + ".\nEstimated Arrival Time: " + this.estimatedArrivalTime.toString();
        res += ", Items Included:\n";
        res += "- Good Items:\n";
        for (Item item : goodItems.keySet()) {
            res += item.toString() + ", Amount: " + goodItems.get(item) + ".\n";
        }
        res += "- Bad Items:\n";
        for (Item item : badItems.keySet()) {
            res += item.toString() + ", Amount: " + badItems.get(item) + ".\n";
        }
        return res;
    }
}
