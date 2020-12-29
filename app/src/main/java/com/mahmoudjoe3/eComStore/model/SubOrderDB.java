package com.mahmoudjoe3.eComStore.model;

public class SubOrderDB {
    private String product_Key;
    private int Qty;

    public SubOrderDB(String product_Key,int Qty) {
        this.product_Key = product_Key;
        this.Qty = Qty;
    }

    public SubOrderDB() {
    }

    public String getProduct_Key() {
        return product_Key;
    }

    public void setProduct_Key(String product_Key) {
        this.product_Key = product_Key;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }
}
