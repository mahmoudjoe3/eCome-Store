package com.mahmoudjoe3.eComStore.repo;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FirebaseRepo {
    private static final String TAG = "FirebaseRepo";
    static FirebaseRepo instance;

    static StorageReference mProductImageReference;
    static DatabaseReference mReference;

    ArrayList<String>url = new ArrayList<>();
    Product mProduct;
    public static synchronized FirebaseRepo getInstance(){
        if(instance==null)
        {
            instance=new FirebaseRepo();
        }
        mProductImageReference= FirebaseStorage.getInstance().getReference(Prevalent.refStorage_productImage);
        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_product);

        return instance;
    }

    /////////////////////////////////////    product fitch    //////////////////////////////////////

    public void fitchProducts(){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> products=new ArrayList<>();
                for(DataSnapshot snap:snapshot.getChildren()){
                    products.add(snap.getValue(Product.class));
                }
                if(mOnFitchProductListener!=null)
                    mOnFitchProductListener.onSuccess(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(mOnFitchProductListener!=null)
                    mOnFitchProductListener.onFailure(error.getMessage());
            }
        });


    }

    public OnFitchProductListener mOnFitchProductListener;
    public void setOnFitchProductListener(OnFitchProductListener mOnFitchProductListener) {
        this.mOnFitchProductListener = mOnFitchProductListener;
    }
    public interface OnFitchProductListener{
        void onFailure(String error);
        void onSuccess(List<Product> productList);
    }





    /////////////////////////////////////    product insertion    //////////////////////////////////////
    public void insertProduct(Product product, Uri[] mImageUri) {
        String date,time,productKey;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat DateFormat= new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat TimeFormat= new SimpleDateFormat("HH:mm:ss a");

        date=DateFormat.format(calendar.getTime());
        time=TimeFormat.format(calendar.getTime());
        productKey = mImageUri[0].getLastPathSegment() + date + time  ;

        product.setDate(date);
        product.setTime(time);
        mProduct=new Product(product);
        ArrayList<Uri> imageUri=new ArrayList<>();
        for(Uri u: mImageUri)
            if (u!=null)
                imageUri.add(u);

        uploadImage(imageUri,0,productKey);
        Log.d(TAG, "insertProduct: "+mProduct);
    }

    void uploadImage(ArrayList<Uri> mImageUri, int i, String productKey) {
        if(i>=mImageUri.size()) {
            insertProductTodatabase();
            Log.d(TAG, "onSuccess: Done");
            return;
        }
        if (mImageUri.get(i) != null) {
            mProductImageReference
                    .child(productKey + "_" + i )
                    .putFile(mImageUri.get(i))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            mProduct.getmImageUri().add(imageUrl);
                                            Log.d(TAG, "onSuccess: " + i + "  " + imageUrl);
                                            uploadImage(mImageUri,i+1,productKey);
                                        }
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mOnAddProductListener != null)
                                mOnAddProductListener.onFailure(e.getMessage());
                        }
                    });
        }
    }


    private void insertProductTodatabase() {
        mProduct.setmId(mReference.push().getKey());
        mReference.child(mProduct.getmId()).setValue(mProduct);
        if(mOnAddProductListener!=null)
            mOnAddProductListener.onSuccess();
    }

    public OnAddProductListener mOnAddProductListener;
    public void setOnAddProductListener(OnAddProductListener mOnAddProductListener) {
        this.mOnAddProductListener = mOnAddProductListener;
    }
    public interface OnAddProductListener{
        void onFailure(String error);
        void onSuccess();
    }
}
