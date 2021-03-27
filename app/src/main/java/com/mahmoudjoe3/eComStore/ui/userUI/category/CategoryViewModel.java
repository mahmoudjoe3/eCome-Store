package com.mahmoudjoe3.eComStore.ui.userUI.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

import java.util.List;

public class CategoryViewModel extends ViewModel {

    private FirebaseRepo repo;
    private MutableLiveData<List<Product>> productsLiveData;
    private MutableLiveData<AuthorizedUser> userLiveData;

    public CategoryViewModel() {
        repo = FirebaseRepo.getInstance();
        productsLiveData = new MutableLiveData<>();
        userLiveData = new MutableLiveData<>();

    }

    public LiveData<List<Product>> getProductsLiveData(String OwnerId, String cat) {
        fitchProduct(OwnerId, cat);

        return productsLiveData;
    }

    public LiveData<AuthorizedUser> getUserLiveData(String userId) {
        repo.getUserById(userId);
        repo.setOnFindUserListener(user -> userLiveData.setValue(user));
        return userLiveData;
    }

    private void fitchProduct(String productOwner, String cat) {
        if (cat.equalsIgnoreCase("home"))
            repo.fitchProducts(null, null);
        else
            repo.fitchProducts(null, cat);
        repo.setOnFitchProductListener(new FirebaseRepo.OnFitchProductListener() {
            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onSuccess(List<Product> productList) {
                productsLiveData.setValue(productList);
            }
        });
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