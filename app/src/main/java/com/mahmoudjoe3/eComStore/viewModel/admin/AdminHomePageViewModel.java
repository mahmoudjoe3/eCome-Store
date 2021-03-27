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
    private MutableLiveData<List<Product>> productsLiveData;
    private FirebaseRepo.onProductDeleted onProductDeleted;


    public AdminHomePageViewModel(@NonNull Application application) {
        super(application);
        repo = FirebaseRepo.getInstance();
        productsLiveData = new MutableLiveData<>();

    }

    void fitchProduct(String productOwner) {
        repo.fitchProducts(productOwner, null);
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

    public LiveData<List<Product>> getProductsLiveData(String OwnerId) {
        fitchProduct(OwnerId);
        return productsLiveData;
    }

    public void deleteProduct(Product product) {
        repo.deleteProduct(product);
        repo.setOnProductDeleted(() -> {
            if (onProductDeleted != null) onProductDeleted.onSuccess();
        });
    }

    public void setOnProductDeleted(FirebaseRepo.onProductDeleted onProductDeleted) {
        this.onProductDeleted = onProductDeleted;
    }


}
