package com.mahmoudjoe3.eComStore.ui.userUI.wishlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

import java.util.List;

public class WishlistViewModel extends ViewModel {

    private MutableLiveData<List<Product>> productsLiveData;
    private MutableLiveData<AuthorizedUser> userLiveData;
    private FirebaseRepo repo;

    public WishlistViewModel() {
        repo = FirebaseRepo.getInstance();
        userLiveData = new MutableLiveData<>();
        productsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Product>> getProductsByIds(List<String> productIds) {
        repo.getProductListByIds(productIds);
        repo.setOnGetProductListener(new FirebaseRepo.OnGetProductListener() {
            @Override
            public void onSuccess(List<Product> products) {
                productsLiveData.setValue(products);
            }
        });
        return productsLiveData;
    }

    public LiveData<AuthorizedUser> getUserLiveData(String userId) {
        repo.getUserById(userId);
        repo.setOnFindUserListener(new FirebaseRepo.onFindUserListener() {
            @Override
            public void onSuccess(AuthorizedUser user) {
                userLiveData.setValue(user);
            }
        });
        return userLiveData;
    }

    public void addFav(String fav, AuthorizedUser user) {
        repo.addFav(fav, user);
    }

    public void RemoveFav(String fav, AuthorizedUser user) {
        repo.removeFav(fav, user);
    }

    public void addCart(String cart, AuthorizedUser user) {
        repo.addCart(cart, user);
    }

    public void removeCart(String cart, AuthorizedUser user) {
        repo.removeCart(cart, user);
    }

}