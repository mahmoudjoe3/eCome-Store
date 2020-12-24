package com.mahmoudjoe3.eComStore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AuthorizedUser implements Serializable {
    private List<String> cartList,favList;
    private String name;
    private String phone;
    private String password;

    public AuthorizedUser( String name, String phone, String password) {
        this.cartList = new ArrayList<>();
        this.favList = new ArrayList<>();
        this.name = name;
        this.phone = phone;
        this.password = password;
    }


    public AuthorizedUser() {
    }

    public List<String> getCartList() {
        if(this.cartList==null)this.cartList=new ArrayList<>();
        return cartList;
    }

    public void setCartList(List<String> cartList) {
        if(this.cartList==null)this.cartList=new ArrayList<>();
        this.cartList=new ArrayList<>(cartList);
    }

    public List<String> getFavList() {
        if(this.favList==null)this.favList=new ArrayList<>();
        return favList;
    }

    public void setFavList(List<String> favList) {
        if(this.favList==null)this.favList=new ArrayList<>();
        this.favList=new ArrayList<>(favList);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
