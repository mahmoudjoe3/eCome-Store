package com.mahmoudjoe3.eComStore.model;

import android.net.Uri;

public class Product {
    private Uri[] mImageUri;
    private String mTitle;
    private String mCategory;
    private String mDescription;
    private float mPrice;
    private User mAdmin;

    public Product(User mAdmin, Uri[] mImageUri, String mTitle, String mCategory, String mDescription, float mPrice) {
        this.mImageUri = mImageUri;
        this.mTitle = mTitle;
        this.mCategory = mCategory;
        this.mDescription = mDescription;
        this.mPrice = mPrice;
        this.mAdmin=mAdmin;
    }

    public User getmAdmin() {
        return mAdmin;
    }

    public void setmAdmin(User mAdmin) {
        this.mAdmin = mAdmin;
    }

    public Uri[] getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(Uri[] mImageUri) {
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
}
