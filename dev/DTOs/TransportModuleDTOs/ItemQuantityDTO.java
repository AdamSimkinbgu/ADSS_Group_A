package DTOs.TransportModuleDTOs;

public class ItemQuantityDTO {
    private int itemInItemsDocId;
    private ItemDTO item;
    private int quantity;

    public ItemQuantityDTO(){}
    public ItemQuantityDTO(int itemInItemsDocId, ItemDTO item, int quantity) {
        this.itemInItemsDocId = itemInItemsDocId;
        this.item = item;
        this.quantity = quantity;
    }

    public int getItemInItemsDocId() {return itemInItemsDocId;}
    public void setItemInItemsDocId(int itemInItemsDocId) {this.itemInItemsDocId = itemInItemsDocId;}
    public ItemDTO getItem() {return item;}
    public void setItem(ItemDTO item) {this.item = item;}
    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}

    @Override
    public String toString() {
        return "ItemQuantityDTO [item=" + item.toString() + ", quantity=" + quantity + "]";
    }
}

