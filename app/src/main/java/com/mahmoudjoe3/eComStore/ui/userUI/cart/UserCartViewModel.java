package com.mahmoudjoe3.eComStore.ui.userUI.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

import java.util.List;

public class UserCartViewModel extends ViewModel {

    private FirebaseRepo repo;
    private MutableLiveData<AuthorizedUser> userLiveData;
    private MutableLiveData<List<Product>> productsLiveData;

    public UserCartViewModel() {
        repo = FirebaseRepo.getInstance();
        userLiveData = new MutableLiveData<>();
        productsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Product>> getProductsByIds(List<String> productIds) {
        repo.getProductListByIds(productIds);
        repo.setOnGetProductListener(products -> productsLiveData.setValue(products));
        return productsLiveData;
    }

    public LiveData<AuthorizedUser> getUserLiveData(String userId) {
        repo.getUserById(userId);
        repo.setOnFindUserListener(user -> userLiveData.setValue(user));
        return userLiveData;
    }

    public void removeCart(String cart, AuthorizedUser user) {
        repo.removeCart(cart, user);
    }


}
