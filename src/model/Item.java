package model;

import java.util.Objects;

public class Item {
    private String itemCode;
    private String description;
    private String packSize;
    private int qtyOnHand;
    private double uniitPrice;

    public Item() {
    }

    public Item(String itemCode, String description, String packSize, int qtyOnHand, double uniitPrice) {
        this.itemCode = itemCode;
        this.description = description;
        this.packSize = packSize;
        this.qtyOnHand = qtyOnHand;
        this.uniitPrice = uniitPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public double getUniitPrice() {
        return uniitPrice;
    }

    public void setUniitPrice(double uniitPrice) {
        this.uniitPrice = uniitPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemCode='" + itemCode + '\'' +
                ", description='" + description + '\'' +
                ", packSize='" + packSize + '\'' +
                ", qtyOnHand=" + qtyOnHand +
                ", uniitPrice=" + uniitPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(itemCode, item.itemCode) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemCode);
    }
}
