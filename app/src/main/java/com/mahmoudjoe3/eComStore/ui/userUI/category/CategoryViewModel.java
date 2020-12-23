package com.mahmoudjoe3.eComStore.ui.userUI.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

import java.util.List;

public class CategoryViewModel extends ViewModel {

    private FirebaseRepo repo;
    private MutableLiveData<List<Product>> productsLiveData;

    public CategoryViewModel() {
        repo=FirebaseRepo.getInstance();
        productsLiveData= new MutableLiveData<>();
    }

    private void fitchProduct(String productOwner,String cat){
        if(cat.equalsIgnoreCase("home"))
            repo.fitchProducts(null,null);
        else
            repo.fitchProducts(null,cat);
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

    public LiveData<List<Product>> getProductsLiveData(String OwnerId,String cat) {
        fitchProduct(OwnerId,cat);
        return productsLiveData;
    }
}