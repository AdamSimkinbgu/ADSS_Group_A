package DTOs.TransportModuleDTOs;

import java.util.Objects;

public class ItemDTO {
    private String name;
    private double weight;   // In whole numbers, in grams.
    private Boolean condition;

    public ItemDTO() {}
    public ItemDTO(String name, double weight, Boolean condition){
        this.name = name;
        this.weight = weight;
        this.condition = condition;
    }
    public ItemDTO(String name, double weight){
        this.name = name;
        this.weight = weight;
        this.condition = true;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public double getWeight() {return weight;}
    public void setWeight(double weight) {this.weight = weight;}
    public Boolean getCondition() {return condition;}
    public void setCondition(Boolean condition) {this.condition = condition;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemDTO item = (ItemDTO) obj;
        return Double.compare(item.weight, weight) == 0 &&
                condition == item.condition &&
                name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weight, condition);
    }

    @Override
    public String toString() {
        String res = "Item Name: " + name + ", Item Weight: " + weight + ", Item Condition: " + condition;
        return res;
    }
}
