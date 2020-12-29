package com.mahmoudjoe3.eComStore.model;

import java.io.Serializable;
import java.util.List;

public class OrderUI implements Serializable {
    private List<SubOrderUI> orderList;
    private String id;
    private String phone;
    private float totalPrice;
    private String location;
    private String deliveryDate;
    private boolean delivered;
    private boolean approved;

    public OrderUI(List<SubOrderUI> orderList) {
        this.orderList = orderList;
        this.id =null;
    }

    public OrderUI(List<SubOrderUI> orderList, String id, String phone, float totalPrice
            , String location, String deliveryDate, boolean delivered, boolean approved) {
        this.orderList = orderList;
        this.id = id;
        this.phone = phone;
        this.totalPrice = totalPrice;
        this.location = location;
        this.deliveryDate = deliveryDate;
        this.delivered = delivered;
        this.approved = approved;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public List<SubOrderUI> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<SubOrderUI> orderList) {
        this.orderList = orderList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
