package com.mahmoudjoe3.eComStore.model;

import java.io.Serializable;

public class SubOrderUI implements Serializable {
    private Product product;
    private int Qty;

    public SubOrderUI(Product product, int qty) {
        this.product = product;
        Qty = qty;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }
}
