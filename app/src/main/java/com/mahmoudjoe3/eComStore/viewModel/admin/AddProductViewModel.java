package com.mahmoudjoe3.eComStore.viewModel.admin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

public class AddProductViewModel extends AndroidViewModel {

    private FirebaseRepo repo;
    public AddProductViewModel(@NonNull Application application) {
        super(application);
        repo= FirebaseRepo.getInstance(application);
    }

    public void insertProduct(Product product){
        repo.insertProduct(product);
    }
}
