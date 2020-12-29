package com.mahmoudjoe3.eComStore.model;

import java.io.Serializable;
import java.util.List;

public class OrderDB implements Serializable {
    private String id;
    private List<SubOrderDB> orderList;
    private String phone;
    private float totalPrice;
    private String location;
    private String deliveryDate;
    private boolean delivered;
    private boolean approved;

    public OrderDB(List<SubOrderDB> orderList, String phone, float totalPrice, String location
            , String deliveryDate,boolean delivered,boolean approved) {
        this.id = null;
        this.orderList = orderList;
        this.phone = phone;
        this.totalPrice = totalPrice;
        this.location = location;
        this.deliveryDate = deliveryDate;
        this.delivered =delivered;
        this.approved=approved;
    }

    public OrderDB() {
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SubOrderDB> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<SubOrderDB> orderList) {
        this.orderList = orderList;
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
}

