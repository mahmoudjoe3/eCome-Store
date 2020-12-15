package com.mahmoudjoe3.eComStore.repo;

import android.annotation.SuppressLint;
import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mahmoudjoe3.eComStore.model.Product;

public class FirebaseRepo {
    static FirebaseRepo instance;

    static DatabaseReference mReference;

    public static synchronized FirebaseRepo getInstance(){
        if(instance==null)
        {
            instance=new FirebaseRepo();
        }
        mReference= FirebaseDatabase.getInstance().getReference();
        return instance;
    }

    public void insertProduct(Product product) {
        //do it in the background
    }
}
