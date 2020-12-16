package com.mahmoudjoe3.eComStore.viewModel.admin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

import java.util.List;

public class AdminHomePageViewModel extends AndroidViewModel {
    private FirebaseRepo repo;
    private FirebaseRepo.OnFitchProductListener onFitchProductListener;
    private MutableLiveData<List<Product>> productsLiveData;

    public AdminHomePageViewModel(@NonNull Application application) {
        super(application);
        repo=FirebaseRepo.getInstance();
        productsLiveData= new MutableLiveData<>();
        fitchProduct();
    }

    void fitchProduct(){
        repo.fitchProducts();
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

    public LiveData<List<Product>> getProductsLiveData() {
        return productsLiveData;
    }

    void setProductsLiveData(MutableLiveData<List<Product>> productsLiveData) {
        this.productsLiveData = productsLiveData;
    }
}
