package com.mahmoudjoe3.eComStore.model;

import java.util.List;

public class Order {
    private List<SubOrderDB> orderList;
    private String id;

    public Order(List<SubOrderDB> orderList, String id) {
        this.orderList = orderList;
        this.id = id;
    }

    public List<SubOrderDB> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<SubOrderDB> orderList) {
        this.orderList = orderList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class SubOrderDB {
        private String product_Key;
        private int Qty;

        public SubOrderDB(String product_Key) {
            this.product_Key = product_Key;
            Qty = 0;
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

    public static class SubOrderUI {
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
}
