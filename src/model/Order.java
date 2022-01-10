package model;

import java.util.ArrayList;

public class Order {
    private String orderId;
    private String custId;
    private String orderDate;
    private double netTotal;
    private ArrayList<ItemDetails>items;

    public Order() {
    }

    public Order(String orderId, String custId, String orderDate, double netTotal, ArrayList<ItemDetails> items) {
        this.orderId = orderId;
        this.custId = custId;
        this.orderDate = orderDate;
        this.netTotal = netTotal;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    public ArrayList<ItemDetails> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemDetails> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", custId='" + custId + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", netTotal=" + netTotal +
                ", items=" + items +
                '}';
    }
}
