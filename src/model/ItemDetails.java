package model;

public class ItemDetails {
    private String itemCode;
    private int qty;
    private double discount;

    public ItemDetails() {
    }

    public ItemDetails(String itemCode, int qty, double discount) {
        this.itemCode = itemCode;
        this.qty = qty;
        this.discount = discount;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "ItemDetails{" +
                "itemCode='" + itemCode + '\'' +
                ", qty=" + qty +
                ", discount=" + discount +
                '}';
    }
}
