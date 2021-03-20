package com.mahmoudjoe3.eComStore.viewModel.admin;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

public class AddProductViewModel extends AndroidViewModel {

    private FirebaseRepo repo;
    private FirebaseRepo.OnAddProductListener mOnAddProductListener;

    public AddProductViewModel(@NonNull Application application) {
        super(application);
        repo = FirebaseRepo.getInstance();
    }

    public void insertProduct(Product product, Uri[] mImageUri) {
        repo.insertProduct(product, mImageUri);
        repo.setOnAddProductListener(new FirebaseRepo.OnAddProductListener() {
            @Override
            public void onFailure(String error) {
                if (mOnAddProductListener != null)
                    mOnAddProductListener.onFailure(error);
            }

            @Override
            public void onSuccess() {
                if (mOnAddProductListener != null)
                    mOnAddProductListener.onSuccess();
            }
        });
    }

    public void setmOnAddProductListener(FirebaseRepo.OnAddProductListener mOnAddProductListener) {
        this.mOnAddProductListener = mOnAddProductListener;
    }
}
