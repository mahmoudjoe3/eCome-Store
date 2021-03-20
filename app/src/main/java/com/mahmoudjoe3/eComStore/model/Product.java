package com.mahmoudjoe3.eComStore.model;


import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    private String mId;
    private ArrayList<String> mImageUri;
    private String mTitle;
    private String mCategory;
    private float mPrice;


    private String mTime;
    private String mDate;
    private int mQuantity;
    private Admin mAdmin;
    private String mDescription;

    public Product() {
    }

    public Product(Product p) {
        this.mImageUri = p.mImageUri;
        this.mTitle = p.mTitle;
        this.mCategory = p.mCategory;
        this.mDescription = p.mDescription;
        this.mPrice = p.mPrice;
        this.mAdmin = p.mAdmin;
        this.mId = p.mId;
        this.mTime = p.mTime;
        this.mDate = p.mDate;
        this.mQuantity = p.mQuantity;
    }

    public Product(Admin mAdmin, String mTitle, String mCategory, String mDescription, float mPrice, int quantity) {
        this.mTitle = mTitle;
        this.mCategory = mCategory;
        this.mDescription = mDescription;
        this.mPrice = mPrice;
        this.mAdmin = mAdmin;
        this.mQuantity = quantity;
        this.mImageUri = new ArrayList<>();
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmDate() {
        return mDate;
    }


    public Admin getmAdmin() {
        return mAdmin;
    }

    public void setmAdmin(Admin mAdmin) {
        this.mAdmin = mAdmin;
    }

    public ArrayList<String> getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(ArrayList<String> mImageUri) {
        this.mImageUri = mImageUri;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public float getmPrice() {
        return mPrice;
    }

    public void setmPrice(float mPrice) {
        this.mPrice = mPrice;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }
}
